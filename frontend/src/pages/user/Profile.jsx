import { useEffect, useState } from "react";
import { me as getMe } from "../../api/authApi";
import { updateProfile, changePassword, uploadProfilePhoto } from "../../api/userApi";
import { useAuth } from "../../context/AuthContext";
import { UserRound, Camera, ShieldCheck } from "lucide-react";

export default function Profile() {
  const { updateUser } = useAuth();
  const [form, setForm] = useState({ name: "", mobile: "", profileImage: "", email: "", roles: [] });
  const [password, setPassword] = useState({ currentPassword: "", newPassword: "" });
  const [photo, setPhoto] = useState(null);
  const [preview, setPreview] = useState("");
  const [imageVersion, setImageVersion] = useState(Date.now());
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const load = async () => {
    try {
      const response = await getMe();
      const user = response.data?.data || {};
      setForm({
        name: user.name || "",
        mobile: user.mobile || user.contactNumber || "",
        profileImage: user.profileImage || user.profileImageUrl || "",
        email: user.email || "",
        roles: user.roles || [],
      });
      updateUser({ profileImage: user.profileImage || user.profileImageUrl || "", name: user.name, mobile: user.mobile || user.contactNumber });
      setImageVersion(Date.now());
    } catch {
      setError("Could not load profile");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const choosePhoto = (event) => {
    const selected = event.target.files?.[0] || null;
    setPhoto(selected);
    setPreview(selected ? URL.createObjectURL(selected) : "");
  };

  const savePhoto = async () => {
    if (!photo) return;
    setMessage("");
    setError("");
    try {
      const response = await uploadProfilePhoto(photo);
      const user = response.data?.data || {};
      const image = user.profileImage || user.profileImageUrl || "";
      if (image) { setForm((current) => ({ ...current, profileImage: image })); updateUser({ profileImage: image, updatedAt: Date.now() }); }
      setPhoto(null);
      setPreview("");
      setImageVersion(Date.now());
      setMessage("Profile photo updated successfully");
      await load();
    } catch (err) {
      setError(err.response?.data?.message || "Could not upload photo");
    }
  };

  const save = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");
    try {
      await updateProfile({ name: form.name, mobile: form.mobile });
      setMessage("Profile settings updated");
      await load();
    } catch (err) {
      setError(err.response?.data?.message || "Could not update profile");
    }
  };

  const updatePassword = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");
    try {
      await changePassword(password);
      setPassword({ currentPassword: "", newPassword: "" });
      setMessage("Password changed successfully");
    } catch (err) {
      setError(err.response?.data?.message || "Could not change password");
    }
  };

  const imageUrl = form.profileImage ? `${form.profileImage}${form.profileImage.includes("?") ? "&" : "?"}v=${imageVersion}` : "";

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <p className="text-sm text-slate-400">ACCOUNT</p>
      <h1 className="text-3xl font-black">Profile &amp; Settings</h1>
      {message && <div className="my-4 rounded-xl bg-emerald-50 p-3 text-sm text-emerald-700">{message}</div>}
      {error && <div className="my-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}

      <div className="card mt-6 p-6">
        <div className="flex flex-col gap-5 sm:flex-row sm:items-center">
          <div className="relative h-24 w-24 shrink-0 overflow-hidden rounded-full bg-slate-100">
            {preview || imageUrl ? (
              <img src={preview || imageUrl} alt="Profile photo" className="h-full w-full object-cover" onError={(event) => { event.currentTarget.style.display = "none"; }} />
            ) : (
              <div className="grid h-full place-items-center text-slate-400"><UserRound size={32} /></div>
            )}
          </div>
          <div>
            <h2 className="font-bold">Profile photo</h2>
            <p className="mt-1 text-sm text-slate-500">Use a clear JPG, PNG or WebP image.</p>
            <div className="mt-3 flex flex-wrap items-center gap-2">
              <label className="btn-secondary inline-flex cursor-pointer items-center"><Camera size={16} className="mr-2" />Choose photo<input hidden type="file" accept="image/jpeg,image/png,image/webp" onChange={choosePhoto} /></label>
              {photo && <button type="button" className="btn-primary" onClick={savePhoto}>Upload photo</button>}
            </div>
          </div>
        </div>
      </div>

      <form onSubmit={save} className="card mt-6 space-y-4 p-6">
        <div className="flex items-center gap-2"><ShieldCheck size={18} /><h2 className="font-bold">Profile settings</h2></div>
        <input className="input bg-slate-50" value={form.email} readOnly />
        <input className="input" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="Full name" required />
        <input className="input" value={form.mobile} onChange={(event) => setForm({ ...form, mobile: event.target.value })} placeholder="Contact number" required />
        <p className="text-xs text-slate-400">Roles: {form.roles.join(", ")}</p>
        <button className="btn-primary" type="submit">Save settings</button>
      </form>

      <form onSubmit={updatePassword} className="card mt-6 space-y-4 p-6">
        <h2 className="font-bold">Change password</h2>
        <input type="password" className="input" value={password.currentPassword} onChange={(event) => setPassword({ ...password, currentPassword: event.target.value })} placeholder="Current password" required />
        <input type="password" className="input" value={password.newPassword} onChange={(event) => setPassword({ ...password, newPassword: event.target.value })} placeholder="New password" minLength="6" required />
        <button className="btn-secondary" type="submit">Change password</button>
      </form>
    </div>
  );
}
