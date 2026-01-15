import { useEffect, useState } from "react";
import { getProducts } from "../api/productsApi";
import type { Product } from "../domain/types";
import { isNetworkError } from "../utils/result";

export type ProductsState =
  | { kind: "loading" }
  | { kind: "ready"; products: Product[] }
  | { kind: "error"; text: string };

export function useProducts() {
  const [state, setState] = useState<ProductsState>({ kind: "loading" });

  useEffect(() => {
    (async () => {
      try {
        const product = await getProducts();

        if (isNetworkError(product)) {
          setState({ kind: "error", text: "Failed to load products (network error)." });
          return;
        }

        if (!product.ok) {
          setState({ kind: "error", text: "Failed to load products." });
          return;
        }

        if (!product.data) {
          setState({ kind: "error", text: "Failed to load products (invalid response)." });
          return;
        }

        setState({ kind: "ready", products: product.data });
      } catch {
        setState({ kind: "error", text: "Failed to load products (network error)." });
      }
    })();
  }, []);

  return state;
}
