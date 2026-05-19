import { useQuery } from '@tanstack/react-query';
import { Building2, CreditCard, PackageCheck, Server, Truck } from 'lucide-react';
import { getCompanies, getCouriers, getEarnings, getHealth, getPayouts } from '../api/courierpayApi';
import { Card, PageHeader } from '../components/ui';

export function DashboardPage() {
  const health = useQuery({ queryKey: ['health'], queryFn: getHealth });
  const companies = useQuery({ queryKey: ['companies', 'dashboard'], queryFn: () => getCompanies() });
  const couriers = useQuery({ queryKey: ['couriers', 'dashboard'], queryFn: () => getCouriers() });
  const earnings = useQuery({ queryKey: ['earnings', 'dashboard'], queryFn: () => getEarnings() });
  const payouts = useQuery({ queryKey: ['payouts', 'dashboard'], queryFn: () => getPayouts() });

  const stats = [
    { label: 'API status', value: health.data?.status ?? '...', icon: Server },
    { label: 'Companies', value: companies.data?.totalElements ?? '...', icon: Building2 },
    { label: 'Couriers', value: couriers.data?.totalElements ?? '...', icon: Truck },
    { label: 'Earnings', value: earnings.data?.totalElements ?? '...', icon: PackageCheck },
    { label: 'Payouts', value: payouts.data?.totalElements ?? '...', icon: CreditCard },
  ];

  return (
    <>
      <PageHeader title="Dashboard" description="Quick operational overview from the CourierPay API." />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.label}>
              <Icon className="h-5 w-5 text-cyan-300" />
              <p className="mt-4 text-sm text-slate-400">{stat.label}</p>
              <p className="mt-1 text-2xl font-bold text-white">{stat.value}</p>
            </Card>
          );
        })}
      </div>
    </>
  );
}
