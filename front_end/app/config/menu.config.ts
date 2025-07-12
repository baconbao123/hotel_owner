import {
  HomeIcon,
  CogIcon,
  BuildingOffice2Icon,
  UserIcon,
  RectangleGroupIcon,
  IdentificationIcon,
  MapIcon,
  TagIcon,
  ShieldCheckIcon,
} from "@heroicons/react/24/outline";

export const navigation: MenuItem[] = [
  {
    name: "Dashboard",
    href: "/dashboard",
    icon: HomeIcon,
    resourceName: "Dashboard",
  },
  // {
  //   name: "Users Management",
  //   href: "/user",
  //   icon: UserIcon,
  //   resourceName: "User",
  // },
  // {
  //   name: "Role Management",
  //   href: "/role",
  //   icon: IdentificationIcon,
  //   resourceName: "Role",
  // },
  // {
  //   name: "Permission Management",
  //   href: "/permission",
  //   icon: ShieldCheckIcon,
  //   resourceName: "Permissions",
  // },
  // {
  //   name: "Street Management",
  //   href: "/streets",
  //   icon: MapIcon,
  //   resourceName: "Street",
  // },
  {
    name: "Hotel Management",
    href: "/hotels",
    icon: BuildingOffice2Icon,
    resourceName: "Hotel",
  },
  // {
  //   name: "Hotel Facilities",
  //   href: "/facilities",
  //   resourceName: "Facilities",
  //   icon: TagIcon,
  // },
];

export interface MenuItem {
  name: string;
  href: string;
  icon?: React.ComponentType<{ className?: string }>;
  resourceName: string;
  children?: MenuItem[];
}
