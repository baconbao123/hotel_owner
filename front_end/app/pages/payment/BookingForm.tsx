import { useState, useEffect, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { Dropdown } from "primereact/dropdown";
import { Calendar } from "primereact/calendar";
import type { Nullable } from "primereact/ts-helpers";
import { addDays, addHours, format, isBefore, set, startOfDay } from "date-fns";
import { useAppDispatch } from "@/store";
import { fetchCommonData, type CommonData } from "@/test/commonDataSlice";

interface Props {
  roomId: any;
  id?: string;
  open: boolean;
  mode?: "create" | "edit" | "view";
  onClose: () => void;
  loadDataById: (id: string) => Promise<any>;
  createItem: (data: object | FormData) => Promise<any>;
  updateItem: (id: string, data: object | FormData) => Promise<any>;
  error: Object | null;
}

export default function BookingForm({
  roomId,
  id,
  open,
  mode = "create",
  onClose,
  loadDataById,
  createItem,
  updateItem,
  error,
}: Props) {
  const [checkInDateTime, setCheckInDateTime] = useState<Nullable<Date>>(null);
  const [checkOutDateTime, setCheckOutDateTime] =
    useState<Nullable<Date>>(null);
  const [isHourly, setIsHourly] = useState(true);
  const [selectedCustomer, setSelectedCustomer] = useState<any>(null);
  const [selectedHours, setSelectedHours] = useState<number>(1);
  const [customers, setCustomers] = useState<any>([]);
  const [note, setNote] = useState("");
  const [status, setStatus] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const dispatch = useAppDispatch();
  const toast = useRef<Toast>(null);

  const loadOwners = async (keyword = "", page = 0) => {
    const result = await dispatch(
      fetchCommonData({
        types: ["customers"],
        force: true,
        params: { keyword, pageOwner: page },
      })
    );
    const res = result.payload as CommonData;
    const newCustomers: any = res.customers || [];
    if (page === 0) {
      setCustomers(newCustomers);
    } else {
      setCustomers((prev: any) => [...prev, ...newCustomers]);
    }
  };

  useEffect(() => {
    loadOwners();
  }, []);

  useEffect(() => {
    if (id && open) {
      loadDataById(id).then((data) => {
        setStatus(data.status ?? true);
        setNote(data.note || "");
        setSelectedCustomer(data.customer || null);
        const checkIn = data.checkInDateTime
          ? new Date(data.checkInDateTime)
          : null;
        const checkOut = data.checkOutDateTime
          ? new Date(data.checkOutDateTime)
          : null;
        setCheckInDateTime(checkIn);
        setCheckOutDateTime(checkOut);
      });
    } else {
      setStatus(true);
      setCheckInDateTime(null);
      setCheckOutDateTime(null);
      setNote("");
      setSelectedCustomer(null);
    }
  }, [id, open]);

  const handleCheckInChange = (e: any) => {
    const newDate = e.value as Date | null;
    setCheckInDateTime(newDate);
    if (isHourly && newDate) {
      const checkOut = addHours(newDate, selectedHours);
      setCheckOutDateTime(checkOut);
    } else if (!isHourly && newDate) {
      const fixedCheckIn = set(startOfDay(newDate), { hours: 14, minutes: 0 });
      const fixedCheckOut = set(addDays(fixedCheckIn, 1), {
        hours: 12,
        minutes: 0,
      });
      setCheckInDateTime(fixedCheckIn);
      setCheckOutDateTime(fixedCheckOut);
    }
  };

  const handleHoursChange = (e: any) => {
    setSelectedHours(e.value);
    if (isHourly && checkInDateTime) {
      const newCheckOut = addHours(checkInDateTime, e.value);
      setCheckOutDateTime(newCheckOut);
    }
  };

  const validateDateTimes = () => {
    const now = new Date();
    if (!checkInDateTime || !checkOutDateTime) return false;
    if (isBefore(checkInDateTime, now)) {
      toast.current?.show({
        severity: "error",
        summary: "Invalid Check-In Time",
        detail: "Check-in time cannot be in the past.",
        life: 3000,
      });
      return false;
    }
    if (isBefore(checkOutDateTime, checkInDateTime)) {
      toast.current?.show({
        severity: "error",
        summary: "Invalid Check-Out Time",
        detail: "Check-out time must be after check-in.",
        life: 3000,
      });
      return false;
    }
    return true;
  };

  const submit = async () => {
    if (!validateDateTimes()) return;
    setSubmitting(true);
    const formData = new FormData();
    formData.append("userId", selectedCustomer?.id || "");
    formData.append("roomId", roomId);
    formData.append(
      "checkInTime",
      format(checkInDateTime!, "yyyy-MM-dd'T'HH:mm")
    );
    formData.append(
      "checkOutTime",
      format(checkOutDateTime!, "yyyy-MM-dd'T'HH:mm")
    );
    formData.append("status", JSON.stringify(status));
    formData.append("note", note);
    try {
      if (id) await updateItem(id, formData);
      else await createItem(formData);
      toast.current?.show({
        severity: "success",
        summary: "Success",
        detail: "Saved successfully",
        life: 3000,
      });
      onClose();
    } catch (err: any) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: err.response?.data?.message || "Failed",
        life: 3000,
      });
    } finally {
      setSubmitting(false);
    }
  };

  useEffect(() => {
    if (!checkInDateTime) return;

    if (isHourly) {
      setCheckOutDateTime(addHours(checkInDateTime, selectedHours));
    } else {
      const fixedCheckIn = set(startOfDay(checkInDateTime), {
        hours: 14,
        minutes: 0,
      });
      const fixedCheckOut = set(addDays(fixedCheckIn, 1), {
        hours: 12,
        minutes: 0,
      });
      setCheckInDateTime(fixedCheckIn);
      setCheckOutDateTime(fixedCheckOut);
    }
  }, [isHourly]);

  return (
    <>
      <Toast ref={toast} />
      <Dialog
        visible={open}
        onHide={onClose}
        header={mode === "edit" ? "Edit" : "Create"}
        modal
        style={{ width: "50%", maxWidth: "95vw" }}
        breakpoints={{ "960px": "85vw", "641px": "95vw" }}
      >
        <div className="p-4 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label>Customer</label>
              <Dropdown
                value={selectedCustomer}
                onChange={(e) => setSelectedCustomer(e.value)}
                options={customers}
                optionLabel="fullName"
                filter
                showClear
                className="w-full"
              />
            </div>
            <div>
              <label>Note</label>
              <InputText
                value={note}
                onChange={(e) => setNote(e.target.value)}
                className="w-full"
              />
            </div>
          </div>

          <div className="flex gap-4 items-center">
            <label className="font-bold">Booking Mode:</label>
            <Button
              label="Hourly"
              outlined={!isHourly}
              onClick={() => setIsHourly(true)}
            />
            <Button
              label="Overnight"
              outlined={isHourly}
              onClick={() => setIsHourly(false)}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            {isHourly && (
              <div>
                <label>Hours</label>
                <Dropdown
                  value={selectedHours}
                  options={[1, 2, 3, 4, 5].map((h) => ({
                    label: `${h} hour(s)`,
                    value: h,
                  }))}
                  onChange={handleHoursChange}
                  className="w-full"
                />
              </div>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label>Check-In</label>
              <Calendar
                value={checkInDateTime}
                onChange={handleCheckInChange}
                showTime
                hourFormat="24"
                minDate={new Date()}
                className="w-full"
              />
            </div>
            {isHourly ? (
              <div>
                <label>Auto Check-Out</label>
                <Calendar
                  value={checkOutDateTime}
                  showTime
                  hourFormat="24"
                  disabled
                  className="w-full"
                />
              </div>
            ) : (
              <div>
                <label>Check-Out</label>
                <Calendar
                  value={checkOutDateTime}
                  showTime
                  hourFormat="24"
                  disabled
                  className="w-full"
                />
              </div>
            )}
          </div>

          <div className="flex justify-end gap-2 mt-4">
            <Button
              label="Close"
              onClick={onClose}
              severity="info"
              outlined
              disabled={submitting}
            />
            <Button
              label="Save"
              onClick={submit}
              severity="success"
              loading={submitting}
            />
          </div>
        </div>
      </Dialog>
    </>
  );
}
