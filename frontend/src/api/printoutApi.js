import http from "./http";
export const getPrintoutServices=()=>http.get("/printouts/services");
export const getMyPrintoutServices=()=>http.get("/printouts/services/mine");
export const savePrintoutService=(p,id)=>id?http.put(`/printouts/services/${id}`,p):http.post("/printouts/services",p);
export const deletePrintoutService=id=>http.delete(`/printouts/services/${id}`);
export const createPrintoutRequest=p=>http.post("/printouts/requests",p);
export const getMyPrintoutRequests=()=>http.get("/printouts/requests/my");
export const getVendorPrintoutRequests=()=>http.get("/printouts/requests/vendor");
export const updatePrintoutStatus=(id,p)=>http.put(`/printouts/requests/${id}/status`,p);
