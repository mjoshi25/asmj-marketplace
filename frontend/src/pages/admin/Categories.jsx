import {useEffect,useMemo,useState} from "react";
import {getCategories,createCategory,deleteCategory} from "../../api/categoryApi";

const slugify = value => value.trim().toLowerCase().replace(/[^a-z0-9]+/g,"-").replace(/^-|-$/g,"");
export default function Categories(){
 const [items,setItems]=useState([]),[name,setName]=useState(""),[parentId,setParentId]=useState(""),[error,setError]=useState(""),[busy,setBusy]=useState(false);
 const load=()=>getCategories().then(r=>setItems(r.data?.data||[])).catch(e=>setError(e.response?.data?.message||"Could not load categories."));
 useEffect(()=>{load()},[]);
 const parents=useMemo(()=>items.filter(x=>!x.parentId),[items]);
 const children=useMemo(()=>items.filter(x=>x.parentId),[items]);
 async function add(e,isChild){e.preventDefault();if(!name.trim())return;setBusy(true);setError("");try{await createCategory({name:name.trim(),slug:slugify(name),parentId:isChild?parentId:null,active:true,attributes:[]});setName("");if(isChild)setParentId("");await load()}catch(err){setError(err.response?.data?.message||"Could not create category.")}finally{setBusy(false)}}
 async function remove(c){if(children.some(x=>x.parentId===c.id)){setError("Remove its subcategories first.");return}if(!window.confirm(`Delete ${c.name}?`))return;try{await deleteCategory(c.id);await load()}catch(e){setError(e.response?.data?.message||"Could not delete category.")}}
 return <div className="mx-auto max-w-6xl px-4 py-8"><p className="text-sm text-slate-400">ADMIN</p><h1 className="text-3xl font-black">Categories & subcategories</h1><p className="mt-2 text-sm text-slate-600">Create main marketplace categories first, then add subcategories under each one. Vendors will see these as dropdown choices when creating listings.</p>
 {error&&<div className="mt-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}
 <div className="mt-6 grid gap-5 md:grid-cols-2">
  <section className="card p-5"><h2 className="text-lg font-bold">1. Create main category</h2><form onSubmit={e=>add(e,false)} className="mt-4 flex flex-col gap-3"><label className="text-sm font-medium">Category name<input className="input mt-1" value={name} onChange={e=>setName(e.target.value)} placeholder="e.g. Products" required/></label><button className="btn-primary" disabled={busy}>Add main category</button></form></section>
  <section className="card p-5"><h2 className="text-lg font-bold">2. Create subcategory</h2><form onSubmit={e=>add(e,true)} className="mt-4 flex flex-col gap-3"><label className="text-sm font-medium">Main category<select className="input mt-1" value={parentId} onChange={e=>setParentId(e.target.value)} required><option value="">Select main category</option>{parents.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></label><label className="text-sm font-medium">Subcategory name<input className="input mt-1" value={name} onChange={e=>setName(e.target.value)} placeholder="e.g. Books" required/></label><button className="btn-primary" disabled={busy||!parentId}>Add subcategory</button></form></section>
 </div>
 <div className="mt-7 space-y-3"><h2 className="text-xl font-bold">Existing categories</h2>{parents.map(c=><section className="card p-4" key={c.id}><div className="flex items-center justify-between gap-3"><div><b>{c.name}</b><span className="ml-2 text-xs text-slate-400">Main category</span></div><button className="btn-secondary" onClick={()=>remove(c)}>Delete</button></div><div className="mt-3 space-y-2 border-l-2 border-slate-200 pl-4">{children.filter(x=>x.parentId===c.id).map(x=><div className="flex items-center justify-between gap-3" key={x.id}><span>{x.name}<span className="ml-2 text-xs text-slate-400">{x.slug}</span></span><button className="btn-secondary" onClick={()=>remove(x)}>Delete</button></div>)}{!children.some(x=>x.parentId===c.id)&&<p className="text-sm text-slate-400">No subcategories yet.</p>}</div></section>)}{!parents.length&&<p className="text-sm text-slate-500">No categories created yet.</p>}</div>
 </div>
}
