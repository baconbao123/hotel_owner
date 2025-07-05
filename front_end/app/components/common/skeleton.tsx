import { Card } from "antd";
import { Column } from "primereact/column";
import { DataTable } from "primereact/datatable";
import { Skeleton } from "primereact/skeleton";

export const SkeletonTemplate = (title: string, columns: number) => (
  <Card title={title}>
    <DataTable
      value={Array(columns).fill({})}
      showGridlines
      className="p-datatable-striped"
    >
      <Column body={<Skeleton width="100%" height="2rem" />} />
      <Column body={<Skeleton width="100%" height="2rem" />} />
      <Column body={<Skeleton width="100%" height="2rem" />} />
      <Column body={<Skeleton width="100%" height="2rem" />} />
      <Column body={<Skeleton width="100%" height="2rem" />} />
    </DataTable>
  </Card>
);
