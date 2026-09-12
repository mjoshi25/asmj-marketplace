import http from "./http";
export const createBooking = payload => http.post("/bookings", payload);
export const getMyBookings = () => http.get("/bookings/my");
export const getBooking = id => http.get(`/bookings/${id}`);
export const cancelBooking = id => http.put(`/bookings/my/${id}/cancel`);

export const getAdminBookings = () => http.get("/bookings/admin/all");
export const updateAdminBookingStatus = (id, payload) => http.put(`/bookings/admin/${id}/status`, payload);
