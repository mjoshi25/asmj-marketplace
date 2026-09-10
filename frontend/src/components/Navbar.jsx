import {Link,useNavigate} from "react-router-dom";
import {LogOut,LayoutDashboard,PlusCircle,Search,Heart,MessageCircle,Bell,ShieldCheck,UserRound,Menu,X,ClipboardList} from "lucide-react";
import {useState} from "react";
import Logo from "./Logo";
import {useAuth} from "../context/AuthContext";

export default function Navbar(){
 const {user,logout}=useAuth(); const nav=useNavigate(); const [open,setOpen]=useState(false);
 const roles=user?.roles||[]; const admin=roles.includes("ADMIN"),vendor=roles.includes("VENDOR"),driver=roles.includes("DRIVER");
 const home=admin?"/admin":driver?"/driver":vendor?"/vendor":"/dashboard";
 const links=admin?[["/admin","Dashboard"],["/admin/products","Products"],["/admin/users","Users"],["/admin/vendors","Vendors"],["/admin/approvals","Approvals"],["/admin/categories","Categories"],["/admin/analytics","Analytics"],["/admin/rentals","Rental control"],["/admin/events","Events"],["/admin/insurance","Insurance"],["/admin/payments","Payments"]]
   :driver?[["/driver","Trips"]]
  :vendor?[["/vendor","Dashboard"],["/vendor/products","Products"],["/vendor/listings","Listings"],["/vendor/requests","Requests"],["/vendor/enquiries","Enquiries"],["/vendor/messages","Messages"],["/vendor/analytics","Analytics"],["/vendor/rentals","Rental fleet"],["/vendor/rentals/bookings","Rental bookings"],["/vendor/events","Events"],["/vendor/insurance","Insurance"],["/payments","Payments"]]
  :[["/discover","Discover"],["/products","Shop products"],["/products/orders","My orders"],["/rentals/book","Book rental"],["/rentals/bookings","My rentals"],["/events","Events"],["/events/registrations","My events"],["/insurance","Insurance"],["/insurance/quotes","My quotes"],["/payments","Payments"],["/payments/upload","Upload payment"],["/favorites","Favorites"],["/enquiries","Enquiries"],["/messages","Messages"],["/notifications","Notifications"]];
 const close=()=>setOpen(false); const signout=()=>{logout();nav("/");close()};
 return <>
 <header className="sticky top-0 z-40 border-b border-slate-200/80 bg-white/90 backdrop-blur-xl">
  <div className="mx-auto flex min-h-[72px] w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6 lg:px-8">
   <Link to={home} onClick={close} className="shrink-0"><Logo/></Link>
   {user&&<nav className="hidden items-center gap-0.5 xl:flex">{links.map(([h,t])=><Link key={h} className="navlink" to={h}>{t}</Link>)}</nav>}
   <div className="flex items-center gap-2">
    {user&&<Link className="btn-secondary hidden sm:inline-flex" to={home}><LayoutDashboard size={16}/> <span>Dashboard</span></Link>}
    {vendor&&!admin&&<><Link className="btn-secondary hidden sm:inline-flex" to="/vendor/rentals">Rental fleet</Link><Link className="btn-primary hidden sm:inline-flex" to="/vendor/listings/new"><PlusCircle size={16}/> Post</Link></>}
    {user&&<Link className="icon-btn" to="/profile" title="Profile"><UserRound size={17}/></Link>}
    {admin&&<Link className="icon-btn hidden sm:inline-flex" to="/admin/audit-logs" title="Audit logs"><ShieldCheck size={17}/></Link>}
    {user&&<button className="btn-secondary hidden sm:inline-flex" onClick={signout}><LogOut size={16}/> Logout</button>}
    {user&&<button className="icon-btn xl:hidden" onClick={()=>setOpen(!open)} aria-label="Menu">{open?<X size={19}/>:<Menu size={19}/>}</button>}
   </div>
  </div>
  {user&&open&&<div className="border-t border-slate-100 bg-white px-4 py-4 shadow-lg xl:hidden"><nav className="mx-auto grid w-full max-w-7xl gap-1 sm:grid-cols-2">{links.map(([h,t])=><Link key={h} onClick={close} className="mobile-navlink" to={h}>{t}</Link>)}<Link onClick={close} className="mobile-navlink" to="/profile">Profile & Settings</Link>{vendor&&!admin&&<><Link onClick={close} className="mobile-navlink" to="/vendor/rentals">Rental fleet</Link><Link onClick={close} className="mobile-navlink font-bold" to="/vendor/listings/new">+ Create listing</Link></>}<button onClick={signout} className="mobile-navlink text-left text-red-600">Sign out</button></nav></div>}
 </header></>
}
