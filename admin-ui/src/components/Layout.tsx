import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { BarChart3, Building2, CreditCard, Home, LogOut, PackageCheck, Truck, Wallet } from 'lucide-react';
import { clearTokens } from '../api/authStorage';

const navItems = [
  { to: '/', label: 'Dashboard', icon: Home },
  { to: '/companies', label: 'Companies', icon: Building2 },
  { to: '/couriers', label: 'Couriers', icon: Truck },
  { to: '/earnings', label: 'Earnings', icon: PackageCheck },
  { to: '/payouts', label: 'Payouts', icon: CreditCard },
  { to: '/balances', label: 'Balances', icon: Wallet },
  { to: '/reports', label: 'Reports', icon: BarChart3 },
];

export function Layout() {
  const navigate = useNavigate();

  function logout() {
    clearTokens();
    navigate('/login');
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <aside className="fixed inset-y-0 left-0 hidden w-64 border-r border-slate-800 bg-slate-950/95 p-5 md:block">
        <Link to="/" className="text-xl font-bold tracking-tight text-white">
          CourierPay Admin
        </Link>
        <p className="mt-1 text-sm text-slate-400">Internal operations dashboard</p>

        <nav className="mt-8 space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-xl px-3 py-2 text-sm transition ${
                    isActive ? 'bg-cyan-500/15 text-cyan-300' : 'text-slate-300 hover:bg-slate-900 hover:text-white'
                  }`
                }
              >
                <Icon className="h-4 w-4" />
                {item.label}
              </NavLink>
            );
          })}
        </nav>

        <button
          onClick={logout}
          className="absolute bottom-5 left-5 right-5 flex items-center justify-center gap-2 rounded-xl border border-slate-800 px-3 py-2 text-sm text-slate-300 hover:bg-slate-900"
        >
          <LogOut className="h-4 w-4" />
          Logout
        </button>
      </aside>

      <main className="md:pl-64">
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
