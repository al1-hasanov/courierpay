import { apiClient } from './client';
import type { AuthResponse, Balance, Company, Courier, Earning, Page, Payout } from '../types/api';

export async function login(email: string, password: string) {
  const { data } = await apiClient.post<AuthResponse>('/api/v1/auth/login', { email, password });
  return data;
}

export async function getHealth() {
  const { data } = await apiClient.get<{ service: string; status: string }>('/healthz');
  return data;
}

export async function getCompanies(name?: string) {
  const { data } = await apiClient.get<Page<Company>>('/api/v1/companies', {
    params: { name: name || undefined, size: 50, sort: 'id,desc' },
  });
  return data;
}

export async function createCompany(payload: { name: string; commissionRate: number }) {
  const { data } = await apiClient.post<Company>('/api/v1/companies', payload);
  return data;
}

export async function getCouriers(filters?: { fullName?: string; companyId?: string; active?: string }) {
  const { data } = await apiClient.get<Page<Courier>>('/api/v1/couriers', {
    params: {
      fullName: filters?.fullName || undefined,
      companyId: filters?.companyId || undefined,
      active: filters?.active || undefined,
      size: 50,
      sort: 'id,desc',
    },
  });
  return data;
}

export async function getEarnings(status?: string) {
  const { data } = await apiClient.get<Page<Earning>>('/api/v1/earnings', {
    params: { status: status || undefined, size: 50, sort: 'id,desc' },
  });
  return data;
}

export async function processEarning(id: number) {
  await apiClient.post(`/api/v1/earnings/${id}/process`);
}

export async function getPayouts(status?: string) {
  const { data } = await apiClient.get<Page<Payout>>('/api/v1/payouts', {
    params: { status: status || undefined, size: 50, sort: 'id,desc' },
  });
  return data;
}

export async function approvePayout(id: number) {
  const { data } = await apiClient.post<Payout>(`/api/v1/payouts/${id}/approve`);
  return data;
}

export async function rejectPayout(id: number) {
  const { data } = await apiClient.post<Payout>(`/api/v1/payouts/${id}/reject`);
  return data;
}

export async function getBalance(courierId: string) {
  const { data } = await apiClient.get<Balance>(`/api/v1/balances/couriers/${courierId}`);
  return data;
}

export function getTransactionsExportUrl() {
  return `${import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}/api/v1/reports/transactions/export`;
}
