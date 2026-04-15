export const ROLES = {
    ADMIN: 'ROLE_ADMIN',
    AGENCY: 'ROLE_AGENCY'
};

export const PATHS = {
    LOGIN: '/login',
    DASHBOARD: '/dashboard',
    ROUTES: '/routes',
    LOCATIONS: '/locations',
    TRANSPORTATIONS: '/transportations'
};

export const STORAGE_KEYS = {
    TOKEN: 'token',
    ROLE: 'role',
    REFRESH_TOKEN: 'REFRESH_TOKEN'
};

export const LOCATION_TYPES = {
    AIRPORT: 'AIRPORT',
    OTHER: 'OTHER'
};

export const TRANSPORTATION_TYPES = {
    FLIGHT: 'FLIGHT',
    BUS: 'BUS',
    SUBWAY: 'SUBWAY',
    UBER: 'UBER'
};

export const EXTERNAL_URLS = {
    AIRPORTS_DATA: 'https://raw.githubusercontent.com/algolia/datasets/master/airports/airports.json'
};

export const API_ENDPOINTS = {
    AUTH: {
        LOGIN: '/api/v1/auth/login',
        LOGOUT: '/api/v1/auth/logout',
        REFRESH: '/api/v1/auth/refresh'
    },
    LOCATIONS: {
        BASE: '/locations'
    },
    TRANSPORTATIONS: {
        BASE: '/transportations'
    },
    ROUTES: {
        SEARCH: '/routes/search'
    }
};

export const MENU_ITEMS = [
    { path: PATHS.ROUTES, name: 'Route Search', allowedRoles: [ROLES.ADMIN, ROLES.AGENCY] },
    { path: PATHS.LOCATIONS, name: 'Locations', allowedRoles: [ROLES.ADMIN] },
    { path: PATHS.TRANSPORTATIONS, name: 'Transportations', allowedRoles: [ROLES.ADMIN] },
];
