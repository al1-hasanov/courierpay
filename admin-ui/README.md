# CourierPay Admin UI

React + TypeScript + Vite admin dashboard for the CourierPay Spring Boot API.

The UI is intended to be deployed separately from the backend as a Render Static Site while using the Spring Boot API deployed on Render.

## Features

- JWT login flow
- Protected routes
- Dashboard shell with sidebar navigation
- Companies list/create
- Couriers list
- Earnings list/process action
- Payouts list with approve/reject actions
- Courier balance lookup
- Transaction report export/download

## Local setup

```powershell
npm.cmd install
copy .env.example .env
npm.cmd run dev
```

Local URL:

```text
http://localhost:5173
```

Local backend API URL in `.env`:

```text
VITE_API_BASE_URL=http://localhost:8080
```

For macOS/Linux or Windows terminals where `npm` works normally, the equivalent commands are:

```bash
npm install
cp .env.example .env
npm run dev
```

## Render deployment

Create a Render Static Site with:

```text
Root Directory: admin-ui
Build Command: npm install && npm run build
Publish Directory: dist
```

Set this environment variable in the Render Static Site:

```text
VITE_API_BASE_URL=https://courierpay.onrender.com
```

Add this rewrite rule for React Router:

```text
Source: /*
Destination: /index.html
Action: Rewrite
```

Then set this environment variable in the Render backend service so the browser can call the API:

```text
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,https://YOUR-ADMIN-UI.onrender.com
```

After changing backend CORS values, redeploy the backend service.

## Build check

Before pushing frontend changes, run:

```bash
npm run build
```

This catches TypeScript and Vite build errors before Render deploys the static site.
