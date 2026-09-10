import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { login as loginApi, register as registerApi, me as meApi } from "../api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem("asmj_user") || "null"); }
    catch { return null; }
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!localStorage.getItem("asmj_token")) return;
    meApi()
      .then(r => {
        const u = r.data?.data || r.data;
        if (u) {
          setUser(u);
          localStorage.setItem("asmj_user", JSON.stringify(u));
        }
      })
      .catch(() => {})
  }, []);

  const saveAuth = (data) => {
    const body = data?.data || data;
    const token = body?.token || body?.accessToken || data?.token || data?.accessToken;
    const u = body?.user || body?.userDetails || body;
    if (token) localStorage.setItem("asmj_token", token);
    if (u) {
      setUser(u);
      localStorage.setItem("asmj_user", JSON.stringify(u));
    }
    return u;
  };

  const login = async (payload) => {
    setLoading(true);
    try { return saveAuth((await loginApi(payload)).data); }
    finally { setLoading(false); }
  };

  const register = async (payload) => {
    setLoading(true);
    try { return saveAuth((await registerApi(payload)).data); }
    finally { setLoading(false); }
  };

  const logout = () => {
    localStorage.removeItem("asmj_token");
    localStorage.removeItem("asmj_user");
    setUser(null);
  };

  const value = useMemo(() => ({ user, loading, login, register, logout }), [user, loading]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
