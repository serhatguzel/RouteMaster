import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import DashboardLayout from './components/DashboardLayout';
import DashboardHome from './components/DashboardHome';
import ProtectedRoute from './components/ProtectedRoute';
import { ROLES, PATHS } from './utils/constants';
import LocationPage from './pages/Location';
import TransportationPage from './pages/Transportation';
import RouteSearchPage from './pages/RouteSearch';

function App() {
  return (
    <Router>
      <Routes>
        <Route path={PATHS.LOGIN} element={<Login />} />

        <Route path="/" element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }>

          <Route path={PATHS.DASHBOARD.replace('/', '')} element={<DashboardHome />} />
          <Route path={PATHS.ROUTES.replace('/', '')} element={<RouteSearchPage />} />

          <Route path={PATHS.LOCATIONS.replace('/', '')} element={
            <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
              <LocationPage />
            </ProtectedRoute>
          } />

          <Route path={PATHS.TRANSPORTATIONS.replace('/', '')} element={
            <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
              <TransportationPage />
            </ProtectedRoute>
          } />

          <Route index element={<Navigate to={PATHS.DASHBOARD} replace />} />
          <Route path="*" element={<Navigate to={PATHS.DASHBOARD} replace />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
