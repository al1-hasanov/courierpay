import { useState } from 'react';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { BarChart3, Building2, CreditCard, Home, LogOut, Menu, PackageCheck, Truck, Wallet, X } from 'lucide-react';
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
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  function logout() {
    clearTokens();
    setMobileMenuOpen(false);
    navigate('/login');
  }

  function closeMobileMenu() {
    setMobileMenuOpen(false);
  }

  const navigation = (
      <>
        <nav className="mt-8 space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
                <NavLink
                    key={item.to}
                    to={item.to}
                    onClick={closeMobileMenu}
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
            className="mt-8 flex w-full items-center justify-center gap-2 rounded-xl border border-slate-800 px-3 py-2 text-sm text-slate-300 hover:bg-slate-900 md:absolute md:bottom-5 md:left-5 md:right-5 md:mt-0 md:w-auto"
        >
          <LogOut className="h-4 w-4" />
          Logout
        </button>
      </>
  );

  return (
      <div className="min-h-screen bg-slate-950 text-slate-100">
        <aside className="fixed inset-y-0 left-0 hidden w-64 border-r border-slate-800 bg-slate-950/95 p-5 md:block">
          <Link to="/" className="text-xl font-bold tracking-tight text-white">
            CourierPay Admin
          </Link>
          <p className="mt-1 text-sm text-slate-400">Internal operations dashboard</p>
          {navigation}
        </aside>

        <header className="sticky top-0 z-30 flex items-center justify-between border-b border-slate-800 bg-slate-950/95 px-4 py-3 backdrop-blur md:hidden">
          <Link to="/" className="text-lg font-bold tracking-tight text-white" onClick={closeMobileMenu}>
            CourierPay Admin
          </Link>
          <button
              type="button"
              onClick={() => setMobileMenuOpen(true)}
              className="rounded-xl border border-slate-800 p-2 text-slate-200 hover:bg-slate-900"
              aria-label="Open navigation menu"
          >
            <Menu className="h-5 w-5" />
          </button>
        </header>

        {mobileMenuOpen ? (
            <div className="fixed inset-0 z-40 md:hidden">
              <button
                  type="button"
                  aria-label="Close navigation menu"
                  className="absolute inset-0 bg-black/60"
                  onClick={closeMobileMenu}
              />
              <aside className="absolute inset-y-0 left-0 flex w-72 max-w-[85vw] flex-col border-r border-slate-800 bg-slate-950 p-5 shadow-2xl">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <Link to="/" className="text-xl font-bold tracking-tight text-white" onClick={closeMobileMenu}>
                      CourierPay Admin
                    </Link>
                    <p className="mt-1 text-sm text-slate-400">Internal operations dashboard</p>
                  </div>
                  <button
                      type="button"
                      onClick={closeMobileMenu}
                      className="rounded-xl border border-slate-800 p-2 text-slate-200 hover:bg-slate-900"
                      aria-label="Close navigation menu"
                  >
                    <X className="h-5 w-5" />
                  </button>
                </div>
                {navigation}
              </aside>
            </div>
        ) : null}

        <main className="md:pl-64">
          <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
            <Outlet />
          </div>
        </main>
      </div>
  );
}
