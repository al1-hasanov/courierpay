import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { approvePayout, getPayouts, rejectPayout } from '../api/courierpayApi';
import { getApiErrorMessage } from '../api/client';
import { Card, ErrorMessage, PageHeader, SecondaryButton, Select, Table } from '../components/ui';

export function PayoutsPage() {
  const queryClient = useQueryClient();
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const payouts = useQuery({ queryKey: ['payouts', status], queryFn: () => getPayouts(status) });
  const approve = useMutation({ mutationFn: approvePayout, onSuccess: () => queryClient.invalidateQueries({ queryKey: ['payouts'] }), onError: (err) => setError(getApiErrorMessage(err)) });
  const reject = useMutation({ mutationFn: rejectPayout, onSuccess: () => queryClient.invalidateQueries({ queryKey: ['payouts'] }), onError: (err) => setError(getApiErrorMessage(err)) });

  return (
    <>
      <PageHeader title="Payouts" description="Approve or reject requested payouts." />
      <Card>
        <div className="mb-4 max-w-xs">
          <Select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All statuses</option><option value="REQUESTED">Requested</option><option value="COMPLETED">Completed</option><option value="REJECTED">Rejected</option>
          </Select>
        </div>
        <ErrorMessage message={error} />
        <div className="mt-4">
          <Table>
            <thead className="bg-slate-950/60"><tr><th className="px-4 py-3 text-left">ID</th><th className="px-4 py-3 text-left">Courier</th><th className="px-4 py-3 text-left">Amount</th><th className="px-4 py-3 text-left">Status</th><th className="px-4 py-3 text-left">Actions</th></tr></thead>
            <tbody className="divide-y divide-slate-800">
              {payouts.data?.content.map((payout) => (
                <tr key={payout.id}>
                  <td className="px-4 py-3">{payout.id}</td><td className="px-4 py-3">{payout.courierId}</td><td className="px-4 py-3">{payout.amount}</td><td className="px-4 py-3">{payout.status}</td>
                  <td className="space-x-2 px-4 py-3">{payout.status === 'REQUESTED' ? <><SecondaryButton onClick={() => approve.mutate(payout.id)} disabled={approve.isPending}>Approve</SecondaryButton><SecondaryButton onClick={() => reject.mutate(payout.id)} disabled={reject.isPending}>Reject</SecondaryButton></> : '-'}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </Card>
    </>
  );
}
