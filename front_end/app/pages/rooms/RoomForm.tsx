import { useState, useEffect, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { InputSwitch } from "primereact/inputswitch";
import { Dropdown } from "primereact/dropdown";
import { MultiSelect } from "primereact/multiselect";
import type { RcFile } from "antd/es/upload";
import GalleryUploader from "~/utils/GalleryUploader";
import { useCommonData } from "~/test/useCommonData";
import ImageUploader from "~/utils/ImageUploader";

interface Props {
  hotelId: any;
  id?: string;
  open: boolean;
  mode?: "create" | "edit" | "view";
  onClose: () => void;
  loadDataById: (id: string) => Promise<any>;
  createItem: (data: object | FormData) => Promise<any>;
  updateItem: (id: string, data: object | FormData) => Promise<any>;
  error: Object | null;
}

export default function RoomForm({
  hotelId,
  id,
  open,
  mode = "create",
  onClose,
  loadDataById,
  createItem,
  updateItem,
  error,
}: Props) {
  const [selectedFile, setSelectedFile] = useState<RcFile | null>(null);
  const [selectedImgsFile, setSelectedImgsFile] = useState<RcFile[]>([]);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [roomNumber, setRoomNumber] = useState("");
  const [roomArea, setRoomArea] = useState("");
  const [priceHour, setPriceHour] = useState("");
  const [priceNight, setPriceNight] = useState("");
  const [limit, setLmit] = useState("");
  const [selectedType, setSelectedType] = useState(null);
  const [selectedFacilies, setSelectedFacilies] = useState<number[]>([]);
  const [status, setStatus] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [roomAvatar, setroomAvatar] = useState<string | null>(null);
  const [keepAvatar, setKeepAvatar] = useState("true");
  const [existingImages, setExistingImages] = useState<any[]>([]);

  const toast = useRef<Toast>(null);

  const { commonData } = useCommonData(["roomtypes", "hotelfacilities"]);

  const roomTypes = commonData.roomTypes;
  const hotelFacilities = commonData.hotelFacilities;

  const header = mode === "edit" ? "EDIT" : "ADD";

  const handleRemoveExistingImage = (index: number) => {
    setExistingImages((prev) => prev.filter((_, i) => i !== index));
  };

  const getError = (field: string) =>
    error &&
    typeof error === "object" &&
    (error as Record<string, string>)[field];

  const submit = async () => {
    setSubmitting(true);
    const formData = new FormData();

    // Basic info
    formData.append("name", name);
    formData.append("description", description || "");
    formData.append("status", JSON.stringify(status));
    formData.append("roomNumber", roomNumber);
    formData.append("roomArea", roomArea);
    formData.append("roomType", selectedType ?? "");
    formData.append("hotelId", hotelId);
    formData.append("priceHour", priceHour);
    formData.append("priceNight", priceNight);
    formData.append("limitPerson", limit);

    // Avatar
    formData.append("keepAvatar", keepAvatar);
    if (selectedFile) {
      formData.append("roomAvatar", selectedFile, selectedFile.name);
      formData.append("keepAvatar", "false"); // Upload mới, không giữ avatar cũ
    } else if (roomAvatar) {
      formData.append("existingroomAvatar", roomAvatar);
      formData.append("keepAvatar", "true"); // Giữ avatar cũ
    }

    // Images
    selectedImgsFile.forEach((file, index) => {
      formData.append(`images[${index}].imageFile`, file, file.name);
    });

    // Existing Images
    existingImages.forEach((img, index) => {
      formData.append(
        `images[${selectedImgsFile.length + index}].imageId`,
        img.id.toString()
      );
      formData.append(
        `images[${selectedImgsFile.length + index}].existingImageUrl`,
        img.imagesUrl
      );
    });

    // Facilities
    selectedFacilies.forEach((facilityId) => {
      if (typeof facilityId === "number" && !isNaN(facilityId)) {
        formData.append("facilities", facilityId.toString());
      } else {
        console.error("Invalid facility ID:", facilityId);
      }
    });

    try {
      if (id) {
        await updateItem(id, formData);
        toast.current?.show({
          severity: "success",
          summary: "Success",
          detail: "Room updated successfully",
          life: 3000,
        });
      } else {
        await createItem(formData);
        toast.current?.show({
          severity: "success",
          summary: "Success",
          detail: "Room created successfully",
          life: 3000,
        });
      }
      onClose();
    } catch (err: any) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: err.response?.data?.message || "Failed to save room",
        life: 3000,
      });
    } finally {
      setSubmitting(false);
    }
  };

  // Updated useEffect for loading hotel data
  useEffect(() => {
    if (id && open) {
      loadDataById(id)
        .then(async (data) => {
          const result = data;

          // Map basic info
          setName(result.name || "");
          setDescription(result.description || "");
          setRoomNumber(result.roomNumber?.toString() || "");
          setRoomArea(result.roomArea?.toString() || "");
          setPriceHour(result.priceHours?.toString() || "");
          setPriceNight(result.priceNight?.toString() || "");
          setLmit(result.limitPerson?.toString() || "");
          setStatus(result.status ?? true);

          // Set roomType
          if (result.roomType && roomTypes) {
            const roomType =
              roomTypes.find((r: any) => r.name === result.roomType) || null;
            setSelectedType(roomType ? roomType.id : null);
          }

          // Set facilities
          if (result.roomFacilities && Array.isArray(hotelFacilities)) {
            const selectedFacilityIds = result.roomFacilities
              .filter((facility: any) =>
                hotelFacilities.some((option: any) => option.id === facility.id)
              )
              .map((facility: any) => facility.id)
              .filter((id: number) => typeof id === "number" && !isNaN(id));
            setSelectedFacilies(selectedFacilityIds);
          } else {
            setSelectedFacilies([]);
          }

          // Set avatar
          if (result.roomAvatar) {
            setroomAvatar(result.roomAvatar);
            setKeepAvatar("true");
          } else {
            setroomAvatar(null);
            setKeepAvatar("false");
          }

          // Set gallery images
          const images = result.images
            ? result.images
                .filter(
                  (img: any) =>
                    img?.imagesUrl && typeof img.imagesUrl === "string"
                )
                .map((img: any) => ({
                  id: img.id,
                  imagesUrl: img.imagesUrl,
                }))
            : [];
          setExistingImages(images);
        })
        .catch((err) => {
          console.error("Error loading room data:", err);
          toast.current?.show({
            severity: "error",
            summary: "Error",
            detail: err.response?.data?.message || "Failed to load room data",
            life: 3000,
          });
        });
    } else {
      setName("");
      setDescription("");
      setRoomNumber("");
      setRoomArea("");
      setPriceHour("");
      setPriceNight("");
      setLmit("");
      setStatus(true);
      setSelectedType(null);
      setSelectedFacilies([]);
      setSelectedImgsFile([]);
      setSelectedFile(null);
      setroomAvatar(null);
      setKeepAvatar("false");
      setExistingImages([]);
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
        modal
        className="p-fluid rounded-lg shadow-lg bg-white"
        breakpoints={{ "960px": "85vw", "641px": "95vw" }}
      >
        <div className="pl-4 pr-4">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
            <div className="col-span-12">
              <h3 className="text-lg font-semibold text-gray-800 mb-2">
                Basic Information
              </h3>
            </div>

            <div className="col-span-12 md:col-span-12">
              <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
                <div className="col-span-12 md:col-span-6">
                  <h4 className="text-md font-semibold text-gray-800 mb-2">
                    Room Avatar
                  </h4>
                  <div className="border rounded-lg p-4 bg-gray-50">
                    <label
                      htmlFor="avatar"
                      className="block text-sm font-medium text-gray-700 mb-1"
                    >
                      Upload 1 image
                    </label>
                    <ImageUploader
                      initialImageUrl={
                        roomAvatar
                          ? `${
                              import.meta.env
                                .VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                            }/${roomAvatar}`
                          : undefined
                      }
                      onFileChange={(file) => setSelectedFile(file)}
                      disabled={submitting}
                    />
                    {getError("avatar") && (
                      <small className="text-red-500 text-xs mt-1">
                        {getError("avatar")}
                      </small>
                    )}
                  </div>
                </div>

                <div className="col-span-12 md:col-span-6">
                  <h4 className="text-md font-semibold text-gray-800 mb-2">
                    Gallery Images
                  </h4>
                  <div className="border rounded-lg p-4 bg-gray-50">
                    <label
                      htmlFor="images"
                      className="block text-sm font-medium text-gray-700 mb-1"
                    >
                      Images (Up to 3)
                    </label>
                    <GalleryUploader
                      onFilesChange={(files) => {
                        setSelectedImgsFile(files);
                      }}
                      onRemoveExistingImage={handleRemoveExistingImage}
                      disabled={submitting}
                      initialImageUrls={existingImages.map(
                        (img) =>
                          `${
                            import.meta.env.VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                          }/${img.imagesUrl}`
                      )}
                      maxCount={3}
                    />
                    {getError("images") && (
                      <small className="text-red-500 text-xs mt-1">
                        {getError("images")}
                      </small>
                    )}
                  </div>
                </div>
              </div>
            </div>

            {/* Infor */}
            <div className="col-span-12 md:col-span-12">
              {/* Name + Desc */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label
                    htmlFor="name"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Name <span className="text-red-500">*</span>
                  </label>
                  <InputText
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("name") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("name") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("name")}
                    </small>
                  )}
                </div>

                <div>
                  <label
                    htmlFor="description"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Description
                  </label>
                  <InputText
                    id="description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("description") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("description") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("description")}
                    </small>
                  )}
                </div>
              </div>

              {/* Room number + area */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                <div>
                  <label
                    htmlFor="name"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Room Number <span className="text-red-500">*</span>
                  </label>
                  <InputText
                    id="name"
                    value={roomNumber}
                    onChange={(e) => setRoomNumber(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("roomNumber") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("roomNumber") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("roomNumber")}
                    </small>
                  )}
                </div>

                <div>
                  <label
                    htmlFor="roomArea"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Room Area <span className="text-red-500">*</span>
                  </label>
                  <InputText
                    id="roomArea"
                    value={roomArea}
                    onChange={(e) => setRoomArea(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("roomArea") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("roomArea") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("roomArea")}
                    </small>
                  )}
                </div>
              </div>

              {/* Room type + Room Facilities */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                <div>
                  <label
                    htmlFor="roomType"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Room Type <span className="text-red-500">*</span>
                  </label>
                  <Dropdown
                    id="roomType"
                    value={selectedType}
                    onChange={(e) => setSelectedType(e.value)}
                    options={roomTypes}
                    optionLabel="name"
                    optionValue="id"
                    placeholder="Select Room Type"
                    className={`w-full ${
                      getError("roomType") ? "p-invalid" : ""
                    }`}
                  />

                  {getError("roomType") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("roomType")}
                    </small>
                  )}
                </div>

                {/* Facilities */}
                <div>
                  <label
                    htmlFor="facilities"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Room Facilities <span className="text-red-500">*</span>
                  </label>
                  <MultiSelect
                    value={selectedFacilies}
                    onChange={(e) => {
                      setSelectedFacilies(e.value);
                    }}
                    options={hotelFacilities || []}
                    optionLabel="name"
                    optionValue="id"
                    display="chip"
                    placeholder="Select Facilities"
                    className={`w-full ${
                      getError("facilities") ? "p-invalid" : ""
                    }`}
                    disabled={submitting}
                  />
                  {getError("facilities") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("facilities")}
                    </small>
                  )}
                </div>
              </div>

              {/* Price Hour + Price Night */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                <div>
                  <label
                    htmlFor="name"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Price Hour <span className="text-red-500">*</span>
                  </label>
                  <InputText
                    id="name"
                    value={priceHour}
                    onChange={(e) => setPriceHour(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("priceHour") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("priceHour") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("priceHour")}
                    </small>
                  )}
                </div>

                <div>
                  <label
                    htmlFor="priceNight"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Price Night <span className="text-red-500">*</span>
                  </label>
                  <InputText
                    id="priceNight"
                    value={priceNight}
                    onChange={(e) => setPriceNight(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("priceNight") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("priceNight") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("priceNight")}
                    </small>
                  )}
                </div>
              </div>

              {/* Limit + Status */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                <div>
                  <label
                    htmlFor="name"
                    className="block text-sm font-medium text-gray-700 mb-1"
                  >
                    Limit <span className="text-red-500">*</span>
                  </label>
                  <InputText
                    id="name"
                    value={limit}
                    onChange={(e) => setLmit(e.target.value)}
                    disabled={submitting}
                    className={`w-full p-2 border rounded-lg ${
                      getError("limitPerson") ? "p-invalid" : ""
                    }`}
                  />
                  {getError("limitPerson") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("limitPerson")}
                    </small>
                  )}
                </div>

                <div className="flex items-center gap-3">
                  <label
                    htmlFor="status"
                    className="text-sm font-medium text-gray-700"
                  >
                    Status <span className="text-red-500">*</span>
                  </label>
                  <InputSwitch
                    id="status"
                    checked={status}
                    onChange={(e) => setStatus(e.value)}
                    disabled={submitting}
                  />
                  {getError("status") && (
                    <small className="text-red-500 text-xs mt-1">
                      {getError("status")}
                    </small>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </Dialog>
    </div>
  );
}
