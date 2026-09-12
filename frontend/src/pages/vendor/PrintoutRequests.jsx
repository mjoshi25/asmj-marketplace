import { useEffect, useState } from "react";
import { getVendorPrintoutRequests, updatePrintoutStatus } from "../../api/printoutApi";

const actions = {
  SUBMITTED: [["ACCEPTED", "Accept request", "btn-primary"], ["REJECTED", "Reject", "btn-secondary text-red-600"]],
  ACCEPTED: [["PRINTING", "Start printing", "btn-primary"]],
  PRINTING: [["READY_FOR_PICKUP", "Ready for pickup", "btn-primary"]],
  READY_FOR_PICKUP: [["COMPLETED", "Mark completed", "btn-primary"]],
  OUT_FOR_DELIVERY: [["COMPLETED", "Mark delivered", "btn-primary"]],
};

export default function PrintoutRequests() {
  const [data, setData] = useState([]);
  const [quotes, setQuotes] = useState({});
  const [remarks, setRemarks] = useState({});
  const [message, setMessage] = useState("");
  const load = () => getVendorPrintoutRequests().then((r) => setData(r.data?.data || []));
  useEffect(() => { load(); }, []);

  const update = async (item, status) => {
    try {
      await updatePrintoutStatus(item.id, {
        status,
        vendorRemarks: remarks[item.id] || "",
        quotedAmount: quotes[item.id] ? Number(quotes[item.id]) : null,
      });
      setMessage(`Request ${status.toLowerCase().replaceAll("_", " ")}.`);
      load();
    } catch (e) {
      setMessage(e.response?.data?.message || "Could not update request");
    }
  };

  return (
    <div className="page-shell vendor-bg">
      <p className="eyebrow">VENDOR WORKSPACE</p>
      <h1 className="page-title">Printout requests</h1>
      <p className="mt-2 text-sm text-slate-500">Review customer documents, send a final quote, and manage fulfilment.</p>
      {message && <div className="alert mt-4">{message}</div>}
      <div className="mt-6 space-y-5">
        {data.map((x) => (
          <div className="card p-5" key={x.id}>
            <div className="flex flex-wrap justify-between gap-3">
              <div>
                <h2 className="font-black">{x.requestNumber} · {x.documentName}</h2>
                <p className="mt-1 text-sm text-slate-500">{x.printMode} · {x.pageCount} pages · {x.copies} copies · {x.fulfilmentType}</p>
              </div>
              <span className="status-pill bg-indigo-100 text-indigo-700">{x.status}</span>
            </div>
            <div className="mt-4 grid gap-2 rounded-2xl bg-slate-50 p-4 text-sm sm:grid-cols-3">
              <span>Calculated amount: <b>₹{Number(x.totalAmount || 0).toLocaleString("en-IN")}</b></span>
              <span>Quoted amount: <b>₹{Number(x.quotedAmount || x.totalAmount || 0).toLocaleString("en-IN")}</b></span>
              <span>Payment: <b>{x.paymentStatus || "Not submitted"}</b></span>
            </div>
            {x.deliveryAddress && <p className="mt-3 text-sm text-slate-600">Delivery: {x.deliveryAddress}, {x.deliveryCity}, {x.deliveryState} - {x.deliveryPincode}</p>}
            <div className="mt-4 flex flex-wrap gap-2">
              {(x.files || []).map((f) => <a className="btn-secondary" href={f.url} target="_blank" rel="noreferrer" key={f.url}>View {f.fileName}</a>)}
            </div>
            {x.status !== "COMPLETED" && x.status !== "CANCELLED" && x.status !== "REJECTED" && (
              <div className="mt-5 grid gap-3 rounded-2xl border border-slate-100 p-4 sm:grid-cols-2">
                <label className="text-sm font-semibold">Final quote amount
                  <input className="input mt-1" type="number" min="0" step="0.01" placeholder={String(x.quotedAmount || x.totalAmount || 0)} value={quotes[x.id] ?? ""} onChange={(e) => setQuotes({ ...quotes, [x.id]: e.target.value })} />
                </label>
                <label className="text-sm font-semibold">Vendor remarks
                  <input className="input mt-1" placeholder="Expected completion time or notes" value={remarks[x.id] ?? ""} onChange={(e) => setRemarks({ ...remarks, [x.id]: e.target.value })} />
                </label>
              </div>
            )}
            <div className="mt-4 flex flex-wrap gap-2">
              {(actions[x.status] || []).map(([status, label, cls]) => <button className={cls} key={status} onClick={() => update(x, status)}>{label}</button>)}
              {x.status === "READY_FOR_PICKUP" && x.fulfilmentType === "HOME_DELIVERY" && <button className="btn-secondary" onClick={() => update(x, "OUT_FOR_DELIVERY")}>Out for delivery</button>}
            </div>
          </div>
        ))}
        {!data.length && <div className="card p-10 text-center text-slate-500">No printout requests received yet.</div>}
      </div>
    </div>
  );
}
