import React, { useState, useEffect } from "react";
import { useNavigate, Navigate } from "react-router";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Checkbox } from "primereact/checkbox";
import "primereact/resources/themes/lara-light-blue/theme.css";
import "primereact/resources/primereact.min.css";
import $axios from "~/axios";
import Cookies from "js-cookie";
import { useAppDispatch } from "~/store";
import { disableLoading, setLoading } from "~/store/slice/commonSlince";

import { Password } from "primereact/password";
import { toast } from "react-toastify";
import type { Route } from "./+types/Login";
import commonHook from "~/hook/commonHook";
interface LoginForm {
    email: string;
    password: string;
    remember: boolean;
}
export function meta({}: Route.MetaArgs) {
  return [
    { title: "Login" },
    { name: "description", content: "Hotel Admin Login" },
  ];
}

const Login = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [remember, setRemember] = useState<boolean>(false);
    const [emailError, setEmailError] = useState<string>("");
    const [passwordError, setPasswordError] = useState<string>("");
    const [loginError, setLoginError] = useState<string>("");
    const [serverError, setServerError] = useState(false);
    const {fetchUserInfo} = commonHook()
    const dispatch = useAppDispatch();

    useEffect(() => {

        checkUserInfo();
    }, [])
    
    const checkUserInfo = async () => {
        const result = await fetchUserInfo();
        if (result) {
            navigate('/dashboard');
        }
    };
    const handleSubmit = async (e: any): Promise<void> => {
        e.preventDefault();
        setEmailError("");
        setPasswordError("");
        setLoginError("");
        setServerError(false);

        if (!email) {
            setEmailError("Email is required");
            return;
        }
        if (!password) {
            setPasswordError("Password is required");
            return;
        }
        dispatch(setLoading());

        const loginData: LoginForm = { email, password, remember };
        $axios
            .post("/auth/login", loginData)
            .then((res: any) => {
                if (res.data.code === 200) {
                    Cookies.set("token", res.data.result.token, {
                        expires: 1 / 24,
                    });
                    Cookies.set("refreshToken", res.data.result.refreshToken, {
                        expires: 7,
                    });
                }
                toast.success("Login successfully", {
                    autoClose: 3000,
                });
                navigate("/dashboard");
            })
            .catch((error: any) => {
                if (!error.response || error.code === "ERR_NETWORK") {
                    setServerError(true);
                    return;
                }

                const errorMessage =
                    error.response?.data?.message ||
                    "Invalid email or password";
                toast.error(errorMessage, {
                    autoClose: 3000,
                });
                setLoginError(errorMessage);
            })
            .finally(() => {
                dispatch(disableLoading());
            });
    };

    if (serverError) {
        return <Navigate to="/500" replace />;
    }

    return (
        <div className="flex items-center justify-center p-4 relative">
            <div className="lg:hidden">
                <i className="fas fa-hotel text-3xl text-blue-600"></i>
            </div>

            <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-lg">
                <div className="text-center mb-8">
                    <h1 className="flex items-center justify-center gap-3 text-2xl md:text-3xl font-bold text-gray-800 mb-2">
                        <img
                            src="app/asset/images/minilogo.png"
                            alt="Hotel Logo"
                            className=" md:w-10 md:h-10 "
                        />
                        Portal Hotel Management
                    </h1>
                    <p className="text-gray-600">Sign in to stay connected.</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="space-y-2">
                        <label
                            htmlFor="email"
                            className="block text-sm font-medium text-gray-700"
                        >
                            Email
                        </label>
                        <div className="relative">
                            <InputText
                                id="email"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className={`w-full pl-10 pr-4 py-2 border ${
                                    emailError
                                        ? "border-red-500"
                                        : "border-gray-300"
                                } rounded-lg focus:ring-2 ${
                                    emailError
                                        ? "focus:ring-red-500 focus:border-red-500"
                                        : "focus:ring-blue-500 focus:border-blue-500"
                                } text-sm hover:border-blue-400 transition-colors`}
                            />
                            {emailError && (
                                <p className="mt-1 text-xs text-red-500">
                                    {emailError}
                                </p>
                            )}
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label
                            htmlFor="password"
                            className="block text-sm font-medium text-gray-700"
                        >
                            Password
                        </label>
                        <div className="w-full">
                            <Password
                                feedback={false}
                                toggleMask
                                id="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                inputClassName="w-full"
                                className="w-full"
                                style={{ display: "inline-block" }}
                                //     className={`w-full pl-10 pr-4 py-2 border ${
                                //       passwordError ? "border-red-500" : "border-gray-300"
                                //     } rounded-lg focus:ring-2 ${
                                //       passwordError
                                //         ? "focus:ring-red-500 focus:border-red-500"
                                //         : "focus:ring-blue-500 focus:border-blue-500"
                                //     } text-sm hover:border-blue-400 transition-colors`

                                // }
                            />
                            {passwordError && (
                                <p className="mt-1 text-xs text-red-500">
                                    {passwordError}
                                </p>
                            )}
                        </div>
                    </div>

                    <div className="flex items-center justify-between">
                        <div className="flex items-center">
                            <Checkbox
                                inputId="remember"
                                checked={remember}
                                onChange={(e) =>
                                    setRemember(e.checked ?? false)
                                }
                                className="mr-2"
                            />
                            <label
                                htmlFor="remember"
                                className="text-sm text-gray-600"
                            >
                                Remember me?
                            </label>
                        </div>
                        <a
                            href="#"
                            className="text-sm text-blue-600 hover:text-blue-800"
                        >
                            Forgot Password?
                        </a>
                    </div>

                    <Button
                        label="Sign in"
                        type="submit"
                        className={
                            "w-full bg-blue-400 text-white py-2 rounded-lg transition-colors"
                        }
                    />

                    {loginError && (
                        <div className="p-3 mt-3 bg-red-50 border border-red-200 rounded-lg">
                            <p className="text-sm text-red-600">{loginError}</p>
                        </div>
                    )}
                </form>
            </div>
        </div>
    );
};

export default Login;
