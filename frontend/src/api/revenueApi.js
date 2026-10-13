import http from "./http";
export const getCommissionRules=()=>http.get("/admin/revenue/rules");
export const saveCommissionRule=rule=>http.put("/admin/revenue/rules",rule);
export const previewPlatformFee=payload=>http.post("/admin/revenue/preview",payload);
export const getFeeLedger=()=>http.get("/admin/revenue/ledger");
export const getRevenueSummary=()=>http.get("/admin/revenue/summary");
