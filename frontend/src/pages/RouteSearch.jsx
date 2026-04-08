import React, { useState, useEffect } from 'react';
import api from '../services/api';
import {
    Plane, Bus, ArrowRight, Info, Navigation, Train, Car, MapPin, ChevronRight
} from 'lucide-react';
import { TRANSPORTATION_TYPES, API_ENDPOINTS } from '../utils/constants';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import { Drawer } from '@mui/material';
import { X } from 'lucide-react';

const RouteSearchPage = () => {

    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searching, setSearching] = useState(false);
    const [results, setResults] = useState([]);
    const [error, setError] = useState('');
    const [selectedRoute, setSelectedRoute] = useState(null);
    
    const typeConfig = {
        [TRANSPORTATION_TYPES.FLIGHT]: { Icon: Plane, color: 'bg-indigo-600', textColor: 'text-indigo-600', light: 'bg-indigo-50', label: 'Flight' },
        [TRANSPORTATION_TYPES.BUS]: { Icon: Bus, color: 'bg-amber-500', textColor: 'text-amber-600', light: 'bg-amber-50', label: 'Bus' },
        [TRANSPORTATION_TYPES.SUBWAY]: { Icon: Train, color: 'bg-emerald-500', textColor: 'text-emerald-600', light: 'bg-emerald-50', label: 'Subway' },
        [TRANSPORTATION_TYPES.UBER]: { Icon: Car, color: 'bg-slate-800', textColor: 'text-slate-600', light: 'bg-slate-100', label: 'Uber' }
    };

    const getSegmentConfig = (type) => typeConfig[type] || typeConfig[TRANSPORTATION_TYPES.BUS];

    const [searchCriteria, setSearchCriteria] = useState({
        originId: '',
        destinationId: '',
        date: dayjs()
    });

    useEffect(() => {
        fetchLocations();
    }, []);

    const fetchLocations = async () => {
        try {
            setLoading(true);
            const res = await api.get(API_ENDPOINTS.LOCATIONS.BASE);
            setLocations(res.data);
        } catch (err) {
            setError('Locations could not be loaded.');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (e) => {
        if (e) e.preventDefault();

        if (!searchCriteria.originId || !searchCriteria.destinationId) {
            setError('Please select both origin and destination.');
            return;
        }

        if (searchCriteria.originId === searchCriteria.destinationId) {
            setError('Origin and destination cannot be the same.');
            return;
        }

        try {
            setSearching(true);
            setError('');

            const res = await api.get(API_ENDPOINTS.ROUTES.SEARCH, {
                params: {
                    originId: searchCriteria.originId,
                    destinationId: searchCriteria.destinationId,
                    date: searchCriteria.date.format('DD-MM-YYYY')
                }
            });

            setResults(res.data);
            if (res.data.length === 0) {
                setError('No routes found for the selected criteria.');
            }
        } catch (err) {
            const serverMessage = err.response?.data?.message || 'Route search failed.';
            setError(serverMessage);
        } finally {
            setSearching(false);
        }
    };

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
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <div className="space-y-6 animate-fade-in pb-12">
                {/* Header */}
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold font-outfit text-slate-900">Route Search</h1>
                        <p className="text-slate-500 text-sm mt-1">Discover the best aviation network paths</p>
                    </div>
                </div>

                {/* Search Form */}
                <div className="bg-white p-6 rounded-3xl shadow-xl border border-slate-100">
                    <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-6 items-end">
                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700 flex items-center gap-2">Origin</label>
                            <select
                                className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer"
                                value={searchCriteria.originId}
                                onChange={(e) => setSearchCriteria({ ...searchCriteria, originId: e.target.value })}
                            >
                                <option value="">Select Origin</option>
                                {locations.map(loc => (
                                    <option key={loc.id} value={loc.id}>{loc.name} ({loc.locationCode})</option>
                                ))}
                            </select>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700 flex items-center gap-2">Destination</label>
                            <select
                                className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer"
                                value={searchCriteria.destinationId}
                                onChange={(e) => setSearchCriteria({ ...searchCriteria, destinationId: e.target.value })}
                            >
                                <option value="">Select Destination</option>
                                {locations.map(loc => (
                                    <option key={loc.id} value={loc.id}>{loc.name} ({loc.locationCode})</option>
                                ))}
                            </select>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700 flex items-center gap-2">Travel Date</label>
                            <DatePicker
                                value={searchCriteria.date}
                                format="DD-MM-YYYY"
                                onChange={(newValue) => setSearchCriteria({ ...searchCriteria, date: newValue })}
                                slotProps={{ textField: { fullWidth: true, size: 'small', sx: { '& .MuiOutlinedInput-root': { borderRadius: '12px', backgroundColor: '#f8fafc' } } } }}
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={searching}
                            className="bg-slate-900 hover:bg-slate-800 text-white font-bold py-3.5 px-6 rounded-2xl shadow-xl shadow-slate-200 transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-95"
                        >
                            {searching ? 'Searching...' : 'Search'}
                        </button>
                    </form>
                </div>

                {error && (
                    <div className="bg-rose-50 text-rose-600 p-4 rounded-xl flex items-center gap-3 animate-shake">
                        <Info size={20} /> {error}
                    </div>
                )}

                {/* REFINED AVAILABLE ROUTES LIST */}
                {results.length > 0 && (
                    <div className="mt-12 space-y-4 animate-slide-up">
                        <div className="flex items-center gap-3 mb-6 ml-2">
                            <div className="h-6 w-1 bg-indigo-500 rounded-full"></div>
                            <h2 className="text-xl font-bold text-slate-800 tracking-tight">Available Routes</h2>
                        </div>
                        
                        <div className="space-y-3">
                            {results.map((route, index) => {
                                const startLoc = route.beforeFlight ? route.beforeFlight.origin : route.flight.origin;
                                const endLoc = route.afterFlight ? route.afterFlight.destination : route.flight.destination;
                                
                                return (
                                    <div 
                                        key={index} 
                                        onClick={() => setSelectedRoute(route)} 
                                        className="bg-white p-5 px-8 rounded-2xl border border-slate-100 hover:border-indigo-100 hover:shadow-lg hover:shadow-slate-100/50 transition-all cursor-pointer group flex items-center justify-between"
                                    >
                                        <div className="flex items-center gap-8">

                                            <div className="flex items-center gap-2">
                                                <div className="w-7 h-7 rounded-lg bg-slate-50 flex items-center justify-center text-slate-400 group-hover:bg-indigo-50 group-hover:text-indigo-500 transition-colors">
                                                    <Plane size={14} />
                                                </div>
                                                <span className="text-sm font-medium text-slate-500 group-hover:text-slate-700 transition-colors">
                                                    Via {route.flight.origin.name} <span className="text-xs font-bold text-slate-300 group-hover:text-indigo-300">({route.flight.origin.locationCode})</span>
                                                </span>
                                            </div>
                                        </div>

                                        <ChevronRight size={18} className="text-slate-200 group-hover:text-indigo-500 transform group-hover:translate-x-1 transition-all" />
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                )}
            </div>

            {/* ROUTE DETAIL DRAWER */}
            <Drawer
                anchor="right"
                open={!!selectedRoute}
                onClose={() => setSelectedRoute(null)}
                PaperProps={{
                    sx: { width: { xs: '100%', sm: 420 }, borderRadius: { xs: 0, sm: '32px 0 0 32px' } }
                }}
            >
                {selectedRoute && (
                    <div className="h-full flex flex-col p-0 bg-white font-outfit overflow-hidden">
                        {/* Header */}
                        <div className="p-8 pb-4 flex items-center justify-between">
                            <div>
                                <h2 className="text-2xl font-black text-slate-900 tracking-tight">Route Details</h2>
                            </div>
                            <button onClick={() => setSelectedRoute(null)} className="p-2.5 bg-slate-50 text-slate-400 hover:text-slate-900 rounded-2xl transition-all">
                                <X size={20} />
                            </button>
                        </div>

                        {/* SKETCH-BASED TIMELINE */}
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
                                                        <p className="text-base font-bold text-slate-900 flex items-center gap-2">
                                                            {node.data.name}
                                                        </p>
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
                                                        <div>
                                                            <p className="text-sm font-bold text-slate-700 tracking-tight">{config.label}</p>
                                                        </div>
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
        </LocalizationProvider>
    );

};

export default RouteSearchPage;
