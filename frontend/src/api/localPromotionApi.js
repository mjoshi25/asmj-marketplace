import http from "./http";
export const getFeaturedPromotions = () => http.get("/local-promotions/featured");
export const getMyPromotions = () => http.get("/local-promotions/mine");
export const createPromotion = (payload) => http.post("/local-promotions", payload);
export const getAdminPromotions = () => http.get("/local-promotions/admin");
export const reviewPromotion = (id, approved, note = "") => http.post(`/local-promotions/admin/${id}/review`, { approved, note });
export const recordPromotionEvent = (id, type) => http.post(`/local-promotions/${id}/event`, null, { params: { type } });
