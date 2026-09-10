import http from "./http";

export const uploadFile = (file, purpose = "listing") => {
  const data = new FormData();
  data.append("file", file);
  data.append("purpose", purpose);
  return http.post("/uploads", data, { headers: { "Content-Type": "multipart/form-data" } });
};
