type StatusState =
  | { kind: "none" }
  | { kind: "info"; text: string }
  | { kind: "success"; text: string }
  | { kind: "soldout"; text: string }
  | { kind: "notfound"; text: string }
  | { kind: "error"; text: string };

type Props = { state: StatusState };

export function StatusMessage({ state }: Props) {
  if (state.kind === "none") return null;

  return (
    <p className={`status-message status-message--${state.kind}`}>
      {state.text}
    </p>
  );
}
