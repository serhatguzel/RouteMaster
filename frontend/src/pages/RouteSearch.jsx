import React, { useState, useEffect } from 'react';
import api from '../services/api';
import {
    Plane, Bus, ArrowRight, Info, Navigation
} from 'lucide-react';
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
            const res = await api.get('/locations');
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

            const res = await api.get('/routes/search', {
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
            // 3. Backend'den gelen spesifik hata mesajını göster (Yeni!)
            const serverMessage = err.response?.data?.message || 'Route search failed.';
            setError(serverMessage);
        } finally {
            setSearching(false);
        }
    };

    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <div className="space-y-6 animate-fade-in pb-12">
                {/* Başlık ve Açıklama */}
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold font-outfit text-slate-900">Route Search</h1>
                        <p className="text-slate-500 text-sm mt-1">Discover the best aviation network paths</p>
                    </div>
                </div>

                {/* Arama Formu (Card) */}
                <div className="bg-white p-6 rounded-3xl shadow-xl border border-slate-100">
                    <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-6 items-end">

                        {/* Kalkış Noktası */}
                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700 flex items-center gap-2">
                                Origin
                            </label>
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

                        {/* Varış Noktası */}
                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700 flex items-center gap-2">
                                Destination
                            </label>
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

                        {/* Tarih Seçimi (MUI DatePicker) */}
                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700 flex items-center gap-2">
                                Travel Date
                            </label>
                            <DatePicker
                                value={searchCriteria.date}
                                format="DD-MM-YYYY"
                                onChange={(newValue) => setSearchCriteria({ ...searchCriteria, date: newValue })}
                                slotProps={{ textField: { fullWidth: true, size: 'small', sx: { '& .MuiOutlinedInput-root': { borderRadius: '12px', backgroundColor: '#f8fafc' } } } }}
                            />
                        </div>

                        {/* Arama Butonu */}
                        <button
                            type="submit"
                            disabled={searching}
                            className="bg-slate-900 hover:bg-slate-800 text-white font-bold py-3.5 px-6 rounded-2xl shadow-xl shadow-slate-200 transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-95"
                        >
                            Search
                        </button>
                    </form>
                </div>

                {/* Hata Gösterimi */}
                {error && (
                    <div className="bg-rose-50 text-rose-600 p-4 rounded-xl flex items-center gap-3 animate-shake">
                        <Info size={20} /> {error}
                    </div>
                )}

                {/* Arama Sonuçları Listesi */}
                <div className="space-y-4 mt-8">
                    {results.map((route, index) => (
                        <div key={index} onClick={() => setSelectedRoute(route)} className="bg-white rounded-3xl p-6 shadow-lg border border-slate-50 hover:shadow-xl transition-all animate-slide-up group cursor-pointer hover:border-slate-200">
                            <div className="flex flex-col md:flex-row items-center justify-between gap-8">

                                {/* 1. Segment: Before Flight (Varsa) */}
                                {route.beforeFlight && (
                                    <div className="flex items-center gap-4 flex-1">
                                        <div className="w-12 h-12 rounded-2xl bg-indigo-50 flex items-center justify-center text-indigo-600">
                                            <Bus size={24} />
                                        </div>
                                        <div>
                                            <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Transfer</p>
                                            <p className="font-bold text-slate-700">{route.beforeFlight.origin.locationCode} → {route.beforeFlight.destination.locationCode}</p>
                                        </div>
                                        <ArrowRight className="text-slate-300 hidden md:block" />
                                    </div>
                                )}

                                {/* 2. Segment: Flight (Zorunlu) */}
                                <div className="flex items-center gap-6 flex-[2] bg-slate-50 p-4 rounded-2xl border border-dashed border-slate-200">
                                    <div className="w-14 h-14 rounded-2xl bg-indigo-600 flex items-center justify-center text-white shadow-lg shadow-indigo-200">
                                        <Plane size={28} />
                                    </div>
                                    <div className="flex-1">
                                        <div className="flex items-center justify-between">
                                            <span className="text-2xl font-black font-outfit text-slate-800">{route.flight.origin.locationCode}</span>
                                            <div className="flex-1 mx-4 h-px bg-slate-300 relative">
                                                <Plane size={14} className="absolute -top-1.5 left-1/2 -translate-x-1/2 text-indigo-400" />
                                            </div>
                                            <span className="text-2xl font-black font-outfit text-slate-800">{route.flight.destination.locationCode}</span>
                                        </div>
                                        <div className="flex justify-between text-xs text-slate-500 mt-1 font-medium">
                                            <span>{route.flight.origin.city}</span>
                                            <span className="bg-indigo-100 text-indigo-700 px-2 py-0.5 rounded-full">Flight</span>
                                            <span>{route.flight.destination.city}</span>
                                        </div>
                                    </div>
                                </div>

                                {/* 3. Segment: After Flight (Varsa) */}
                                {route.afterFlight && (
                                    <div className="flex items-center gap-4 flex-1">
                                        <ArrowRight className="text-slate-300 hidden md:block" />
                                        <div className="w-12 h-12 rounded-2xl bg-rose-50 flex items-center justify-center text-rose-600">
                                            <Navigation size={24} />
                                        </div>
                                        <div>
                                            <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Transfer</p>
                                            <p className="font-bold text-slate-700">{route.afterFlight.origin.locationCode} → {route.afterFlight.destination.locationCode}</p>
                                        </div>
                                    </div>
                                )}

                            </div>
                        </div>
                    ))}
                </div>

            </div>
            {/* ROUTE DETAIL DRAWER */}
            <Drawer
                anchor="right"
                open={!!selectedRoute}
                onClose={() => setSelectedRoute(null)}
                PaperProps={{
                    sx: { width: { xs: '100%', sm: 450 }, borderRadius: { xs: 0, sm: '24px 0 0 24px' } }
                }}
            >
                {selectedRoute && (
                    <div className="h-full flex flex-col p-8 bg-white font-outfit">
                        {/* Header */}
                        <div className="flex items-center justify-between mb-10">
                            <h2 className="text-2xl font-black text-slate-900 uppercase tracking-tight">Route Details</h2>
                            <button
                                onClick={() => setSelectedRoute(null)}
                                className="p-2 bg-slate-50 text-slate-400 hover:text-slate-900 rounded-full transition-all"
                            >
                                <X size={24} />
                            </button>
                        </div>

                        {/* Content (Zaman Tüneli / Timeline Mantığı) */}
                        <div className="flex-1 overflow-y-auto pr-2">
                            <div className="relative pl-8 space-y-12 py-4">

                                {/* Dikey Çizgi (Arka Plan) */}
                                <div className="absolute left-[15px] top-4 bottom-4 w-0.5 bg-slate-100 border-l border-dashed border-slate-300"></div>

                                {/* 1. SEGMEN: Before Flight */}
                                {selectedRoute.beforeFlight && (
                                    <div className="relative">
                                        <div className="absolute -left-[33px] w-8 h-8 rounded-full bg-indigo-50 border-4 border-white shadow-sm flex items-center justify-center text-indigo-600 z-10">
                                            <Bus size={14} />
                                        </div>
                                        <div>
                                            <span className="text-[10px] font-black uppercase tracking-widest text-indigo-500 bg-indigo-50 px-2 py-0.5 rounded-full mb-2 inline-block">Transfer</span>
                                            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100">
                                                <div className="flex justify-between items-center mb-1">
                                                    <span className="font-black text-slate-800 text-lg">{selectedRoute.beforeFlight.origin.locationCode}</span>
                                                    <ArrowRight size={14} className="text-slate-300" />
                                                    <span className="font-black text-slate-800 text-lg">{selectedRoute.beforeFlight.destination.locationCode}</span>
                                                </div>
                                                <p className="text-xs text-slate-500 font-medium">{selectedRoute.beforeFlight.origin.city} → {selectedRoute.beforeFlight.destination.city}</p>
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {/* 2. SEGMEN: Ana Uçuş (Zorunlu) */}
                                <div className="relative">
                                    <div className="absolute -left-[33px] w-8 h-8 rounded-full bg-slate-900 border-4 border-white shadow-sm flex items-center justify-center text-white z-10">
                                        <Plane size={14} />
                                    </div>
                                    <div>
                                        <span className="text-[10px] font-black uppercase tracking-widest text-slate-400 bg-slate-50 px-2 py-0.5 rounded-full mb-2 inline-block">Main Flight</span>
                                        <div className="bg-white p-5 rounded-2xl border-2 border-slate-900 shadow-xl shadow-slate-100">
                                            <div className="flex justify-between items-center mb-2">
                                                <div className="text-center">
                                                    <p className="text-2xl font-black text-slate-900 leading-none">{selectedRoute.flight.origin.locationCode}</p>
                                                    <p className="text-[10px] text-slate-400 font-bold mt-1 uppercase">{selectedRoute.flight.origin.city}</p>
                                                </div>
                                                <div className="flex-1 px-4 flex flex-col items-center">
                                                    <Plane size={18} className="text-slate-200 mb-1" />
                                                    <div className="w-full h-px bg-slate-100 italic text-[9px] text-slate-300 text-center">FLIGHT</div>
                                                </div>
                                                <div className="text-center">
                                                    <p className="text-2xl font-black text-slate-900 leading-none">{selectedRoute.flight.destination.locationCode}</p>
                                                    <p className="text-[10px] text-slate-400 font-bold mt-1 uppercase">{selectedRoute.flight.destination.city}</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {/* 3. SEGMEN: After Flight */}
                                {selectedRoute.afterFlight && (
                                    <div className="relative">
                                        <div className="absolute -left-[33px] w-8 h-8 rounded-full bg-rose-50 border-4 border-white shadow-sm flex items-center justify-center text-rose-600 z-10">
                                            <Navigation size={14} />
                                        </div>
                                        <div>
                                            <span className="text-[10px] font-black uppercase tracking-widest text-rose-500 bg-rose-50 px-2 py-0.5 rounded-full mb-2 inline-block">Arrival Transfer</span>
                                            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100">
                                                <div className="flex justify-between items-center mb-1">
                                                    <span className="font-black text-slate-800 text-lg">{selectedRoute.afterFlight.origin.locationCode}</span>
                                                    <ArrowRight size={14} className="text-slate-300" />
                                                    <span className="font-black text-slate-800 text-lg">{selectedRoute.afterFlight.destination.locationCode}</span>
                                                </div>
                                                <p className="text-xs text-slate-500 font-medium">{selectedRoute.afterFlight.origin.city} → {selectedRoute.afterFlight.destination.city}</p>
                                            </div>
                                        </div>
                                    </div>
                                )}

                            </div>
                        </div>

                    </div>
                )}
            </Drawer>

        </LocalizationProvider>
    );

};

export default RouteSearchPage;
