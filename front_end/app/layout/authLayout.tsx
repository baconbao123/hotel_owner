import { Outlet } from "react-router";
import "@/asset/styles/main.scss";
export default function AuthLayout() {
  return (
   
      <div className="relative z-10 flex items-center justify-center min-h-screen bg-gray-100">
        <Outlet />
      </div>
  );
}
