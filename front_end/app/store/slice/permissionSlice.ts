import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { RootState } from "..";

interface Permission {
  resourceName: string;
  actionNames: string[];
}

interface PermissionState {
  permissions: Permission[];
}

const initialState: PermissionState = {
  permissions: [],
};

const permissionSlice = createSlice({
  name: "permissions",
  initialState,
  reducers: {
    setPermissions(state, action: PayloadAction<Permission[]>) {
      state.permissions = action.payload || [];
    },
    startLoading(state) {
    },
  },
});

export const { setPermissions, startLoading } = permissionSlice.actions;
export default permissionSlice.reducer;

