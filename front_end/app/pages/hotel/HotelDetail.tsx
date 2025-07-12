import { useState, useEffect, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { Tag } from "primereact/tag";
import { format } from "date-fns";
import noImg from "@/asset/images/no-img.png";
import { Image } from "antd";

interface Props {
  id?: string;
  open: boolean;
  mode?: "create" | "edit" | "view";
  onClose: () => void;
  loadDataById: (id: string) => Promise<any>;
}

export default function HotelDetail({
  id,
  open,
  mode = "view",
  onClose,
  loadDataById,
}: Props) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState(true);
  const [createdData, setCreatedData] = useState("");
  const [createdAt, setCreatedAt] = useState("");
  const [updatedData, setUpdatedData] = useState("");
  const [updateAt, setUpdateAt] = useState("");
  const [avatarUrl, setAvatarUrl] = useState("");
  const [streetNumber, setStreetNumber] = useState("");
  const [wardName, setWardName] = useState("");
  const [districtName, setDistrictName] = useState("");
  const [provinceName, setProvinceName] = useState("");
  const [streetName, setStreetName] = useState("");
  const [note, setNote] = useState("");
  const [hotelNote, setHotelNote] = useState("");
  const [ownerName, setOwnerName] = useState("");

  const [images, setImages] = useState<{ id: number; imagesUrl: string }[]>([]);
  const [typeHotels, setTypeHotels] = useState<{ id: number; name: string }[]>(
    []
  );
  const [facilities, setFacilities] = useState<
    { id: number; name: string; icon: string }[]
  >([]);
  const [documents, setDocuments] = useState<
    {
      documentId: number;
      documentName: string;
      typeId: number;
      typeName: string;
      documentUrl: string;
    }[]
  >([]);
  const [policy, setPolicy] = useState<{
    id: number;
    policyName: string;
    policyDescription: string;
  } | null>(null);

  const toast = useRef<Toast>(null);

  const header =
    mode === "view"
      ? "HOTEL DETAILS"
      : mode === "edit"
      ? "EDIT HOTEL"
      : "ADD NEW HOTEL";

  useEffect(() => {
    if (id && open) {
      loadDataById(id)
        .then((data) => {
          setName(data.name || "");
          setDescription(data.description || "");
          setStatus(data.status ?? true);
          setCreatedAt(data.createdAt || "");
          setUpdateAt(data.updatedAt || "");
          setCreatedData(data.createdName || "");
          setUpdatedData(data.updatedName || "");
          setAvatarUrl(data.avatarUrl || "");
          setStreetNumber(data.streetNumber || "");
          setWardName(data.wardName || "");
          setDistrictName(data.districtName || "");
          setProvinceName(data.provinceName || "");
          setStreetName(data.streetName || "");
          setNote(data.note || "");
          setHotelNote(data.hotelNote || "");
          setOwnerName(data.ownerName || "");
          setImages(data.images || []);
          setTypeHotels(data.typeHotels || []);
          setFacilities(data.facilities || []);
          setDocuments(data.documents || []);
          setPolicy(data.policies || null);
        })
        .catch(() =>
          toast.current?.show({
            severity: "error",
            summary: "Error",
            detail: "Failed to load hotel details",
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
          <div className="flex justify-center gap-4">
            <Button
              outlined
              label="Close"
              onClick={onClose}
              severity="secondary"
              className="px-6 py-2"
            />
          </div>
        }
        style={{ width: "50%" }}
        modal
        className="p-fluid"
        breakpoints={{ "960px": "75vw", "641px": "90vw" }}
      >
        <div className="space-y-6 pl-4 pr-4">
          {/* Basic Info & Address Info */}
          <div className="grid grid-cols-2 gap-6">
            {/* Left Column: Avatar + Basic Info */}
            <div className="space-y-4">
              {/* Avatar */}
              <div className="flex items-center space-x-4">
                <span className="font-semibold text-gray-700">Avatar</span>
                {avatarUrl ? (
                  <Image
                    width={80}
                    src={
                      avatarUrl
                        ? `${
                            import.meta.env.VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                          }/${avatarUrl}`
                        : noImg
                    }
                    className="rounded-lg object-cover"
                  />
                ) : (
                  <Image
                    width={80}
                    src={noImg}
                    className="rounded-lg object-cover"
                  />
                )}
              </div>

              {/* Basic Info */}
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Hotel Name:
                </label>
                <span className="w-2/3 text-gray-900">{name || "-"}</span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Description:
                </label>
                <span className="w-2/3 text-gray-900">
                  {description || "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Hotel Types:
                </label>
                <span className="w-2/3 text-gray-900">
                  {typeHotels.length > 0
                    ? typeHotels.map((type) => type.name).join(", ")
                    : "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Facilities:
                </label>
                <span className="w-2/3 text-gray-900">
                  {facilities.length > 0
                    ? facilities.map((f) => f.name).join(", ")
                    : "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Owner Name:
                </label>
                <span className="w-2/3 text-gray-900">{ownerName ?? "-"}</span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Hotel Note:
                </label>
                <span className="w-2/3 text-gray-900">
                  {hotelNote && hotelNote.toString().trim()
                    ? hotelNote.toString().trim()
                    : "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Status:
                </label>
                <span className="w-2/3">
                  <Tag
                    value={status ? "Active" : "Inactive"}
                    severity={status ? "success" : "danger"}
                    className="px-2 py-1 text-sm"
                  />
                </span>
              </div>
            </div>

            {/* Right Column: Images + Address */}
            <div className="space-y-4">
              {/* Images */}
              <div className="flex items-center gap-4">
                <span className="font-semibold text-gray-700 w-24 text-left">
                  Images
                </span>
                <div className="flex flex-wrap gap-2">
                  {images.length > 0 &&
                    images.map((img) => (
                      <Image
                        key={img.id}
                        width={80}
                        src={`${
                          import.meta.env.VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                        }/${img.imagesUrl}`}
                        className="rounded-lg object-cover"
                      />
                    ))}

                  {images.length === 0 && (
                    <Image
                      width={80}
                      src={noImg}
                      className="rounded-lg object-cover"
                    />
                  )}
                </div>
              </div>

              {/* Address Info */}
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Province:
                </label>
                <span className="w-2/3 text-gray-900">
                  {provinceName || "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  District:
                </label>
                <span className="w-2/3 text-gray-900">
                  {districtName || "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Ward:
                </label>
                <span className="w-2/3 text-gray-900">{wardName || "-"}</span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Street:
                </label>
                <span className="w-2/3 text-gray-900">{streetName || "-"}</span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Street Number:
                </label>
                <span className="w-2/3 text-gray-900">
                  {streetNumber || "-"}
                </span>
              </div>
              <div className="flex items-center space-x-4">
                <label className="font-semibold w-1/3 text-gray-700">
                  Note:
                </label>
                <span className="w-2/3 text-gray-900">{note || "-"}</span>
              </div>
            </div>
          </div>

          {/* Documents */}
          {documents.length > 0 && (
            <div className="space-y-2">
              <label className="font-semibold text-gray-700">Documents:</label>
              <div className="border rounded-lg p-4 bg-gray-50">
                <table className="w-full text-gray-900">
                  <thead>
                    <tr className="border-b">
                      <th className="text-left py-2">Name</th>
                      <th className="text-left py-2">Type</th>
                      <th className="text-left py-2">Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {documents.map((doc) => (
                      <tr key={doc.documentId} className="border-b">
                        <td className="py-2">{doc.documentName}</td>
                        <td className="py-2">{doc.typeName}</td>
                        <td className="py-2">
                          <a
                            href={`${
                              import.meta.env
                                .VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                            }/${doc.documentUrl}`}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-500 underline"
                          >
                            View
                          </a>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Policy */}
          {policy && (
            <div className="space-y-2">
              <label className="font-semibold text-gray-700">Policy:</label>
              <div className="border rounded-lg p-4 bg-gray-50">
                <p className="text-gray-900">
                  <strong>{policy.policyName}</strong>
                </p>
                <p className="text-gray-900">{policy.policyDescription}</p>
              </div>
            </div>
          )}

          {/* Created and Updated Info */}
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <div className="flex items-center space-x-4">
                  <label className="font-semibold w-1/3 text-gray-700">
                    Created By:
                  </label>
                  <span className="w-2/3 text-gray-900">
                    {createdData || "-"}
                  </span>
                </div>
                <div className="flex items-center space-x-4">
                  <label className="font-semibold w-1/3 text-gray-700">
                    Created At:
                  </label>
                  <span className="w-2/3 text-gray-900">
                    {createdAt
                      ? format(new Date(createdAt), "yyyy-MM-dd HH:mm:ss")
                      : "-"}
                  </span>
                </div>
              </div>
              <div>
                <div className="flex items-center space-x-4">
                  <label className="font-semibold w-1/3 text-gray-700">
                    Updated By:
                  </label>
                  <span className="w-2/3 text-gray-900">
                    {updatedData || "-"}
                  </span>
                </div>
                <div className="flex items-center space-x-4">
                  <label className="font-semibold w-1/3 text-gray-700">
                    Updated At:
                  </label>
                  <span className="w-2/3 text-gray-900">
                    {updateAt
                      ? format(new Date(updateAt), "yyyy-MM-dd HH:mm:ss")
                      : "-"}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Dialog>
    </div>
  );
}
