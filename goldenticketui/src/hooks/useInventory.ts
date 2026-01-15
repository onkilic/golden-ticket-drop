import { useEffect, useRef, useState } from "react";
import { getInventory } from "../api/productsApi";
import { isNetworkError } from "../utils/result";

const POLL_INTERVAL_MS = 1000;

export type InventoryState =
  | { kind: "loading"; remaining: number | null }
  | { kind: "idle"; remaining: number | null }
  | { kind: "notfound"; remaining: null }
  | { kind: "error"; remaining: number | null; text: string };

export function useInventory(productId: number | null) {
  const [state, setState] = useState<InventoryState>({
    kind: "loading",
    remaining: null,
  });

  const pollRef = useRef<number | null>(null);

  async function load(currentProductId: number) {
    try {
      const r = await getInventory(currentProductId);

      if (isNetworkError(r)) {
        setState((prev) => ({
          kind: "error",
          remaining: prev.remaining,
          text: "Failed to load inventory (network error).",
        }));
        return;
      }

      if (r.status === 404) {
        setState({ kind: "notfound", remaining: null });
        return;
      }

      if (!r.ok || !r.data) {
        setState((prev) => ({
          kind: "error",
          remaining: prev.remaining,
          text: "Failed to load inventory.",
        }));
        return;
      }

      setState({ kind: "idle", remaining: r.data.remaining });
    } catch {
      setState((prev) => ({
        kind: "error",
        remaining: prev.remaining,
        text: "Failed to load inventory (network error).",
      }));
    }
  }

  useEffect(() => {
    if (productId == null) return;

    let cancelled = false;

    const run = () => {
      if (cancelled) return;
      void load(productId);
    };

    queueMicrotask(run);

    if (pollRef.current) window.clearInterval(pollRef.current);
    pollRef.current = window.setInterval(run, POLL_INTERVAL_MS);

    return () => {
      cancelled = true;
      if (pollRef.current) window.clearInterval(pollRef.current);
    };
  }, [productId]);

  return {
    ...state,
    refresh: () => {
      if (productId == null) return;
      void load(productId);
    },
  };
}