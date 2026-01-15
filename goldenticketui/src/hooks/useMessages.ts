import { useMemo, useState } from "react";
import type { ProductsState } from "./useProducts";
import type { InventoryState } from "./useInventory";

export type MessageState =
  | { kind: "none" }
  | { kind: "info"; text: string }
  | { kind: "success"; text: string }
  | { kind: "soldout"; text: string }
  | { kind: "notfound"; text: string }
  | { kind: "error"; text: string };

function deriveSystemMessage(productsState: ProductsState, inventoryState: InventoryState): MessageState {
  if (productsState.kind === "loading") {
    return { kind: "info", text: "Loading product..." };
  }

  if (productsState.kind === "error") {
    return { kind: "error", text: productsState.text };
  }

  if (productsState.kind === "ready" && productsState.products.length === 0) {
    return { kind: "info", text: "No products available." };
  }

  if (inventoryState.kind === "notfound") {
    return { kind: "notfound", text: "Product not found." };
  }

  if (inventoryState.kind === "error") {
    return { kind: "error", text: inventoryState.text };
  }

  if (inventoryState.kind === "idle") {
    const remaining = inventoryState.remaining;
    if (remaining != null && remaining <= 0) {
      return { kind: "soldout", text: "Sold out." };
    }
  }

  return { kind: "none" };
}

function isHardSystemMessage(m: MessageState) {
  return m.kind === "soldout" || m.kind === "error" || m.kind === "notfound";
}

export function useUiMessage(args: {
  productsState: ProductsState;
  inventoryState: InventoryState;
}) {
  const { productsState, inventoryState } = args;

  const [userMessage, setUserMessage] = useState<MessageState>({ kind: "none" });

  const systemMessage = useMemo(
    () => deriveSystemMessage(productsState, inventoryState),
    [productsState, inventoryState]
  );

  
  const message = useMemo(() => {
    if (isHardSystemMessage(systemMessage)) return systemMessage;
    if (userMessage.kind !== "none") return userMessage;
    return systemMessage;
  }, [systemMessage, userMessage]);

  return { message, setUserMessage };
}