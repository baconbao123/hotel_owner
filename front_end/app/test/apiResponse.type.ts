// src/types/apiResponse.ts
export class Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;

  constructor(data: Partial<Sort> = {}) {
    this.empty = data.empty ?? false;
    this.sorted = data.sorted ?? false;
    this.unsorted = data.unsorted ?? false;
  }
}

export class Pageable {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  paged: boolean;
  unpaged: boolean;

  constructor(data: Partial<Pageable> = {}) {
    this.pageNumber = data.pageNumber ?? 0;
    this.pageSize = data.pageSize ?? 30;
    this.sort = new Sort(data.sort);
    this.offset = data.offset ?? 0;
    this.paged = data.paged ?? true;
    this.unpaged = data.unpaged ?? false;
  }
}

export class ApiResponse {
  code: number;
  result: {
    content: any[];
    pageable: Pageable;
    last: boolean;
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    sort: Sort;
    first: boolean;
    numberOfElements: number;
    empty: boolean;
  };

  constructor(data: Partial<ApiResponse> | any[] = {}) {
    if (Array.isArray(data)) {
      this.code = 200;
      this.result = {
        content: data || [],
        pageable: new Pageable({ pageNumber: 0, pageSize: 30 }),
        last: true,
        totalPages: 1,
        totalElements: data?.length || 0,
        size: 30,
        number: 0,
        sort: new Sort({ empty: false, sorted: true, unsorted: false }),
        first: true,
        numberOfElements: data?.length || 0,
        empty: !data || data.length === 0,
      };
    } else {
      this.code = data.code ?? 200;
      this.result = {
        content: Array.isArray(data.result?.content) ? data.result.content : [],
        pageable: new Pageable(data.result?.pageable),
        last: data.result?.last ?? true,
        totalPages: data.result?.totalPages ?? 1,
        totalElements: data.result?.totalElements ?? 0,
        size: data.result?.size ?? 30,
        number: data.result?.number ?? 0,
        sort: new Sort(data.result?.sort),
        first: data.result?.first ?? true,
        numberOfElements: data.result?.numberOfElements ?? 0,
        empty: data.result?.empty ?? false,
      };
    }
  }
}
