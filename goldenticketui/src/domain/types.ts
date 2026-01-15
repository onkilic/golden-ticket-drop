export type Product = {
  id: number;
  name: string;
  description: string;
};

export type InventoryResponse = {
  productId: number;
  remaining: number;
};

export type PurchaseStatus =
  | "SUCCESS"
  | "SOLD_OUT"
  | "NOT_FOUND"
  | "INVALID_QUANTITY";

export const PurchaseStatus = {
  SUCCESS: "SUCCESS",
  SOLD_OUT: "SOLD_OUT",
  NOT_FOUND: "NOT_FOUND",
  INVALID_QUANTITY: "INVALID_QUANTITY",
} as const;


export type BuyResponse = {
  status: PurchaseStatus;
  remaining: number;
};
