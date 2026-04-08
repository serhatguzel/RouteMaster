import React, { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { Menu, ChevronLeft } from 'lucide-react';
import logo from '../assets/turkish-airlines-logo.png';
import { ROLES, PATHS, STORAGE_KEYS, MENU_ITEMS } from '../utils/constants';
import { logout } from '../services/api';

const DashboardLayout = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);
    const location = useLocation();
    const userRole = localStorage.getItem(STORAGE_KEYS.ROLE) || 'Unknown';

    // Sadece yetkimin olduğu menüleri listele
    const visibleMenuItems = MENU_ITEMS.filter(item =>
        item.allowedRoles.some(role => role.toUpperCase() === userRole.toUpperCase())
    );

    const handleLogout = async () => {
        await logout();
    };

    return (
        <div className="flex h-screen bg-slate-50 overflow-hidden">
            {/* Sidebar Açılır/Kapanır */}
            <aside className={`${isSidebarOpen ? 'w-64' : 'w-20'} transition-all duration-300 bg-slate-900 border-r border-slate-800 flex flex-col`}>
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

                <nav className="flex-1 py-6 px-3 space-y-2 overflow-y-auto custom-scrollbar">
                    {visibleMenuItems.map((item) => {
                        const isActive = location.pathname.startsWith(item.path);
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`flex items-center px-4 py-3 rounded-xl transition-all ${isActive
                                    ? 'bg-slate-800 text-white shadow-md'
                                    : 'text-slate-400 hover:bg-white/5 hover:text-white'
                                    }`}
                                title={!isSidebarOpen ? item.name : ""}
                            >
                                {isSidebarOpen && <span className="font-semibold text-sm whitespace-nowrap uppercase tracking-wider">{item.name}</span>}
                                {!isSidebarOpen && <span className="font-bold text-xs uppercase tracking-tighter">{item.name.charAt(0)}</span>}
                            </Link>
                        );
                    })}
                </nav>

                <div className="p-4 border-t border-white/10">
                    <button
                        onClick={handleLogout}
                        className="flex items-center justify-center w-full px-3 py-3 rounded-xl text-red-400 hover:bg-red-400/10 transition-colors font-bold text-xs uppercase tracking-widest"
                        title={!isSidebarOpen ? "Logout" : ""}
                    >
                        {isSidebarOpen ? "Logout" : "L"}
                    </button>
                </div>
            </aside>
            {/* Header */}
            <div className="flex-1 flex flex-col h-screen overflow-hidden">
                <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-8 shrink-0 z-10 shadow-sm">
                    <div className="flex items-center gap-4">
                        {!isSidebarOpen && (
                            <Link to={PATHS.DASHBOARD} className="hover:opacity-80 transition-opacity shrink-0">
                                <img src={logo} alt="Logo" className="w-10 h-auto" />
                            </Link>
                        )}
                        <h2 className="text-xl font-bold text-slate-800 font-outfit">
                            {visibleMenuItems.find(m => location.pathname.startsWith(m.path))?.name || 'Dashboard'}
                        </h2>
                    </div>
                    <div className="flex items-center gap-3">
                        <span className="text-sm font-semibold text-slate-500 uppercase tracking-widest">
                            {userRole}
                        </span>
                        <div className="w-10 h-10 rounded-full bg-slate-900 border-[3px] border-slate-100 shadow-sm flex items-center justify-center text-white font-bold text-sm">
                            {userRole.charAt(0)}
                        </div>
                    </div>
                </header>

                <main className="flex-1 overflow-y-auto bg-slate-50 p-8 relative">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default DashboardLayout;
