export interface User {
  id: number;
  username: string;
  email: string;
}

export interface Task {
  id: number;
  title: string;
  description: string;
  column: { id: number; name: string };
  assignee: User | null;
  sortOrder: number;
  createdAt: string;
}

export interface BoardColumn {
  id: number;
  name: string;
  sortOrder: number;
  tasks: Task[];
}

export interface Board {
  id: number;
  name: string;
  owner: User;
  columns: BoardColumn[];
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  username: string;
}
