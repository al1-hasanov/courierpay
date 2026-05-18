import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createCompany, getCompanies } from '../api/courierpayApi';
import { getApiErrorMessage } from '../api/client';
import { Button, Card, ErrorMessage, Input, PageHeader, Table } from '../components/ui';

export function CompaniesPage() {
  const queryClient = useQueryClient();
  const [nameFilter, setNameFilter] = useState('');
  const [name, setName] = useState('');
  const [commissionRate, setCommissionRate] = useState('10');
  const [error, setError] = useState('');

  const companies = useQuery({ queryKey: ['companies', nameFilter], queryFn: () => getCompanies(nameFilter) });
  const mutation = useMutation({
    mutationFn: createCompany,
    onSuccess: () => {
      setName('');
      setCommissionRate('10');
      queryClient.invalidateQueries({ queryKey: ['companies'] });
    },
    onError: (err) => setError(getApiErrorMessage(err)),
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    setError('');
    mutation.mutate({ name, commissionRate: Number(commissionRate) });
  }

  return (
    <>
      <PageHeader title="Companies" description="Create companies and review commission rates." />
      <div className="grid gap-6 lg:grid-cols-[380px_1fr]">
        <Card>
          <h2 className="mb-4 font-semibold text-white">Create company</h2>
          <form onSubmit={submit} className="space-y-4">
            <Input value={name} onChange={(e) => setName(e.target.value)} placeholder="Company name" required />
            <Input value={commissionRate} onChange={(e) => setCommissionRate(e.target.value)} type="number" step="0.01" min="0" max="100" placeholder="Commission rate" required />
            <ErrorMessage message={error} />
            <Button disabled={mutation.isPending}>Create</Button>
          </form>
        </Card>
        <Card>
          <div className="mb-4 flex items-center gap-3">
            <Input value={nameFilter} onChange={(e) => setNameFilter(e.target.value)} placeholder="Filter by name" />
          </div>
          <Table>
            <thead className="bg-slate-950/60"><tr><th className="px-4 py-3 text-left">ID</th><th className="px-4 py-3 text-left">Name</th><th className="px-4 py-3 text-left">Commission</th></tr></thead>
            <tbody className="divide-y divide-slate-800">
              {companies.data?.content.map((company) => (
                <tr key={company.id}><td className="px-4 py-3">{company.id}</td><td className="px-4 py-3">{company.name}</td><td className="px-4 py-3">{company.commissionRate}%</td></tr>
              ))}
            </tbody>
          </Table>
        </Card>
      </div>
    </>
  );
}
