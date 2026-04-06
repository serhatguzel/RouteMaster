import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { MapPin, Trash2, Loader2, Search, Edit } from 'lucide-react';
import { fetchAirports, getHierarchicalData, getAirportsByCity } from '../services/airportService';

const LocationPage = () => {

    const [loading, setLoading] = useState(true);
    const [locations, setLocations] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [error, setError] = useState('');
    const [errors, setErrors] = useState({});
    const [deleteModal, setDeleteModal] = useState({ show: false, id: null });
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentLocationId, setCurrentLocationId] = useState(null);
    const [allAirports, setAllAirports] = useState([]);
    const [hierarchy, setHierarchy] = useState({});
    const [isDataLoading, setIsDataLoading] = useState(true);
    const [formData, setFormData] = useState({
        name: '',
        locationCode: '',
        city: '',
        country: '',
        type: 'AIRPORT'
    });

    useEffect(() => {
        getLocations();
        loadAirportData();
    }, []);

    const loadAirportData = async () => {
        try {
            setIsDataLoading(true);
            const data = await fetchAirports();
            setAllAirports(data);
            const hierarchyData = getHierarchicalData(data);
            setHierarchy(hierarchyData);
        } catch (err) {
            console.error('Failed to load airport data');
        } finally {
            setIsDataLoading(false);
        }
    };

    const getLocations = async () => {
        try {
            setLoading(true);
            const response = await api.get('/locations');
            setLocations(response.data);
        } catch (err) {
            setError('Locations could not be loaded.');
        } finally {
            setLoading(false);
        }
    };

    const handleAdd = () => {
        setFormData({
            name: '',
            locationCode: '',
            city: '',
            country: '',
            type: 'AIRPORT'
        });
        setIsEditing(false);
        setIsModalOpen(true);
        setErrors({});
    };

    const handleEdit = (location) => {
        setFormData({
            name: location.name,
            locationCode: location.locationCode,
            city: location.city,
            country: location.country,
            type: location.type || 'AIRPORT'
        });
        setIsEditing(true);
        setCurrentLocationId(location.id);
        setIsModalOpen(true);
        setErrors({});
    };

    const validate = () => {
        let tempErrors = {};
        if (!formData.country) tempErrors.country = true;
        if (!formData.city) tempErrors.city = true;
        if (!formData.name) tempErrors.name = true;
        if (!formData.locationCode) tempErrors.locationCode = true;
        if (!formData.type) tempErrors.type = true;

        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) {
            setError('Lütfen tüm zorunlu alanları doldurun.');
            setTimeout(() => setError(''), 3000);
            return;
        }

        try {
            if (isEditing) {
                const response = await api.put(`/locations/${currentLocationId}`, formData);
                setLocations(locations.map(loc => loc.id === currentLocationId ? response.data : loc));
            } else {
                const response = await api.post('/locations', formData);
                setLocations([...locations, response.data]);
            }
            setIsModalOpen(false);
            setError('');
        } catch (err) {
            setError('İşlem sırasında bir hata oluştu. Lütfen bilgileri kontrol edin.');
            setTimeout(() => setError(''), 3000);
        }
    };

    const handleDeleteClick = (id) => {
        setDeleteModal({ show: true, id });
    };

    const confirmDelete = async () => {
        try {
            await api.delete(`/locations/${deleteModal.id}`);
            setLocations(locations.filter(loc => loc.id !== deleteModal.id));
            setDeleteModal({ show: false, id: null });
        } catch (err) {
            setError('Delete operation failed.');
            setTimeout(() => setError(''), 3000);
        }
    };

    const filteredLocations = locations.filter(loc =>
        loc.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        loc.locationCode.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <>
            {/* ERROR TOAST - EN ÜSTTE VE NET */}
            {error && (
                <div className="fixed top-6 right-6 z-[999] bg-red-50 border border-red-100 text-red-600 px-6 py-4 rounded-2xl shadow-2xl animate-bounce-in flex items-center gap-3">
                    <div className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></div>
                    <span className="font-semibold text-sm">{error}</span>
                </div>
            )}

            <div className="space-y-6 animate-fade-in pb-12">
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold font-outfit text-slate-900">Location Management</h1>
                        <p className="text-slate-500 text-sm mt-1">Manage airports, cities and transit points.</p>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                            <input
                                type="text"
                                placeholder="Search code or name..."
                                className="pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none text-sm w-72 text-slate-900 transition-all shadow-sm"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                        <button
                            onClick={handleAdd}
                            className="flex items-center gap-2 bg-slate-900 text-white px-5 py-2.5 rounded-xl hover:bg-slate-800 transition-all font-bold text-sm shadow-lg shadow-slate-100 active:scale-95"
                        >
                            Add
                        </button>
                    </div>
                </div>

                <div className="bg-white rounded-3xl border border-slate-100 shadow-xl shadow-slate-200/50 shadow-slate-200/50 overflow-hidden">
                    {loading ? (
                        <div className="p-20 flex flex-col items-center justify-center text-slate-400 gap-3">
                            <Loader2 className="animate-spin" size={32} />
                            <span className="text-sm font-medium">Fetching locations...</span>
                        </div>) : (
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="border-b border-slate-100 bg-slate-50/50">
                                    <th className="px-6 py-4 font-bold text-slate-500 text-[11px] font-bold tracking-widest uppercase tracking-widest">Type</th>
                                    <th className="px-6 py-4 font-bold text-slate-500 text-[11px] font-bold tracking-widest uppercase tracking-widest">Code</th>
                                    <th className="px-6 py-4 font-bold text-slate-500 text-[11px] font-bold tracking-widest uppercase tracking-widest">Name</th>
                                    <th className="px-6 py-4 font-bold text-slate-500 text-[11px] font-bold tracking-widest uppercase tracking-widest">City/Country</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-50">
                                {filteredLocations.map((loc) => {
                                    return (
                                        <tr key={loc.id} className="hover:bg-slate-50/30 transition-colors group">
                                            <td className="px-6 py-4">
                                                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold tracking-widest font-bold ${loc.type === 'AIRPORT'
                                                    ? 'bg-blue-100 text-blue-700'
                                                    : 'bg-slate-100 text-slate-700'
                                                    }`}>
                                                    {loc.type || 'AIRPORT'}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 font-mono font-bold text-slate-400 text-xs uppercase tracking-tighter">
                                                {loc.locationCode}
                                            </td>
                                            <td className="px-6 py-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-8 h-8 rounded-lg bg-slate-900 flex items-center justify-center text-white">
                                                        <MapPin size={16} />
                                                    </div>
                                                    <span className="font-bold text-slate-700 text-sm tracking-tight">{loc.name}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-slate-500 text-sm font-medium">
                                                {loc.city}, {loc.country}
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                <div className="flex items-center justify-end gap-2">
                                                    <button
                                                        onClick={() => handleEdit(loc)}
                                                        className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100/50 rounded-xl transition-all duration-200"
                                                        title="Edit"
                                                    >
                                                        <Edit size={18} />
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteClick(loc.id)}
                                                        className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all duration-200"
                                                        title="Delete"
                                                    >
                                                        <Trash2 size={18} />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>)}
                </div>

                {/* MODAL */}
                {isModalOpen && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                        <div className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm animate-fade-in"
                            onClick={() => setIsModalOpen(false)}></div>
                        <div className="relative bg-white rounded-[2rem] shadow-2xl p-8 max-w-lg w-full animate-slide-up border border-slate-100">
                            <h3 className="text-2xl font-bold text-slate-900 mb-8 font-outfit uppercase tracking-tight">
                                {isEditing ? 'Edit Location' : 'New Location'}
                            </h3>

                            <form onSubmit={handleSubmit} className="space-y-4" noValidate>
                                <div>
                                    <label className="block text-[11px] font-bold tracking-widest font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Country</label>
                                    <select
                                        required
                                        className={`w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer ${errors.country ? 'border-red-500 ring-2 ring-red-50' : ''
                                            }`}
                                        value={formData.country}
                                        onChange={(e) => setFormData({
                                            ...formData,
                                            country: e.target.value,
                                            city: '',
                                            name: '',
                                            locationCode: ''
                                        })}
                                    >
                                        <option value="">Select Country</option>
                                        {Object.keys(hierarchy).map(country => (
                                            <option key={country} value={country}>{country}</option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <label className="block text-[11px] font-bold tracking-widest font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">City</label>
                                    <select
                                        required
                                        disabled={!formData.country}
                                        className={`w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer disabled:opacity-50 ${errors.city ? 'border-red-500 ring-2 ring-red-50' : ''
                                            }`}
                                        value={formData.city}
                                        onChange={(e) => setFormData({
                                            ...formData,
                                            city: e.target.value,
                                            name: '',
                                            locationCode: ''
                                        })}
                                    >
                                        <option value="">Select City</option>
                                        {formData.country && hierarchy[formData.country]?.map(city => (
                                            <option key={city} value={city}>{city}</option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <label className="block text-[11px] font-bold tracking-widest font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Location Type</label>
                                    <select
                                        required
                                        className={`w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer ${errors.type ? 'border-red-500 ring-2 ring-red-50' : ''
                                            }`}
                                        value={formData.type}
                                        onChange={(e) => setFormData({
                                            ...formData,
                                            type: e.target.value,
                                            name: '',
                                            locationCode: ''
                                        })}
                                    >
                                        <option value="AIRPORT">Airport (IATA)</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                </div>

                                {formData.type === 'AIRPORT' ? (
                                    <>
                                        <div>
                                            <label className="block text-[11px] font-bold tracking-widest font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Airport</label>
                                            <select
                                                required
                                                disabled={!formData.city || isDataLoading}
                                                className={`w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer disabled:opacity-50 ${errors.name ? 'border-red-500 ring-2 ring-red-50' : ''
                                                    }`}
                                                value={formData.locationCode}
                                                onChange={(e) => {
                                                    const selectedAirport = allAirports.find(a => a.iata === e.target.value);
                                                    if (selectedAirport) {
                                                        setFormData({
                                                            ...formData,
                                                            locationCode: selectedAirport.iata,
                                                            name: selectedAirport.name
                                                        });
                                                    }
                                                }}
                                            >
                                                <option value="">{isDataLoading ? 'Loading Airports...' : 'Select Airport'}</option>
                                                {formData.city && getAirportsByCity(allAirports, formData.country, formData.city).map(airport => (
                                                    <option key={airport.iata} value={airport.iata}>
                                                        {airport.name} ({airport.iata})
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div>
                                            <label className="block text-[11px] font-bold tracking-widest font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Name</label>
                                            <input
                                                type="text"
                                                required
                                                className={`w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm ${errors.name ? 'border-red-500 ring-2 ring-red-50' : ''
                                                    }`}
                                                placeholder="e.g. City Center Terminal"
                                                value={formData.name}
                                                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                            />
                                        </div>
                                        <div>
                                            <label className="block text-[11px] font-bold tracking-widest font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Code</label>
                                            <input
                                                type="text"
                                                required
                                                maxLength={10}
                                                className={`w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm ${errors.locationCode ? 'border-red-500 ring-2 ring-red-50' : ''
                                                    }`}
                                                placeholder="e.g. CENTER"
                                                value={formData.locationCode}
                                                onChange={(e) => setFormData({ ...formData, locationCode: e.target.value.toUpperCase() })}
                                            />
                                        </div>
                                    </>
                                )}

                                <div className="flex gap-3 pt-6">
                                    <button
                                        type="button"
                                        onClick={() => { setIsModalOpen(false); setErrors({}); setError('') }}
                                        className="flex-1 py-3.5 bg-slate-50 text-slate-400 rounded-2xl font-bold hover:bg-slate-100 transition-all text-xs"
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="flex-1 py-3.5 bg-slate-900 text-white rounded-2xl font-bold hover:bg-slate-800 shadow-xl shadow-slate-200/50 shadow-slate-200 transition-all text-xs"
                                    >
                                        Save
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}

                {/* DELETE MODAL */}
                {deleteModal.show && (
                    <div className="fixed inset-0 z-[110] flex items-center justify-center p-4">
                        <div className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm animate-fade-in"
                            onClick={() => setDeleteModal({ show: false, id: null })}></div>

                        <div className="relative bg-white rounded-[2.5rem] shadow-2xl p-10 max-w-sm w-full animate-bounce-in border border-slate-100 text-center">
                            <div className="w-20 h-20 bg-red-50 text-red-600 rounded-full flex items-center justify-center mx-auto mb-6">
                                <Trash2 size={40} />
                            </div>
                            <h3 className="text-2xl font-bold text-slate-900 mb-3 font-outfit uppercase tracking-tight">Are you sure?</h3>
                            <p className="text-slate-500 text-sm mb-8 leading-relaxed font-medium">
                                This action cannot be undone. This location will be permanently removed.
                            </p>
                            <div className="flex gap-3">
                                <button
                                    onClick={() => setDeleteModal({ show: false, id: null })}
                                    className="flex-1 py-3.5 bg-slate-50 text-slate-500 rounded-2xl font-bold hover:bg-slate-100 transition-all text-xs"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={confirmDelete}
                                    className="flex-1 py-3.5 bg-red-600 text-white rounded-2xl font-bold hover:bg-red-700 shadow-lg shadow-red-200 transition-all text-xs"
                                >
                                    Delete
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
};

export default LocationPage;