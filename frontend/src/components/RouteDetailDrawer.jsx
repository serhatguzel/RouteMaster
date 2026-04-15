import React from 'react';
import { Drawer } from '@mui/material';
import { X } from 'lucide-react';
import { TRANSPORTATION_TYPES } from '../utils/constants';

const RouteDetailDrawer = ({ selectedRoute, onClose, getSegmentConfig }) => {
    
    // Timeline verisini hazırlayan mantığı ana sayfadan buraya taşıdık
    const getTimelineData = (route) => {
        if (!route) return [];
        const nodes = [];
        const startLoc = route.beforeFlight ? route.beforeFlight.origin : route.flight.origin;
        nodes.push({ type: 'location', data: startLoc, isStart: true });
        
        if (route.beforeFlight) {
            nodes.push({ type: 'transport', transportType: route.beforeFlight.transportationType });
            nodes.push({ type: 'location', data: route.beforeFlight.destination }); 
        }
        
        nodes.push({ type: 'transport', transportType: TRANSPORTATION_TYPES.FLIGHT });
        nodes.push({ type: 'location', data: route.flight.destination }); 
        
        if (route.afterFlight) {
            nodes.push({ type: 'transport', transportType: route.afterFlight.transportationType });
            nodes.push({ type: 'location', data: route.afterFlight.destination, isEnd: true });
        } else {
            nodes[nodes.length - 1].isEnd = true;
        }
        return nodes;
    };

    return (
        <Drawer
            anchor="right"
            open={!!selectedRoute}
            onClose={onClose}
            PaperProps={{
                sx: { width: { xs: '100%', sm: 420 }, borderRadius: { xs: 0, sm: '32px 0 0 32px' } }
            }}
        >
            {selectedRoute && (
                <div className="h-full flex flex-col p-0 bg-white font-outfit overflow-hidden">
                    {/* Header */}
                    <div className="p-8 pb-4 flex items-center justify-between">
                        <h2 className="text-2xl font-black text-slate-900 tracking-tight">Route Details</h2>
                        <button onClick={onClose} className="p-2.5 bg-slate-50 text-slate-400 hover:text-slate-900 rounded-2xl transition-all">
                            <X size={20} />
                        </button>
                    </div>

                    {/* Timeline */}
                    <div className="flex-1 overflow-y-auto px-10 py-8">
                        <div className="relative">
                            <div className="absolute left-[11px] top-6 bottom-6 w-0.5 border-l-2 border-dashed border-slate-200"></div>
                            <div className="space-y-0">
                                {getTimelineData(selectedRoute).map((node, i) => {
                                    if (node.type === 'location') {
                                        return (
                                            <div key={i} className="relative flex items-center group pb-2">
                                                <div className={`w-6 h-6 rounded-full border-2 ${node.isStart || node.isEnd ? 'border-slate-900 bg-white' : 'border-slate-300 bg-white'} z-10 flex items-center justify-center`}>
                                                    <div className={`w-2 h-2 rounded-full ${node.isStart || node.isEnd ? 'bg-slate-900' : 'bg-slate-300'}`}></div>
                                                </div>
                                                <div className="ml-6 py-2">
                                                    <p className="text-base font-bold text-slate-900">{node.data.name}</p>
                                                    <p className="text-[11px] text-slate-400 font-medium uppercase tracking-wider mt-0.5">{node.data.city}</p>
                                                </div>
                                            </div>
                                        );
                                    } else {
                                        const config = getSegmentConfig(node.transportType);
                                        return (
                                            <div key={i} className="relative ml-[32px] py-6 my-[-8px]">
                                                <div className="flex items-center gap-4">
                                                    <div className={`w-8 h-8 rounded-xl ${config.light} ${config.textColor} flex items-center justify-center shadow-sm border border-slate-50`}>
                                                        <config.Icon size={16} />
                                                    </div>
                                                    <p className="text-sm font-bold text-slate-700 tracking-tight">{config.label}</p>
                                                </div>
                                            </div>
                                        );
                                    }
                                })}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </Drawer>
    );
};

export default RouteDetailDrawer;
