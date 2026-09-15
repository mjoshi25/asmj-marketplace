import { useEffect, useMemo, useState } from "react";
import { getConfiguredLocations } from "../api/rentalApi";

export default function LocationPicker({ value = {}, onChange, required = false, className = "" }) {
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    getConfiguredLocations().then(r => setLocations(r.data?.data || [])).catch(() => setLocations([])).finally(() => setLoading(false));
  }, []);
  const states = useMemo(() => [...new Map(locations.map(x => [x.stateCode || x.stateName, x])).values()].sort((a,b) => (a.stateName||"").localeCompare(b.stateName||"")), [locations]);
  const cities = useMemo(() => locations.filter(x => (x.stateCode || x.stateName) === (value.stateCode || value.state)), [locations, value.stateCode, value.state]);
  const selectedCity = locations.find(x => x.cityName === value.city && (x.stateCode || x.stateName) === (value.stateCode || value.state));
  const set = (key, val) => onChange?.({ ...value, [key]: val });
  const selectState = code => {
    const state = locations.find(x => (x.stateCode || x.stateName) === code);
    onChange?.({ ...value, stateCode: code, state: state?.stateName || "", city: "", pincode: "" });
  };
  const selectCity = city => {
    const item = locations.find(x => x.cityName === city && (x.stateCode || x.stateName) === (value.stateCode || value.state));
    onChange?.({ ...value, city, pincode: item?.pincode || "" });
  };
  return <div className={`grid gap-4 sm:grid-cols-3 ${className}`}>
    <label className="block"><span className="label">State{required ? " *" : ""}</span><select className="input" required={required} value={value.stateCode || value.state || ""} onChange={e => selectState(e.target.value)} disabled={loading}><option value="">Select state</option>{states.map(x => <option key={x.stateCode || x.stateName} value={x.stateCode || x.stateName}>{x.stateName}</option>)}</select></label>
    <label className="block"><span className="label">City{required ? " *" : ""}</span><select className="input" required={required} value={value.city || ""} onChange={e => selectCity(e.target.value)} disabled={loading || !cities.length}><option value="">Select city</option>{cities.map(x => <option key={x.id || x.cityName} value={x.cityName}>{x.cityName}</option>)}</select></label>
    <label className="block"><span className="label">PIN Code{required ? " *" : ""}</span><input className="input" required={required} inputMode="numeric" pattern="[0-9]{6}" maxLength={6} value={value.pincode || selectedCity?.pincode || ""} onChange={e => set("pincode", e.target.value.replace(/\D/g, "").slice(0, 6))} placeholder="6-digit PIN" /></label>
  </div>;
}
