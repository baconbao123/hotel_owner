import type { Route } from "../../+types/root";

export function meta({}: Route.MetaArgs) {
  return [{ title: "Setting" }, { name: "description", content: "" }];
}
import BusinessCalendar from "@/components/common/calendar";
import { useEffect, useRef, useState } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Calendar } from "primereact/calendar";
import { Button } from "primereact/button";
import { Tag } from "primereact/tag";
import type {
  Booking,
  BookingFormData,
  Room,
  BookingStatus,
} from "@/types/booking";
import { mockApiService } from "@/mockData/bookingData";
import { Toast } from "primereact/toast";
import type { CalendarEvent } from "@/types/calendar";

// Add booking colors object
const bookingColors: Record<string, string> = {
  pending: "#FFA726",
  confirmed: "#4CAF50",
  checkedIn: "#2196F3",
  checkedOut: "#9E9E9E",
  cancelled: "#F44336",
};

const CreateBookingForm = ({
  visible,
  onHide,
  onSubmit,
  rooms,
  loading,
}: {
  visible: boolean;
  onHide: () => void;
  // Update the type to match BookingFormData
  onSubmit: (data: BookingFormData) => Promise<void>;
  rooms: Room[];
  loading: boolean;
}) => {
  const [formData, setFormData] = useState<BookingFormData>({
    guestName: "",
    roomId: "",
    startDate: null,
    endDate: null,
    status: "pending",
  });

  const footer = (
    <div>
      <Button
        label="Cancel"
        icon="pi pi-times"
        onClick={onHide}
        className="p-button-text"
      />
      <Button
        label="Create"
        icon="pi pi-check"
        onClick={() => onSubmit(formData)}
        loading={loading}
      />
    </div>
  );

  return (
    <Dialog
      header="Create Booking"
      visible={visible}
      style={{ width: "450px" }}
      modal
      footer={footer}
      onHide={onHide}
    >
      <div className="grid">
        <div className="col-12">
          <span className="p-float-label">
            <InputText
              id="guestName"
              value={formData.guestName}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  guestName: e.target.value,
                }))
              }
              className="w-full"
            />
            <label htmlFor="guestName">Guest Name*</label>
          </span>
        </div>

        <div className="col-12">
          <span className="p-float-label">
            <Dropdown
              id="room"
              value={formData.roomId}
              options={rooms.map((room) => ({
                label: `Room ${room.roomNumber}`,
                value: room.id,
              }))}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  roomId: e.value,
                }))
              }
              className="w-full"
            />
            <label htmlFor="room">Room*</label>
          </span>
        </div>

        <div className="col-6">
          <span className="p-float-label">
            <Calendar
              id="startDate"
              value={formData.startDate}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  startDate: e.value as Date,
                }))
              }
              showIcon
              className="w-full"
            />
            <label htmlFor="startDate">Check-in Date*</label>
          </span>
        </div>

        <div className="col-6">
          <span className="p-float-label">
            <Calendar
              id="endDate"
              value={formData.endDate}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  endDate: e.value as Date,
                }))
              }
              showIcon
              className="w-full"
              minDate={formData.startDate || undefined}
            />
            <label htmlFor="endDate">Check-out Date*</label>
          </span>
        </div>
      </div>
    </Dialog>
  );
};

const ViewBookingDetails = ({
  visible,
  onHide,
  booking,
  room,
}: {
  visible: boolean;
  onHide: () => void;
  booking?: Booking;
  room?: Room;
}) => {
  const getStatusSeverity = (status: BookingStatus) => {
    switch (status) {
      case "confirmed":
        return "success";
      case "pending":
        return "warning";
      case "cancelled":
        return "danger";
      case "checkedIn":
        return "info";
      case "checkedOut":
        return "secondary";
      default:
        return "info";
    }
  };

  return (
    <Dialog
      header="Booking Details"
      visible={visible}
      style={{ width: "450px" }}
      modal
      onHide={onHide}
      footer={<Button label="Close" icon="pi pi-times" onClick={onHide} />}
    >
      {booking && (
        <div className="grid">
          <div className="col-12">
            <h3 className="mb-3">Guest Information</h3>
            <p>
              <strong>Name:</strong> {booking.guestName}
            </p>
            <p>
              <strong>Room:</strong> {room?.roomNumber}
            </p>
            <p>
              <strong>Check-in:</strong> {booking.start.toLocaleDateString()}
            </p>
            <p>
              <strong>Check-out:</strong> {booking.end.toLocaleDateString()}
            </p>
            <p>
              <strong>Status:</strong>{" "}
              <Tag
                value={
                  booking.status.charAt(0).toUpperCase() +
                  booking.status.slice(1)
                }
                severity={getStatusSeverity(booking.status)}
              />
            </p>
          </div>
        </div>
      )}
    </Dialog>
  );
};

