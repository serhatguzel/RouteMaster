import React from 'react';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

const RouteSearchBar = ({
    searchCriteria,
    locations,
    searching,
    handleChange,
    handleSearch
}) => {
    return (
        <div className="bg-white p-6 rounded-3xl shadow-xl border border-slate-100">
            <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-6 items-end">
                {/* Origin */}
                <div className="space-y-2">
                    <label className="text-sm font-semibold text-slate-700">Origin</label>
                    <select
                        name="originId"
                        className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer"
                        value={searchCriteria.originId}
                        onChange={handleChange}
                    >
                        <option value="">Select Origin</option>
                        {locations.map(loc => (
                            <option key={loc.id} value={loc.id}>{loc.name} ({loc.locationCode})</option>
                        ))}
                    </select>
                </div>

                {/* Destination */}
                <div className="space-y-2">
                    <label className="text-sm font-semibold text-slate-700">Destination</label>
                    <select
                        name="destinationId"
                        className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-2 focus:ring-slate-900 outline-none transition-all text-slate-900 font-bold text-sm cursor-pointer"
                        value={searchCriteria.destinationId}
                        onChange={handleChange}
                    >
                        <option value="">Select Destination</option>
                        {locations.map(loc => (
                            <option key={loc.id} value={loc.id}>{loc.name} ({loc.locationCode})</option>
                        ))}
                    </select>
                </div>

                {/* Travel Date */}
                <div className="space-y-2">
                    <label className="text-sm font-semibold text-slate-700">Travel Date</label>
                    <DatePicker
                        value={searchCriteria.date}
                        format="DD-MM-YYYY"
                        onChange={(newValue) => handleChange({ target: { name: 'date', value: newValue } })}
                        slotProps={{
                            textField: {
                                fullWidth: true,
                                size: 'small',
                                sx: { '& .MuiOutlinedInput-root': { borderRadius: '12px', backgroundColor: '#f8fafc' } }
                            }
                        }}
                    />
                </div>

                {/* Submit Button */}
                <button
                    type="submit"
                    disabled={searching}
                    className="bg-slate-900 hover:bg-slate-800 text-white font-bold py-3.5 px-6 rounded-2xl shadow-xl shadow-slate-200 transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-95"
                >
                    {searching ? 'Searching...' : 'Search'}
                </button>
            </form>
        </div>
    );
};

export default RouteSearchBar;
