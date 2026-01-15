export const API_BASE = "http://localhost:8080";

export async function fetchJson<T>(
  path: string,
  init?: RequestInit
): Promise<{ ok: boolean; status: number; data?: T }> {
  try {
    const res = await fetch(`${API_BASE}${path}`, init);
    const status = res.status;

    const text = await res.text();
    if (!text) return { ok: res.ok, status };

    try {
      return { ok: res.ok, status, data: JSON.parse(text) as T };
    } catch {
      return { ok: res.ok, status };
    }
  } catch {
     return { ok: false, status: 0 };
  }
}
