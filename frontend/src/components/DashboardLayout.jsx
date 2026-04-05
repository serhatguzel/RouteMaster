import React, { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { MapPin, Truck, Plane, LogOut, Menu, ChevronLeft, Home } from 'lucide-react';
import logo from '../assets/turkish-airlines-logo.png';
import { ROLES, PATHS, STORAGE_KEYS } from '../utils/constants';

const MENU_ITEMS = [
    { path: PATHS.ROUTES, name: 'Route Search', icon: Plane, allowedRoles: [ROLES.ADMIN, ROLES.AGENCY] },
    { path: PATHS.LOCATIONS, name: 'Locations', icon: MapPin, allowedRoles: [ROLES.ADMIN] },
    { path: PATHS.TRANSPORTATIONS, name: 'Transportations', icon: Truck, allowedRoles: [ROLES.ADMIN] },
];

const DashboardLayout = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);
    const location = useLocation();
    const navigate = useNavigate();
    const userRole = localStorage.getItem(STORAGE_KEYS.ROLE) || 'Unknown';

    // 2. FİLTRELEME: Sadece yetkimin olduğu menüleri listele (Magic happens here)
    const visibleMenuItems = MENU_ITEMS.filter(item =>
        item.allowedRoles.some(role => role.toUpperCase() === userRole.toUpperCase())
    );

    const handleLogout = () => {
        localStorage.removeItem(STORAGE_KEYS.TOKEN);
        localStorage.removeItem(STORAGE_KEYS.ROLE);
        navigate(PATHS.LOGIN);
    };

    return (
        <div className="flex h-screen bg-slate-50 overflow-hidden">
            {/* SOL KISIM: Sidebar (Açılır/Kapanır) */}
            <aside className={`${isSidebarOpen ? 'w-64' : 'w-20'} transition-all duration-300 bg-slate-900 border-r border-slate-800 flex flex-col`}>

                {/* Sol Üst: Logo ve İkon Alanı */}
                <div className="h-16 flex items-center justify-between px-4 border-b border-white/10 shrink-0">
                    {isSidebarOpen && (
                        <div className="flex items-center gap-3 overflow-hidden">
                            <Link to={PATHS.DASHBOARD} className="flex items-center gap-3 overflow-hidden hover:opacity-80 transition-opacity">
                                <img src={logo} alt="Logo" className="w-8 h-auto filter invert brightness-0" />
                                <span className="text-white font-bold tracking-wider uppercase text-sm">RouteMaster</span>
                            </Link>
                        </div>
                    )}
                    <button
                        onClick={() => setIsSidebarOpen(!isSidebarOpen)}
                        className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition-colors mx-auto"
                    >
                        {isSidebarOpen ? <ChevronLeft size={20} /> : <Menu size={20} />}
                    </button>
                </div>

                {/* Sol Orta: Dinamik Menü Linkleri */}
                <nav className="flex-1 py-6 px-3 space-y-2 overflow-y-auto custom-scrollbar">
                    {visibleMenuItems.map((item) => {
                        const Icon = item.icon;
                        const isActive = location.pathname.startsWith(item.path);

                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`flex items-center gap-3 px-3 py-3 rounded-xl transition-all ${isActive
                                    ? 'bg-slate-800 text-white shadow-md'
                                    : 'text-slate-400 hover:bg-white/5 hover:text-white'
                                    }`}
                                title={!isSidebarOpen ? item.name : ""}
                            >
                                <Icon size={20} className="shrink-0" />
                                {isSidebarOpen && <span className="font-medium text-sm whitespace-nowrap">{item.name}</span>}
                            </Link>
                        );
                    })}
                </nav>

                {/* Sol Alt: Çıkış Yap */}
                <div className="p-4 border-t border-white/10">
                    <button
                        onClick={handleLogout}
                        className="flex items-center gap-3 w-full px-3 py-3 rounded-xl text-red-400 hover:bg-red-400/10 transition-colors"
                        title={!isSidebarOpen ? "Logout" : ""}
                    >
                        <LogOut size={20} className="shrink-0" />
                        {isSidebarOpen && <span className="font-medium text-sm">Logout</span>}
                    </button>
                </div>
            </aside>

            {/* SAĞ KISIM: Main Content Area */}
            <div className="flex-1 flex flex-col h-screen overflow-hidden">

                {/* Üst Kısım: Sticky Header */}
                <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-8 shrink-0 z-10 shadow-sm">
                    <h2 className="text-xl font-bold text-slate-800 font-outfit">
                        {visibleMenuItems.find(m => location.pathname.startsWith(m.path))?.name || 'Dashboard'}
                    </h2>
                    <div className="flex items-center gap-3">
                        <span className="text-sm font-semibold text-slate-500 uppercase tracking-widest">
                            {userRole}
                        </span>
                        <div className="w-10 h-10 rounded-full bg-slate-900 border-[3px] border-slate-100 shadow-sm flex items-center justify-center text-white font-bold text-sm">
                            {userRole.charAt(0)}
                        </div>
                    </div>
                </header>

                {/* Alt Kısım: Sayfa İçeriği (İçeriği Kaydırılabilir Alan) */}
                <main className="flex-1 overflow-y-auto bg-slate-50 p-8 relative">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default DashboardLayout;
