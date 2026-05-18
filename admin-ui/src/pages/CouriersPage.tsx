import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getCouriers } from '../api/courierpayApi';
import { Card, Input, PageHeader, Select, Table } from '../components/ui';

export function CouriersPage() {
  const [fullName, setFullName] = useState('');
  const [active, setActive] = useState('');
  const couriers = useQuery({ queryKey: ['couriers', fullName, active], queryFn: () => getCouriers({ fullName, active }) });

  return (
    <>
      <PageHeader title="Couriers" description="Search registered couriers." />
      <Card>
        <div className="mb-4 grid gap-3 md:grid-cols-3">
          <Input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Filter by full name" />
          <Select value={active} onChange={(e) => setActive(e.target.value)}>
            <option value="">All statuses</option><option value="true">Active</option><option value="false">Inactive</option>
          </Select>
        </div>
        <Table>
          <thead className="bg-slate-950/60"><tr><th className="px-4 py-3 text-left">ID</th><th className="px-4 py-3 text-left">Name</th><th className="px-4 py-3 text-left">Company</th><th className="px-4 py-3 text-left">Phone</th><th className="px-4 py-3 text-left">Active</th></tr></thead>
          <tbody className="divide-y divide-slate-800">
            {couriers.data?.content.map((courier) => (
              <tr key={courier.id}><td className="px-4 py-3">{courier.id}</td><td className="px-4 py-3">{courier.fullName}</td><td className="px-4 py-3">{courier.companyName}</td><td className="px-4 py-3">{courier.phoneNumber}</td><td className="px-4 py-3">{courier.active ? 'Yes' : 'No'}</td></tr>
            ))}
          </tbody>
        </Table>
      </Card>
    </>
  );
}
