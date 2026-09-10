import {Outlet,useLocation} from "react-router-dom";
import Navbar from "../components/Navbar";import Footer from "../components/Footer";
export default function AppLayout(){const{pathname}=useLocation();const tone=pathname.startsWith("/admin")?"admin-bg":pathname.startsWith("/vendor")?"vendor-bg":pathname.startsWith("/messages")?"chat-bg":pathname.startsWith("/listing/")?"listing-detail-bg":"user-bg";return <div className={`min-h-screen flex flex-col ${tone}`}><Navbar/><main className="flex-1"><Outlet/></main><Footer/></div>}
