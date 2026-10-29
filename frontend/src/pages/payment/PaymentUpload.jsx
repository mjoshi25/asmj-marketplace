import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ReceiptIndianRupee, Upload, CheckCircle2, RefreshCw } from "lucide-react";
import { getPaymentReferences, uploadPaymentScreenshot, uploadPaymentProof } from "../../api/paymentApi";

const initialForm = () => ({ bookingId: "", bookingType: "", paymentType: "PAYMENT", amount: "", currency: "INR", paymentMethod: "UPI", paymentReference: "", paymentDate: new Date().toISOString().slice(0, 10), notes: "" });
const money = value => value == null || value === "" ? "" : `₹${Number(value).toLocaleString("en-IN", { maximumFractionDigits: 2 })}`;

export default function PaymentUpload() {
  const nav = useNavigate();
  const [f, setF] = useState(initialForm);
  const [file, setFile] = useState(null);
  const [busy, setBusy] = useState(false);
  const [loadingRefs, setLoadingRefs] = useState(true);
  const [refs, setRefs] = useState([]);
  const [msg, setMsg] = useState("");
  const [refError, setRefError] = useState("");

  const selectedKey = f.bookingType && f.bookingId ? `${f.bookingType}::${f.bookingId}` : "";
  const selected = useMemo(() => refs.find(x => `${x.bookingType}::${x.displayId}` === selectedKey), [refs, selectedKey]);
  const grouped = useMemo(() => refs.reduce((acc, item) => { (acc[item.bookingType] ||= []).push(item); return acc; }, {}), [refs]);

  const loadReferences = async () => {
    setLoadingRefs(true);
    setRefError("");
    try {
      const response = await getPaymentReferences();
      const items = response.data?.data || [];
      setRefs(items);
      if (!items.length) setRefError("No related orders or bookings were found for this account.");
    } catch (e) {
      setRefError(e.response?.data?.message || "Could not load your orders/bookings. Please refresh or contact admin.");
    } finally { setLoadingRefs(false); }
  };
  useEffect(() => { loadReferences(); }, []);

  const submit = async e => {
    e.preventDefault(); setBusy(true); setMsg("");
    try {
      if (!selected) throw new Error("Please select an order or booking from the list.");
      if (!file) throw new Error("Please select the payment screenshot or receipt.");
      const uploaded = await uploadPaymentScreenshot(file);
      const asset = uploaded.data?.data || {};
      await uploadPaymentProof({ ...f, amount: Number(f.amount), screenshotUrl: asset.secureUrl, screenshotPublicId: asset.publicId, orderId: selected.bookingType === "PRODUCT" ? selected.displayId : f.bookingId });
      setMsg("Payment/refund proof submitted for admin verification.");
      setF(initialForm()); setFile(null);
      await loadReferences();
    } catch (e) { setMsg(e.response?.data?.message || e.message || "Could not submit proof"); }
    finally { setBusy(false); }
  };

  const chooseReference = value => {
    if (!value) { setF(prev => ({ ...prev, bookingId: "", bookingType: "" })); return; }
    const split = value.indexOf("::");
    const type = value.slice(0, split), id = value.slice(split + 2);
    const item = refs.find(x => x.bookingType === type && x.displayId === id);
    setF(prev => ({ ...prev, bookingType: type, bookingId: id, amount: item?.amount != null ? String(item.amount) : prev.amount }));
  };

  return <div className="page-shell user-bg"><div className="mx-auto max-w-3xl">
    <p className="eyebrow">PAYMENTS · V27</p><h1 className="page-title">Upload payment / refund proof</h1>
    <p className="mt-2 text-sm text-slate-500">Choose the related order or booking from your account. Admins can select from all available orders and bookings.</p>
    {msg && <div className="mt-5 flex gap-2 rounded-xl bg-slate-900 p-4 text-sm text-white"><CheckCircle2 size={18}/>{msg}</div>}
    <form onSubmit={submit} className="card mt-6 p-5 sm:p-7">
      <div className="mb-5">
        <div className="mb-2 flex flex-wrap items-center justify-between gap-2"><label className="label">Related order / booking *</label><button type="button" className="btn-secondary" onClick={loadReferences} disabled={loadingRefs}><RefreshCw size={14}/>{loadingRefs ? "Loading…" : "Refresh list"}</button></div>
        <select className="input" value={selectedKey} onChange={e => chooseReference(e.target.value)} required disabled={loadingRefs || refs.length === 0}>
          <option value="">{loadingRefs ? "Loading your orders and bookings…" : refs.length ? "Select an order / booking ID" : "No orders/bookings available"}</option>
          {Object.entries(grouped).map(([type, items]) => <optgroup key={type} label={type === "PRODUCT" ? "Product orders" : `${type.charAt(0)}${type.slice(1).toLowerCase()} bookings / requests`}>
            {items.map(item => <option key={`${item.bookingType}::${item.displayId}`} value={`${item.bookingType}::${item.displayId}`}>{item.displayId} · {item.status || "Status unavailable"}{item.amount != null ? ` · ${money(item.amount)}` : ""}{item.customerName ? ` · ${item.customerName}` : ""}{item.vendorName ? ` · ${item.vendorName}` : ""}</option>)}
          </optgroup>)}
        </select>
        {refError && <p className="mt-2 text-xs text-amber-700">{refError}</p>}
        {selected && <p className="mt-2 text-xs text-slate-500">Selected: {selected.bookingType} · {selected.displayId}{selected.status ? ` · ${selected.status}` : ""}{selected.amount != null ? ` · Reference amount ${money(selected.amount)}` : ""}. Confirm the amount before submitting.</p>}
      </div>
      <div className="grid gap-5 sm:grid-cols-2">
        <div><label className="label">Transaction type *</label><select className="input" value={f.paymentType} onChange={e=>setF({...f,paymentType:e.target.value})}><option value="PAYMENT">Amount Paid</option><option value="REFUND">Amount Refunded</option></select></div>
        <div><label className="label">Amount *</label><input className="input" type="number" min="0.01" step="0.01" value={f.amount} onChange={e=>setF({...f,amount:e.target.value})} placeholder="0.00" required/></div>
        <div><label className="label">Payment method *</label><select className="input" value={f.paymentMethod} onChange={e=>setF({...f,paymentMethod:e.target.value})}><option>UPI</option><option>BANK_TRANSFER</option><option>CASH</option><option>CARD</option><option>OTHER</option></select></div>
        <div><label className="label">Transaction / UTR reference *</label><input className="input" value={f.paymentReference} onChange={e=>setF({...f,paymentReference:e.target.value})} placeholder="UPI UTR / bank reference" required/></div>
        <div><label className="label">Payment / refund date *</label><input className="input" type="date" value={f.paymentDate} onChange={e=>setF({...f,paymentDate:e.target.value})} required/></div>
        <div><label className="label">Currency</label><input className="input" value={f.currency} onChange={e=>setF({...f,currency:e.target.value.toUpperCase()})}/></div>
      </div>
      <div className="mt-5"><label className="label">Payment screenshot / receipt *</label><label className="flex cursor-pointer items-center gap-3 rounded-xl border-2 border-dashed border-slate-200 bg-slate-50 p-5 hover:bg-slate-100"><Upload size={22}/><div><div className="text-sm font-bold">Choose screenshot</div><div className="text-xs text-slate-400">PNG, JPG or PDF · proof for this transaction</div></div><input className="hidden" type="file" accept="image/*,.pdf" onChange={e=>setFile(e.target.files?.[0]||null)} required/></label>{file&&<p className="mt-2 text-xs font-semibold text-slate-600">Selected: {file.name}</p>}</div>
      <div className="mt-5"><label className="label">Notes</label><textarea className="input min-h-24" value={f.notes} onChange={e=>setF({...f,notes:e.target.value})} placeholder="Optional details"/></div>
      <div className="mt-6 flex flex-wrap gap-3"><button className="btn-primary" disabled={busy || loadingRefs || !refs.length}><ReceiptIndianRupee size={17}/>{busy?"Uploading…":"Submit payment / refund proof"}</button><button type="button" className="btn-secondary" onClick={()=>nav("/payments")}>View my payments</button></div>
    </form>
  </div></div>;
}
