type Props = {
  productName: string;
  remaining: number | null;
  loading: boolean;
  canBuy: boolean;
  onBuy: () => void;
};

export function ProductCard({ productName, remaining, loading, canBuy, onBuy }: Props) {
  return (
    <div className="card">
      <h1 className="title">{productName}</h1>

      <div className="row">
        <span className="muted">Items remaining</span>
        <strong>{remaining ?? "-"}</strong>
      </div>

      <button className="btn" onClick={onBuy} disabled={!canBuy}>
        {loading ? "Processing..." : "Buy now"}
      </button>
    </div>
  );
}
