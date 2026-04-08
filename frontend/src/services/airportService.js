import { EXTERNAL_URLS } from '../utils/constants';


let cachedAirports = null;

export const fetchAirports = async () => {
    if (cachedAirports) return cachedAirports;

    try {
        const response = await fetch(EXTERNAL_URLS.AIRPORTS_DATA);
        const data = await response.json();
        
        // Algolia dataset structure: { iata_code: "IST", name: "...", city: "...", country: "...", ... }
        // Fields we need: iata_code as iata, name, city, country
        cachedAirports = data
            .filter(a => a.iata_code && a.country && a.city && a.name)
            .map(a => ({
                iata: a.iata_code,
                name: a.name,
                city: a.city,
                country: a.country
            }));
        return cachedAirports;
    } catch (error) {
        console.error('Error fetching airport data:', error);
        return [];
    }
};

export const getHierarchicalData = (airports) => {
    const hierarchy = {};

    airports.forEach(airport => {
        const { country, city } = airport;
        
        if (!hierarchy[country]) {
            hierarchy[country] = new Set();
        }
        hierarchy[country].add(city);
    });

    const result = {};
    Object.keys(hierarchy).sort().forEach(country => {
        result[country] = Array.from(hierarchy[country]).sort();
    });

    return result;
};

export const getAirportsByCity = (airports, country, city) => {
    return airports
        .filter(a => a.country === country && a.city === city)
        .sort((a, b) => a.name.localeCompare(b.name));
};
