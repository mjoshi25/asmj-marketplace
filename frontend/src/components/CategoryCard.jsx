import { BriefcaseBusiness, CalendarDays, GraduationCap, HeartPulse, Package, Wrench } from "lucide-react";

const icons = { PRODUCT: Package, SERVICE: Wrench, EVENT: CalendarDays, INSURANCE: HeartPulse, JOB: BriefcaseBusiness, TUTOR: GraduationCap };

export default function CategoryCard({ type, title, onClick }) {
  const Icon = icons[type] || Package;
  return (
    <button onClick={onClick} className="card group p-5 text-left hover:-translate-y-1 hover:shadow-lg transition">
      <div className="mb-4 grid h-12 w-12 place-items-center rounded-xl bg-slate-100 group-hover:bg-slate-900 group-hover:text-white transition">
        <Icon size={22} />
      </div>
      <div className="font-bold">{title}</div>
      <div className="mt-1 text-xs text-slate-400">Explore {title.toLowerCase()}</div>
    </button>
  );
}