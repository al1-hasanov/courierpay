import { useEffect, useState } from 'react';
import { getAuditLogs } from '../api/courierpayApi';
import type { AuditLog } from '../types/api';

const actions = [
  'REGISTERED_USER',
  'LOGIN',
  'REFRESHED_TOKEN',
  'CREATED_COMPANY',
  'CREATED_COURIER',
  'CREATED_EARNING',
  'PROCESSED_EARNING',
  'REQUESTED_PAYOUT',
  'APPROVED_PAYOUT',
  'REJECTED_PAYOUT',
  'CREDITED_BALANCE',
  'DEBITED_BALANCE',
  'RECORDED_TRANSACTION',
];

export function AuditLogsPage() {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [actorEmail, setActorEmail] = useState('');
  const [action, setAction] = useState('');
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const page = await getAuditLogs({ actorEmail, action, status });
      setLogs(page.content);
    } catch {
      setError('Could not load audit logs. Make sure your user has the ADMIN role.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Audit Logs</h1>
          <p className="mt-1 text-sm text-slate-400">Track important authentication, payout, earning, company, and courier actions.</p>
        </div>
        <button
          onClick={load}
          disabled={loading}
          className="rounded-xl bg-cyan-500 px-4 py-2 text-sm font-semibold text-slate-950 hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? 'Loading...' : 'Refresh'}
        </button>
      </div>

      <div className="mb-4 grid gap-3 lg:grid-cols-4">
        <input
          value={actorEmail}
          onChange={(event) => setActorEmail(event.target.value)}
          placeholder="Actor email"
          className="rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-white placeholder:text-slate-500"
        />
        <select
          value={action}
          onChange={(event) => setAction(event.target.value)}
          className="rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-white"
        >
          <option value="">All actions</option>
          {actions.map((item) => (
            <option key={item} value={item}>{item}</option>
          ))}
        </select>
        <select
          value={status}
          onChange={(event) => setStatus(event.target.value)}
          className="rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-white"
        >
          <option value="">All statuses</option>
          <option value="SUCCESS">SUCCESS</option>
          <option value="FAILURE">FAILURE</option>
        </select>
        <button
          onClick={load}
          disabled={loading}
          className="rounded-xl border border-slate-800 px-4 py-2 text-sm font-semibold text-slate-200 hover:bg-slate-900 disabled:cursor-not-allowed disabled:opacity-60"
        >
          Apply filters
        </button>
      </div>

      {error ? <div className="mb-4 rounded-xl border border-red-900/60 bg-red-950/40 px-4 py-3 text-sm text-red-200">{error}</div> : null}

      <div className="overflow-hidden rounded-2xl border border-slate-800">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-800 text-sm">
            <thead className="bg-slate-900/70 text-slate-300">
              <tr>
                <th className="px-4 py-3 text-left">Time</th>
                <th className="px-4 py-3 text-left">Actor</th>
                <th className="px-4 py-3 text-left">Action</th>
                <th className="px-4 py-3 text-left">Entity</th>
                <th className="px-4 py-3 text-left">Status</th>
                <th className="px-4 py-3 text-left">Message</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {logs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-900/50">
                  <td className="whitespace-nowrap px-4 py-3 text-slate-300">{new Date(log.createdAt).toLocaleString()}</td>
                  <td className="whitespace-nowrap px-4 py-3 text-slate-300">{log.actorEmail ?? 'System'}</td>
                  <td className="whitespace-nowrap px-4 py-3 font-medium text-white">{log.action}</td>
                  <td className="whitespace-nowrap px-4 py-3 text-slate-300">
                    {log.entityType ?? '-'} {log.entityId ? `#${log.entityId}` : ''}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3">
                    <span className="rounded-full border border-slate-700 px-2 py-1 text-xs text-slate-200">{log.status}</span>
                  </td>
                  <td className="min-w-60 px-4 py-3 text-slate-300">{log.message ?? '-'}</td>
                </tr>
              ))}
              {logs.length === 0 ? (
                <tr>
                  <td className="px-4 py-6 text-center text-slate-400" colSpan={6}>
                    {loading ? 'Loading audit logs...' : 'No audit logs found.'}
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
