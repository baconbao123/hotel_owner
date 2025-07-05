import { useState, useEffect } from "react";
import {
  BellIcon,
  Bars3Icon,
  XMarkIcon,
  ArrowRightEndOnRectangleIcon,
  UserCircleIcon,
} from "@heroicons/react/24/outline";
import { Dialog } from "primereact/dialog";
import { Link, useSearchParams } from "react-router";
import { useSelector, useDispatch } from "react-redux";
import type { RootState } from "~/store";
import Cookies from "js-cookie";
import { useNavigate } from "react-router";
import { Button } from "primereact/button";
import $axios from "~/axios";
import { setUser, type UserLogin } from "~/store/slice/userDataSlice";
import { disableLoading, setLoading } from "~/store/slice/commonSlice";
import { setPermissions } from "~/store/slice/permissionSlice";
import { Sidebar } from "primereact/sidebar";
import { toast } from "react-toastify";
import { InputText } from "primereact/inputtext";
import ImageUploader from "../../utils/ImageUploader";
export default function Navbar() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const navigate = useNavigate();
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const dispatch = useDispatch();
  const user = useSelector((state: RootState) => state.userDataSlice);
  const [showProfile, setShowProfile] = useState(false);
  const [showFormEdit, setShowFormEdit] = useState(false);
  const [showFormPassword, setShowFormPassword] = useState(false);

  useEffect(() => {
    fetchUserInfo();
  }, []);

  const fetchUserInfo = () => {
    dispatch(setLoading());
    $axios
      .get("/user/profile")
      .then((res: any) => {
        const result = res.data.result;

        const userDataLoad: UserLogin = {
          id: result.id || 0,
          fullname: result.fullName || "",
          email: result.email || "",
          phoneNumber: result.phoneNumber || "",
          avatarUrl: result.avatarUrl || "",
          roles: result.roles || [],
          loading: false,
        };
        dispatch(setUser(userDataLoad));
        // fetchPermission()
      })
      .catch((err: any) => {
        console.log("Init user error ", err);
      })
      .finally(() => {
        dispatch(disableLoading());
      });
  };

  const fetchPermission = () => {
    dispatch(setLoading());
    $axios
      .get("/permission/resources")
      .then((res: any) => {
        if (res.data?.result) {
          dispatch(setPermissions(res.data.result));
        }
      })
      .catch((err: any) => {
        console.log("Init permission error ", err);
      })
      .finally(() => {
        dispatch(disableLoading());
      });
  };

  const handleLogoutClick = () => {
    setIsProfileOpen(false);
    setShowLogoutConfirm(true);
  };

  const confirmLogout = () => {
    Cookies.remove("token");
    Cookies.remove("refreshToken");
    navigate("/login");
    setShowLogoutConfirm(false);
  };

  const ConfirmForm = () => {
    return (
      <Dialog
        visible={showLogoutConfirm}
        onHide={() => setShowLogoutConfirm(false)}
        modal
        className="p-fluid w-100"
        header={"Are you sure ?"}
        breakpoints={{ "960px": "75vw", "641px": "90vw" }}
        footer={() => (
          <div className="flex justify-center  gap-2">
            <Button
              label={"Cancle"}
              icon="pi pi-times"
              onClick={() => setShowLogoutConfirm(false)}
              severity="secondary"
              outlined
            />
            <Button
              label={"Logout"}
              icon="pi pi-check"
              onClick={confirmLogout}
              severity="danger"
              autoFocus
            />
          </div>
        )}
      >
        <div className="flex flex-col gap-2">
          <p className="text-sm text-gray-500 m-0">
            You will be redirected to the login page.
          </p>
        </div>
      </Dialog>
    );
  };

  const FormEditProfile = () => {
    const [formData, setFormData] = useState({
      name: user.fullname,
      email: user.email,
      phoneNumber: user.phoneNumber,
      avatarUrl: user.avatarUrl,
    });
    const [error, setError] = useState<Object | null>(null);

    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const getError = (field: string) =>
      (error &&
        typeof error === "object" &&
        (error as Record<string, string>)[field]) ||
      null;
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      setFormData((prev) => ({
        ...prev,
        [e.target.name]: e.target.value,
      }));
    };

    const submit = () => {
      setError({});
      dispatch(setLoading());
      const formDataToSend = new FormData();
      formDataToSend.append("fullName", formData.name);
      formDataToSend.append("email", formData.email);
      formDataToSend.append("phoneNumber", formData.phoneNumber);

      if (selectedFile) {
        formDataToSend.append("avatarUrl", selectedFile);
        formDataToSend.append("keepAvatar", "false");
        // formDataToSend.append("keepAvatar", "true");
      }
      $axios
        .put(`/user/profile?id=${user.id}`, formDataToSend, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((res: any) => {
          toast.success("Update successfully");
          window.location.reload();
        })

        .catch((err: any) => {
          setError(
            err.response?.data?.errorMessages || "Failed to update profile"
          );
          toast.error("Update failed");
          console.log(err);
        })
        .finally(() => {
          dispatch(disableLoading());
        });
    };
    return (
      <Dialog
        visible={showFormEdit}
        onHide={() => setShowFormEdit(false)}
        modal
        className="p-fluid"
        header={"Edit profile"}
        breakpoints={{ "3000px": "40vw", "960px": "75vw", "641px": "90vw" }}
        footer={() => (
          <div className="flex justify-center  gap-2 mt-10">
            <Button
              label={"Close"}
              icon="pi pi-times"
              onClick={() => setShowFormEdit(false)}
              severity="secondary"
              outlined
            />
            <Button
              label={"Save"}
              icon="pi pi-check"
              onClick={submit}
              autoFocus
            />
          </div>
        )}
      >
        <div className="flex flex-col gap-2">
          <div className="flex justify-center">
            <ImageUploader
              initialImageUrl={
                formData.avatarUrl
                  ? `${
                      import.meta.env.VITE_REACT_APP_BACK_END_LINK_UPLOAD_USER
                    }/${formData.avatarUrl}`
                  : undefined
              }
              onFileChange={(file) => setSelectedFile(file)}
              maxFileSize={100}
            />
          </div>

          <div className="mt-5">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Name
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2"
            />
            {getError("name") && (
              <small className="text-red-500 text-xs">{getError("name")}</small>
            )}
          </div>

          <div className="mt-5">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Email
            </label>
            <input
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2"
            />
            {getError("email") && (
              <small className="text-red-500 text-xs">
                {getError("email")}
              </small>
            )}
          </div>

          <div className="mt-5">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Phone Number
            </label>
            <input
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2"
            />
            {getError("phoneNumber") && (
              <small className="text-red-500 text-xs">
                {getError("phoneNumber")}
              </small>
            )}
          </div>
        </div>
      </Dialog>
    );
  };

  const FormResetPassword = () => {
    const [formData, setFormData] = useState({
      name: user.fullname,
      email: user.email,
      phoneNumber: user.phoneNumber,
      avatarUrl: user.avatarUrl,
    });
    const [error, setError] = useState("");

    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [searchParams] = useSearchParams();
    const [token, setToken] = useState<string | null>(null);
    useEffect(() => {
      const tokenFromUrl = searchParams.get("token");
      if (tokenFromUrl) {
        setToken(tokenFromUrl);
      } else {
        setError("Invalid or missing token.");
      }
    }, [searchParams]);

    const submit = () => {
      if (newPassword !== confirmPassword) {
        setError("Passwords do not match.");
        return;
      }

      if (!token) {
        setError("Invalid or missing token.");
        return;
      }
      // setError({});
      dispatch(setLoading());

      $axios
        .post(
          `/auth/reset-password-profile?token=${token}&newPassword=${newPassword}`
        )
        .then((res: any) => {
          toast.success("Update successfully");
          window.location.reload();
        })

        .catch((err: any) => {
          setError(
            err.response?.data?.errorMessages || "Failed to update profile"
          );
          toast.error("Update failed");
          console.log(err);
        })
        .finally(() => {
          dispatch(disableLoading());
        });
    };
    return (
      <Dialog
        visible={showFormPassword}
        onHide={() => setShowFormPassword(false)}
        modal
        className="p-fluid"
        header={"Reset password"}
        breakpoints={{ "3000px": "40vw", "960px": "75vw", "641px": "90vw" }}
        footer={() => (
          <div className="flex justify-center  gap-2 mt-10">
            <Button
              label={"Close"}
              icon="pi pi-times"
              onClick={() => setShowFormPassword(false)}
              severity="secondary"
              outlined
            />
            <Button
              label={"Save"}
              icon="pi pi-check"
              onClick={submit}
              autoFocus
            />
          </div>
        )}
      >
        <div className="">
          <label htmlFor="newPassword">New Password</label>
          <InputText
            type="password"
            id="newPassword"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
          />
        </div>
        <div className="mt-5">
          <label htmlFor="confirmPassword">Confirm Password</label>
          <InputText
            type="password"
            id="confirmPassword"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>
        {error && <p className="text-red-500 text-xs">{error}</p>}
      </Dialog>
    );
  };

  const SideBarProfile = () => {
    return (
      <div>
        <Sidebar
          visible={showProfile}
          onHide={() => setShowProfile(!showProfile)}
          position="right"
          showCloseIcon={false}
        >
          <div className="flex justify-center">
            <img
              className="w-30 h-30 rounded-full"
              src={
                user.avatarUrl
                  ? `${
                      import.meta.env.VITE_REACT_APP_BACK_END_LINK_UPLOAD_USER
                    }/${user?.avatarUrl}`
                  : "/images/no-img.png"
              }
              alt="User profile"
            />
          </div>
          <div className="flex justify-center mt-10">
            <p className="text-3xl text-gray-700">
              {user?.fullname ?? "Austin Robertson"}
            </p>
          </div>
          <div className="flex justify-center">
            <p className="text-gray-500">{user?.roles ?? "Administrator"}</p>
          </div>
          <div className="flex justify-center mt-20 gap-2">
            <Button
              size="small"
              label={"Edit profile"}
              icon="pi pi-check"
              className="w-50"
              onClick={() => {
                setShowFormEdit(true);
                setShowProfile(!showProfile);
              }}
            />
          </div>
          <div className="flex justify-center mt-5 gap-2">
            <Button
              size="small"
              label={"Change password"}
              icon="pi pi-check"
              className="w-50"
              onClick={() => {
                setShowFormPassword(true);
                setShowProfile(!showProfile);
              }}
            />
          </div>
        </Sidebar>
      </div>
    );
  };

  return (
    <nav className="bg-white border-b border-gray-200">
      <div className="w-full mx-auto px-4 py-3">
        <div className="flex justify-between items-center">
          {/* Left side */}
          <h1 className="text-xl font-semibold text-gray-800"></h1>

          {/* Right side */}
          <div className="flex items-center space-x-4">
            {/* Notifications */}
            <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-lg relative">
              <BellIcon className="w-5 h-5" />
              <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
            </button>
            {/* Desktop Profile */}
            <div className="hidden md:flex items-center relative">
              <button
                onClick={() => setIsProfileOpen(!isProfileOpen)}
                className="flex items-center space-x-2 focus:outline-none"
              >
                <img
                  className="w-8 h-8 rounded-full"
                  src={`${
                    import.meta.env.VITE_REACT_APP_BACK_END_LINK_UPLOAD_USER
                  }/${user?.avatarUrl}`}
                  alt="User profile"
                />
                <div className="text-sm">
                  <p className="font-medium text-gray-700">
                    {user?.fullname ?? "Austin Robertson"}
                  </p>
                  <p className="text-xs text-gray-500">
                    {user?.roles ?? "Administrator"}
                  </p>
                </div>
              </button>

              {/* Profile Dropdown Menu */}
              {isProfileOpen && (
                <div
                  className="absolute right-0 top-full mt-2 w-48 bg-white rounded-lg shadow-lg overflow-hidden
                  animate-in fade-in slide-in-from-top-2 duration-200
                  border border-gray-100 origin-top"
                >
                  <div className="py-1">
                    <div
                      className="w-full flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 rounded transition"
                      onClick={() => {
                        setShowProfile(true);
                        // setIsProfileOpen(false)
                      }}
                    >
                      <UserCircleIcon className="w-5 h-5" />
                      <span>Profile</span>
                    </div>
                    <button
                      onClick={handleLogoutClick}
                      className="w-full px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2
                        transition-colors duration-150"
                    >
                      <ArrowRightEndOnRectangleIcon className="w-5 h-5" />
                      <span>Logout</span>
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="md:hidden p-1.5 text-gray-500 hover:bg-gray-100 rounded-lg"
            >
              {isMobileMenuOpen ? (
                <XMarkIcon className="w-5 h-5" />
              ) : (
                <Bars3Icon className="w-5 h-5" />
              )}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {isMobileMenuOpen && (
          <div className="md:hidden py-3 space-y-3">
            <div className="flex items-center space-x-2 p-2 z-0">
              <img
                className="w-8 h-8 rounded-full "
                src={user?.avatarUrl}
                alt="User profile"
              />
              <div className="text-sm">
                <p className="font-medium text-gray-700">
                  {user?.email ?? "Austin Robertson"}
                </p>
                <p className="text-xs text-gray-500">
                  {user?.roles ?? "Administrator"}
                </p>
              </div>
            </div>
            {/* Menu Drop for Profile and Logout */}
            <div className="flex flex-col gap-1 px-2">
              <div
                className="w-full flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 rounded transition"
                onClick={() => setShowProfile(true)}
              >
                <UserCircleIcon className="w-5 h-5" />
                <span>Profile</span>
              </div>
              <button
                onClick={() => {
                  setIsMobileMenuOpen(false);
                  handleLogoutClick();
                }}
                className="w-full flex items-center gap-2 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 rounded transition"
              >
                <ArrowRightEndOnRectangleIcon className="w-5 h-5" />
                <span>Logout</span>
              </button>
            </div>
          </div>
        )}
        <ConfirmForm />
        <SideBarProfile />
        <FormEditProfile />
        <FormResetPassword />
      </div>
    </nav>
  );
}
