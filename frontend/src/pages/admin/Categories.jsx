import { useEffect, useMemo, useState } from "react";
import { getCategories, createCategory, deleteCategory } from "../../api/categoryApi";

export default function Categories() {
  const [items, setItems] = useState([]);
  const [name, setName] = useState("");
  const [parentId, setParentId] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const load = () => getCategories().then(r => setItems(r.data?.data || [])).catch(() => setItems([]));
  useEffect(() => { load(); }, []);
  const roots = useMemo(() => items.filter(c => !c.parentId), [items]);
  const childrenOf = id => items.filter(c => c.parentId === id);
  const add = async e => {
    e.preventDefault();
    if (!name.trim()) return;
    setBusy(true); setError("");
    try {
      await createCategory({ name: name.trim(), slug: name.trim().toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, ""), parentId: parentId || null, active: true, attributes: [] });
      setName(""); await load();
    } catch (e) { setError(e.response?.data?.message || "Could not create category."); }
    finally { setBusy(false); }
  };
  const remove = async c => {
    if (childrenOf(c.id).length) { setError("Delete or move its subcategories before deleting this category."); return; }
    try { await deleteCategory(c.id); await load(); } catch (e) { setError(e.response?.data?.message || "Could not delete category."); }
  };
  return <div className="mx-auto max-w-6xl px-4 py-8">
    <p className="text-sm text-slate-400">ADMIN</p><h1 className="text-3xl font-black">Categories & subcategories</h1>
    <p className="mt-2 text-sm text-slate-500">Create main marketplace categories and their subcategories. Vendors will see subcategories as dropdown options while creating listings.</p>
    {error && <div className="mt-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}
    <form onSubmit={add} className="card mt-6 grid gap-3 p-4 sm:grid-cols-[1fr_1fr_auto]">
      <input className="input" value={name} onChange={e => setName(e.target.value)} placeholder="Category or subcategory name" required />
      <select className="input" value={parentId} onChange={e => setParentId(e.target.value)}><option value="">Main category</option>{roots.map(c => <option key={c.id} value={c.id}>Subcategory of: {c.name}</option>)}</select>
      <button className="btn-primary" disabled={busy}>{busy ? "Adding…" : "Add category"}</button>
    </form>
    <div className="mt-5 space-y-3">{roots.map(c => <div className="card p-4" key={c.id}>
      <div className="flex items-center justify-between gap-3"><div><b>{c.name}</b><span className="ml-3 text-xs text-slate-400">{c.slug}</span><span className="ml-2 text-xs text-slate-400">Main category</span></div><button className="btn-secondary" onClick={() => remove(c)}>Delete</button></div>
      {childrenOf(c.id).length > 0 && <div className="mt-3 space-y-2 border-l-2 border-slate-100 pl-4">{childrenOf(c.id).map(sub => <div className="flex items-center justify-between gap-3 rounded-lg bg-slate-50 p-3" key={sub.id}><div><span className="font-medium">{sub.name}</span><span className="ml-3 text-xs text-slate-400">{sub.slug}</span></div><button className="btn-secondary" onClick={() => remove(sub)}>Delete</button></div>)}</div>}
      {childrenOf(c.id).length === 0 && <p className="mt-2 text-xs text-slate-400">No subcategories yet.</p>}
    </div>)}</div>
  </div>;
}