const EditBookingForm = ({
  visible,
  onHide,
  onSubmit,
  booking,
  rooms,
  loading,
}: {
  visible: boolean;
  onHide: () => void;
  onSubmit: (data: BookingFormData) => Promise<void>;
  booking?: Booking;
  rooms: Room[];
  loading: boolean;
}) => {
  const [formData, setFormData] = useState<BookingFormData>({
    guestName: booking?.guestName || "",
    roomId: booking?.roomId || "",
    startDate: booking?.start || null,
    endDate: booking?.end || null,
    status: booking?.status || "pending",
  });

  useEffect(() => {
    if (booking) {
      setFormData({
        guestName: booking.guestName,
        roomId: booking.roomId,
        startDate: booking.start,
        endDate: booking.end,
        status: booking.status,
      });
    }
  }, [booking]);

  const footer = (
    <div>
      <Button
        label="Cancel"
        icon="pi pi-times"
        onClick={onHide}
        className="p-button-text"
      />
      <Button
        label="Update"
        icon="pi pi-check"
        onClick={() => onSubmit(formData)}
        loading={loading}
      />
    </div>
  );

  return (
    <Dialog
      header="Edit Booking"
      visible={visible}
      style={{ width: "450px" }}
      modal
      footer={footer}
      onHide={onHide}
    >
      <div className="grid">
        <div className="col-12">
          <span className="p-float-label">
            <InputText
              id="guestName"
              value={formData.guestName}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  guestName: e.target.value,
                }))
              }
              className="w-full"
            />
            <label htmlFor="guestName">Guest Name*</label>
          </span>
        </div>

        <div className="col-12">
          <span className="p-float-label">
            <Dropdown
              id="room"
              value={formData.roomId}
              options={rooms.map((room) => ({
                label: `Room ${room.roomNumber}`,
                value: room.id,
              }))}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  roomId: e.value,
                }))
              }
              className="w-full"
            />
            <label htmlFor="room">Room*</label>
          </span>
        </div>

        <div className="col-6">
          <span className="p-float-label">
            <Calendar
              id="startDate"
              value={formData.startDate}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  startDate: e.value as Date,
                }))
              }
              showIcon
              className="w-full"
            />
            <label htmlFor="startDate">Check-in Date*</label>
          </span>
        </div>

        <div className="col-6">
          <span className="p-float-label">
            <Calendar
              id="endDate"
              value={formData.endDate}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  endDate: e.value as Date,
                }))
              }
              showIcon
              className="w-full"
              minDate={formData.startDate || undefined}
            />
            <label htmlFor="endDate">Check-out Date*</label>
          </span>
        </div>

        <div className="col-12">
          <span className="p-float-label">
            <Dropdown
              id="status"
              value={formData.status}
              options={[
                { label: "Pending", value: "pending" },
                { label: "Confirmed", value: "confirmed" },
                { label: "Checked In", value: "checkedIn" },
                { label: "Checked Out", value: "checkedOut" },
                { label: "Cancelled", value: "cancelled" },
              ]}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  status: e.value,
                }))
              }
              className="w-full"
            />
            <label htmlFor="status">Status</label>
          </span>
        </div>
      </div>
    </Dialog>
  );
};

