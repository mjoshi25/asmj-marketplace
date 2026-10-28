import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, Search, Store, MapPin, MessageCircle, ShieldCheck, Tag, Users, Megaphone, Sparkles, ShoppingBag, Wrench, GraduationCap, Utensils, HeartHandshake, LoaderCircle } from "lucide-react";
import AuthModal from "../components/AuthModal";
import Logo from "../components/Logo";
import Footer from "../components/Footer";
import PostCard from "../components/PostCard";
import { getPublicVendors } from "../api/vendorApi";
import { getPosts } from "../api/postApi";
import { getFeaturedPromotions, recordPromotionEvent } from "../api/localPromotionApi";

const categories = [
  { name: "Shops & Retail", icon: ShoppingBag },
  { name: "Food & Grocery", icon: Utensils },
  { name: "Home & Repairs", icon: Wrench },
  { name: "Education", icon: GraduationCap },
  { name: "Health & Wellness", icon: HeartHandshake },
  { name: "Other Local Services", icon: Store },
];
const read = (r) => r?.data?.data ?? r?.data?.content ?? r?.data ?? [];
const phoneDigits = (v) => String(v || "").replace(/\D/g, "");
const addressText = (v) => typeof v === "string" ? v : [v?.area, v?.city, v?.state, v?.pincode].filter(Boolean).join(", ");

