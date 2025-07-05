import { useState, useEffect, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { Dropdown } from "primereact/dropdown";
import { Calendar } from "primereact/calendar";
import type { Nullable } from "primereact/ts-helpers";
import { addDays, addHours, format, isBefore, set, startOfDay } from "date-fns";
import { useAppDispatch } from "~/store";
import { fetchCommonData, type CommonData } from "~/test/commonDataSlice";
import { InputSwitch } from "primereact/inputswitch";
import { useCommonData } from "~/test/useCommonData";
import $axios from "~/axios";

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

  // payment
  const [selectedPayment, setSelectedPayment] = useState<any>(null);
  const [amount, setAmount] = useState<string>("0");
  const [notePayment, setNotePayment] = useState("");
  const [statusPayment, setStatusPayment] = useState<boolean>(true);

  // data
  const [priceData, setPriceData] = useState<any>(null);

  const [submitting, setSubmitting] = useState(false);
  const dispatch = useAppDispatch();
  const toast = useRef<Toast>(null);

  const { commonData } = useCommonData(["paymentmethods"]);

  const paymentMethods = commonData.paymentMethods;

  // load owner
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

  // load by id
  useEffect(() => {
    if (id && open) {
      loadDataById(id).then((data) => {
        setStatus(data.status ?? true);
        setNote(data.note || "");
        setSelectedCustomer(
          data.userId ? { id: data.userId, fullName: data.userName } : null
        );
        setSelectedPayment(
          data.paymentId ? { id: data.paymentId, name: data.paymentName } : null
        );
        setNotePayment(data.notePayment || "");
        setStatusPayment(data.paymentStatus ?? true);
        const checkIn = data.checkInTime ? new Date(data.checkInTime) : null;
        const checkOut = data.checkOutTime ? new Date(data.checkOutTime) : null;
        setCheckInDateTime(checkIn);
        setCheckOutDateTime(checkOut);
        setAmount(data.amount?.toString() || "0");
        if (checkIn && checkOut) {
          const hoursDiff =
            (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60);
          setIsHourly(hoursDiff <= 24);
          if (hoursDiff <= 24) {
            setSelectedHours(Math.ceil(hoursDiff));
          }
        }
      });
    } else {
      // Reset form for create mode
      setStatus(true);
      setCheckInDateTime(null);
      setCheckOutDateTime(null);
      setNote("");
      setSelectedCustomer(null);
      setSelectedPayment(null);
      setNotePayment("");
      setStatusPayment(true);
      setAmount("0");
      setIsHourly(true);
      setSelectedHours(1);
    }
  }, [id, open, loadDataById]);

  // handle change time
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

  // handle amount
  useEffect(() => {
    if (priceData) {
      if (isHourly) {
        const calculatedAmount = (selectedHours * priceData.priceHours).toFixed(
          2
        );
        setAmount(calculatedAmount);
      } else {
        setAmount(priceData.priceNights.toFixed(2));
      }
    } else {
      setAmount("0");
    }
  }, [isHourly, selectedHours, priceData]);

  // handle change time
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

  // submit
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

    formData.append("amount", amount);
    formData.append("methodId", selectedPayment?.id);
    formData.append("notePayment", notePayment);
    formData.append("statusPayment", statusPayment.toString());

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

  // handle change time
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

  // fetch prices data
  useEffect(() => {
    const fetchData = async () => {
      const res = await $axios.get(`/booking/${roomId}/prices`);
      setPriceData(res.data.result);
      console.log(res.data.result.priceHours);
    };

    fetchData();
  }, [roomId]);

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
              <label
                htmlFor="customer"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Customer
              </label>
              <Dropdown
                id="customer"
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
              <label
                htmlFor="note"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Note
              </label>
              <InputText
                id="note"
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
                <label
                  htmlFor="hourly"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Hours
                </label>
                <Dropdown
                  id="hourly"
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
              <label
                htmlFor="checkin"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Check In
              </label>
              <Calendar
                id="checkin"
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
                <label
                  htmlFor="checkouthourly"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Auto Check-Out
                </label>
                <Calendar
                  id="checkouthourly"
                  value={checkOutDateTime}
                  showTime
                  hourFormat="24"
                  disabled
                  className="w-full"
                />
              </div>
            ) : (
              <div>
                <label
                  htmlFor="checkout"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Check-Out
                </label>
                <Calendar
                  id="checkout"
                  value={checkOutDateTime}
                  showTime
                  hourFormat="24"
                  disabled
                  className="w-full"
                />
              </div>
            )}

            <div className="flex items-center gap-4">
              <label
                htmlFor="status"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Status
              </label>
              <InputSwitch
                id="status"
                checked={status}
                onChange={(e) => setStatus(e.value)}
                disabled={submitting}
              />
            </div>
          </div>

          <div className="mt-10">
            <label className="font-bold">Payment</label>

            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <label
                  htmlFor="method"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Payment Method
                </label>
                <Dropdown
                  id="method"
                  value={selectedPayment}
                  onChange={(e) => setSelectedPayment(e.value)}
                  options={paymentMethods}
                  optionLabel="name"
                  showClear
                  className="w-full"
                />
              </div>

              <div>
                <label
                  htmlFor="notePayment"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Note
                </label>
                <InputText
                  id="notePayment"
                  value={notePayment}
                  onChange={(e) => setNotePayment(e.target.value)}
                  disabled={submitting}
                  className="w-full p-2 border rounded-lg"
                />
              </div>

              <div className="flex items-center gap-4">
                <label
                  htmlFor="statusPayment"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Status
                </label>
                <InputSwitch
                  id="statusPayment"
                  checked={statusPayment}
                  onChange={(e) => setStatusPayment(e.value)}
                  disabled={submitting}
                />
              </div>
            </div>
          </div>

          <div className="flex justify-between">
            {priceData ? (
              <div>
                <>
                  Price Hours: {priceData.priceHours} <br />
                  Price Night: {priceData.priceNights}
                </>
              </div>
            ) : (
              "Loading prices..."
            )}

            <label htmlFor="amount">Total</label>
            <div className="font-bold">{amount}</div>
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
