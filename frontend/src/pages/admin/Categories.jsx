import { useEffect, useMemo, useState } from "react";
import { getCategories, createCategory, updateCategory, deleteCategory } from "../../api/categoryApi";

const slugify = value => value.trim().toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
const blank = { id: "", name: "", slug: "", description: "", parentId: "" };

export default function Categories() {
  const [items, setItems] = useState([]);
  const [form, setForm] = useState(blank);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [busy, setBusy] = useState(false);
  const [loading, setLoading] = useState(true);

  const parents = useMemo(() => items.filter(c => !c.parentId), [items]);
  const childrenOf = parentId => items.filter(c => c.parentId === parentId);

  async function load() {
    setLoading(true);
    try {
      const response = await getCategories();
      setItems(response.data?.data || []);
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Could not load categories. Please refresh and try again.");
    } finally { setLoading(false); }
  }
  useEffect(() => { load(); }, []);

  function startEdit(category) {
    setForm({ id: category.id, name: category.name || "", slug: category.slug || "", description: category.description || "", parentId: category.parentId || "" });
    setNotice(""); setError("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
  function resetForm() { setForm(blank); setError(""); setNotice(""); }
  async function save(e) {
    e.preventDefault();
    if (!form.name.trim()) { setError("Enter a category name."); return; }
    if (form.parentId && form.parentId === form.id) { setError("A category cannot be its own parent."); return; }
    setBusy(true); setError(""); setNotice("");
    const payload = { name: form.name.trim(), slug: (form.slug.trim() || slugify(form.name)), description: form.description.trim(), parentId: form.parentId || null, active: true, attributes: [] };
    try {
      if (form.id) await updateCategory(form.id, payload); else await createCategory(payload);
      setNotice(form.id ? "Category updated successfully." : (form.parentId ? "Subcategory created successfully." : "Main category created successfully."));
      setForm(blank); await load();
    } catch (e) { setError(e.response?.data?.message || "Could not save category. Check that the category name and slug are unique."); }
    finally { setBusy(false); }
  }
  async function remove(category) {
    if (childrenOf(category.id).length) { setError("Delete or move this category's subcategories before deleting the main category."); return; }
    if (!window.confirm(`Delete ${category.name}? This cannot be undone.`)) return;
    setError(""); setNotice("");
    try { await deleteCategory(category.id); if (form.id === category.id) resetForm(); setNotice(`${category.name} deleted.`); await load(); }
    catch (e) { setError(e.response?.data?.message || "Could not delete category."); }
  }

  return <div className="mx-auto max-w-6xl px-4 py-8">
    <p className="text-sm text-slate-400">ADMIN</p>
    <h1 className="text-3xl font-black">Categories &amp; subcategories</h1>
    <p className="mt-1 text-sm text-slate-500">Manage the marketplace category structure. Vendors will see subcategories as dropdowns when creating listings.</p>
    {(error || notice) && <div role="status" className={`mt-4 rounded-xl p-3 text-sm ${error ? "bg-red-50 text-red-700" : "bg-emerald-50 text-emerald-700"}`}>{error || notice}</div>}

    <form onSubmit={save} className="card mt-6 space-y-4 p-5">
      <div className="flex flex-wrap items-center justify-between gap-2"><h2 className="text-lg font-bold">{form.id ? "Edit category" : "Create category"}</h2>{form.id && <button type="button" className="btn-secondary" onClick={resetForm}>Cancel edit</button>}</div>
      <div className="grid gap-4 md:grid-cols-2">
        <div><label className="label">Category name *</label><input className="input" required value={form.name} onChange={e=>setForm(f=>({...f,name:e.target.value,slug:slugify(e.target.value)}))} placeholder="e.g. Books" /></div>
        <div><label className="label">URL slug</label><input className="input" value={form.slug} onChange={e=>setForm(f=>({...f,slug:slugify(e.target.value)}))} placeholder="e.g. books" /></div>
        <div><label className="label">Parent category</label><select className="input" value={form.parentId} onChange={e=>setForm(f=>({...f,parentId:e.target.value}))}><option value="">None — main category</option>{parents.filter(p=>p.id!==form.id).map(p=><option value={p.id} key={p.id}>{p.name}</option>)}</select><p className="mt-1 text-xs text-slate-500">Choose a parent to create a subcategory; leave blank for a main category.</p></div>
        <div><label className="label">Description (optional)</label><input className="input" value={form.description} onChange={e=>setForm(f=>({...f,description:e.target.value}))} placeholder="Short description" /></div>
      </div>
      <div className="flex flex-wrap gap-2"><button className="btn-primary" disabled={busy}>{busy ? "Saving…" : form.id ? "Save changes" : form.parentId ? "Create subcategory" : "Create main category"}</button>{!form.id && <button type="button" className="btn-secondary" onClick={resetForm}>Clear</button>}</div>
    </form>

    <div className="mt-6 flex items-center justify-between"><h2 className="text-xl font-bold">Existing categories</h2><button type="button" className="btn-secondary" onClick={load}>Refresh</button></div>
    {loading ? <p className="py-8 text-center text-slate-500">Loading categories…</p> : parents.length === 0 ? <div className="card mt-3 p-6 text-sm text-slate-500">No main categories found. Create one above.</div> : <div className="mt-3 space-y-3">{parents.map(parent=><section className="card p-4" key={parent.id}>
      <div className="flex flex-wrap items-center justify-between gap-3"><div><div className="font-bold">{parent.name}</div><div className="text-xs text-slate-400">{parent.slug}</div></div><div className="flex gap-2"><button type="button" className="btn-secondary" onClick={()=>startEdit(parent)}>Edit</button><button type="button" className="btn-secondary" onClick={()=>remove(parent)}>Delete</button></div></div>
      {childrenOf(parent.id).length>0 && <div className="mt-3 space-y-2 border-l-2 border-slate-200 pl-4">{childrenOf(parent.id).map(child=><div key={child.id} className="flex flex-wrap items-center justify-between gap-2 rounded-lg bg-slate-50 p-3"><div><div className="text-sm font-semibold">↳ {child.name}</div><div className="text-xs text-slate-400">{child.slug}</div></div><div className="flex gap-2"><button type="button" className="btn-secondary" onClick={()=>startEdit(child)}>Edit</button><button type="button" className="btn-secondary" onClick={()=>remove(child)}>Delete</button></div></div>)}</div>}
    </section>)}</div>}
  </div>;
}
