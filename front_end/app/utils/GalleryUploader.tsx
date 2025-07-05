import { useState, useEffect, useRef } from "react";
import { Upload, Image } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import type { GetProp, UploadFile, UploadProps } from "antd";
import { Toast as PrimeToast } from "primereact/toast";
import "antd/dist/reset.css";
import { v4 as uuidv4 } from "uuid";

type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];
type RcFile = import("antd/es/upload").RcFile;

interface GalleryUploaderProps {
  initialImageUrls?: string[];
  onFilesChange: (files: RcFile[]) => void;
  onRemoveExistingImage?: (index: number) => void;
  maxFileSize?: number;
  disabled?: boolean;
  maxCount?: number;
}

const getBase64 = (file: FileType): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
  });

const GalleryUploader: React.FC<GalleryUploaderProps> = ({
  initialImageUrls,
  onFilesChange,
  onRemoveExistingImage,
  maxFileSize = 2,
  disabled = false,
  maxCount = 5,
}) => {
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const toast = useRef<PrimeToast>(null);

  useEffect(() => {
    console.log("GalleryUploader: initialImageUrls =", initialImageUrls);

    if (initialImageUrls && initialImageUrls.length > 0) {
      const existingFiles = initialImageUrls.map((url, index) => ({
        uid: `existing-${index}`,
        name: `image-${index}`,
        status: "done" as const,
        url,
      }));

      const newFiles = fileList.filter(
        (file) => !file.uid.startsWith("existing-")
      );

      const updatedFileList = [...existingFiles, ...newFiles].slice(
        0,
        maxCount
      );
      setFileList(updatedFileList);

      console.log("GalleryUploader: Updated fileList =", updatedFileList);
    } else {
      const newFiles = fileList.filter(
        (file) => !file.uid.startsWith("existing-")
      );
      setFileList(newFiles);
    }
  }, [initialImageUrls, maxCount]);

  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as FileType);
    }
    setPreviewImage(file.url || (file.preview as string));
    setPreviewOpen(true);
  };

  const handleFileChange: UploadProps["onChange"] = ({
    fileList: newFileList,
    file,
  }) => {
    console.log("GalleryUploader: New fileList =", newFileList);
    console.log("GalleryUploader: Changed file =", file);

    const updatedFileList = newFileList
      .map((f) => {
        if (!f.uid || f.uid.startsWith("upload-")) {
          return {
            ...f,
            uid: `upload-${uuidv4()}`,
            status: f.status || "uploading", 
          };
        }
        return f;
      })
      .slice(0, maxCount);

    setFileList(updatedFileList);

    // Extract files for onFilesChange
    const files = updatedFileList
      .filter((f) => f.originFileObj && f.status !== "removed")
      .map((f) => f.originFileObj as RcFile);
    console.log("GalleryUploader: Files to onFilesChange =", files);
    onFilesChange(files);
  };

  const handleRemove = (file: UploadFile) => {
    console.log("GalleryUploader: Removing file =", file);
    const index = parseInt(file.uid.replace("existing-", ""), 10);
    if (file.uid.startsWith("existing-") && !isNaN(index)) {
      onRemoveExistingImage?.(index);
    }
    const newFileList = fileList.filter((f) => f.uid !== file.uid);
    setFileList(newFileList);
    const files = newFileList
      .filter((f) => f.originFileObj && f.status !== "removed")
      .map((f) => f.originFileObj as RcFile);
    console.log("GalleryUploader: Files after remove =", files);
    onFilesChange(files);
    return true;
  };

  const beforeUpload = (file: FileType) => {
    console.log("GalleryUploader: Before upload, file =", file);
    console.log("GalleryUploader: Current fileList =", fileList);
    const isImage = file.type.startsWith("image/");
    if (!isImage) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: "You can only upload image files!",
        life: 3000,
      });
      return Upload.LIST_IGNORE;
    }
    const isLtMaxSize = file.size / 1024 / 1024 < maxFileSize;
    if (!isLtMaxSize) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: `Image must be smaller than ${maxFileSize}MB!`,
        life: 3000,
      });
      return Upload.LIST_IGNORE;
    }
    const existingImagesCount = fileList.filter((f) =>
      f.uid.startsWith("existing-")
    ).length;
    const newImagesCount = fileList.filter(
      (f) => f.originFileObj && !f.uid.startsWith("existing-")
    ).length;
    if (existingImagesCount + newImagesCount >= maxCount) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: `You can only have up to ${maxCount} images!`,
        life: 3000,
      });
      return Upload.LIST_IGNORE;
    }
    return false; // Let onChange handle the file addition
  };

  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );

  return (
    <>
      <style>
        {`
          .ant-upload-list-picture-card .ant-upload-list-item {
            border: 1px solid #d9d9d9 !important;
          }
          .ant-upload-list-picture-card .ant-upload-list-item-uploading {
            border: 1px solid #d9d9d9 !important;
          }
        `}
      </style>
      <PrimeToast ref={toast} />
      <Upload
        listType="picture-card"
        fileList={fileList}
        onPreview={handlePreview}
        onChange={handleFileChange}
        beforeUpload={beforeUpload}
        accept="image/*"
        disabled={disabled}
        multiple
        maxCount={maxCount}
        onRemove={handleRemove}
        showUploadList={{
          showPreviewIcon: true,
          showRemoveIcon: true,
        }}
      >
        {fileList.length < maxCount && uploadButton}
      </Upload>
      {previewImage && (
        <Image
          wrapperStyle={{ display: "none" }}
          preview={{
            visible: previewOpen,
            onVisibleChange: (visible) => setPreviewOpen(visible),
            afterOpenChange: (visible) => !visible && setPreviewImage(""),
          }}
          src={previewImage}
          onError={() => console.error("Failed to load image:", previewImage)}
        />
      )}
    </>
  );
};

export default GalleryUploader;
