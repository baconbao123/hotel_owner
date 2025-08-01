import { useState, useEffect, useRef, useCallback } from "react";
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

interface BookingTime {
  checkInTime: string;
  checkOutTime: string;
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
  const [customers, setCustomers] = useState<any[]>([]);
  const [note, setNote] = useState("");
  const [status, setStatus] = useState(true);
  const [selectedPayment, setSelectedPayment] = useState<any>(null);
  const [amount, setAmount] = useState<string>("0");
  const [notePayment, setNotePayment] = useState("");
  const [statusPayment, setStatusPayment] = useState<boolean>(true);
  const [priceData, setPriceData] = useState<any>(null);
  const [bookedHours, setBookedHours] = useState<BookingTime[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const dispatch = useAppDispatch();
  const toast = useRef<Toast>(null);

  const { commonData } = useCommonData(["paymentmethods"]);
  const paymentMethods = commonData.paymentMethods;

  const loadCustomers = async (keyword = "", page = 0) => {
    const result = await dispatch(
      fetchCommonData({
        types: ["customers"],
        force: true,
        params: { keyword, pageOwner: page },
      })
    );
    const res = result.payload as CommonData;
    const newCustomers: any[] = res.customers || [];
    if (page === 0) {
      setCustomers(newCustomers);
    } else {
      setCustomers((prev) => [...prev, ...newCustomers]);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const fetchBookedHours = useCallback(
    async (date: string) => {
      try {
        const res = await $axios.get(
          `/booking/${roomId}/booked-hours?startOfDay=${date}`
        );
        setBookedHours(res.data.result || []);
      } catch (err) {
        console.error("Failed to fetch booked hours", err);
      }
    },
    [roomId]
  );

  useEffect(() => {
    let timeoutId: NodeJS.Timeout;
    if (checkInDateTime) {
      const date = format(checkInDateTime, "yyyy-MM-dd");
      timeoutId = setTimeout(() => fetchBookedHours(date), 500);
    } else {
      setBookedHours([]);
    }
    return () => clearTimeout(timeoutId);
  }, [checkInDateTime, fetchBookedHours]);

  useEffect(() => {
    if (id && open) {
      loadDataById(id).then((data) => {
        const customerData = customers.find((c: any) => c.id === data.userId);
        setSelectedCustomer(customerData);
        setNote(data.note || "");
        setStatus(data.status ?? true);

        const checkIn = data.checkInTime ? new Date(data.checkInTime) : null;
        const checkOut = data.checkOutTime ? new Date(data.checkOutTime) : null;
        setCheckInDateTime(checkIn);
        setCheckOutDateTime(checkOut);

        let isHourlyBooking = true;
        if (checkIn && checkOut) {
          const hoursDiff =
            (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60);
          const isCheckInAt2PM =
            checkIn.getHours() === 14 && checkIn.getMinutes() === 0;
          const isCheckOutAtNoon =
            checkOut.getHours() === 12 && checkOut.getMinutes() === 0;
          const isNextDay = checkOut.getDate() === checkIn.getDate() + 1;
          if (isCheckInAt2PM && isCheckOutAtNoon && isNextDay) {
            isHourlyBooking = false;
          } else {
            isHourlyBooking = hoursDiff <= 24;
            setSelectedHours(Math.ceil(hoursDiff));
          }
        }
        setIsHourly(isHourlyBooking);

        const selectedPaymentMethod = paymentMethods?.find(
          (p: any) => p.id === data.paymentId
        );
        setSelectedPayment(selectedPaymentMethod);
        setAmount(data.amount?.toString() || "0");
        setNotePayment(data.notePayment || "");
        setStatusPayment(data.paymentStatus ?? true);
      });
    } else {
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
  }, [id, open, loadDataById, customers, paymentMethods]);

  useEffect(() => {
    const fetchData = async () => {
      const res = await $axios.get(`/booking/${roomId}/prices`);
      setPriceData(res.data.result);
    };
    fetchData();
  }, [roomId]);

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

  const handleCheckInChange = (e: any) => {
    const newDate = e.value as Date | null;
    if (!newDate) {
      setCheckInDateTime(null);
      setCheckOutDateTime(null);
      return;
    }

    const isBooked = bookedHours.some((booking) => {
      const bookingCheckIn = new Date(booking.checkInTime);
      const bookingCheckOut = new Date(booking.checkOutTime);
      return (
        (newDate.toDateString() === bookingCheckOut.toDateString() &&
          newDate < bookingCheckOut) ||
        (newDate.toDateString() === bookingCheckIn.toDateString() &&
          newDate >= bookingCheckIn) ||
        (newDate > bookingCheckIn && newDate < bookingCheckOut)
      );
    });

    if (isBooked) {
      toast.current?.show({
        severity: "error",
        summary: "Invalid Check-In Time",
        detail: "Selected time is already booked.",
        life: 3000,
      });
      return;
    }

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

  const handleHourSelect = (hour: number) => {
    if (!checkInDateTime) return;
    const newCheckIn = new Date(checkInDateTime);
    newCheckIn.setHours(hour, 0, 0, 0);

    const isBooked = bookedHours.some((booking) => {
      const bookingCheckIn = new Date(booking.checkInTime);
      const bookingCheckOut = new Date(booking.checkOutTime);
      return newCheckIn >= bookingCheckIn && newCheckIn < bookingCheckOut;
    });

    if (isBooked) {
      toast.current?.show({
        severity: "error",
        summary: "Invalid Check-In Time",
        detail: "Selected hour is already booked.",
        life: 3000,
      });
      return;
    }

    setCheckInDateTime(newCheckIn);
    if (isHourly) {
      const checkOut = addHours(newCheckIn, selectedHours);
      setCheckOutDateTime(checkOut);
    }
  };

  const handleHoursChange = (e: any) => {
    const hours = e.value;
    if (checkInDateTime) {
      const checkOut = addHours(checkInDateTime, hours);
      const isBooked = bookedHours.some((booking) => {
        const bookingCheckIn = new Date(booking.checkInTime);
        const bookingCheckOut = new Date(booking.checkOutTime);
        return (
          (checkOut > bookingCheckIn && checkInDateTime < bookingCheckOut) ||
          (checkInDateTime >= bookingCheckIn &&
            checkInDateTime < bookingCheckOut)
        );
      });

      if (isBooked) {
        toast.current?.show({
          severity: "error",
          summary: "Invalid Duration",
          detail: "Selected duration overlaps with a booked slot.",
          life: 3000,
        });
        return;
      }
    }

    setSelectedHours(hours);
    if (isHourly && checkInDateTime) {
      setCheckOutDateTime(addHours(checkInDateTime, hours));
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
  }, [isHourly, checkInDateTime, selectedHours]);

  const availableHours = [1, 2, 3, 4, 5].filter((h) => {
    if (!checkInDateTime) return true;
    const checkOut = addHours(checkInDateTime, h);
    return !bookedHours.some((booking) => {
      const bookingCheckIn = new Date(booking.checkInTime);
      const bookingCheckOut = new Date(booking.checkOutTime);
      return checkOut > bookingCheckIn && checkInDateTime < bookingCheckOut;
    });
  });

  const hourSlots = Array.from({ length: 24 }, (_, i) => i).map((hour) => {
    const checkInDate = new Date(checkInDateTime || new Date());
    checkInDate.setHours(hour, 0, 0, 0);

    const isBooked = bookedHours.some((booking) => {
      const bookingCheckIn = new Date(booking.checkInTime);
      const bookingCheckOut = new Date(booking.checkOutTime);
      return checkInDate >= bookingCheckIn && checkInDate < bookingCheckOut;
    });

    return {
      hour,
      isBooked,
    };
  });

  const validateDateTimes = () => {
    if (!checkInDateTime || !checkOutDateTime) {
      toast.current?.show({
        severity: "error",
        summary: "Invalid Input",
        detail: "Please select check-in and check-out times.",
        life: 3000,
      });
      return false;
    }

    if (isHourly && isBefore(checkInDateTime, new Date())) {
      toast.current?.show({
        severity: "error",
        summary: "Invalid Check-In Time",
        detail: "Check-in time cannot be in the past for hourly booking.",
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

    const isBooked = bookedHours.some((booking) => {
      const bookingCheckIn = new Date(booking.checkInTime);
      const bookingCheckOut = new Date(booking.checkOutTime);
      return (
        checkInDateTime < bookingCheckOut && checkOutDateTime > bookingCheckIn
      );
    });

    if (isBooked) {
      toast.current?.show({
        severity: "error",
        summary: "Time Conflict",
        detail: "Selected time slot is already booked.",
        life: 3000,
      });
      return false;
    }

    return true;
  };

  const submit = async () => {
    setSubmitting(true);

    if (!validateDateTimes()) {
      setSubmitting(false);
      return;
    }

    const formData = new FormData();
    formData.append("userId", selectedCustomer?.id || "");
    formData.append("roomId", roomId || "");
    formData.append(
      "checkInTime",
      format(checkInDateTime!, "yyyy-MM-dd'T'HH:mm") || ""
    );
    formData.append(
      "checkOutTime",
      format(checkOutDateTime!, "yyyy-MM-dd'T'HH:mm") || ""
    );
    formData.append("status", JSON.stringify(status) || "");
    formData.append("note", note || "");
    formData.append("amount", amount || "");
    formData.append("methodId", selectedPayment?.id || "");
    formData.append("notePayment", notePayment || "");
    formData.append("statusPayment", JSON.stringify(statusPayment) || "");

    try {
      if (id) {
        await updateItem(id, formData);
        toast.current?.show({
          severity: "success",
          summary: "Success",
          detail: "Booking updated",
          life: 3000,
        });
      } else {
        await createItem(formData);
        toast.current?.show({
          severity: "success",
          summary: "Success",
          detail: "Booking created",
          life: 3000,
        });
      }
      onClose();
    } catch (err: any) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: err.message || "Failed to save booking",
        life: 3000,
      });
    } finally {
      setSubmitting(false);
    }
  };

  const getError = (field: string) =>
    (error &&
      typeof error === "object" &&
      (error as Record<string, string>)[field]) ||
    null;

  return (
    <>
      <Toast ref={toast} />
      <Dialog
        visible={open}
        onHide={onClose}
        header={mode === "edit" ? "Edit" : "Create"}
        modal
        footer={
          <div className="flex justify-center gap-2">
            <Button
              label="Close"
              onClick={onClose}
              severity="secondary"
              outlined
              disabled={submitting}
              style={{ padding: "8px 40px" }}
            />
            <Button
              label="Save"
              onClick={submit}
              severity="success"
              disabled={submitting}
              loading={submitting}
              className="btn_submit"
              style={{ padding: "8px 40px" }}
            />
          </div>
        }
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
                className={`w-full border rounded-lg focus:ring-2 focus:ring-blue-500 ${
                  getError("userId") ? "p-invalid" : ""
                }`}
              />
              {getError("userId") && (
                <small className="text-red-600 text-xs mt-1 block">
                  {getError("userId")}
                </small>
              )}
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
                  options={availableHours.map((h) => ({
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
                dateFormat="yy/mm/dd"
                className="w-full"
              />
              {isHourly && checkInDateTime && (
                <div className="mt-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Available Hours
                  </label>
                  <div className="grid grid-cols-4 gap-2">
                    {hourSlots.map((slot) => (
                      <Button
                        key={slot.hour}
                        label={`${slot.hour}:00`}
                        style={{color: "white"}}
                        className={`p-2 text-sm`}
                        disabled={slot.isBooked}
                        onClick={() => handleHourSelect(slot.hour)}
                      />
                    ))}
                  </div>
                </div>
              )}
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
                  dateFormat="yy/mm/dd"
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
                  dateFormat="yy/mm/dd"
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
                  checkmark
                  highlightOnSelect={false}
                  className={`w-full border rounded-lg focus:ring-2 focus:ring-blue-500 ${
                    getError("methodId") ? "p-invalid" : ""
                  }`}
                />
                {getError("methodId") && (
                  <small className="text-red-600 text-xs mt-1 block">
                    {getError("methodId")}
                  </small>
                )}
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
                Price Hours: {priceData.priceHours} <br />
                Price Night: {priceData.priceNights}
              </div>
            ) : (
              "Loading prices..."
            )}
            <label htmlFor="amount">Total</label>
            <div className="font-bold">{amount}</div>
          </div>
        </div>
      </Dialog>
    </>
  );
}
