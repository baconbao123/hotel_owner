import { useState, useEffect, useCallback, useRef } from "react";
import { useDispatch } from "react-redux";
import { disableLoading, setLoading } from "../store/slice/commonSlice";
import $axios from "@/axios";
import { ApiResponse } from "./apiResponse.type";

interface ErrorResponse {
  code: number;
  message: string;
}

export default function useCrud(
  baseUrl: string,
  param1?: string,
  param2?: string
) {
  const [data, setData] = useState<any[]>([]);
  const [error, setError] = useState<Object | null>(null);
  const [openForm, setOpenForm] = useState(false);
  const [openFormDetail, setOpenFormDetail] = useState(false);
  const [page, setPage] = useState(0);
  const [tableLoading, setTableLoading] = useState(true);
  const [pageSize, setPageSize] = useState(10);
  const [totalRecords, setTotalRecords] = useState(0);
  const [sortField, setSortField] = useState<string>();
  const [sortOrder, setSortOrder] = useState<number>();
  const [filters, setFilters] = useState<Record<string, string>>({});
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);
  const isMountedRef = useRef(true);

  const dispatch = useDispatch();

  const parseQueryParams = useCallback(() => {
    const params = new URLSearchParams(window.location.search);
    const result: Record<string, string> = {};
    params.forEach((value, key) => {
      if (key !== "page" && key !== "size" && !key.startsWith("sort[")) {
        result[key] = value;
      }
    });
    return result;
  }, []);

  useEffect(() => {
    isMountedRef.current = true;
    const abortController = new AbortController();
    const initialFilters = parseQueryParams();
    setFilters(initialFilters);

    loadData(initialFilters, undefined, undefined, true, page, pageSize, {
      signal: abortController.signal,
    }).catch((err) => {
      if (err.name === "AbortError") return;
      console.log(err);
    });

    return () => {
      isMountedRef.current = false;
      abortController.abort();
    };
  }, [baseUrl, param1, param2, parseQueryParams, page, pageSize]);

  const loadData = async (
    currentFilters: Record<string, string>,
    sortFieldParam = sortField,
    sortOrderParam = sortOrder,
    showLoading = true,
    pageParam = page,
    pageSizeParam = pageSize,
    options?: { signal?: AbortSignal }
  ) => {
    if (!isMountedRef.current) return;

    if (showLoading) {
      setTableLoading(true);
      dispatch(setLoading());
    }
    try {
      const query = new URLSearchParams();
      Object.entries(currentFilters).forEach(
        ([key, value]) => value && query.append(key, value)
      );
      if (sortFieldParam && sortOrderParam)
        query.append(
          `sort[${sortFieldParam}]`,
          sortOrderParam === 1 ? "desc" : "asc"
        );
      query.append("page", pageParam.toString());
      query.append("size", pageSizeParam.toString());

      let url = baseUrl;
      if (param1 && param2) {
        url = `${baseUrl}/${param1}/${param2}`;
      } else if (param1) {
        url = `${baseUrl}/${param1}`;
      }

      const res = await $axios.get(`${url}?${query}`, {
        signal: options?.signal,
      });
      const apiResponse = new ApiResponse(res.data);
      setData(apiResponse.result.content || []);
      setTotalRecords(apiResponse.result.totalElements || 0);
      setError(null);

      const urlParams = new URLSearchParams();
      Object.entries(currentFilters).forEach(
        ([key, value]) => value && urlParams.append(key, value)
      );
      window.history.replaceState(
        null,
        "",
        urlParams.toString()
          ? `${window.location.pathname}?${urlParams}`
          : window.location.pathname
      );
    } catch (err: any) {
      if (err.name === "AbortError") return;
      const errorResponse = err.response?.data as ErrorResponse;
      setError(errorResponse?.message || "Failed to load data");
      throw new Error(errorResponse?.message || "Failed to load data");
    } finally {
      if (showLoading) {
        setTableLoading(false);
        dispatch(disableLoading());
      }
    }
  };

  const updatePageData = useCallback(
    (newPage: number, newPageSize: number) => {
      setPage(newPage);
      setPageSize(newPageSize);
      loadData(
        filters,
        sortField,
        sortOrder,
        false,
        newPage,
        newPageSize
      ).catch((err) => {
        console.log(err);
      });
    },
    [filters, sortField, sortOrder]
  );

  const handleSort = useCallback(
    (field: string, order: number) => {
      setSortField(field);
      setSortOrder(order);
      loadData(filters, field, order).catch((err) => {
        console.log(err);
      });
    },
    [filters]
  );

  const handleSearch = useCallback(
    (field: string, value: string) => {
      const newFilters = { ...filters, [field]: value };
      if (!value) delete newFilters[field];
      setFilters(newFilters);

      if (timeoutRef.current) clearTimeout(timeoutRef.current);
      timeoutRef.current = setTimeout(() => {
        loadData(newFilters, sortField, sortOrder, false).catch((err) => {
          console.log(err);
        });
      }, 1000);
    },
    [filters, sortField, sortOrder]
  );

  const resetFilters = useCallback(() => {
    setFilters({});
    loadData({}).catch((err) => {
      console.log(err);
    });
  }, []);

  const loadById = useCallback(
    async (id: string) => {
      dispatch(setLoading());
      try {
        const res = await $axios.get(`${baseUrl}/${id}`);
        console.log(res.data.result);
        
        return res.data.result;
      } catch (err: any) {
        const errorResponse = err.response?.data as ErrorResponse;
        setError(errorResponse?.message || "Failed to load data");
        throw new Error(errorResponse?.message || "Failed to load data");
      } finally {
        dispatch(disableLoading());
      }
    },
    [baseUrl]
  );

  const createItem = async (item: object | FormData) => {
    dispatch(setLoading());
    try {
      const config =
        item instanceof FormData
          ? { headers: { "Content-Type": "multipart/form-data" } }
          : { headers: { "Content-Type": "application/json" } };
      const res = await $axios.post(baseUrl, item, config);
      await loadData(filters, sortField, sortOrder, true);
      setError(null);
      return res.data;
    } catch (err: any) {
      setError(err.response?.data?.errorMessages || "Failed to create item");
      throw new Error(err.response?.data?.message || "Failed to create item");
    } finally {
      dispatch(disableLoading());
    }
  };

  const updateItem = async (id: string, item: object | FormData) => {
    dispatch(setLoading());
    try {
      const config =
        item instanceof FormData
          ? { headers: { "Content-Type": "multipart/form-data" } }
          : { headers: { "Content-Type": "application/json" } };
      const res = await $axios.put(`${baseUrl}/${id}`, item, config);
      await loadData(filters, sortField, sortOrder, true);
      setError(null);
      return res.data;
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to update item");
      throw new Error(err.response?.data?.message || "Failed to update item");
    } finally {
      dispatch(disableLoading());
    }
  };

  const deleteItem = async (id: string) => {
    dispatch(setLoading());
    try {
      const res = await $axios.delete(`${baseUrl}/${id}`);
      await loadData(filters, sortField, sortOrder, true);
      setError(null);
      return res.data;
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to delete item");
      throw new Error(err.response?.data?.message || "Failed to delete item");
    } finally {
      dispatch(disableLoading());
    }
  };

  const closeForm = useCallback(() => {
    setOpenForm(false);
    setError(null);
  }, []);

  return {
    setData,
    tableLoading,
    data,
    error,
    openForm,
    setOpenForm,
    openFormDetail,
    setOpenFormDetail,
    loadById,
    loadData,
    updatePageData,
    handleSort,
    handleSearch,
    resetFilters,
    createItem,
    updateItem,
    deleteItem,
    page,
    pageSize,
    totalRecords,
    filters,
    sortField,
    sortOrder,
    closeForm,
  };
}
