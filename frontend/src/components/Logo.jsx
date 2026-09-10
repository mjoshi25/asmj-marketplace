import { Store } from "lucide-react";

export default function Logo() {
  return (
    <div className="flex items-center gap-2">
      <div className="grid h-10 w-10 place-items-center rounded-xl bg-slate-900 text-white">
        <Store size={20} />
      </div>
      <div>
        <div className="text-lg font-extrabold tracking-tight">ASMJ</div>
        <div className="-mt-1 text-[10px] font-semibold uppercase tracking-[.2em] text-slate-400">Marketplace</div>
      </div>
    </div>
  );
}