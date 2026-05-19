import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from 'react-router-dom';
import { login } from '../api/courierpayApi';
import { getApiErrorMessage } from '../api/client';
import { saveTokens } from '../api/authStorage';
import { Button, Card, ErrorMessage, Input } from '../components/ui';

export function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await login(email, password);
      saveTokens(response.accessToken, response.refreshToken);
      navigate('/');
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-950 px-4 text-slate-100">
      <Card className="w-full max-w-md">
        <h1 className="text-2xl font-bold text-white">CourierPay Admin</h1>
        <p className="mt-2 text-sm text-slate-400">Sign in with an admin or company manager account.</p>
        <form onSubmit={onSubmit} className="mt-6 space-y-4">
          <div>
            <label className="mb-1 block text-sm text-slate-300">Email</label>
            <Input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required placeholder="admin@example.com" />
          </div>
          <div>
            <label className="mb-1 block text-sm text-slate-300">Password</label>
            <Input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required placeholder="••••••••" />
          </div>
          <ErrorMessage message={error} />
          <Button disabled={loading} className="w-full">
            {loading ? 'Signing in...' : 'Sign in'}
          </Button>
        </form>
      </Card>
    </div>
  );
}
