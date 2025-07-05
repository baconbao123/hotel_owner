import { useState, useEffect, useRef } from "react";
import { Upload, Image } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import type { GetProp, UploadFile, UploadProps } from "antd";
import { Toast as PrimeToast } from "primereact/toast";
import "antd/dist/reset.css";

type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];
type RcFile = import("antd/es/upload").RcFile;

interface ImageUploaderProp {
  initialImageUrl?: string;
  onFileChange: (file: RcFile | null) => void;
  maxFileSize?: number;
  disabled?: boolean;
}

const getBase64 = (file: FileType): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
  });

const ImageUploader: React.FC<ImageUploaderProp> = ({
  initialImageUrl,
  onFileChange,
  maxFileSize = 2,
  disabled = false,
}) => {
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const toast = useRef<PrimeToast>(null);
  const hasUploadedFile = useRef(false); // Track if a file has been uploaded

  // Sync fileList with initialImageUrl only when no file has been uploaded
  useEffect(() => {
    console.log("ImageUploader: initialImageUrl =", initialImageUrl);
    if (initialImageUrl && !hasUploadedFile.current) {
      setFileList([
        {
          uid: "-1",
          name: "avatar",
          status: "done",
          url: initialImageUrl,
        },
      ]);
      setPreviewImage(initialImageUrl);
    } else if (!initialImageUrl && !hasUploadedFile.current) {
      setFileList([]);
      setPreviewImage("");
    }
  }, [initialImageUrl]);

  const handlePreview = async (file: UploadFile) => {
    console.log("handlePreview: file =", file);
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as FileType);
    }
    setPreviewImage(file.url || (file.preview as string));
    setPreviewOpen(true);
  };

  const handleFileChange: UploadProps["onChange"] = ({
    fileList: newFileList,
  }) => {
    console.log("handleFileChange: newFileList =", newFileList);
    setFileList(newFileList);
    if (newFileList.length > 0 && newFileList[0].originFileObj) {
      const file = newFileList[0].originFileObj as RcFile;
      console.log("Selected file:", file.name);
      hasUploadedFile.current = true; // Mark that a file has been uploaded
      onFileChange(file);
      getBase64(file).then((base64) => {
        console.log("Base64 generated for preview");
        setPreviewImage(base64);
        setFileList([
          {
            uid: "-1",
            name: file.name,
            status: "done",
            url: base64,
          },
        ]);
      });
    } else {
      console.log("No file selected, clearing preview");
      hasUploadedFile.current = false; // Reset when file is removed
      onFileChange(null);
      setPreviewImage("");
      setFileList([]);
    }
  };

  const beforeUpload = (file: FileType) => {
    console.log("beforeUpload: file =", file.name);
    const isImage = file.type.startsWith("image/");
    if (!isImage) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: "You can only upload image files!",
        life: 3000,
      });
      return false;
    }
    const isLtMaxSize = file.size / 1024 / 1024 < maxFileSize;
    if (!isLtMaxSize) {
      toast.current?.show({
        severity: "error",
        summary: "Error",
        detail: `Image must be smaller than ${maxFileSize}MB!`,
        life: 3000,
      });
      return false;
    }
    return false; // Prevent any upload attempt
  };

  const customRequest: UploadProps["customRequest"] = ({
    file,
    onSuccess,
    onError,
  }) => {
    console.log("customRequest triggered for file:", file);
    setTimeout(() => {
      onSuccess?.("ok");
    }, 0);
  };

  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );

  return (
    <>
      <PrimeToast ref={toast} />
      <Upload
        listType="picture-card"
        fileList={fileList}
        onPreview={handlePreview}
        onChange={handleFileChange}
        beforeUpload={beforeUpload}
        customRequest={customRequest}
        action={undefined}
        maxCount={1}
        accept="image/*"
        disabled={disabled}
      >
        {fileList.length >= 1 ? null : uploadButton}
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
        />
      )}
    </>
  );
};

export default ImageUploader;
