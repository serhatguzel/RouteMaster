import React from 'react';
import { STORAGE_KEYS } from '../utils/constants';

const DashboardHome = () => {
    const role = localStorage.getItem(STORAGE_KEYS.ROLE) || 'User';

    return (
        <div className="flex flex-col items-center justify-center h-[70vh] text-slate-800">
            <div className="bg-white p-10 rounded-3xl shadow-xl shadow-slate-200 border border-slate-100 text-center max-w-lg">
                <h1 className="text-4xl font-bold font-outfit text-slate-900 mb-4">
                    Welcome to RouteMaster
                </h1>
                <div className="inline-block px-4 py-1 bg-slate-100 text-slate-600 rounded-full text-xs font-bold tracking-widest uppercase mb-6">
                    Logged in as: {role}
                </div>
                <p className="text-slate-500 text-sm leading-relaxed">
                    Please select an option from the sidebar to manage system data or search for valid aviation routes.
                </p>
            </div>
        </div>
    );
};

export default DashboardHome;
