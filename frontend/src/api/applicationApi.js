import http from "./http";
export const applyForJob = payload => http.post("/applications", payload);
export const getMyApplications = () => http.get("/applications/my");
export const getApplication = id => http.get(`/applications/${id}`);
export const getApplicationStatus = postId => http.get(`/applications/post/${postId}/status`);
