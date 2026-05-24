import type { ReactNode } from 'react';

export function PageHeader({ title, description }: { title: string; description?: string }) {
  return (
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">{title}</h1>
        {description ? <p className="mt-1 text-sm text-slate-400">{description}</p> : null}
      </div>
  );
}

export function Card({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <div className={`rounded-2xl border border-slate-800 bg-slate-900/70 p-5 shadow-xl ${className}`}>{children}</div>;
}

export function Button({ children, className = '', ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
      <button
          className={`rounded-xl bg-cyan-500 px-4 py-2 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-50 ${className}`}
          {...props}
      >
        {children}
      </button>
  );
}

export function SecondaryButton({ children, className = '', ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
      <button
          className={`rounded-xl border border-slate-700 px-4 py-2 text-sm font-semibold text-slate-200 transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50 ${className}`}
          {...props}
      >
        {children}
      </button>
  );
}

export function Input(props: React.InputHTMLAttributes<HTMLInputElement>) {
  return <input className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3 py-2 text-sm outline-none ring-cyan-500/40 focus:ring-2" {...props} />;
}

export function Select(props: React.SelectHTMLAttributes<HTMLSelectElement>) {
  return <select className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3 py-2 text-sm outline-none ring-cyan-500/40 focus:ring-2" {...props} />;
}

export function ErrorMessage({ message }: { message?: string }) {
  if (!message) return null;
  return <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-200">{message}</div>;
}

export function Table({ children }: { children: ReactNode }) {
  return <div className="overflow-x-auto rounded-2xl border border-slate-800"><table className="min-w-full divide-y divide-slate-800 text-sm">{children}</table></div>;
}
