import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getEarnings, processEarning } from '../api/courierpayApi';
import { getApiErrorMessage } from '../api/client';
import { Card, ErrorMessage, PageHeader, SecondaryButton, Select, Table } from '../components/ui';

export function EarningsPage() {
  const queryClient = useQueryClient();
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const earnings = useQuery({ queryKey: ['earnings', status], queryFn: () => getEarnings(status) });
  const processMutation = useMutation({
    mutationFn: processEarning,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['earnings'] }),
    onError: (err) => setError(getApiErrorMessage(err)),
  });

  return (
    <>
      <PageHeader title="Earnings" description="Review earnings and process pending records." />
      <Card>
        <div className="mb-4 max-w-xs">
          <Select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All statuses</option><option value="PENDING">Pending</option><option value="PROCESSED">Processed</option>
          </Select>
        </div>
        <ErrorMessage message={error} />
        <div className="mt-4">
          <Table>
            <thead className="bg-slate-950/60"><tr><th className="px-4 py-3 text-left">ID</th><th className="px-4 py-3 text-left">Courier</th><th className="px-4 py-3 text-left">Gross</th><th className="px-4 py-3 text-left">Commission</th><th className="px-4 py-3 text-left">Net</th><th className="px-4 py-3 text-left">Status</th><th className="px-4 py-3 text-left">Action</th></tr></thead>
            <tbody className="divide-y divide-slate-800">
              {earnings.data?.content.map((earning) => (
                <tr key={earning.id}>
                  <td className="px-4 py-3">{earning.id}</td><td className="px-4 py-3">{earning.courierId}</td><td className="px-4 py-3">{earning.grossAmount}</td><td className="px-4 py-3">{earning.commissionAmount}</td><td className="px-4 py-3">{earning.netAmount}</td><td className="px-4 py-3">{earning.status}</td>
                  <td className="px-4 py-3">{earning.status === 'PENDING' ? <SecondaryButton onClick={() => processMutation.mutate(earning.id)} disabled={processMutation.isPending}>Process</SecondaryButton> : '-'}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </Card>
    </>
  );
}
