import http from "./http";
export const getCategories = () => http.get("/categories");
export const createCategory = payload => http.post("/categories", payload);
export const updateCategory = (id,payload) => http.put(`/categories/${id}`, payload);
export const deleteCategory = id => http.delete(`/categories/${id}`);
