import { useSelector } from "react-redux";
import type { RootState } from "~/store";

export default function commonHook() {
  const permissions = useSelector(
    (state: RootState) => state.permissionSlice.permissions
  );
  const checkPermission = (resource: String, action: String) => {
    let item = permissions.find((item: any) => {
      if (item.resourceName === resource && item.actionNames.includes(action)) {
        return true;
      }
    });

    if (item) {
      return true;
    }

    return false;
  };

  return {
    checkPermission: checkPermission,
  };
}
