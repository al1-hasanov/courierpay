import { useState } from "react";
import type { FormEvent } from "react";
import { useQuery } from '@tanstack/react-query';
import { getBalance } from '../api/courierpayApi';
import { Button, Card, Input, PageHeader } from '../components/ui';

export function BalancesPage() {
  const [courierId, setCourierId] = useState('');
  const [submittedCourierId, setSubmittedCourierId] = useState('');
  const balance = useQuery({ queryKey: ['balance', submittedCourierId], queryFn: () => getBalance(submittedCourierId), enabled: Boolean(submittedCourierId) });

  function submit(event: FormEvent) {
    event.preventDefault();
    setSubmittedCourierId(courierId);
  }

  return (
    <>
      <PageHeader title="Balances" description="Check available and reserved amounts by courier ID." />
      <div className="grid gap-6 lg:grid-cols-[380px_1fr]">
        <Card>
          <form onSubmit={submit} className="space-y-4">
            <Input value={courierId} onChange={(e) => setCourierId(e.target.value)} placeholder="Courier ID" required />
            <Button>Load balance</Button>
          </form>
        </Card>
        {balance.data ? (
          <Card>
            <p className="text-sm text-slate-400">Courier #{balance.data.courierId}</p>
            <p className="mt-4 text-3xl font-bold text-white">{balance.data.availableAmount}</p>
            <p className="text-sm text-slate-400">Available amount</p>
            <p className="mt-4 text-xl font-semibold text-slate-200">{balance.data.reservedAmount}</p>
            <p className="text-sm text-slate-400">Reserved amount</p>
          </Card>
        ) : null}
      </div>
    </>
  );
}
