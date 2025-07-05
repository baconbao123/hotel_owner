import axios from "axios";
import Cookies from "js-cookie";

const $axios = axios.create({
  baseURL: import.meta.env.VITE_REACT_APP_BACK_END_LINK,
  headers: {
    "Content-Type": "application/json",
  },
});

let requestCount = 0;

$axios.interceptors.request.use(
  (config) => {
    requestCount++;
    if (
      document.readyState === "complete" ||
      document.readyState === "interactive"
    ) {
      window.dispatchEvent(new CustomEvent("loading", { detail: true }));
    }
    const token = Cookies.get("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    requestCount--;
    if (
      requestCount === 0 &&
      (document.readyState === "complete" ||
        document.readyState === "interactive")
    ) {
      window.dispatchEvent(new CustomEvent("loading", { detail: false }));
    }
    return Promise.reject(error);
  }
);

$axios.interceptors.response.use(
  (response) => {
    requestCount--;
    if (
      requestCount === 0 &&
      (document.readyState === "complete" ||
        document.readyState === "interactive")
    ) {
      window.dispatchEvent(new CustomEvent("loading", { detail: false }));
    }
    return response;
  },
  async (error) => {
    requestCount--;
    if (
      requestCount === 0 &&
      (document.readyState === "complete" ||
        document.readyState === "interactive")
    ) {
      window.dispatchEvent(new CustomEvent("loading", { detail: false }));
    }
    const originalRequest = error.config;

    if (error.response) {
      switch (error.response.status) {
        case 401:
          if (
            !originalRequest._retry &&
            window.location.pathname !== "/login"
          ) {
            originalRequest._retry = true;
            const refreshToken = Cookies.get("refreshToken");
            if (refreshToken) {
              try {
                console.log("Attempting token refresh...");
                const response = await $axios.post("/Auth/refreshToken", {
                  refreshToken,
                  publicKey: "mynameisnguyen",
                });
                const { token, refreshToken: newRefreshToken } = response.data;
                Cookies.set("token", token, { expires: 1 / 24 });
                Cookies.set("refreshToken", newRefreshToken, { expires: 7 });
                originalRequest.headers.Authorization = `Bearer ${token}`;
                console.log("Token refreshed successfully");
                return $axios(originalRequest);
              } catch (err) {
                console.error("Token refresh failed:", err);
                Cookies.remove("token");
                Cookies.remove("refreshToken");
                window.location.href = "/login";
                return Promise.reject(err);
              }
            } else {
              window.location.href = "/login";
            }
          }
          break;
        case 403:
          window.location.href = "/403";
          break;
        // case 404:
        //   if (!error.response.data && window.location.pathname !== "/404") {
        //     window.location.href = "/404";
        //   }
        //   break;
        // case 500:
        //   window.location.href = "/500";
        //   break;
      }
    }

    return Promise.reject(error);
  }
);

export const authorization = (token: string) => {
  return { Authorization: `Bearer ${token}` };
};

export default $axios;
