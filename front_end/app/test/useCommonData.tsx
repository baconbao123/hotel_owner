import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchCommonData,
  type CommonData,
  type CommonDataResponse,
} from "./commonDataSlice";
import type { AppDispatch, RootState } from "../store";

const typeMapping: Partial<Record<keyof CommonData, keyof CommonDataResponse>> =
  {
    roomtypes: "roomTypes",
    paymentmethods: "paymentMethods",
    provinces: "provinces",
    customers: "customers",
    hoteldocuments: "documentTypes",
    hoteltypes: "hotelTypes",
    hotelfacilities: "hotelFacilities",
  };

export const useCommonData = (
  types: (keyof CommonData)[],
  options: { force?: boolean } = {}
) => {
  const dispatch = useDispatch<AppDispatch>();
  const commonData = useSelector(
    (state: RootState) => state.commonDataSlice.data
  );
  const status = useSelector(
    (state: RootState) => state.commonDataSlice.status
  );
  const error = useSelector((state: RootState) => state.commonDataSlice.error);
  const hasFetchedRef = useRef(false);

  useEffect(() => {
    if (hasFetchedRef.current && !options.force) return;
    hasFetchedRef.current = true;
    console.log("Dispatching fetchCommonData", { types, force: options.force });
    dispatch(fetchCommonData({ types, force: options.force }));
  }, [dispatch, types, options.force]);

  const mappedData = types.reduce((acc, type) => {
    const responseKey = typeMapping[type];
    if (responseKey) {
      acc[responseKey] = commonData[type] || [];
    }
    return acc;
  }, {} as CommonDataResponse);

  return { commonData: mappedData, status, error };
};
