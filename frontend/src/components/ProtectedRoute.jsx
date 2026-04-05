import React from 'react';
import { Navigate } from 'react-router-dom';
import { PATHS, STORAGE_KEYS } from '../utils/constants';

const ProtectedRoute = ({ children, allowedRoles }) => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    const userRole = localStorage.getItem(STORAGE_KEYS.ROLE);

    if (!token) {
        return <Navigate to={PATHS.LOGIN} replace />;
    }

    if (allowedRoles && !allowedRoles.includes(userRole)) {
        return <Navigate to={PATHS.DASHBOARD} replace />;
    }

    return children;
};

export default ProtectedRoute;
