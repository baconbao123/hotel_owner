import { useState, useEffect, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { Tag } from "primereact/tag";
import { format } from "date-fns";
import { Image } from "antd";
import noImg from "@/asset/images/no-img.png";

interface Props {
  id?: string;
  open: boolean;
  mode?: "create" | "edit" | "view";
  onClose: () => void;
  loadDataById: (id: string) => Promise<any>;
}

export default function RoomDetail({
  id,
  open,
  mode = "view",
  onClose,
  loadDataById,
}: Props) {
  const [name, setName] = useState("");
  const [avatar, setAvatar] = useState("");
  const [hotelName, setHotelName] = useState("");
  const [roomArea, setRoomArea] = useState("");
  const [roomNumber, setRoomNumber] = useState("");
  const [roomType, setRoomType] = useState("");
  const [priceHours, setPriceHours] = useState("");
  const [priceNight, setPriceNight] = useState("");
  const [limitPerson, setLimitPerson] = useState("");
  const [description, setDescription] = useState("");
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
          setName(data.name || "");
          setAvatar(data.roomAvatar || "");
          setHotelName(data.hotelName || "");
          setRoomArea(data.roomArea || "");
          setRoomNumber(data.roomNumber || "");
          setRoomType(data.roomType || "");
          setPriceHours(data.priceHours || "");
          setPriceNight(data.priceNight || "");
          setLimitPerson(data.limitPerson || "");
          setDescription(data.description || "");
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
            detail: "Failed to load role",
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
              Avatar:
            </label>
            <span id="name" className="col-span-2">
              {avatar ? (
                <Image
                  width={80}
                  src={`${
                    import.meta.env.VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                  }/${avatar}`}
                  className="rounded-lg object-cover"
                />
              ) : (
                <Image
                  width={80}
                  src={noImg}
                  className="rounded-lg object-cover"
                />
              )}
            </span>
          </div>

          <div></div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="name" className="font-bold col-span-1">
              Name:
            </label>
            <span id="name" className="col-span-2">
              {name || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="description" className="font-bold col-span-1">
              Description:
            </label>
            <span id="description" className="col-span-2">
              {description || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="hotelName" className="font-bold col-span-1">
              Hotel Name:
            </label>
            <span id="hotelName" className="col-span-2">
              {hotelName || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="roomArea" className="font-bold col-span-1">
              Room Area:
            </label>
            <span id="roomArea" className="col-span-2">
              {roomArea || "-"}m2
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="roomNumber" className="font-bold col-span-1">
              Room Number:
            </label>
            <span id="roomNumber" className="col-span-2">
              {roomNumber || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="roomNumber" className="font-bold col-span-1">
              Room Type:
            </label>
            <span id="roomNumber" className="col-span-2">
              {roomType || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="roomNumber" className="font-bold col-span-1">
              Price Hours:
            </label>
            <span id="roomNumber" className="col-span-2">
              {priceHours || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="roomNumber" className="font-bold col-span-1">
              Price Night:
            </label>
            <span id="roomNumber" className="col-span-2">
              {priceNight || "-"}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 items-center mb-2">
            <label htmlFor="roomNumber" className="font-bold col-span-1">
              Limit:
            </label>
            <span id="roomNumber" className="col-span-2">
              {limitPerson || "-"}
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
        </div>

        <div></div>

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
