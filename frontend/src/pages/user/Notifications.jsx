import { useEffect, useState } from "react";
import { getNotifications, markNotificationRead, markAllNotificationsRead } from "../../api/interactionApi";

export default function Notifications() {
  const [items, setItems] = useState([]);
  const load = () => getNotifications().then((r) => setItems(r.data?.data || [])).catch(() => setItems([]));
  useEffect(() => { load(); }, []);
  const unread = items.filter((n) => !n.read).length;
  const markAll = async () => { await markAllNotificationsRead(); load(); };
  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div><p className="text-sm font-semibold uppercase tracking-widest text-indigo-500">Account updates</p><h1 className="text-3xl font-black">Notifications</h1><p className="mt-1 text-sm text-slate-500">Stay updated about bookings, payments, enquiries and approvals.</p></div>
        {unread > 0 && <button className="btn-secondary" onClick={markAll}>Mark all as read</button>}
      </div>
      <div className="mt-6 grid gap-3 sm:grid-cols-3">
        <div className="card p-4"><p className="text-sm text-slate-500">Total notifications</p><b className="text-2xl">{items.length}</b></div>
        <div className="card p-4"><p className="text-sm text-slate-500">Unread</p><b className="text-2xl text-indigo-600">{unread}</b></div>
        <div className="card p-4"><p className="text-sm text-slate-500">Status</p><b className="text-2xl">{unread ? "Action needed" : "All caught up"}</b></div>
      </div>
      <div className="mt-6 space-y-3">
        {items.map((n) => <div className={`card p-5 ${!n.read ? "border border-indigo-300 bg-indigo-50/40" : ""}`} key={n.id}><div className="flex flex-wrap justify-between gap-4"><div className="min-w-0"><div className="flex items-center gap-2"><b>{n.title || "ASMJ update"}</b>{!n.read && <span className="rounded-full bg-indigo-600 px-2 py-0.5 text-xs font-bold text-white">NEW</span>}</div><p className="mt-1 text-sm text-slate-500">{n.message}</p><p className="mt-2 text-xs uppercase tracking-wide text-slate-400">{n.type || "General notification"}</p></div>{!n.read && <button className="btn-secondary self-start" onClick={async () => { await markNotificationRead(n.id); load(); }}>Mark read</button>}</div></div>)}
        {!items.length && <div className="card p-10 text-center text-slate-400">No notifications yet.</div>}
      </div>
    </div>
  );
}
