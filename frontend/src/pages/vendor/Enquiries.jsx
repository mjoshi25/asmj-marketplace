import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getVendorEnquiries } from "../../api/vendorApi";
import { getPostPreview } from "../../api/postApi";
import { replyToEnquiry, startConversation } from "../../api/interactionApi";
import { MessageCircle, ExternalLink, X, Image as ImageIcon, Clock3, Send, UserRound, Phone, Mail } from "lucide-react";

export default function Enquiries(){
 const nav=useNavigate();
 const [items,setItems]=useState([]),[selected,setSelected]=useState(null),[loading,setLoading]=useState(true),[notice,setNotice]=useState(""),[reply,setReply]=useState(""),[sending,setSending]=useState(false);
 const load=async()=>{
   setLoading(true);setNotice("");
   try{
     const r=await getVendorEnquiries();
     const list=r.data?.data||[];
     const unique=[...new Map(list.filter(e=>e?.id).map(e=>[e.id,e])).values()];
     const enriched=await Promise.all(unique.map(async e=>{
       try{const p=await getPostPreview(e.postId);return {...e,post:p.data?.data||p.data};}
       catch{return {...e,post:{title:e.listingTitle,type:e.listingType,images:e.listingImage?[e.listingImage]:[]}};}
     }));
     setItems(enriched);
   }catch(e){setItems([]);setNotice(e.response?.data?.message||"Unable to load customer enquiries.");}
   finally{setLoading(false);}
 };
 useEffect(()=>{load()},[]);
 const openChat=async e=>{
   try{const r=await startConversation(e.postId);setSelected(null);setReply("");nav("/vendor/messages",{state:{conversationId:(r.data?.data||r.data)?.id}});}
   catch(err){setNotice(err.response?.data?.message||"Could not open chat.");}
 };
 const sendReply=async()=>{
   if(!selected||!reply.trim()||sending)return;
   setSending(true);setNotice("");
   try{
     const r=await replyToEnquiry(selected.id,reply.trim());
     const cid=(r.data?.data||r.data)?.id;
     setItems(v=>v.map(x=>x.id===selected.id?{...x,status:"RESPONDED"}:x));
     setSelected(null);setReply("");
     if(cid) nav("/vendor/messages",{state:{conversationId:cid}});
   }catch(e){setNotice(e.response?.data?.message||"Reply could not be sent.");}
   finally{setSending(false);}
 };
 const count=useMemo(()=>items.length,[items]);
 return <div className="page-shell vendor-bg">
   <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
    <div><p className="eyebrow">VENDOR</p><h1 className="page-title">Customer enquiries</h1><p className="mt-2 max-w-2xl text-sm text-slate-500">Every enquiry is linked to the exact listing. Read the customer's message, reply directly, or continue in chat.</p></div>
    <div className="rounded-2xl bg-slate-100 px-4 py-3 text-sm font-bold text-slate-700">{count} {count===1?"enquiry":"enquiries"}</div>
   </div>
   {notice&&<div className="mt-5 rounded-2xl border border-red-100 bg-red-50 px-4 py-3 text-sm font-semibold text-red-700">{notice}</div>}
   <div className="mt-6 space-y-4">
    {loading&&<div className="card p-10 text-center text-slate-400">Loading enquiries...</div>}
    {!loading&&!items.length&&<div className="card p-12 text-center"><div className="mx-auto grid h-14 w-14 place-items-center rounded-2xl bg-slate-100 text-slate-400"><MessageCircle/></div><h2 className="mt-4 font-black">No enquiries yet</h2><p className="mt-1 text-sm text-slate-500">Customer enquiries will appear here with their listing details.</p></div>}
    {!loading&&items.map(e=>{const p=e.post||{};const image=p.images?.[0]||e.listingImage;return <div className="card overflow-hidden" key={e.id}>
      <div className="flex flex-col gap-4 p-4 sm:flex-row sm:p-5">
       <div className="h-28 w-full shrink-0 overflow-hidden rounded-2xl bg-slate-100 sm:h-24 sm:w-32">{image?<img src={image} alt={p.title||e.listingTitle||"Listing"} className="h-full w-full object-cover"/>:<div className="grid h-full place-items-center text-slate-300"><ImageIcon/></div>}</div>
       <div className="min-w-0 flex-1"><div className="flex flex-wrap items-start justify-between gap-3"><div><p className="text-xs font-bold uppercase tracking-wider text-slate-400">{p.type||e.type||e.listingType||"ENQUIRY"}</p><h2 className="mt-1 text-lg font-black text-slate-950">{p.title||e.listingTitle||`Listing #${e.postId}`}</h2><p className="mt-1 text-xs text-slate-400">Customer: {e.customerName||"Marketplace user"}</p></div><span className={`status-pill ${e.status==="RESPONDED"?"bg-indigo-50 text-indigo-700":"bg-emerald-50 text-emerald-700"}`}>{e.status||"OPEN"}</span></div><div className="mt-3 rounded-2xl bg-slate-50 p-3.5"><p className="text-sm leading-6 text-slate-700">{e.message}</p></div><div className="mt-4 flex flex-wrap gap-2"><button className="btn-primary" onClick={()=>{setSelected(e);setReply("")}}><MessageCircle size={16}/> Open & reply</button><button className="btn-secondary" onClick={()=>openChat(e)}><MessageCircle size={16}/> Chat customer</button><Link className="btn-secondary" to={`/listing/${e.postId}`}><ExternalLink size={16}/> View listing</Link></div></div>
      </div>
    </div>})}
   </div>
   {selected&&<div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/60 p-4"><div className="card max-h-[92vh] w-full max-w-2xl overflow-auto p-5 sm:p-7">
     <div className="flex items-start justify-between gap-4"><div><p className="eyebrow">CUSTOMER ENQUIRY</p><h2 className="mt-1 text-2xl font-black">{selected.post?.title||selected.listingTitle||`Listing #${selected.postId}`}</h2><p className="mt-1 text-xs text-slate-400">{selected.post?.type||selected.type||selected.listingType}</p></div><button className="icon-btn" onClick={()=>setSelected(null)}><X size={18}/></button></div>
     <div className="mt-5 grid gap-5 sm:grid-cols-[180px_1fr]">{selected.post?.images?.[0]||selected.listingImage?<img src={selected.post?.images?.[0]||selected.listingImage} alt={selected.post?.title||selected.listingTitle||"Listing"} className="h-40 w-full rounded-2xl object-cover sm:h-36"/>:<div className="grid h-40 place-items-center rounded-2xl bg-slate-100 text-slate-300"><ImageIcon/></div>}<div>
       <p className="text-xs font-bold uppercase tracking-wider text-slate-400">Customer</p><p className="mt-1 font-black">{selected.customerName||"Marketplace user"}</p>{selected.customerMobile&&<p className="mt-2 flex items-center gap-2 text-sm text-slate-500"><Phone size={14}/>{selected.customerMobile}</p>}{selected.customerEmail&&<p className="mt-1 flex items-center gap-2 text-sm text-slate-500"><Mail size={14}/>{selected.customerEmail}</p>}
       <p className="mt-4 flex items-center gap-2 text-xs text-slate-400"><Clock3 size={13}/> {selected.status||"OPEN"}</p>
     </div></div>
     <div className="mt-5 rounded-2xl border border-slate-200 bg-white p-4"><p className="text-xs font-bold uppercase tracking-wider text-slate-400">Customer message</p><p className="mt-2 text-sm leading-6 text-slate-700">{selected.message}</p></div>
     <div className="mt-5"><label className="label">Your reply<textarea className="input mt-1 min-h-28" rows="4" value={reply} onChange={e=>setReply(e.target.value)} placeholder="Write a helpful reply to the customer..."/></label></div>
     <div className="mt-5 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end"><button className="btn-secondary" onClick={()=>setSelected(null)}>Close</button><button className="btn-secondary" onClick={()=>openChat(selected)}><MessageCircle size={16}/> Open chat</button><button className="btn-primary" disabled={sending||!reply.trim()} onClick={sendReply}><Send size={16}/>{sending?"Sending…":"Send reply & chat"}</button></div>
   </div></div>}
 </div>
}
