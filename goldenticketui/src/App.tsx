import { useMemo, useState } from "react";
import { createPurchase } from "./api/purchasesApi";
import { PurchaseStatus } from "./domain/types";
import type { PurchaseStatus as PurchaseStatusType } from "./domain/types";

import { ProductCard } from "./components/ProductCard";
import { StatusMessage } from "./components/StatusMessage";

import { useProducts } from "./hooks/useProducts";
import { useInventory } from "./hooks/useInventory";
import { useUiMessage } from "./hooks/useMessages";
import { isNetworkError } from "./utils/result";

function isPurchaseStatus(status: unknown): status is PurchaseStatusType {
  return (
    status === PurchaseStatus.SUCCESS ||
    status === PurchaseStatus.SOLD_OUT ||
    status === PurchaseStatus.NOT_FOUND ||
    status === PurchaseStatus.INVALID_QUANTITY
  );
}

export default function App() {
  const productsState = useProducts();
  const currentProduct =
    productsState.kind === "ready" && productsState.products.length > 0
      ? productsState.products[0]
      : null;

  const inventory = useInventory(currentProduct?.id ?? null);

  const [buying, setBuying] = useState(false);

const { message, setUserMessage } = useUiMessage({
  productsState,
  inventoryState: inventory,
});

  const remaining = inventory.remaining ?? null;

  const canBuy = useMemo(() => {
    if (buying) return false;
    if (!currentProduct) return false;
    if (remaining == null) return false;
    return remaining > 0;
  }, [buying, currentProduct, remaining]);

  async function buy() {
    if (!currentProduct) return;

    try {
      setBuying(true);
      setUserMessage({ kind: "none" });

      const r = await createPurchase(currentProduct.id);

      if (isNetworkError(r)) {
        setUserMessage({ kind: "error", text: "Purchase failed (network error)." });
        return;
      }

      if (r.status === 404) {
        setUserMessage({ kind: "notfound", text: "Product not found." });
        return;
      }
      if (r.status === 409) {
        setUserMessage({ kind: "soldout", text: "Sold out." });
        return;
      }
      if (r.status === 400) {
        setUserMessage({ kind: "error", text: "Invalid request." });
        return;
      }

      if (!r.ok || !r.data || !isPurchaseStatus(r.data.status)) {
        setUserMessage({ kind: "error", text: "Purchase failed (invalid response)." });
        return;
      }

      if (r.data.status === PurchaseStatus.SUCCESS) {
        setUserMessage({ kind: "success", text: "Purchase successful!" });
      } else if (r.data.status === PurchaseStatus.SOLD_OUT) {
        setUserMessage({ kind: "soldout", text: "Sold out." });
      } else if (r.data.status === PurchaseStatus.NOT_FOUND) {
        setUserMessage({ kind: "notfound", text: "Product not found." });
      } else {
        setUserMessage({ kind: "error", text: "Invalid request." });
      }

      inventory.refresh();
    } catch {
      setUserMessage({ kind: "error", text: "Purchase failed (network error)." });
    } finally {
      setBuying(false);
    }
  }

  return (
    <div className="page">
      <ProductCard
        productName={currentProduct?.name ?? "Loading..."}
        remaining={remaining}
        loading={buying || inventory.kind === "loading" || productsState.kind === "loading"}
        canBuy={canBuy}
        onBuy={buy}
      />

      <div className="status">
        <StatusMessage state={message} />
      </div>
    </div>
  );
}
