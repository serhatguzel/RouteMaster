import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import DashboardLayout from './components/DashboardLayout';
import DashboardHome from './components/DashboardHome';
import ProtectedRoute from './components/ProtectedRoute';
import { ROLES, PATHS } from './utils/constants';

// ---- GEÇİCİ SAYFALAR
const RouteSearchPage = () => <div className="p-6 bg-white rounded-2xl shadow-sm border border-slate-100"><h2 className="text-2xl font-bold font-outfit text-slate-800">Route Search (Aviation Network)</h2><p className="text-slate-500 mt-2">Visible to everyone. Enter Origin and Destination to find flights.</p></div>;
const LocationsPage = () => <div className="p-6 bg-white rounded-2xl shadow-sm border border-slate-100"><h2 className="text-2xl font-bold font-outfit text-slate-800">Location Management</h2><p className="text-slate-500 mt-2">Admin only. CRUD operations for Cities and Airports.</p></div>;
const TransportationsPage = () => <div className="p-6 bg-white rounded-2xl shadow-sm border border-slate-100"><h2 className="text-2xl font-bold font-outfit text-slate-800">Transportation Management</h2><p className="text-slate-500 mt-2">Admin only. CRUD operations for Flights.</p></div>;

function App() {
  return (
    <Router>
      <Routes>
        {/* 1. Dışarıya Açık Kapı */}
        <Route path={PATHS.LOGIN} element={<Login />} />

        {/* 2. Güvenli Bölge (Sadece giriş yapanlar girebilir) */}
        <Route path="/" element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }>
          {/* --- Layout'un içindeki Alt Sayfalar --- */}

          {/* A. Ortak Sayfalar */}
          <Route path={PATHS.DASHBOARD.replace('/', '')} element={<DashboardHome />} />
          <Route path={PATHS.ROUTES.replace('/', '')} element={<RouteSearchPage />} />

          {/* B. Kısıtlı Sayfalar (Sadece ADMIN) */}
          <Route path={PATHS.LOCATIONS.replace('/', '')} element={
            <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
              <LocationsPage />
            </ProtectedRoute>
          } />

          <Route path={PATHS.TRANSPORTATIONS.replace('/', '')} element={
            <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
              <TransportationsPage />
            </ProtectedRoute>
          } />

          {/* C. Yönlendirmeler */}
          <Route index element={<Navigate to={PATHS.DASHBOARD} replace />} />
          <Route path="*" element={<Navigate to={PATHS.DASHBOARD} replace />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
