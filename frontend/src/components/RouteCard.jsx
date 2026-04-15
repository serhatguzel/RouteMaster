import React from 'react';
import { Plane, ChevronRight } from 'lucide-react';

const RouteCard = ({ route, onClick }) => {
    // Karmaşık mantıkları ana sayfadan buraya taşıdık
    const startLoc = route.beforeFlight ? route.beforeFlight.origin : route.flight.origin;

    return (
        <div
            onClick={() => onClick(route)}
            className="bg-white p-5 px-8 rounded-2xl border border-slate-100 hover:border-indigo-100 hover:shadow-lg hover:shadow-slate-100/50 transition-all cursor-pointer group flex items-center justify-between"
        >
            <div className="flex items-center gap-8">
                <div className="flex items-center gap-2">
                    <div className="w-7 h-7 rounded-lg bg-slate-50 flex items-center justify-center text-slate-400 group-hover:bg-indigo-50 group-hover:text-indigo-500 transition-colors">
                        <Plane size={14} />
                    </div>
                    <span className="text-sm font-medium text-slate-500 group-hover:text-slate-700 transition-colors">
                        Via {route.flight.origin.name}
                        <span className="ml-1 text-xs font-bold text-slate-300 group-hover:text-indigo-300">
                            ({route.flight.origin.locationCode})
                        </span>
                    </span>
                </div>
            </div>

            <ChevronRight
                size={18}
                className="text-slate-200 group-hover:text-indigo-500 transform group-hover:translate-x-1 transition-all"
            />
        </div>
    );
};

export default RouteCard;
