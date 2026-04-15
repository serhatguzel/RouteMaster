import React, { useState, useEffect } from 'react';
import api from '../services/api';
import {
    Plane, Bus, Info, Train, Car
} from 'lucide-react';
import { TRANSPORTATION_TYPES, API_ENDPOINTS } from '../utils/constants';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import RouteCard from '../components/RouteCard';
import RouteDetailDrawer from '../components/RouteDetailDrawer';
import RouteSearchBar from '../components/RouteSearchBar';

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

    const handleChange = (e) => {
        setSearchCriteria({
            ...searchCriteria,
            [e.target.name]: e.target.value
        });
    }

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
                <RouteSearchBar
                    searchCriteria={searchCriteria}
                    locations={locations}
                    searching={searching}
                    handleChange={handleChange}
                    handleSearch={handleSearch}
                />

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
                            {results.map((route, index) => (
                                <RouteCard
                                    key={index}
                                    route={route}
                                    onClick={setSelectedRoute}
                                />
                            ))}
                        </div>
                    </div>
                )}
            </div>

            <RouteDetailDrawer
                selectedRoute={selectedRoute}
                onClose={() => setSelectedRoute(null)}
                getSegmentConfig={getSegmentConfig}
            />
        </LocalizationProvider>
    );

};

export default RouteSearchPage;
