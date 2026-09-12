import { useEffect, useState } from "react";
import { getMyPrintoutServices, savePrintoutService, deletePrintoutService } from "../../api/printoutApi";

const initial = {
  serviceName: "Printout Service",
  description: "",
  pickupAvailable: true,
  homeDeliveryAvailable: true,
  minimumHomeDeliveryAmount: 100,
  homeDeliveryChargePercent: 10,
  bwSingleRate: 2,
  bwDoubleRate: 3,
  colorSingleRate: 10,
  colorDoubleRate: 15,
  a3AdditionalCharge: 5,
  bindingCharge: 40,
  laminationCharge: 20,
};

const numericFields = [
  "minimumHomeDeliveryAmount",
  "homeDeliveryChargePercent",
  "bwSingleRate",
  "bwDoubleRate",
  "colorSingleRate",
  "colorDoubleRate",
  "a3AdditionalCharge",
  "bindingCharge",
  "laminationCharge",
];

export default function PrintoutPricing() {
  const [form, setForm] = useState(initial);
  const [serviceId, setServiceId] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [approvalStatus, setApprovalStatus] = useState("");

  useEffect(() => {
    let active = true;

    const loadPricing = async () => {
      try {
        setLoading(true);
        const response = await getMyPrintoutServices();
        const data = response.data?.data ?? response.data;
        const saved = Array.isArray(data) ? data[0] : data;
        if (active && saved) {
          setForm({ ...initial, ...saved });
          setServiceId(saved.id || "");
          setApprovalStatus(saved.approvalStatus || "");
        }
      } catch (err) {
        if (active) setError(err.response?.data?.message || "Could not load printout pricing");
      } finally {
        if (active) setLoading(false);
      }
    };

    loadPricing();
    return () => {
      active = false;
    };
  }, []);

  const change = (event) => {
    const { name, value, type, checked } = event.target;
    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const save = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");
    setSaving(true);

    try {
      const payload = { ...form };
      numericFields.forEach((field) => {
        payload[field] = Number(payload[field]);
      });

      if (payload.minimumHomeDeliveryAmount < 0 || payload.homeDeliveryChargePercent < 0) {
        throw new Error("Minimum order and delivery charge cannot be negative");
      }

      const response = await savePrintoutService(payload, serviceId || undefined);
      const saved = response.data?.data ?? response.data;
      if (saved?.id || saved?._id) setServiceId(saved.id || saved._id);
      setApprovalStatus(saved?.approvalStatus || "PENDING");
      if (saved) setForm((current) => ({ ...current, ...saved }));
      setMessage(response.data?.message || "Printout pricing submitted for admin approval");
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Could not save printout pricing");
    } finally {
      setSaving(false);
    }
  };

  const remove = async () => {
    if (!serviceId || !window.confirm("Delete this printout service?")) return;
    try { await deletePrintoutService(serviceId); setServiceId(""); setApprovalStatus(""); setForm(initial); setMessage("Printout service deleted"); }
    catch (err) { setError(err.response?.data?.message || "Could not delete printout service"); }
  };

  return (
    <div className="page-shell vendor-bg">
      <p className="eyebrow">VENDOR SETTINGS</p>
      <h1 className="page-title">Printout pricing</h1>
      <p className="mt-2 text-sm text-slate-500">Configure your printing rates and delivery rules. New or edited services are reviewed by Admin before becoming visible.</p>
      {approvalStatus && <div className="mt-4 rounded-2xl border border-indigo-100 bg-indigo-50 px-4 py-3 text-sm font-semibold text-indigo-700">Approval status: {approvalStatus}</div>}

      {message && <div className="alert mt-4">{message}</div>}
      {error && <div className="mt-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}

      <form onSubmit={save} className="card mt-6 grid gap-4 p-6 sm:grid-cols-2">
        <input className="input sm:col-span-2" name="serviceName" value={form.serviceName || ""} onChange={change} placeholder="Service name" required />
        <textarea className="input sm:col-span-2" name="description" value={form.description || ""} onChange={change} placeholder="Description" rows={3} />
        <label>BW single rate<input className="input" type="number" min="0" step="0.01" name="bwSingleRate" value={form.bwSingleRate ?? 0} onChange={change} /></label>
        <label>BW double rate<input className="input" type="number" min="0" step="0.01" name="bwDoubleRate" value={form.bwDoubleRate ?? 0} onChange={change} /></label>
        <label>Colour single rate<input className="input" type="number" min="0" step="0.01" name="colorSingleRate" value={form.colorSingleRate ?? 0} onChange={change} /></label>
        <label>Colour double rate<input className="input" type="number" min="0" step="0.01" name="colorDoubleRate" value={form.colorDoubleRate ?? 0} onChange={change} /></label>
        <label>Minimum home delivery order<input className="input" type="number" min="0" step="0.01" name="minimumHomeDeliveryAmount" value={form.minimumHomeDeliveryAmount ?? 100} onChange={change} /></label>
        <label>Home delivery charge %<input className="input" type="number" min="0" step="0.01" name="homeDeliveryChargePercent" value={form.homeDeliveryChargePercent ?? 10} onChange={change} /></label>
        <label>A3 extra/page<input className="input" type="number" min="0" step="0.01" name="a3AdditionalCharge" value={form.a3AdditionalCharge ?? 0} onChange={change} /></label>
        <label>Binding charge<input className="input" type="number" min="0" step="0.01" name="bindingCharge" value={form.bindingCharge ?? 0} onChange={change} /></label>
        <label>Lamination charge<input className="input" type="number" min="0" step="0.01" name="laminationCharge" value={form.laminationCharge ?? 0} onChange={change} /></label>
        <div className="flex items-center gap-2 self-end"><input id="pickupAvailable" type="checkbox" name="pickupAvailable" checked={!!form.pickupAvailable} onChange={change} /><label htmlFor="pickupAvailable">Pickup available</label></div>
        <div className="flex items-center gap-2"><input id="homeDeliveryAvailable" type="checkbox" name="homeDeliveryAvailable" checked={!!form.homeDeliveryAvailable} onChange={change} /><label htmlFor="homeDeliveryAvailable">Home delivery available</label></div>
        <div className="flex flex-wrap gap-2 sm:col-span-2"><button className="btn-primary" type="submit" disabled={saving || loading}>{saving ? "Saving..." : serviceId ? "Update pricing" : "Submit for approval"}</button>{serviceId && <button className="btn-secondary text-red-600" type="button" onClick={remove} disabled={saving}>Delete service</button>}</div>
      </form>
    </div>
  );
}
