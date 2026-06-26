import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../api/client';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.login({ username, password });
      localStorage.setItem('token', res.token);
      localStorage.setItem('username', res.username);
      navigate('/');
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 to-purple-200 flex items-center justify-center p-4">
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">TaskBoard</h1>
        <p className="text-gray-500 mb-6">登录你的账号</p>
        {error && <div className="bg-red-50 text-red-600 rounded-lg p-3 mb-4 text-sm">{error}</div>}
        <input
          type="text" placeholder="用户名" value={username} onChange={e => setUsername(e.target.value)}
          className="w-full px-4 py-3 rounded-lg border border-gray-200 mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          required
        />
        <input
          type="password" placeholder="密码" value={password} onChange={e => setPassword(e.target.value)}
          className="w-full px-4 py-3 rounded-lg border border-gray-200 mb-4 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          required
        />
        <button type="submit" className="w-full py-3 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition">
          登录
        </button>
        <p className="text-center text-gray-500 mt-4 text-sm">
          没有账号？<Link to="/register" className="text-indigo-600 hover:underline">注册</Link>
        </p>
      </form>
    </div>
  );
}
