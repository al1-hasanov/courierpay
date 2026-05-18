const ACCESS_TOKEN_KEY = 'courierpay_access_token';
const REFRESH_TOKEN_KEY = 'courierpay_refresh_token';

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function saveTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

export function clearTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
}

export function isAuthenticated() {
  return Boolean(getAccessToken());
}
