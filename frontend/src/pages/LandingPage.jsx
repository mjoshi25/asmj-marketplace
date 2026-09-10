import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, Search, ShieldCheck, MessageCircle, BarChart3, Sparkles } from "lucide-react";
import { getPosts } from "../api/postApi";
import CategoryCard from "../components/CategoryCard";
import PostCard from "../components/PostCard";
import AuthModal from "../components/AuthModal";
import Logo from "../components/Logo";
import Footer from "../components/Footer";

const categories = [
  ["PRODUCT", "Products"], ["SERVICE", "Services"], ["EVENT", "Events"],
  ["INSURANCE", "Insurance"], ["JOB", "Jobs"], ["TUTOR", "Tutors"], ["RENTAL", "Rentals"]
];

export default function LandingPage() {
  const navigate = useNavigate();
  const [auth, setAuth] = useState(null);
  const [query, setQuery] = useState("");
  const [type, setType] = useState("");
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    getPosts({ type: type || undefined, q: query || undefined, size: 8 })
      .then(r => setPosts(r.data?.content || r.data?.data || r.data || []))
      .catch(() => setPosts([]));
  }, [type]);

  const search = async e => {
    e.preventDefault();
    navigate(`/discover?${new URLSearchParams({ ...(query ? {q: query} : {}), ...(type ? {type} : {}) }).toString()}`);
    try {
      const r = await getPosts({ q: query, type: type || undefined, size: 8 });
      setPosts(r.data?.content || r.data?.data || r.data || []);
    } catch { setPosts([]); }
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="absolute left-0 right-0 top-0 z-20">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-5">
          <Logo />
          <div className="flex gap-2">
            <button className="btn-secondary" onClick={() => setAuth("login")}>Login</button>
            <button className="btn-primary" onClick={() => setAuth("register")}>Register</button>
          </div>
        </div>
      </header>

      <section className="relative overflow-hidden bg-slate-950 text-white">
        <div className="absolute -left-24 top-32 h-72 w-72 rounded-full bg-indigo-500/20 blur-3xl" />
        <div className="absolute -right-20 bottom-0 h-96 w-96 rounded-full bg-cyan-500/10 blur-3xl" />
        <div className="mx-auto max-w-7xl px-4 pb-20 pt-36">
          <div className="max-w-3xl">
            <div className="mb-5 inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 text-xs font-semibold">
              <Sparkles size={14} /> One marketplace. Everything you need.
            </div>
            <h1 className="text-4xl font-black leading-tight sm:text-6xl">
              Find, connect and grow with <span className="text-slate-300">ASMJ.</span>
            </h1>
            <p className="mt-5 max-w-2xl text-base leading-7 text-slate-300 sm:text-lg">
              Discover products, services, events, insurance, jobs and tutors in one trusted marketplace.
            </p>
          </div>

          <form onSubmit={search} className="mt-9 flex max-w-4xl flex-col gap-2 rounded-2xl bg-white p-2 sm:flex-row">
            <div className="flex flex-1 items-center gap-2 px-3 text-slate-400">
              <Search size={20} />
              <input value={query} onChange={e => setQuery(e.target.value)} className="w-full py-3 text-sm text-slate-800" placeholder="Search products, jobs, tutors, services..." />
            </div>
            <select value={type} onChange={e => setType(e.target.value)} className="rounded-xl bg-slate-100 px-3 py-3 text-sm font-medium text-slate-700">
              <option value="">All categories</option>
              {categories.map(([v,t]) => <option key={v} value={v}>{t}</option>)}
            </select>
            <button className="btn-primary flex items-center justify-center gap-2 px-6">Search <ArrowRight size={16} /></button>
          </form>

          <div className="mt-10 grid max-w-3xl grid-cols-1 gap-3 sm:grid-cols-3">
            {[
              [ShieldCheck, "Admin approved listings"],
              [MessageCircle, "Direct vendor chat"],
              [BarChart3, "Powerful vendor analytics"]
            ].map(([Icon, text]) => <div key={text} className="flex items-center gap-3 rounded-xl border border-white/10 bg-white/5 p-3 text-sm text-slate-300"><Icon size={18}/>{text}</div>)}
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-4 py-12">
        <div className="mb-6 flex items-end justify-between">
          <div><p className="text-sm font-semibold text-slate-400">EXPLORE</p><h2 className="text-3xl font-black">Everything in one place</h2></div>
        </div>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">
          {categories.map(([v,t]) => <CategoryCard key={v} type={v} title={t} onClick={() => navigate(`/discover?type=${v}`)} />)}
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-4 pb-16">
        <div className="mb-6"><p className="text-sm font-semibold text-slate-400">DISCOVER</p><h2 className="text-3xl font-black">Latest listings</h2></div>
        {posts.length ? <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">{posts.map(p => <PostCard key={p.id || p._id} post={p} />)}</div> :
          <div className="card p-10 text-center text-slate-500">No published listings found yet. Vendors can register and start posting.</div>}
      </section>

      {auth && <AuthModal mode={auth} onClose={() => setAuth(null)} onSwitch={setAuth} />}
      <Footer />
    </div>
  );
}