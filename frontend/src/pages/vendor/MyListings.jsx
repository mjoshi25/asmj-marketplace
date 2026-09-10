import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deletePost, submitPost } from "../../api/postApi";
import { getMyListings } from "../../api/vendorApi";

const STATUS_META = {
  DRAFT: ["Draft", "bg-slate-100 text-slate-700"],
  PENDING_APPROVAL: ["Pending approval", "bg-amber-100 text-amber-800"],
  APPROVED: ["Approved", "bg-emerald-100 text-emerald-800"],
  PUBLISHED: ["Approved", "bg-emerald-100 text-emerald-800"],
  REJECTED: ["Rejected", "bg-red-100 text-red-800"],
  SUSPENDED: ["Suspended", "bg-orange-100 text-orange-800"]
};

export default function MyListings() {
  const [posts,setPosts]=useState([]); const [loading,setLoading]=useState(true); const [busy,setBusy]=useState(""); const [error,setError]=useState("");
  const load=()=>{setLoading(true);getMyListings().then(r=>setPosts(r.data?.data||r.data?.content||r.data||[])).catch(e=>setError(e.response?.data?.message||"Could not load listings.")).finally(()=>setLoading(false));};
  useEffect(()=>{load();},[]);
  const status=p=>p.status||p.approvalStatus||"DRAFT";
  const submit=async p=>{if(!window.confirm("Submit this listing for admin approval?"))return;setBusy(p.id);setError("");try{await submitPost(p.id);load();}catch(e){setError(e.response?.data?.message||"Could not submit listing.");}finally{setBusy("");}};
  const remove=async p=>{if(!window.confirm(`Delete “${p.title}”?`))return;setBusy(`delete-${p.id}`);setError("");try{await deletePost(p.id);load();}catch(e){setError(e.response?.data?.message||"Could not delete listing.");}finally{setBusy("");}};
  return <div className="mx-auto max-w-7xl px-4 py-8"><div className="mb-6 flex items-center justify-between"><div><p className="text-sm text-slate-400">VENDOR</p><h1 className="text-3xl font-black">My listings</h1><p className="mt-1 text-sm text-slate-500">Save drafts, submit listings for approval, and resubmit rejected listings.</p></div><Link to="/vendor/listings/new" className="btn-primary">New listing</Link></div>
    {error&&<div className="mb-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}
    <div className="card overflow-hidden"><div className="hidden grid-cols-5 border-b border-slate-100 px-5 py-3 text-xs font-semibold uppercase tracking-wide text-slate-400 md:grid"><span>Title</span><span>Type</span><span>Status</span><span>Views</span><span>Actions</span></div>
      {loading?<div className="p-8 text-center text-sm text-slate-400">Loading listings…</div>:posts.map(p=>{const s=status(p);const [label,cls]=STATUS_META[s]||[s.replaceAll("_"," "),"bg-slate-100 text-slate-700"];const editable=["DRAFT","REJECTED","PUBLISHED","APPROVED"].includes(s);return <div key={p.id||p._id} className="grid gap-3 border-b border-slate-100 px-5 py-4 md:grid-cols-5 md:items-center"><div><span className="text-xs text-slate-400 md:hidden">Title</span><div className="font-semibold">{p.title}</div></div><div><span className="text-xs text-slate-400 md:hidden">Type</span><div>{p.type}</div></div><div><span className="text-xs text-slate-400 md:hidden">Status</span><span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${cls}`}>{label}</span></div><div><span className="text-xs text-slate-400 md:hidden">Views</span>{p.views||0}</div><div className="flex flex-wrap gap-2"><Link className="btn-secondary text-xs" to={`/listing/${p.id}`}>View</Link>{editable&&<Link className="btn-secondary text-xs" to={`/vendor/listings/edit/${p.id}`}>Edit</Link>}{(s==="DRAFT"||s==="REJECTED")&&<button className="btn-primary text-xs" disabled={busy===p.id} onClick={()=>submit(p)}>{busy===p.id?"Submitting…":s==="REJECTED"?"Resubmit":"Submit"}</button>}{s==="DRAFT"&&<button className="text-xs font-semibold text-red-600" disabled={busy===`delete-${p.id}`} onClick={()=>remove(p)}>{busy===`delete-${p.id}`?"Deleting…":"Delete"}</button>}</div></div>})}
      {!loading&&!posts.length&&<div className="p-10 text-center text-sm text-slate-400">No listings found. Create your first listing.</div>}
    </div>
    <div className="mt-4 rounded-xl bg-slate-50 p-4 text-xs text-slate-500"><b>Workflow:</b> Draft → Submit → Pending approval → Approved/Rejected. Rejected listings can be edited and resubmitted.</div>
  </div>;
}
