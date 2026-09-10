import http from "./http";
export const getPosts = (params = {}) => http.get("/posts", { params });
export const getPost = (id) => http.get(`/posts/${id}`);
export const getPostPreview = (id) => http.get(`/posts/${id}/preview`);
export const createPost = (payload) => http.post("/posts", payload);
export const updatePost = (id, payload) => http.put(`/posts/${id}`, payload);
export const deletePost = (id) => http.delete(`/posts/${id}`);
export const submitPost = (id) => http.post(`/posts/${id}/submit`);
