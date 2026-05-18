import { Navigate, Outlet } from 'react-router-dom';
import { isAuthenticated } from '../api/authStorage';

export function ProtectedRoute() {
  return isAuthenticated() ? <Outlet /> : <Navigate to="/login" replace />;
}
