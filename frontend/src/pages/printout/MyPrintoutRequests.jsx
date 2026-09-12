import { useEffect, useState } from "react";
import { getMyPrintoutRequests, updatePrintoutStatus, uploadPrintoutPaymentProof } from "../../api/printoutApi";
import { uploadFile } from "../../api/uploadApi";

export default function MyPrintoutRequests() {
  const [data, setData] = useState([]);
  const [files, setFiles] = useState({});
  const [refs, setRefs] = useState({});
  const [message, setMessage] = useState("");
  const load = () => getMyPrintoutRequests().then((r) => setData(r.data?.data || []));
  useEffect(() => { load(); }, []);

  const cancel = async (id) => {
    if (!window.confirm("Cancel this printout request?")) return;
    try { await updatePrintoutStatus(id, { status: "CANCELLED", vendorRemarks: "Cancelled by customer" }); setMessage("Request cancelled."); load(); }
    catch (e) { setMessage(e.response?.data?.message || "Could not cancel request"); }
  };

  const submitProof = async (item) => {
    try {
      if (!files[item.id]) throw new Error("Select a payment screenshot first");
      const uploaded = await uploadFile(files[item.id], "payment");
      const f = uploaded.data?.data || {};
      await uploadPrintoutPaymentProof(item.id, { paymentProofUrl: f.secureUrl, paymentProofPublicId: f.publicId, paymentReference: refs[item.id] || "", amount: item.quotedAmount || item.totalAmount });
      setMessage("Payment proof submitted for review.");
      load();
    } catch (e) { setMessage(e.response?.data?.message || e.message || "Could not submit payment proof"); }
  };

  return (
    <div className="page-shell user-bg">
      <p className="eyebrow">MY REQUESTS</p>
      <h1 className="page-title">Printout requests</h1>
      {message && <div className="alert mt-4">{message}</div>}
      <div className="mt-6 space-y-5">
        {data.map((x) => (
          <div className="card p-5" key={x.id}>
            <div className="flex flex-wrap justify-between gap-3">
              <div><h2 className="font-black">{x.requestNumber}</h2><p className="mt-1 text-sm text-slate-500">{x.documentName} · {x.copies} copies · {x.fulfilmentType}</p></div>
              <span className="status-pill bg-indigo-100 text-indigo-700">{x.status}</span>
            </div>
            <div className="mt-4 grid gap-2 rounded-2xl bg-slate-50 p-4 text-sm sm:grid-cols-3"><span>Amount: <b>₹{Number(x.quotedAmount || x.totalAmount || 0).toLocaleString("en-IN")}</b></span><span>Payment: <b>{x.paymentStatus || "Not submitted"}</b></span><span>Vendor note: {x.vendorRemarks || "—"}</span></div>
            {x.status === "ACCEPTED" && x.paymentStatus !== "PENDING_REVIEW" && x.paymentStatus !== "VERIFIED" && (
              <div className="mt-4 rounded-2xl border border-amber-100 bg-amber-50 p-4"><p className="text-sm font-bold text-amber-800">Upload payment proof</p><div className="mt-3 grid gap-3 sm:grid-cols-2"><input className="input" type="file" accept="image/*,.pdf" onChange={(e) => setFiles({ ...files, [x.id]: e.target.files?.[0] })} /><input className="input" placeholder="UPI / UTR reference" value={refs[x.id] || ""} onChange={(e) => setRefs({ ...refs, [x.id]: e.target.value })} /></div><button className="btn-primary mt-3" onClick={() => submitProof(x)}>Submit payment proof</button></div>
            )}
            <div className="mt-4 flex flex-wrap gap-2">{(x.files || []).map((f) => <a className="btn-secondary" href={f.url} target="_blank" rel="noreferrer" key={f.url}>View {f.fileName}</a>)}{!["COMPLETED", "CANCELLED", "REJECTED"].includes(x.status) && <button className="btn-secondary text-red-600" onClick={() => cancel(x.id)}>Cancel request</button>}</div>
          </div>
        ))}
        {!data.length && <div className="card p-10 text-center text-slate-500">No printout requests yet.</div>}
      </div>
    </div>
  );
}
