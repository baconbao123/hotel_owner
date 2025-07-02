import {
  type RouteConfig,
  route,
  index,
  layout,
} from "@react-router/dev/routes";

export default [
  layout("./layout/rootLayout.tsx", [
      route("/dashboard", "./pages/dashboard/Dashboard.tsx"),
//     route("/profile", "./pages/profile/ProfilePage.tsx"),
//     route("", "./routes/protectedRoute.tsx", [
//       route("/dashboard", "./pages/dashboard/DashboardPage.tsx"),
//       route("/user", "./pages/user/UserList.tsx"),
//       route("/role", "./pages/role/RoleList.tsx"),
//       route("/bookings/calendar", "./pages/hotel/BookingCalendar.tsx"),
//       route("/permission", "./pages/permission/PermissionList.tsx"),
//       route("/streets", "./pages/street/StreetList.tsx"),
//       route("/hotels", "./pages/hotel/HotelList.tsx"),
//       route("/facilities", "./pages/facilities/FacilityList.tsx"),
//       route("/room/:hotelId", "./pages/rooms/RoomList.tsx"),
//       route("/booking/:roomId", "./pages/booking/BookingList.tsx"),
//     ]),
  ]),
  layout("./layout/authLayout.tsx", [
    route("/login", "./pages/login/Login.tsx"),
    // route("/reset-password-profile", "./pages/profile/ResetPassword.tsx"),
  ]),
//   route("/404", "./pages/error/NotFound.tsx"),
//   route("/403", "./pages/error/Error403.tsx"),
//   route("/500", "./pages/error/Error500.tsx"),
//   route("*", "./pages/error/NotFound.tsx", { id: "catch-all" }),
] satisfies RouteConfig;
