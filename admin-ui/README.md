# CourierPay Admin UI

React + Vite admin dashboard for the CourierPay Spring Boot API.

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

Then set this environment variable in the Render backend service:

```text
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,https://YOUR-ADMIN-UI.onrender.com
```
