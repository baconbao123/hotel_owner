import { Outlet } from "react-router";
import Sidebar from "~/components/common/Sidebar";
import Navbar from "~/components/common/Navbar";
import "@/asset/styles/globals.scss";

export default function RootLayout() {
  // useFetchPermissions();

  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden text-black">
        <Navbar />
        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100">
          <div className="w-full px-4 py-4">
            <Outlet />
          </div>
        </main>
        <footer className="bg-white border-t border-gray-200 p-4">
          <div className="w-full mx-auto">
            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-600">
                © 2024 Hotel Management System
              </p>
              <div className="flex space-x-4">
                <a
                  href="#"
                  className="text-sm text-gray-600 hover:text-gray-900"
                >
                  Privacy Policy
                </a>
                <a
                  href="#"
                  className="text-sm text-gray-600 hover:text-gray-900"
                >
                  Terms of Service
                </a>
              </div>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
}
