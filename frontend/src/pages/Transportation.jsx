import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Plane, Bus, Train, Plus, Trash2, Loader2, Search, Edit, MapPin, Calendar } from 'lucide-react';

const TransportationPage = () => {
    const [loading, setLoading] = useState(true);
    const [transportations, setTransportations] = useState([]);
    const [locations, setLocations] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [error, setError] = useState('');
    const [errors, setErrors] = useState({});
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentId, setCurrentId] = useState(null);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [itemToDelete, setItemToDelete] = useState(null);

    const [formData, setFormData] = useState({
        origin: { id: '' },
        destination: { id: '' },
        transportationType: 'FLIGHT',
        operationDays: []
    });

    useEffect(() => {
        fetchInitialData();
    }, []);

    const fetchInitialData = async () => {
        try {
            setLoading(true);
            const [transRes, locRes] = await Promise.all([
                api.get('/transportations'),
                api.get('/locations')
            ]);
            setTransportations(transRes.data);
            setLocations(locRes.data);
        } catch (err) {
            setError('Veriler yüklenirken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const toggleDay = (day) => {
        const currentDays = [...formData.operationDays];
        const index = currentDays.indexOf(day);
        if (index > -1) {
            currentDays.splice(index, 1);
        } else {
            currentDays.push(day);
        }
        setFormData({ ...formData, operationDays: currentDays.sort() });
        if (errors.operationDays) setErrors({ ...errors, operationDays: false });
    };

    /* --- VALIDATION LOGIC --- */
    const validate = () => {
        let tempErrors = {};
        let errorMessage = '';

        if (!formData.origin.id) tempErrors.origin = true;
        if (!formData.destination.id) tempErrors.destination = true;
        if (!formData.transportationType) tempErrors.transportationType = true;
        if (formData.operationDays.length === 0) tempErrors.operationDays = true;

        if (formData.origin.id && formData.destination.id && formData.origin.id === formData.destination.id) {
            tempErrors.route = true;
            errorMessage = 'Origin and destination cannot be the same!';
        }

        if (Object.keys(tempErrors).length > 0 && !errorMessage) {
            errorMessage = 'Please fill all required fields correctly.';
        }

        setErrors(tempErrors);
        if (errorMessage) {
            setError(errorMessage);
            setTimeout(() => setError(''), 3000);
        }

        return Object.keys(tempErrors).length === 0;
    };

    /* --- CRUD LOGIC --- */
    const handleAdd = () => {
        setFormData({
            origin: { id: '' },
            destination: { id: '' },
            transportationType: 'FLIGHT',
            operationDays: []
        });
        setIsEditing(false);
        setIsModalOpen(true);
        setErrors({});
    };

    const handleEdit = (t) => {
        setFormData({
            origin: { id: t.origin.id },
            destination: { id: t.destination.id },
            transportationType: t.transportationType,
            operationDays: [...t.operationDays]
        });
        setCurrentId(t.id);
        setIsEditing(true);
        setIsModalOpen(true);
        setErrors({});
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) {
            return;
        }

        try {
            if (isEditing) {
                const res = await api.put(`/transportations/${currentId}`, formData);
                setTransportations(transportations.map(t => t.id === currentId ? res.data : t));
            } else {
                const res = await api.post('/transportations', formData);
                setTransportations([...transportations, res.data]);
            }
            setIsModalOpen(false);
            setErrors({});
        } catch (err) {
            setError('Something went wrong. Please check your data.');
        }
    };

    const handleDelete = (id) => {
        setItemToDelete(id);
        setIsDeleteModalOpen(true);
    };

    const confirmDelete = async () => {
        try {
            await api.delete(`/transportations/${itemToDelete}`);
            setTransportations(transportations.filter(t => t.id !== itemToDelete));
            setIsDeleteModalOpen(false);
        } catch (err) {
            setError('Could not delete. It might be used by another service.');
            setIsDeleteModalOpen(false);
        }
    };

    const filteredTransportations = transportations.filter(t => {
        const search = searchTerm.toLowerCase();
        return (
            t.origin.city.toLowerCase().includes(search) ||
            t.origin.country.toLowerCase().includes(search) ||
            t.origin.locationCode.toLowerCase().includes(search) ||
            t.destination.city.toLowerCase().includes(search) ||
            t.destination.country.toLowerCase().includes(search) ||
            t.destination.locationCode.toLowerCase().includes(search) ||
            t.transportationType.toLowerCase().includes(search)
        );
    });

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <Loader2 className="w-8 h-8 text-slate-400 animate-spin" />
            </div>
        );
    }

    return (
        <>
            {/* ERROR TOAST - EN ÜSTTE */}
            {error && (
                <div className="fixed top-6 right-6 z-[999] bg-red-50 border border-red-100 text-red-600 px-6 py-4 rounded-3xl shadow-2xl animate-bounce-in flex items-center gap-3">
                    <div className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></div>
                    <span className="font-semibold text-sm">{error}</span>
                </div>
            )}

            <div className="space-y-6 animate-fade-in pb-12">
                {/* HEADER & SEARCH */}
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold font-outfit text-slate-900">Transportation Management</h1>
                        <p className="text-slate-500 text-sm mt-1">Manage routes, schedules and transportation types.</p>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                            <input
                                type="text"
                                placeholder="Search routes..."
                                className="pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-slate-900 outline-none text-sm w-64 text-slate-900 transition-all shadow-xl shadow-slate-200/50"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                        <button
                            onClick={handleAdd}
                            className="flex items-center gap-2 bg-slate-900 text-white px-5 py-2.5 rounded-xl hover:bg-slate-800 transition-all font-bold text-sm shadow-lg shadow-slate-100 active:scale-95"
                        >
                            <Plus size={18} />
                            New Transportation
                        </button>
                    </div>
                </div>

                {/* TABLE */}
                <div className="bg-white rounded-3xl shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full border-collapse text-left">
                            <thead>
                                <tr className="bg-slate-50/50 border-b border-slate-100">
                                    <th className="px-6 py-4 text-[11px] font-bold tracking-widest font-bold text-slate-500 uppercase tracking-widest">Type</th>
                                    <th className="px-6 py-4 text-[11px] font-bold tracking-widest font-bold text-slate-500 uppercase tracking-widest">Route</th>
                                    <th className="px-6 py-4 text-[11px] font-bold tracking-widest font-bold text-slate-500 uppercase tracking-widest">Days</th>
                                    <th className="px-6 py-4 text-[11px] font-bold tracking-widest font-bold text-slate-500 uppercase tracking-widest text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-50">
                                {filteredTransportations.map((t) => (
                                    <tr key={t.id} className="hover:bg-slate-50/30 transition-colors group">
                                        <td className="px-6 py-4">
                                            <span className="flex items-center gap-2 font-bold text-slate-700 text-sm">
                                                {t.transportationType === 'FLIGHT' ? <Plane className="w-4 h-4 text-blue-500" /> : <Bus className="w-4 h-4 text-emerald-500" />}
                                                {t.transportationType}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-4">
                                                <div className="text-sm">
                                                    <p className="font-bold text-slate-900">{t.origin.city}</p>
                                                    <p className="text-[10px] text-slate-400 font-mono tracking-tighter uppercase">{t.origin.locationCode}</p>
                                                </div>
                                                <div className="h-[2px] w-6 bg-slate-100 relative">
                                                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1.5 h-1.5 bg-slate-200 rounded-full"></div>
                                                </div>
                                                <div className="text-sm text-right">
                                                    <p className="font-bold text-slate-900">{t.destination.city}</p>
                                                    <p className="text-[10px] text-slate-400 font-mono tracking-tighter uppercase">{t.destination.locationCode}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex gap-1 flex-wrap">
                                                {t.operationDayNames && t.operationDayNames.map((name, index) => (
                                                    <span key={index} className="px-2 py-0.5 bg-slate-100 text-slate-600 text-[9px] rounded-md font-bold uppercase tracking-tighter border border-slate-200">
                                                        {name}
                                                    </span>
                                                ))}
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <div className="flex justify-end gap-2">
                                                <button onClick={() => handleEdit(t)} className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-50 rounded-lg transition-all"><Edit size={16} /></button>
                                                <button onClick={() => handleDelete(t.id)} className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"><Trash2 size={16} /></button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* MODAL */}
                {isModalOpen && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                        <div className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" onClick={() => setIsModalOpen(false)}></div>
                        <div className="relative bg-white w-full max-w-lg rounded-[2rem] shadow-2xl overflow-hidden animate-slide-up">
                            <div className="p-8">
                                <div className="flex justify-between items-center mb-8">
                                    <h3 className="text-2xl font-bold font-outfit text-slate-900">{isEditing ? 'Edit Transportation' : 'New Transportation'}</h3>
                                    <button onClick={() => setIsModalOpen(false)} className="p-2 bg-slate-50 rounded-full text-slate-400 hover:text-slate-900 transition-all">
                                        <Plus className="w-6 h-6 rotate-45" />
                                    </button>
                                </div>

                                <form onSubmit={handleSubmit} className="space-y-6" noValidate>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Origin</label>
                                            <select
                                                required
                                                className={`w-full px-4 py-3 bg-slate-50 border rounded-3xl focus:ring-2 focus:ring-slate-900 outline-none font-bold text-sm text-slate-900 transition-all ${errors.origin || errors.route ? 'border-red-500 ring-2 ring-red-50' : 'border-slate-100'
                                                    }`}
                                                value={formData.origin.id}
                                                onChange={(e) => setFormData({ ...formData, origin: { id: e.target.value } })}
                                            >
                                                <option value="">Select Origin</option>
                                                {locations.map(loc => (
                                                    <option key={loc.id} value={loc.id}>{loc.city} ({loc.locationCode})</option>
                                                ))}
                                            </select>
                                        </div>
                                        <div>
                                            <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-2 px-1">Destination</label>
                                            <select
                                                required
                                                className={`w-full px-4 py-3 bg-slate-50 border rounded-3xl focus:ring-2 focus:ring-slate-900 outline-none font-bold text-sm text-slate-900 transition-all ${errors.destination || errors.route ? 'border-red-500 ring-2 ring-red-50' : 'border-slate-100'
                                                    }`}
                                                value={formData.destination.id}
                                                onChange={(e) => setFormData({ ...formData, destination: { id: e.target.value } })}
                                            >
                                                <option value="">Select Destination</option>
                                                {locations.map(loc => (
                                                    <option key={loc.id} value={loc.id}>{loc.city} ({loc.locationCode})</option>
                                                ))}
                                            </select>
                                        </div>
                                    </div>

                                    <div>
                                        <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-3 px-1 text-center font-outfit">Transportation Type</label>
                                        <div className={`grid grid-cols-4 gap-2 p-1 rounded-3xl transition-all ${errors.transportationType ? 'ring-2 ring-red-500' : ''}`}>
                                            {['FLIGHT', 'BUS', 'SUBWAY', 'UBER'].map(type => (
                                                <button
                                                    key={type} type="button"
                                                    onClick={() => setFormData({ ...formData, transportationType: type })}
                                                    className={`py-3 rounded-3xl text-[9px] font-black tracking-tighter transition-all ${formData.transportationType === type ? 'bg-slate-900 text-white shadow-lg' : 'bg-slate-50 text-slate-400 hover:bg-slate-100'
                                                        }`}
                                                >
                                                    {type}
                                                </button>
                                            ))}
                                        </div>
                                    </div>

                                    <div>
                                        <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-3 px-1 text-center font-outfit">Operating Days</label>
                                        <div className={`flex justify-between gap-1 p-2 rounded-3xl transition-all ${errors.operationDays ? 'bg-red-50 ring-2 ring-red-100' : ''}`}>
                                            {[
                                                { id: 1, l: 'M' }, { id: 2, l: 'T' }, { id: 3, l: 'W' },
                                                { id: 4, l: 'T' }, { id: 5, l: 'F' }, { id: 6, l: 'S' }, { id: 7, l: 'S' }
                                            ].map(day => (
                                                <button
                                                    key={day.id} type="button"
                                                    onClick={() => toggleDay(day.id)}
                                                    className={`w-10 h-10 rounded-full font-bold text-[11px] transition-all flex items-center justify-center ${formData.operationDays.includes(day.id) ? 'bg-blue-600 text-white shadow-md ring-4 ring-blue-50' : 'bg-slate-50 text-slate-400 hover:bg-slate-100'
                                                        }`}
                                                >
                                                    {day.l}
                                                </button>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="flex gap-3 pt-4">
                                        <button
                                            type="button"
                                            onClick={() => setIsModalOpen(false)}
                                            className="flex-1 py-3 bg-slate-50 text-slate-400 rounded-3xl font-bold hover:bg-slate-100 transition-all text-[11px] font-bold tracking-widest"
                                        >
                                            Cancel
                                        </button>
                                        <button
                                            type="submit"
                                            className="flex-1 py-3 bg-slate-900 text-white rounded-3xl font-bold hover:bg-slate-800 transition-all text-[11px] font-bold tracking-widest shadow-xl shadow-slate-200"
                                        >
                                            {isEditing ? 'Save Changes' : 'Create Sefer'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                )}

                {/* DELETE MODAL */}
                {isDeleteModalOpen && (
                    <div className="fixed inset-0 z-[110] flex items-center justify-center p-4">
                        <div className="absolute inset-0 bg-slate-900/60 backdrop-blur-md" onClick={() => setIsDeleteModalOpen(false)}></div>
                        <div className="relative bg-white w-full max-sm rounded-[2.5rem] shadow-2xl p-10 text-center animate-bounce-in border border-slate-100">
                            <div className="w-20 h-20 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-6">
                                <Trash2 size={40} />
                            </div>
                            <h3 className="text-2xl font-bold text-slate-900 mb-3 font-outfit">Are you sure?</h3>
                            <p className="text-slate-500 text-sm mb-8 leading-relaxed font-medium">This action cannot be undone. This route will be permanently removed.</p>

                            <div className="flex gap-3 text-center">
                                <button
                                    onClick={() => setIsDeleteModalOpen(false)}
                                    className="flex-1 py-3 bg-slate-50 text-slate-500 rounded-3xl font-bold hover:bg-slate-100 transition-all text-[11px] font-bold tracking-widest"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={confirmDelete}
                                    className="flex-1 py-3 bg-red-600 text-white rounded-3xl font-bold hover:bg-red-700 transition-all text-[11px] font-bold tracking-widest shadow-lg shadow-red-200"
                                >
                                    Delete Now
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
};

export default TransportationPage;
