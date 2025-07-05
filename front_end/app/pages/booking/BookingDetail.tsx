import { useState, useEffect, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { Tag } from "primereact/tag";
import { format } from "date-fns";

interface Props {
  id?: string;
  open: boolean;
  mode?: "create" | "edit" | "view";
  onClose: () => void;
  loadDataById: (id: string) => Promise<any>;
}

export default function BookingDetail({
  id,
  open,
  mode = "view",
  onClose,
  loadDataById,
}: Props) {
  const [customer, setCustomer] = useState("");
  const [roomNumber, setRoomNumber] = useState("");
  const [checkInTime, setCheckInTime] = useState("");
  const [checkOutTime, setCheckOutTime] = useState("");
  const [status, setStatus] = useState(true);
  const [createdData, setCreatedData] = useState("");
  const [createdAt, setCreatedAt] = useState("");
  const [updatedData, setUpdatedData] = useState("");
  const [updateAt, setUpdateAt] = useState("");

  const toast = useRef<Toast>(null);

  const header = mode === "view" ? "DETAILS" : "ADD";

  useEffect(() => {
    if (id && open) {
      loadDataById(id)
        .then((data) => {
          setCustomer(data.userName || "");
          setRoomNumber(data.roomNumber || "");
          setCheckInTime(data.checkInTime || "");
          setCheckOutTime(data.checkOutTime || "");
          setStatus(data.status ?? true);
          setCreatedAt(data.createdAt || "");
          setUpdateAt(data.updatedAt || "");
          setCreatedData(data.createdName || "");
          setUpdatedData(data.updatedName || "");
        })
        .catch(() =>
          toast.current?.show({
            severity: "error",
            summary: "Error",
            detail: "Failed to load booking",
            life: 3000,
          })
        );
    }
  }, [id, open, loadDataById]);

  return (
    <div>
      <Toast ref={toast} />
      <Dialog
        visible={open}
        onHide={onClose}
        header={header}
        footer={
          <div className="flex justify-center gap-2">
            <Button
              outlined
              label="Close"
              onClick={onClose}
              severity="secondary"
              style={{ padding: "8px 40px" }}
            />
          </div>
        }
        style={{ width: "50%" }}
        modal
        className="p-fluid"
        breakpoints={{ "960px": "75vw", "641px": "90vw" }}
      >
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pl-4 pr-4">
          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="name" className="font-bold col-span-1">
              Customer:
            </label>
            <span id="name" className="col-span-2">
              {customer || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="description" className="font-bold col-span-1">
              Room Number:
            </label>
            <span id="description" className="col-span-2">
              {roomNumber || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="checkInTime" className="font-bold col-span-1">
              Check In Time
            </label>
            <span id="checkInTime" className="col-span-2">
              {checkInTime
                ? format(new Date(checkInTime), "yyyy-MM-dd HH:mm:ss")
                : "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="checkOutTime" className="font-bold col-span-1">
              Check In Out
            </label>
            <span id="checkOutTime" className="col-span-2">
              {checkOutTime
                ? format(new Date(checkOutTime), "yyyy-MM-dd HH:mm:ss")
                : "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="status" className="font-bold col-span-1">
              Status:
            </label>
            <span id="status" className="col-span-2">
              <Tag
                value={status ? "Active" : "Inactive"}
                severity={status ? "success" : "danger"}
              />
            </span>
          </div>

          <div></div>
        </div>

        {/* Info data create/update */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4 pl-4 pr-4">
          {/* Created By and Created At */}
          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="createdBy" className="font-bold col-span-1">
              Created By:
            </label>
            <span id="createdBy" className="whitespace-nowrap">
              {createdData || "-"}
            </span>
          </div>
          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="createdAt" className="font-bold col-span-1">
              Created At:
            </label>
            <span id="createdAt" className="whitespace-nowrap">
              {createdAt
                ? format(new Date(createdAt), "yyyy-MM-dd HH:mm:ss")
                : "-"}
            </span>
          </div>

          {/* Updated By and Update At */}
          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="updatedBy" className="font-bold col-span-1">
              Updated By:
            </label>
            <span id="updatedBy" className="text-gray-700">
              {updatedData || "-"}
            </span>
          </div>
          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="updatedAt" className="font-bold col-span-1">
              Updated At:
            </label>
            <span id="updatedAt" className="whitespace-nowrap">
              {updateAt
                ? format(new Date(updateAt), "yyyy-MM-dd HH:mm:ss")
                : "-"}
            </span>
          </div>
        </div>
      </Dialog>
    </div>
  );
}
