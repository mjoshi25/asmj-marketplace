import { useEffect, useMemo, useState } from "react";
import { getConfiguredLocations } from "../api/rentalApi";

// Offline fallback keeps the location form usable when the rental-location API
// has no configured records or is temporarily unavailable. PIN codes are not
// inferred from a city because one city can have many PIN codes.
const INDIA_LOCATIONS = [
  ["AN","Andaman and Nicobar Islands","Port Blair"],["AP","Andhra Pradesh","Visakhapatnam"],["AR","Arunachal Pradesh","Itanagar"],["AS","Assam","Guwahati"],["BR","Bihar","Patna"],["CH","Chandigarh","Chandigarh"],["CG","Chhattisgarh","Raipur"],["DN","Dadra and Nagar Haveli and Daman and Diu","Daman"],["DL","Delhi","New Delhi"],["GA","Goa","Panaji"],["GJ","Gujarat","Ahmedabad"],["HR","Haryana","Gurugram"],["HP","Himachal Pradesh","Shimla"],["JK","Jammu and Kashmir","Srinagar"],["JH","Jharkhand","Ranchi"],["KA","Karnataka","Bengaluru"],["KL","Kerala","Thiruvananthapuram"],["LA","Ladakh","Leh"],["LD","Lakshadweep","Kavaratti"],["MP","Madhya Pradesh","Bhopal"],["MH","Maharashtra","Mumbai"],["MN","Manipur","Imphal"],["ML","Meghalaya","Shillong"],["MZ","Mizoram","Aizawl"],["NL","Nagaland","Kohima"],["OD","Odisha","Bhubaneswar"],["PB","Punjab","Ludhiana"],["PY","Puducherry","Puducherry"],["RJ","Rajasthan","Jaipur"],["SK","Sikkim","Gangtok"],["TN","Tamil Nadu","Chennai"],["TS","Telangana","Hyderabad"],["TR","Tripura","Agartala"],["UP","Uttar Pradesh","Lucknow"],["UK","Uttarakhand","Dehradun"],["WB","West Bengal","Kolkata"]
].map(([stateCode,stateName,cityName]) => ({ stateCode,stateName,cityName,active:true }));
const EXTRA_CITIES = [
  ["KA","Mysuru"],["KA","Mangaluru"],["KA","Hubballi"],["KA","Belagavi"],
  ["OD","Bhawanipatna"],["OD","Kalahandi"],["OD","Cuttack"],["OD","Rourkela"],["OD","Sambalpur"],["OD","Berhampur"],
  ["AP","Vijayawada"],["AP","Tirupati"],["AS","Dibrugarh"],["BR","Gaya"],["GJ","Surat"],["GJ","Vadodara"],
  ["HR","Faridabad"],["HP","Dharamshala"],["JH","Jamshedpur"],["KL","Kochi"],["MP","Indore"],["MH","Pune"],["MH","Nagpur"],
  ["PB","Amritsar"],["RJ","Jodhpur"],["TN","Coimbatore"],["TN","Madurai"],["TS","Warangal"],["UP","Noida"],["UP","Varanasi"],["WB","Howrah"]
].map(([stateCode,cityName]) => ({ stateCode, cityName, stateName: INDIA_LOCATIONS.find(x=>x.stateCode===stateCode)?.stateName || "", active:true }));

export default function LocationPicker({ value = {}, onChange, required = false, className = "" }) {
  const [apiLocations, setApiLocations] = useState([]);
  useEffect(() => {
    let alive = true;
    getConfiguredLocations().then(r => { if (alive) setApiLocations((r.data?.data || []).filter(x => x.active !== false)); }).catch(() => { if (alive) setApiLocations([]); });
    return () => { alive = false; };
  }, []);

  const locations = useMemo(() => {
    const merged = new Map();
    [...INDIA_LOCATIONS, ...EXTRA_CITIES, ...apiLocations].forEach((x) => {
      if (!x?.stateName || !x?.cityName) return;
      const code = x.stateCode || x.stateName;
      merged.set(`${code.toLowerCase()}|${x.cityName.toLowerCase()}`, { ...x, stateCode: code });
    });
    return [...merged.values()];
  }, [apiLocations]);

  const states = useMemo(() => {
    const byCode = new Map();
    locations.forEach(x => { if (!byCode.has(x.stateCode)) byCode.set(x.stateCode, { stateCode:x.stateCode, stateName:x.stateName }); });
    // Keep legacy records selectable even when their code differs from a fallback code.
    return [...byCode.values()].sort((a,b) => a.stateName.localeCompare(b.stateName));
  }, [locations]);

  const selectedState = states.find(x => x.stateCode === value.stateCode || x.stateName === value.state || x.stateCode === value.state || x.stateName === value.stateCode);
  const stateKey = selectedState?.stateCode || value.stateCode || value.state || "";
  const cities = useMemo(() => locations.filter(x => x.stateCode === stateKey || x.stateName === selectedState?.stateName).sort((a,b)=>a.cityName.localeCompare(b.cityName)), [locations,stateKey,selectedState?.stateName]);
  const selectedCity = cities.find(x => x.cityName.toLowerCase() === String(value.city || "").toLowerCase());
  const isOtherCity = value.city === "__OTHER__" || (Boolean(value.city) && !selectedCity);

  const selectState = code => {
    const state = states.find(x => x.stateCode === code);
    onChange?.({ ...value, stateCode: code, state: state?.stateName || code, city: "", pincode: "" });
  };
  const selectCity = city => {
    if (city === "__OTHER__") { onChange?.({ ...value, city: "__OTHER__", pincode: "" }); return; }
    const item = cities.find(x => x.cityName === city);
    onChange?.({ ...value, city, pincode: item?.pincode ? String(item.pincode) : (value.pincode || "") });
  };
  const set = (key,val) => onChange?.({ ...value, [key]: val });

  return <div className={`grid gap-4 sm:grid-cols-3 ${className}`}>
    <label className="block"><span className="label">State{required ? " *" : ""}</span><select className="input" required={required} value={selectedState?.stateCode || ""} onChange={e => selectState(e.target.value)}><option value="">Select state</option>{states.map(x=><option key={x.stateCode} value={x.stateCode}>{x.stateName}</option>)}</select></label>
    <label className="block"><span className="label">City{required ? " *" : ""}</span><select className="input" required={required && !isOtherCity} value={selectedCity?.cityName || (isOtherCity ? "__OTHER__" : "")} onChange={e=>selectCity(e.target.value)} disabled={!stateKey}><option value="">Select city</option>{cities.map(x=><option key={`${x.stateCode}-${x.cityName}`} value={x.cityName}>{x.cityName}</option>)}<option value="__OTHER__">Other city / locality</option></select></label>
    <label className="block"><span className="label">PIN Code{required ? " *" : ""}</span><input className="input" required={required} inputMode="numeric" autoComplete="postal-code" pattern="[0-9]{6}" maxLength={6} value={value.pincode || selectedCity?.pincode || ""} onChange={e=>set("pincode",e.target.value.replace(/\D/g,"").slice(0,6))} placeholder="6-digit PIN" /></label>
    {isOtherCity && <label className="block sm:col-span-3"><span className="label">Enter city / locality{required ? " *" : ""}</span><input className="input" required={required} value={value.city === "__OTHER__" ? "" : (value.city || "")} onChange={e=>set("city",e.target.value)} placeholder="Enter your city or locality" /></label>}
  </div>;
}
