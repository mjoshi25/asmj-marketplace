import http from "./http";
export const updateProfile=p=>http.put("/users/me",p);
export const changePassword=p=>http.put("/users/me/password",p);
export const uploadProfilePhoto=file=>{const d=new FormData();d.append("file",file);return http.post("/users/me/photo",d,{headers:{"Content-Type":"multipart/form-data"}})};
