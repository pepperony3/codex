import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';

export default function BoardListPage() {
  const [boards, setBoards] = useState<any[]>([]);
  const [newName, setNewName] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  useEffect(() => {
    api.getBoards().then(setBoards).catch(() => setBoards([]));
  }, []);

  const handleCreate = async () => {
    if (!newName.trim()) return;
    const board = await api.createBoard(newName.trim());
    setBoards([...boards, board]);
    setNewName('');
    setShowCreate(false);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 to-purple-200 p-6">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-4xl font-bold text-gray-800">TaskBoard</h1>
            <p className="text-gray-600 mt-1">欢迎，{username}</p>
          </div>
          <button onClick={handleLogout} className="px-4 py-2 bg-white rounded-lg text-gray-600 hover:bg-gray-50 border border-gray-200 transition">
            退出登录
          </button>
        </div>

        {boards.length === 0 && (
          <div className="text-center py-20">
            <p className="text-gray-400 text-lg mb-4">暂无看板</p>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
          {boards.map((board) => (
            <div
              key={board.id}
              onClick={() => navigate(`/board/${board.id}`)}
              className="bg-white rounded-xl shadow-md p-6 cursor-pointer hover:shadow-lg hover:-translate-y-1 transition-all duration-200"
            >
              <h3 className="text-xl font-semibold text-gray-800">{board.name}</h3>
              <p className="text-gray-400 text-sm mt-2">{board.columns?.length || 0} 列</p>
            </div>
          ))}
        </div>

        {showCreate ? (
          <div className="bg-white rounded-xl shadow-md p-6 max-w-md">
            <input type="text" placeholder="看板名称" value={newName} onChange={e => setNewName(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-200 mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              onKeyDown={e => e.key === 'Enter' && handleCreate()} autoFocus />
            <div className="flex gap-2">
              <button onClick={handleCreate} className="px-6 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition">
                创建
              </button>
              <button onClick={() => setShowCreate(false)} className="px-6 py-2 bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition">
                取消
              </button>
            </div>
          </div>
        ) : (
          <button onClick={() => setShowCreate(true)}
            className="px-6 py-3 bg-white rounded-xl shadow-md text-indigo-600 font-semibold border-2 border-dashed border-indigo-300 hover:border-indigo-500 hover:bg-indigo-50 transition">
            + 新建看板
          </button>
        )}
      </div>
    </div>
  );
}
