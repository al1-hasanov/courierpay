import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Layout } from './components/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { BalancesPage } from './pages/BalancesPage';
import { CompaniesPage } from './pages/CompaniesPage';
import { CouriersPage } from './pages/CouriersPage';
import { DashboardPage } from './pages/DashboardPage';
import { EarningsPage } from './pages/EarningsPage';
import { LoginPage } from './pages/LoginPage';
import { PayoutsPage } from './pages/PayoutsPage';
import { ReportsPage } from './pages/ReportsPage';

const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <Layout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: '/companies', element: <CompaniesPage /> },
          { path: '/couriers', element: <CouriersPage /> },
          { path: '/earnings', element: <EarningsPage /> },
          { path: '/payouts', element: <PayoutsPage /> },
          { path: '/balances', element: <BalancesPage /> },
          { path: '/reports', element: <ReportsPage /> },
        ],
      },
    ],
  },
]);

export default function App() {
  return <RouterProvider router={router} />;
}
