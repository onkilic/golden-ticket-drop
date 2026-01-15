import { fetchJson } from "./http";
import type { Product, InventoryResponse } from "../domain/types";

export function getProducts() {
  return fetchJson<Product[]>("/products");
}

export function getInventory(productId: number) {
  return fetchJson<InventoryResponse>(`/products/${productId}/inventory`);
}
