import { fetchJson } from "./http";
import type { BuyResponse } from "../domain/types";

export function createPurchase(productId: number) {
  return fetchJson<BuyResponse>("/buy", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ productId, quantity: 1 }),
  });
}