export default function BookingCalendar() {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedStatus, setSelectedStatus] =
    useState<BookingStatus>("pending");
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [showViewDialog, setShowViewDialog] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [selectedBooking, setSelectedBooking] = useState<Booking>();
  const toast = useRef<Toast>(null);

  // Fetch initial data using mock service
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [bookingsData, roomsData] = await Promise.all([
          mockApiService.getBookings(),
          mockApiService.getRooms(),
        ]);
        setBookings(bookingsData);
        setRooms(roomsData);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const validateBookingDates = (start: Date, end: Date) => {
    if (start < new Date()) {
      return "Cannot create bookings in the past";
    }
    return true;
  };

  const businessHours = {
    startTime: "08:00",
    endTime: "22:00",
    daysOfWeek: [0, 1, 2, 3, 4, 5, 6], // 0 = Sunday
  };

  const handleCreateBooking = async (formData: BookingFormData) => {
    if (!formData.startDate || !formData.endDate) return;

    try {
      setLoading(true);
      const room = rooms.find((r) => r.id === formData.roomId);

      // Transform BookingFormData to Booking format
      const bookingData: Omit<Booking, "id"> = {
        title: `Room ${room?.roomNumber} - ${formData.guestName}`,
        start: formData.startDate,
        end: formData.endDate,
        roomId: formData.roomId,
        guestName: formData.guestName,
        status: formData.status,
      };

      await mockApiService.createBooking(bookingData);
      setShowCreateDialog(false);
    } catch (error) {
      console.error("Failed to create booking:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateBooking = async (formData: BookingFormData) => {
    if (!selectedBooking || !formData.startDate || !formData.endDate) return;

    try {
      setLoading(true);
      const room = rooms.find((r) => r.id === formData.roomId);

      const updatedBooking: Booking = {
        id: selectedBooking.id,
        title: `Room ${room?.roomNumber} - ${formData.guestName}`,
        start: formData.startDate,
        end: formData.endDate,
        roomId: formData.roomId,
        guestName: formData.guestName,
        status: formData.status,
      };

      await mockApiService.updateBooking(updatedBooking);
      setShowEditDialog(false);
    } catch (error) {
      console.error("Failed to update booking:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteBooking = async (bookingId: string) => {
    try {
      await mockApiService.deleteBooking(bookingId);
      setBookings((prev) => prev.filter((b) => b.id !== bookingId));
    } catch (error) {
      throw error;
    }
  };

  // Use BookingStatus in your component
  const bookingStatusOptions: BookingStatus[] = [
    "pending",
    "confirmed",
    "checkedIn",
    "checkedOut",
    "cancelled",
  ];

  const handleCreateBookingDialog = async (data: BookingFormData) => {
    try {
      setLoading(true);
      // Call your create booking API here
      setShowCreateDialog(false);
    } catch (error) {
      console.error("Failed to create booking:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleEventCreate = async (formData: BookingFormData) => {
    if (!formData.startDate || !formData.endDate) return;

    try {
      setLoading(true);
      const room = rooms.find((r) => r.id === formData.roomId);

      if (!room) {
        throw new Error("Room not found");
      }

      const calendarEvent: Omit<CalendarEvent, "id"> = {
        title: `Room ${room.roomNumber} - ${formData.guestName}`,
        start: formData.startDate,
        end: formData.endDate,
        backgroundColor: bookingColors[formData.status],
        borderColor: bookingColors[formData.status],
        extendedProps: {
          guestName: formData.guestName,
          roomId: formData.roomId,
          status: formData.status,
        },
      };

      //   await onEventCreate(calendarEvent);

      //   toast.current?.show({
      //     severity: "success",
      //     summary: "Success",
      //     detail: "Booking created successfully",
      //     life: 3000,
      //   });

      setShowCreateDialog(false);
    } catch (error) {
      console.error("Failed to create booking:", error);
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail:
          error instanceof Error ? error.message : "Failed to create booking",
        life: 3000,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <Toast ref={toast} />
      <BusinessCalendar
        events={bookings}
        createDialogContent={
          <CreateBookingForm
            visible={showCreateDialog}
            onHide={() => setShowCreateDialog(false)}
            onSubmit={handleEventCreate}
            rooms={rooms}
            loading={loading}
          />
        }
        viewDialogContent={
          <ViewBookingDetails
            visible={showViewDialog}
            onHide={() => setShowViewDialog(false)}
            booking={selectedBooking}
            room={rooms.find((r) => r.id === selectedBooking?.roomId)}
          />
        }
        editDialogContent={
          <EditBookingForm
            visible={showEditDialog}
            onHide={() => setShowEditDialog(false)}
            onSubmit={handleUpdateBooking}
            booking={selectedBooking}
            rooms={rooms}
            loading={loading}
          />
        }
        onEventCreate={handleEventCreate}
        // onEventUpdate={handleUpdateBooking}
        onEventDelete={handleDeleteBooking}
        validateDateSelection={validateBookingDates}
        eventColors={bookingColors}
        businessHours={businessHours}
        allowedViews={["dayGridMonth", "timeGridWeek"]}
      />
    </div>
  );
}
