import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

export interface UserLogin {
  id: number;
  email: string;
  fullname: string;
  phoneNumber: string;
  avatarUrl: string;
  loading: boolean;
  roles: [];
}

const initialState: UserLogin = {
  id: 0,
  email: "",
  fullname: "",
  phoneNumber: "",
  avatarUrl: "",
  loading: false,
  roles: [],
};

const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<UserLogin>) {
      state.id = action.payload.id || 0;
      state.email = action.payload.email || "";
      state.fullname = action.payload.fullname || "";
      state.phoneNumber = action.payload.phoneNumber || "";
      state.avatarUrl = action.payload.avatarUrl || "";
      state.loading = false;
      state.roles = action.payload.roles || [];
    },
    startLoading(state) {
      state.loading = true;
    },
    logout(state) {
      state.id = initialState.id;
      state.email = initialState.email;
      state.fullname = initialState.fullname;
      state.phoneNumber = initialState.phoneNumber;
      state.avatarUrl = initialState.avatarUrl;
      state.roles = initialState.roles;
    },
  },
});

export const { setUser, startLoading } = userSlice.actions;
export default userSlice.reducer;
