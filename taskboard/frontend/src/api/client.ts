const API_BASE: string = import.meta.env.VITE_API_BASE ?? '/api/v1';

function authHeaders(): Record<string, string> {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${url}`, { ...options, headers: { ...authHeaders(), ...options?.headers } });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: 'Request failed' }));
    throw new Error(err.error || 'Request failed');
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const api = {
  // Auth
  register: (data: { username: string; email: string; password: string }) =>
    request<{ token: string; username: string }>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  login: (data: { username: string; password: string }) =>
    request<{ token: string; username: string }>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),

  // Boards
  getBoards: () => request<any[]>('/boards'),
  createBoard: (name: string) => request<any>('/boards', { method: 'POST', body: JSON.stringify({ name }) }),
  getBoard: (id: number) => request<any>(`/boards/${id}`),

  // Columns
  createColumn: (boardId: number, name: string) =>
    request<any>(`/boards/${boardId}/columns`, { method: 'POST', body: JSON.stringify({ name }) }),

  // Tasks
  createTask: (boardId: number, columnId: number, data: { title: string; description?: string }) =>
    request<any>(`/boards/${boardId}/columns/${columnId}/tasks`, { method: 'POST', body: JSON.stringify(data) }),
  moveTask: (taskId: number, data: { targetColumnId: number; targetSortOrder: number }) =>
    request<any>(`/tasks/${taskId}/move`, { method: 'PATCH', body: JSON.stringify(data) }),
  deleteTask: (taskId: number) => request<void>(`/tasks/${taskId}`, { method: 'DELETE' }),
};
