import { getAccessToken } from '../api/authStorage';
import { getTransactionsExportUrl } from '../api/courierpayApi';
import { Button, Card, PageHeader } from '../components/ui';

export function ReportsPage() {
  async function downloadTransactions() {
    const response = await fetch(getTransactionsExportUrl(), {
      headers: { Authorization: `Bearer ${getAccessToken()}` },
    });
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'transactions.xlsx';
    link.click();
    window.URL.revokeObjectURL(url);
  }

  return (
    <>
      <PageHeader title="Reports" description="Download operational reports from CourierPay." />
      <Card>
        <h2 className="font-semibold text-white">Transactions export</h2>
        <p className="mt-2 text-sm text-slate-400">Downloads the Excel report from /api/v1/reports/transactions/export.</p>
        <Button onClick={downloadTransactions} className="mt-4">Download Excel</Button>
      </Card>
    </>
  );
}
