import { useState, useRef, useEffect } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { Toast } from "primereact/toast";
import { Tag } from "primereact/tag";
import { Skeleton } from "primereact/skeleton";
import Swal from "sweetalert2";
import noImg from "@/asset/images/no-img.png";
import HotelForm from "./HotelForm";
import HotelDetail from "./HotelDetail";
import { useSelector } from "react-redux";
import { Link } from "react-router";
import useCrud from "~/test/crudHook";
import type { RootState } from "~/store";
import Loading from "~/components/common/Loading";
import { SkeletonTemplate } from "~/components/common/skeleton";
import { Image } from "antd";

export default function RoleList() {
  const [selectedId, setSelectedId] = useState<string>();
  const [formMode, setFormMode] = useState<"create" | "edit" | "view">(
    "create"
  );
  const [mounted, setMounted] = useState(false);
  const toast = useRef<Toast>(null);

  const {
    data,
    tableLoading,
    error,
    openForm,
    setOpenForm,
    openFormDetail,
    setOpenFormDetail,
    loadById,
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
  } = useCrud("/hotel");

  useEffect(() => {
    setMounted(true);
  }, []);

  const loading = useSelector((state: RootState) => state.commonSlince.loading);

  if (loading) <Loading />;

  const handlePageChange = (e: any) => updatePageData(e.page, e.rows);
  const handleSortChange = (e: any) =>
    handleSort(e.sortField || "", e.sortOrder || 0);

  const handleDelete = (id: string) => {
    Swal.fire({
      title: "Delete hotel?",
      showDenyButton: true,
      showCancelButton: true,
      confirmButtonText: "Delete",
    }).then((result) => {
      if (result.isConfirmed) {
        deleteItem(id)
          .then(() => Swal.fire("Deleted!", "", "success"))
          .catch(() =>
            toast.current?.show({
              severity: "error",
              summary: "Error",
              detail: "Failed to delete role",
              life: 3000,
            })
          );
      }
    });
  };

  const statusBody = (row: any) => (
    <div className="flex justify-center">
      <Tag
        rounded
        value={row.status ? "Active" : "Inactive"}
        severity={row.status ? "success" : "danger"}
        style={{
          maxWidth: "5rem",
          display: "flex",
          justifyContent: "center",
          padding: "0.4rem 3rem",
        }}
      />
    </div>
  );

  const statusRoom = (row: any) => (
    <div className="flex justify-center">
      <Link to={`/rooms/${row.id}`}>
        <Button
          label="View Rooms"
          severity="info"
          text
          style={{ color: "#0ea5e9" }}
        />
      </Link>
    </div>
  );

  return (
    <div className="main-container">
      {mounted && <Toast ref={toast} />}
      <div className="mb-5">
        {mounted ? (
          // <BreadCrumbComponent name="HotelList" />
          <></>
        ) : (
          <Skeleton width="100%" height="34px" />
        )}
      </div>

      <Card title="Hotel management">
        <div className="mb-5">
          {mounted ? (
            data.length === 0 ? (
              <div className="text-center">
                You have not been assigned to any hotel. Please contact the
                administrator for assistance.
              </div>
            ) : (
              <div className="grid grid-cols-4 gap-10">
                <div className="col-span-4 2xl:col-span-3">
                  <div className="grid grid-cols-2 gap-2 2xl:grid-cols-6">
                    <InputText
                      placeholder="Search by name"
                      className="w-full"
                      value={filters.name || ""}
                      onChange={(e) => handleSearch("name", e.target.value)}
                    />
                  </div>
                </div>
                {/* <div className="col-span-4 2xl:col-span-1">
                  <div className="flex justify-end gap-2 flex-wrap">
                    <Button
                      label="Add New"
                      className="btn-add-new"
                      onClick={() => {
                        setSelectedId(undefined);
                        setFormMode("create");
                        setOpenForm(true);
                      }}
                    />
                  </div>
                </div> */}
              </div>
            )
          ) : (
            <Skeleton height="100%" />
          )}
        </div>

        {tableLoading
          ? SkeletonTemplate("Hotel Management", 6)
          : data.length > 0 && (
              <DataTable
                value={data}
                paginator
                rows={pageSize}
                rowsPerPageOptions={[1, 5, 10, 25, 30]}
                totalRecords={totalRecords}
                first={page * pageSize}
                onPage={handlePageChange}
                onSort={handleSortChange}
                sortField={sortField}
                sortOrder={sortOrder as 1 | -1 | 0 | undefined}
                showGridlines
                rowHover
                scrollable
                scrollHeight="570px"
                lazy
                paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
                currentPageReportTemplate="From {first} to {last} of {totalRecords}"
                paginatorLeft={
                  <Button
                    severity="secondary"
                    icon="pi pi-refresh"
                    text
                    onClick={resetFilters}
                  />
                }
                className="hotel-datatable"
              >
                <Column
                  sortable
                  field="id"
                  header="ID"
                  style={{ width: "80px" }}
                />
                <Column
                  sortable
                  field="avatarUrl"
                  header="Avatar"
                  body={(row) => (
                    <div className="flex justify-center">
                      {row.avatarUrl ? (
                        <Image
                          src={`${
                            import.meta.env.VITE_REACT_APP_BACK_END_UPLOAD_HOTEL
                          }/${row.avatarUrl}`}
                          width={50}
                          height={50}
                          style={{ objectFit: "cover", borderRadius: "4px" }}
                          preview
                          alt={`Avatar of ${row.name}`}
                        />
                      ) : (
                        <Image src={noImg} alt="No image available" />
                      )}
                    </div>
                  )}
                  style={{ width: "80px" }}
                />
                <Column
                  sortable
                  field="name"
                  header="Name"
                  style={{ width: "200px" }}
                />
                <Column
                  sortable
                  field="ownerName"
                  header="Owner Name"
                  style={{ width: "200px" }}
                  body={(row) => row.ownerName || "-"}
                />
                <Column
                  field="description"
                  header="Description"
                  style={{ width: "200px" }}
                  body={(row) => row.description || "-"}
                />
                <Column
                  field="id"
                  header="Rooms"
                  style={{ width: "70px", textAlign: "center" }}
                  body={statusRoom}
                />
                <Column
                  sortable
                  field="status"
                  header="Status"
                  style={{ width: "50px", textAlign: "center" }}
                  body={statusBody}
                />
                <Column
                  frozen
                  header={() => (
                    <div className="flex justify-center">Actions</div>
                  )}
                  style={{ width: "60px" }}
                  body={(row) => (
                    <div className="flex gap-2 justify-center">
                      <Button
                        icon="pi pi-eye"
                        className="icon-view"
                        rounded
                        text
                        onClick={() => {
                          setSelectedId(String(row.id));
                          setFormMode("view");
                          setOpenFormDetail(true);
                        }}
                        tooltip="View"
                        tooltipOptions={{ position: "top" }}
                        aria-label="View hotel details"
                      />
                      <Button
                        icon="pi pi-pencil"
                        className="icon-edit"
                        rounded
                        text
                        onClick={() => {
                          setSelectedId(String(row.id));
                          setFormMode("edit");
                          setOpenForm(true);
                        }}
                        tooltip="Edit"
                        tooltipOptions={{ position: "top" }}
                        aria-label="Edit hotel"
                      />
                      <Button
                        icon="pi pi-trash"
                        className="icon-delete"
                        rounded
                        text
                        onClick={() => handleDelete(String(row.id))}
                        tooltip="Delete"
                        tooltipOptions={{ position: "top" }}
                        aria-label="Delete hotel"
                      />
                    </div>
                  )}
                />
              </DataTable>
            )}
      </Card>

      <HotelForm
        id={selectedId}
        open={openForm}
        mode={formMode}
        onClose={() => {
          closeForm();
          setFormMode("create");
        }}
        loadDataById={loadById}
        createItem={createItem}
        updateItem={updateItem}
        error={error}
      />

      <HotelDetail
        id={selectedId}
        open={openFormDetail}
        mode={formMode}
        onClose={() => {
          setOpenFormDetail(false);
          setSelectedId(undefined);
          setFormMode("view");
        }}
        loadDataById={loadById}
      />
    </div>
  );
}
