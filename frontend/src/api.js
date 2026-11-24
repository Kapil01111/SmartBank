const API = '/api'
export function setToken(token) { token ? localStorage.setItem('smartbank_token', token) : localStorage.removeItem('smartbank_token') }
export function getToken() { return localStorage.getItem('smartbank_token') }
export async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}), ...options.headers }
  const response = await fetch(`${API}${path}`, { ...options, headers })
  const text = await response.text()
  let data = null
  try { data = text ? JSON.parse(text) : null } catch { data = text }
  if (!response.ok) throw new Error(data?.message || data?.errors ? JSON.stringify(data.errors || data.message) : `Request failed (${response.status})`)
  return data
}
