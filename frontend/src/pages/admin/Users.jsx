import { useEffect, useMemo, useState } from "react";
import { addUserRole, changeUserStatus, getAdminUsers, removeUserRole } from "../../api/adminApi";

const roles = ["USER", "VENDOR", "ADMIN"];

export default function Users() {
  const [users, setUsers] = useState([]);
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("ALL");
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    getAdminUsers().then(r => setUsers(r.data?.data || [])).catch(() => setUsers([])).finally(() => setLoading(false));
  };
  useEffect(() => { load(); }, []);

  const filtered = useMemo(() => users.filter(u => {
    const text = `${u.name || ""} ${u.email || ""} ${u.contactNumber || ""}`.toLowerCase();
    return text.includes(query.toLowerCase()) && (status === "ALL" || u.status === status);
  }), [users, query, status]);

  const updateRole = async (user, role, checked) => {
    try {
      if (checked) await addUserRole(user.id, role);
      else if ((user.roles || []).length > 1) await removeUserRole(user.id, role);
      load();
    } catch (e) { alert(e.response?.data?.message || "Unable to update role"); }
  };

  return <div className="mx-auto max-w-7xl px-4 py-8">
    <div className="flex flex-col justify-between gap-4 md:flex-row md:items-end">
      <div><p className="text-sm font-semibold uppercase tracking-widest text-indigo-500">Administration</p><h1 className="text-3xl font-black">User Management</h1><p className="mt-1 text-slate-500">Manage account status and access roles from one place.</p></div>
      <div className="rounded-2xl bg-indigo-50 px-4 py-3 text-sm font-semibold text-indigo-700">{filtered.length} visible users</div>
    </div>
    <div className="card mt-6 grid gap-3 p-4 md:grid-cols-[1fr_180px]">
      <input className="input" placeholder="Search name, email or contact number" value={query} onChange={e => setQuery(e.target.value)} />
      <select className="input" value={status} onChange={e => setStatus(e.target.value)}><option value="ALL">All statuses</option><option>ACTIVE</option><option>INACTIVE</option><option>SUSPENDED</option></select>
    </div>
    <div className="card mt-4 overflow-hidden">
      {loading && <div className="p-10 text-center text-slate-400">Loading users...</div>}
      {!loading && filtered.map(u => <div className="border-b border-slate-100 p-5 last:border-0" key={u.id}>
        <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-center">
          <div><div className="font-bold">{u.name || "Unnamed user"}</div><div className="text-sm text-slate-500">{u.email} {u.contactNumber && `• ${u.contactNumber}`}</div><div className="mt-2 text-xs text-slate-400">ID: {u.id}</div></div>
          <select className="input max-w-xs" value={u.status} onChange={async e => { await changeUserStatus(u.id, e.target.value); load(); }}><option>ACTIVE</option><option>INACTIVE</option><option>SUSPENDED</option></select>
        </div>
        <div className="mt-4 flex flex-wrap gap-4">{roles.map(role => <label className="flex items-center gap-2 text-sm" key={role}><input type="checkbox" checked={(u.roles || []).includes(role)} disabled={role === "ADMIN" && (u.roles || []).includes("ADMIN")} onChange={e => updateRole(u, role, e.target.checked)} />{role}</label>)}</div>
      </div>)}
      {!loading && !filtered.length && <div className="p-10 text-center text-slate-400">No users match your filters.</div>}
    </div>
  </div>;
}
