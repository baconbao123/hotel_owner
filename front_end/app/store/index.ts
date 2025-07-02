import { configureStore } from "@reduxjs/toolkit";

import {
  type TypedUseSelectorHook,
  useDispatch,
  useSelector,
} from "react-redux";

import commonSlince from './slince/commonSlince'
import userDataSlice from './slince/userDataSlice'
import permissionSlice from './slince/permissionSlice'

export const store = configureStore({
  reducer: {
    commonSlince: commonSlince,
    userDataSlice: userDataSlice,
    permissionSlice: permissionSlice
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export const useAppDispatch: () => AppDispatch = useDispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;