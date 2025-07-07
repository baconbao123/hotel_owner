import {
  createSlice,
  createAsyncThunk,
  type PayloadAction,
} from "@reduxjs/toolkit";
import type { RootState } from "../store";
import $axios from "~/axios";

export interface CommonDataResponse {
  facilityTypes?: any[];
  documentTypes?: any[];
  hotelTypes?: any[];
  roles?: any[];
  provinces?: any[];
  resourceActions?: any[];
  hotelFacilities?: any[];
  paymentMethods?: any[];
  roomTypes?: any[];
  owners?: any[];
  userTypes?: any[];
  customers?: any[];
}

export interface CommonData {
  roomtypes?: any[];
  paymentmethods?: any[];
  provinces?: any[];
  customers?: any[];
  hoteldocuments?: any[];
  hoteltypes?: any[];
  hotelfacilities?: any[];
}

interface CommonDataState {
  data: CommonData;
  status: "idle" | "loading" | "succeeded" | "failed";
  error?: string;
}

const initialState: CommonDataState = {
  data: {},
  status: "idle",
};

const typeMapping: Record<keyof CommonData, keyof CommonDataResponse> = {
  roomtypes: "roomTypes",
  paymentmethods: "paymentMethods",
  provinces: "provinces",
  customers: "customers",
  hoteldocuments: "documentTypes",
  hoteltypes: "hotelTypes",
  hotelfacilities: "hotelFacilities",
};

const reverseTypeMapping: Record<string, keyof CommonData> = Object.entries(
  typeMapping
).reduce((acc, [requestType, responseType]) => {
  acc[responseType] = requestType as keyof CommonData;
  return acc;
}, {} as Record<string, keyof CommonData>);

export const fetchCommonData = createAsyncThunk(
  "commonData/fetchCommonData",
  async (
    {
      types,
      force = false,
      params = {},
    }: {
      types: (keyof CommonData)[];
      force?: boolean;
      params?: Record<string, any>;
    },
    { getState, rejectWithValue }
  ) => {
    const state = getState() as RootState;
    const typesToFetch = force
      ? types
      : types.filter((type) => !state.commonDataSlice.data[type]);

    if (typesToFetch.length === 0) {
      return {};
    }

    try {
      const searchParams = new URLSearchParams();

      typesToFetch.forEach((type) => {
        searchParams.append("types", type);
      });

      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          searchParams.append(key, value.toString());
        }
      });

      const response = await $axios.get(
        `${
          import.meta.env.VITE_REACT_APP_ADMIN_BACK_END_LINK
        }/common-data?${searchParams.toString()}`
      );
      if (response.status !== 200) {
        throw new Error("Failed to fetch common data");
      }

      const responseData: CommonDataResponse = response.data.result;
      const currentData = state.commonDataSlice.data; // Get current Redux state

      const mappedData = Object.entries(responseData).reduce(
        (acc, [responseKey, value]) => {
          const requestKey = reverseTypeMapping[responseKey];
          if (requestKey && typesToFetch.includes(requestKey)) {
            if (Array.isArray(value) && value.length > 0) {
              acc[requestKey] = value;
            } else {
              acc[requestKey] = currentData[requestKey] || [];
            }
          }
          return acc;
        },
        {} as CommonData
      );

      return mappedData;
    } catch (error) {
      return rejectWithValue((error as Error).message);
    }
  }
);

const commonDataSlice = createSlice({
  name: "commonData",
  initialState,
  reducers: {
    clearCommonData: (state, action: PayloadAction<(keyof CommonData)[]>) => {
      action.payload.forEach((type) => {
        delete state.data[type];
      });
      state.status = "idle";
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCommonData.pending, (state) => {
        state.status = "loading";
      })
      .addCase(
        fetchCommonData.fulfilled,
        (state, action: PayloadAction<CommonData>) => {
          state.status = "succeeded";
          state.data = { ...state.data, ...action.payload };
        }
      )
      .addCase(fetchCommonData.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload as string;
      });
  },
});

export const { clearCommonData } = commonDataSlice.actions;
export default commonDataSlice.reducer;
