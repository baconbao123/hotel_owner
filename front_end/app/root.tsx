// root.tsx
import { Meta, Outlet, Scripts, ScrollRestoration } from "react-router";
import { Provider } from "react-redux";
import "./app.css";
import { store } from "./store";
import { PrimeReactProvider } from "primereact/api";
import Loading from "@/components/common/Loading";
import { useSelector } from "react-redux";
import type { RootState } from "./store";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "primeicons/primeicons.css";

import "primereact/resources/themes/lara-light-cyan/theme.css";

function LayoutWithState() {
  const loading = useSelector((state: RootState) => state.commonSlince.loading);
  return (
    <>
      <ToastContainer />
      {loading && <Loading />}
      <Outlet />
    </>
  );
}

function HtmlShell({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
      </head>
      <body>
        {children}
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  return (
    <Provider store={store}>
      <PrimeReactProvider>
        <HtmlShell>
          <LayoutWithState />
        </HtmlShell>
      </PrimeReactProvider>
    </Provider>
  );
}
