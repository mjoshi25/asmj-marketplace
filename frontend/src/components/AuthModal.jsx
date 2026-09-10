import { useState } from "react";
import { X, Eye, EyeOff, ShieldCheck } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function AuthModal({ mode, onClose, onSwitch }) {
  const isRegister = mode === "register";
  const { login, register, loading } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    name: "", email: "", contactNumber: "", password: "", role: "USER"
  });

  const change = e => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async e => {
    e.preventDefault();
    setError("");
    try {
      const u = isRegister ? await register(form) : await login({ email: form.email, password: form.password });
      const roles = u?.roles || (u?.role ? [u.role] : []);
      onClose();
      navigate(roles.includes("ADMIN") ? "/admin" : roles.includes("VENDOR") ? "/vendor" : "/dashboard");
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || "Unable to complete the request.");
    }
  };

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/60 p-4">
      <div className="card max-h-[95vh] w-full max-w-md overflow-y-auto p-6">
        <div className="mb-5 flex items-start justify-between">
          <div>
            <div className="mb-2 inline-flex rounded-xl bg-slate-100 p-2"><ShieldCheck size={20} /></div>
            <h2 className="text-2xl font-bold">{isRegister ? "Create your account" : "Welcome back"}</h2>
            <p className="mt-1 text-sm text-slate-500">{isRegister ? "Join ASMJ and start discovering or publishing listings." : "Sign in to continue to ASMJ Marketplace."}</p>
          </div>
          <button onClick={onClose} className="rounded-lg p-2 hover:bg-slate-100"><X size={20} /></button>
        </div>

        {error && <div className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

        <form onSubmit={submit} className="space-y-4">
          {isRegister && (
            <>
              <div>
                <label className="label">Full name</label>
                <input required name="name" value={form.name} onChange={change} className="input" placeholder="Your name" />
              </div>
              <div>
                <label className="label">Contact number</label>
                <input required name="contactNumber" value={form.contactNumber} onChange={change} className="input" placeholder="9876543210" inputMode="tel" />
              </div>
              <div>
                <label className="label">Role</label>
                <select name="role" value={form.role} onChange={change} className="input">
                  <option value="USER">User</option>
                  <option value="VENDOR">Vendor</option>
                </select>
                <p className="mt-1 text-xs text-slate-400">Admin accounts should be created/managed securely by the backend.</p>
              </div>
            </>
          )}
          <div>
            <label className="label">Email</label>
            <input required type="email" name="email" value={form.email} onChange={change} className="input" placeholder="you@example.com" />
          </div>
          <div>
            <label className="label">Password</label>
            <div className="relative">
              <input required minLength={6} type={showPassword ? "text" : "password"} name="password" value={form.password} onChange={change} className="input pr-11" placeholder="••••••••" />
              <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-3 text-slate-400">
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>
          <button disabled={loading} className="btn-primary w-full disabled:opacity-60">
            {loading ? "Please wait..." : isRegister ? "Create account" : "Sign in"}
          </button>
        </form>

        <div className="mt-5 text-center text-sm text-slate-500">
          {isRegister ? "Already have an account?" : "Don't have an account?"}{" "}
          <button className="font-semibold text-slate-900 underline" onClick={() => onSwitch(isRegister ? "login" : "register")}>
            {isRegister ? "Login" : "Register"}
          </button>
        </div>
      </div>
    </div>
  );
}