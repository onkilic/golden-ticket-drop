export type ApiResult<T> = { ok: boolean; status: number; data?: T };

export function isNetworkError<T>(r: ApiResult<T>) {
  return r.status === 0;
}