export default function LocalNetwork() {
  const navigate = useNavigate();
  const [auth, setAuth] = useState(null);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("All businesses");
  const [shops, setShops] = useState([]);
  const [posts, setPosts] = useState([]);
  const [promotions, setPromotions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(false);

  useEffect(() => {
    document.title = "ASMJ Local Business Network | Bhawanipatna";
    let description = document.querySelector('meta[name="description"]');
    if (!description) { description = document.createElement("meta"); description.name = "description"; document.head.appendChild(description); }
    description.content = "Discover and connect with local shops and services in Bhawanipatna and Kalahandi through ASMJ Local Business Network.";
    let alive = true;
    Promise.allSettled([getPublicVendors(), getPosts({}), getFeaturedPromotions()]).then((results) => {
      if (!alive) return;
      const [vendorsResult, postsResult, promotionsResult] = results;
      if (vendorsResult.status === "fulfilled") setShops(read(vendorsResult.value).filter(v => v.verificationStatus === "APPROVED" && v.status !== "SUSPENDED"));
      else setLoadError(true);
      if (postsResult.status === "fulfilled") setPosts(read(postsResult.value).filter(p => p.status === "PUBLISHED"));
      if (promotionsResult.status === "fulfilled") setPromotions(read(promotionsResult.value));
      setLoading(false);
    });
    return () => { alive = false; };
  }, []);

  const visibleShops = useMemo(() => shops.filter((shop) => {
    const address = addressText(shop.address).toLowerCase();
    const isLocal = address.includes("bhawanipatna") || address.includes("kalahandi");
    const text = [shop.businessName, shop.businessType, shop.description, address].join(" ").toLowerCase();
    const matchesQuery = !query.trim() || text.includes(query.trim().toLowerCase());
    const matchesCategory = category === "All businesses" || text.includes(category.split(" ")[0].toLowerCase());
    return isLocal && matchesQuery && matchesCategory;
  }), [shops, query, category]);

  const localPosts = useMemo(() => posts.filter(p => {
    const location = p.location || {};
    const city = String(location.city || "").toLowerCase();
    const area = String(location.area || "").toLowerCase();
    const text = [p.title, p.description, p.type, location.city, location.area].join(" ").toLowerCase();
    return (city.includes("bhawanipatna") || area.includes("bhawanipatna")) && (!query.trim() || text.includes(query.trim().toLowerCase()));
  }).slice(0, 6), [posts, query]);

  const openVendorRegistration = () => setAuth({ mode: "register", initialRole: "VENDOR" });
  const whatsApp = (phone, name) => {
    const digits = phoneDigits(phone);
    if (!digits) return null;
    const normalized = digits.length === 10 ? `91${digits}` : digits;
    return `https://wa.me/${normalized}?text=${encodeURIComponent(`Hello ${name}, I found your business on ASMJ Local Business Network.`)}`;
  };

  return <div className="min-h-screen bg-[#f7f9fc] text-slate-900">
    <header className="border-b border-slate-200 bg-white/95">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6">
        <div className="flex items-center gap-3"><Logo /><div className="hidden border-l border-slate-200 pl-3 sm:block"><p className="font-bold leading-tight">Local Business Network</p><p className="text-xs text-slate-500">Bhawanipatna · Kalahandi</p></div></div>
        <div className="flex gap-2"><button onClick={() => setAuth({ mode: "login" })} className="rounded-xl px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">Login</button><button onClick={openVendorRegistration} className="rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-bold text-white shadow-sm hover:bg-indigo-700">List your shop</button></div>
      </div>
    </header>

    <section className="relative overflow-hidden bg-slate-950 text-white">
      <div className="absolute -left-24 top-8 h-72 w-72 rounded-full bg-indigo-500/25 blur-3xl"/><div className="absolute -right-16 bottom-0 h-80 w-80 rounded-full bg-emerald-400/10 blur-3xl"/>
      <div className="relative mx-auto grid max-w-7xl gap-10 px-4 py-14 sm:px-6 sm:py-20 lg:grid-cols-[1.2fr_.8fr] lg:items-center">
        <div><div className="mb-5 inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/5 px-3 py-1.5 text-xs font-semibold text-indigo-100"><Sparkles size={14}/> ASMJ LOCAL BUSINESS NETWORK</div>
          <h1 className="max-w-3xl text-4xl font-black leading-tight sm:text-6xl">Local shops.<br/><span className="text-emerald-300">Stronger businesses.</span></h1>
          <p className="mt-5 max-w-2xl text-base leading-7 text-slate-300 sm:text-lg">Discover neighbourhood businesses, compare local services and connect directly. A shared digital space to help Bhawanipatna's businesses reach more people.</p>
          <form onSubmit={e => { e.preventDefault(); document.getElementById("local-businesses")?.scrollIntoView({ behavior: "smooth" }); }} className="mt-8 flex max-w-2xl flex-col gap-2 rounded-2xl bg-white p-2 sm:flex-row"><div className="flex flex-1 items-center gap-2 px-3 text-slate-400"><Search size={19}/><input value={query} onChange={e=>setQuery(e.target.value)} className="w-full py-3 text-sm text-slate-900 outline-none" placeholder="Search shops, products or services" aria-label="Search local businesses"/></div><button className="flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700">Find local <ArrowRight size={16}/></button></form>
          <div className="mt-5 flex flex-wrap gap-x-5 gap-y-2 text-sm text-slate-300"><span className="flex items-center gap-2"><MapPin size={15}/> Bhawanipatna & Kalahandi</span><span className="flex items-center gap-2"><ShieldCheck size={15}/> Admin-reviewed businesses</span></div>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div className="rounded-3xl border border-white/10 bg-white/[0.07] p-5 sm:p-6"><Store className="mb-8 text-emerald-300" size={28}/><p className="text-3xl font-black">Local</p><p className="mt-1 text-sm text-slate-300">Businesses discoverable online</p></div>
          <div className="mt-8 rounded-3xl border border-white/10 bg-white/[0.07] p-5 sm:p-6"><Users className="mb-8 text-indigo-300" size={28}/><p className="text-3xl font-black">Together</p><p className="mt-1 text-sm text-slate-300">Community-led growth</p></div>
          <div className="rounded-3xl border border-white/10 bg-white/[0.07] p-5 sm:p-6"><MessageCircle className="mb-8 text-cyan-300" size={28}/><p className="text-xl font-black">Direct contact</p><p className="mt-1 text-sm text-slate-300">Enquiries without a complex checkout</p></div>
          <div className="mt-8 rounded-3xl border border-white/10 bg-white/[0.07] p-5 sm:p-6"><Megaphone className="mb-8 text-amber-300" size={28}/><p className="text-xl font-black">Local reach</p><p className="mt-1 text-sm text-slate-300">Offers and featured promotions</p></div>
        </div>
      </div>
    </section>

    <section className="mx-auto max-w-7xl px-4 py-12 sm:px-6"><div className="mb-6 flex flex-wrap items-end justify-between gap-3"><div><p className="text-xs font-extrabold tracking-[.18em] text-indigo-600">BROWSE BY NEED</p><h2 className="mt-2 text-2xl font-black sm:text-3xl">What are you looking for?</h2></div><span className="text-sm text-slate-500">Supporting local first</span></div>
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">{categories.map(({name,icon:Icon})=><button key={name} onClick={()=>{setCategory(category===name?"All businesses":name);document.getElementById("local-businesses")?.scrollIntoView({behavior:"smooth"});}} className={`group rounded-2xl border p-4 text-left transition hover:-translate-y-0.5 hover:shadow-md ${category===name?"border-indigo-300 bg-indigo-50":"border-slate-200 bg-white"}`}><span className="mb-4 inline-flex rounded-xl bg-slate-100 p-2.5 text-indigo-700 group-hover:bg-indigo-100"><Icon size={20}/></span><span className="block text-sm font-bold">{name}</span></button>)}</div>
    </section>

    {promotions.length > 0 && <section className="mx-auto max-w-7xl px-4 pb-10 sm:px-6"><div className="mb-5"><p className="text-xs font-extrabold tracking-[.18em] text-amber-600">COMMUNITY SPOTLIGHT</p><h2 className="mt-2 text-2xl font-black">Featured local promotions</h2></div><div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">{promotions.map(p=><article key={p.id} className="rounded-2xl border border-amber-200 bg-gradient-to-br from-amber-50 to-white p-5 shadow-sm"><div className="flex items-center justify-between gap-2"><span className="rounded-full bg-amber-100 px-2.5 py-1 text-xs font-extrabold text-amber-800">FEATURED</span><span className="text-xs text-slate-500">{p.durationDays} days</span></div><h3 className="mt-4 text-lg font-black">{p.businessName}</h3><p className="mt-1 text-sm text-slate-600">{p.packageName}</p><p className="mt-4 text-xs text-slate-500">Promotion verified by ASMJ</p><button onClick={()=>{recordPromotionEvent(p.id,"click").catch(()=>{});setAuth({mode:"login"});}} className="mt-4 w-full rounded-xl bg-slate-950 px-4 py-2.5 text-sm font-bold text-white hover:bg-slate-800">Connect with business</button></article>)}</div></section>}

    <section id="local-businesses" className="mx-auto max-w-7xl scroll-mt-6 px-4 pb-14 sm:px-6"><div className="mb-6 flex flex-wrap items-end justify-between gap-3"><div><p className="text-xs font-extrabold tracking-[.18em] text-indigo-600">DISCOVER LOCAL</p><h2 className="mt-2 text-2xl font-black sm:text-3xl">Businesses near you</h2><p className="mt-2 text-sm text-slate-500">Only approved business profiles are shown here.</p></div><button onClick={openVendorRegistration} className="flex items-center gap-2 rounded-xl border border-indigo-200 bg-white px-4 py-2.5 text-sm font-bold text-indigo-700 hover:bg-indigo-50"><Store size={16}/> Add my business</button></div>
      {loading ? <div className="flex items-center justify-center gap-2 rounded-2xl border border-slate-200 bg-white p-12 text-slate-500"><LoaderCircle className="animate-spin" size={18}/> Loading local businesses…</div> : visibleShops.length ? <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">{visibleShops.map(shop=>{const wa=whatsApp(shop.phone,shop.businessName);return <article key={shop.id} className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition hover:-translate-y-0.5 hover:shadow-lg"><div className="flex gap-4 p-5"><div className="grid h-14 w-14 shrink-0 place-items-center overflow-hidden rounded-2xl bg-indigo-50 text-indigo-700">{shop.logo?<img src={shop.logo} alt={`${shop.businessName} logo`} className="h-full w-full object-cover"/>:<Store size={25}/>}</div><div className="min-w-0 flex-1"><h3 className="truncate text-lg font-extrabold">{shop.businessName}</h3><p className="mt-0.5 text-sm font-medium text-indigo-700">{shop.businessType||"Local business"}</p><p className="mt-2 line-clamp-2 text-sm leading-6 text-slate-600">{shop.description||"Contact this business to learn more about its products and services."}</p>{addressText(shop.address)&&<p className="mt-3 flex items-start gap-1.5 text-xs text-slate-500"><MapPin size={14} className="mt-0.5 shrink-0"/>{addressText(shop.address)}</p>}</div></div><div className="flex gap-2 border-t border-slate-100 bg-slate-50 p-4">{wa?<a href={wa} target="_blank" rel="noreferrer" className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-emerald-600 px-3 py-2.5 text-sm font-bold text-white hover:bg-emerald-700"><MessageCircle size={16}/> WhatsApp</a>:<span className="flex-1 py-2 text-center text-xs text-slate-500">Contact details not provided</span>}<button onClick={()=>navigate("/discover")} className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm font-bold text-slate-700 hover:bg-slate-100">Listings</button></div></article>})}</div> : <div className="rounded-3xl border border-dashed border-slate-300 bg-white px-6 py-12 text-center"><div className="mx-auto mb-4 grid h-14 w-14 place-items-center rounded-2xl bg-indigo-50 text-indigo-700"><Store size={25}/></div><h3 className="text-xl font-black">{loadError?"Business directory is temporarily unavailable":"Let's put local businesses on the map"}</h3><p className="mx-auto mt-2 max-w-xl text-sm leading-6 text-slate-600">{loadError?"Please try again later. You can still register your business and our team can review it.":"There are no approved business profiles matching this search yet. Be one of the first local businesses to join the network."}</p><button onClick={openVendorRegistration} className="mt-5 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700">Register a business <ArrowRight className="ml-1 inline" size={16}/></button></div>}
    </section>

    <section className="mx-auto max-w-7xl px-4 pb-14 sm:px-6"><div className="mb-6"><p className="text-xs font-extrabold tracking-[.18em] text-emerald-700">LOCAL FINDS</p><h2 className="mt-2 text-2xl font-black sm:text-3xl">Products and services around Bhawanipatna</h2><p className="mt-2 text-sm text-slate-500">Published marketplace listings with a Bhawanipatna or Kalahandi location appear here.</p></div>{localPosts.length ? <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">{localPosts.map(p=><PostCard key={p.id || p._id} post={p}/>)}</div> : <div className="rounded-2xl border border-slate-200 bg-white p-6 text-sm leading-6 text-slate-600">No local product or service listings are published yet. Local businesses can register, wait for approval, and then add their products or services for review.</div>}</section>

    <section className="bg-indigo-700 text-white"><div className="mx-auto flex max-w-7xl flex-col gap-6 px-4 py-12 sm:px-6 md:flex-row md:items-center md:justify-between"><div><p className="text-xs font-extrabold tracking-[.18em] text-indigo-200">GROW WITH YOUR COMMUNITY</p><h2 className="mt-2 text-2xl font-black sm:text-3xl">Your shop deserves to be discovered.</h2><p className="mt-2 max-w-2xl text-sm leading-6 text-indigo-100">Create a business profile, share products and offers, and connect with customers. Registration is subject to admin review.</p></div><button onClick={openVendorRegistration} className="shrink-0 rounded-xl bg-white px-5 py-3 font-extrabold text-indigo-700 hover:bg-indigo-50">Join ASMJ Local <ArrowRight className="ml-2 inline" size={17}/></button></div></section>
    {auth && <AuthModal mode={auth.mode} initialRole={auth.initialRole} onClose={()=>setAuth(null)} onSwitch={mode=>setAuth({mode,initialRole:auth.initialRole})}/>}
    <Footer />
  </div>;
}
