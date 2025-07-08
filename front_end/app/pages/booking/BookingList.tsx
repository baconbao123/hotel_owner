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
import { useParams } from "react-router";
import BookingForm from "./BookingForm";
import BookingDetail from "./BookingDetail";
import useCrud from "@/test/crudHook";
import { SkeletonTemplate } from "@/components/common/skeleton";
import { parseISO, format } from "date-fns";

export default function RoomList() {
  const [selectedId, setSelectedId] = useState<string>();
  const [formMode, setFormMode] = useState<"create" | "edit" | "view">(
    "create"
  );
  const [mounted, setMounted] = useState(false);
  const toast = useRef<Toast>(null);

  const { roomId } = useParams();

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
  } = useCrud("booking", roomId, "booking");

  useEffect(() => {
    setMounted(true);
  }, []);

  const handlePageChange = (e: any) => updatePageData(e.page, e.rows);
  const handleSortChange = (e: any) =>
    handleSort(e.sortField || "", e.sortOrder || 0);

  const formatDateTime = (rowData: any, column: any) => {
    const timeValue = rowData[column.field];
    if (!timeValue) return "";
    try {
      const date = parseISO(timeValue);
      return format(date, "yyyy-MM-dd HH:mm:ss");
    } catch (error) {
      console.log(`Error parsing ${column.field}:`, error);
      return timeValue;
    }
  };

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

  return (
    <div className="main-container">
      {mounted && <Toast ref={toast} />}
      <div className="mb-5">
        {mounted ? (
          <></>
        ) : (
          // <BreadCrumbComponent name="RoleList" />
          <Skeleton width="100%" height="34px" />
        )}
      </div>

      <Card title="Booking management">
        <div className="mb-5">
          <div className="grid grid-cols-4 gap-10 card">
            <div className="col-span-4 2xl:col-span-3">
              <div className="grid gap-2 2xl:grid-cols-6 grid-cols-2">
                {mounted ? (
                  <>
                    <InputText
                      placeholder="Name"
                      className="w-full"
                      value={filters.name || ""}
                      onChange={(e) => handleSearch("name", e.target.value)}
                    />
                  </>
                ) : (
                  <>
                    <Skeleton height="100%" />
                  </>
                )}
              </div>
            </div>
            <div className="col-span-4 2xl:col-span-1">
              <div className="flex flex-wrap gap-2 justify-end">
                <Button
                  label="Add New"
                  className="btn_add_new"
                  onClick={() => {
                    setSelectedId(undefined);
                    setFormMode("create");
                    setOpenForm(true);
                  }}
                />
              </div>
            </div>
          </div>
        </div>

        {tableLoading ? (
          SkeletonTemplate("Booking Management", 5)
        ) : (
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
          >
            <Column sortable field="id" header="Id" className="w-20" />
            <Column sortable field="userName" header="User" className="w-100" />
            <Column
              sortable
              field="roomNumber"
              header="Room Number"
              className="w-100"
            />
            <Column
              sortable
              field="checkInTime"
              header="Check In Time"
              className="w-100"
              body={formatDateTime}
            />
            <Column
              sortable
              field="checkOutTime"
              header="Check Out Time"
              className="w-100"
              body={formatDateTime}
            />
            <Column
              sortable
              field="status"
              header="Status"
              className="text-center w-50"
              body={statusBody}
            />
            <Column
              frozen={true}
              header={() => <div className="flex justify-center">Actions</div>}
              className="w-60"
              body={(row) => (
                <div className="flex gap-2 justify-center">
                  <Button
                    icon="pi pi-eye"
                    rounded
                    text
                    className="icon_view"
                    onClick={() => {
                      setSelectedId(String(row.id));
                      setFormMode("view");
                      setOpenFormDetail(true);
                    }}
                    tooltip="View"
                    tooltipOptions={{ position: "top" }}
                  />

                  <Button
                    icon="pi pi-pencil"
                    rounded
                    text
                    className="icon_edit"
                    onClick={() => {
                      setSelectedId(String(row.id));
                      setFormMode("edit");
                      setOpenForm(true);
                    }}
                    tooltip="Edit"
                    tooltipOptions={{ position: "top" }}
                  />

                  <Button
                    icon="pi pi-trash"
                    rounded
                    text
                    className="icon_trash"
                    onClick={() => handleDelete(String(row.id))}
                    tooltip="Delete"
                    tooltipOptions={{ position: "top" }}
                  />
                </div>
              )}
            />
          </DataTable>
        )}
      </Card>

      <BookingForm
        roomId={roomId ?? 0}
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

      <BookingDetail
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
