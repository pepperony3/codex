import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api/client';

export default function BoardPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [board, setBoard] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    api.getBoard(Number(id)).then(data => {
      setBoard(data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="min-h-screen flex items-center justify-center text-gray-400 text-lg">加载中...</div>;
  if (!board) return <div className="min-h-screen flex items-center justify-center text-gray-400 text-lg">看板不存在</div>;

  const handleDragStart = (e: React.DragEvent, taskId: number, fromColumnId: number) => {
    e.dataTransfer.setData('taskId', String(taskId));
    e.dataTransfer.setData('fromColumnId', String(fromColumnId));
    (e.currentTarget as HTMLElement).classList.add('opacity-50');
  };

  const handleDragEnd = (e: React.DragEvent) => {
    (e.currentTarget as HTMLElement).classList.remove('opacity-50');
  };

  const handleDrop = async (e: React.DragEvent, toColumnId: number) => {
    e.preventDefault();
    const taskId = Number(e.dataTransfer.getData('taskId'));
    const fromColumnId = Number(e.dataTransfer.getData('fromColumnId'));
    if (fromColumnId === toColumnId) return;
    try {
      await api.moveTask(taskId, { targetColumnId: toColumnId, targetSortOrder: 0 });
      // Refresh board
      const updated = await api.getBoard(Number(id));
      setBoard(updated);
    } catch (err) {
      console.error('Move failed', err);
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    (e.currentTarget as HTMLElement).classList.add('bg-indigo-50');
  };

  const handleDragLeave = (e: React.DragEvent) => {
    (e.currentTarget as HTMLElement).classList.remove('bg-indigo-50');
  };

  const handleDeleteTask = async (taskId: number) => {
    await api.deleteTask(taskId);
    const updated = await api.getBoard(Number(id));
    setBoard(updated);
  };

  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [activeColumnId, setActiveColumnId] = useState<number | null>(null);

  const handleAddTask = async (columnId: number) => {
    if (!newTaskTitle.trim()) return;
    await api.createTask(Number(id), columnId, { title: newTaskTitle.trim() });
    setNewTaskTitle('');
    setActiveColumnId(null);
    const updated = await api.getBoard(Number(id));
    setBoard(updated);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 to-purple-200 p-6">
      <div className="flex items-center gap-4 mb-6">
        <button onClick={() => navigate('/')} className="text-gray-500 hover:text-gray-700 text-2xl">&larr;</button>
        <h1 className="text-3xl font-bold text-gray-800">{board.name}</h1>
      </div>

      <div className="flex gap-4 overflow-x-auto pb-4">
        {board.columns?.map((column: any) => (
          <div
            key={column.id}
            onDrop={e => handleDrop(e, column.id)}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            className="bg-white/60 backdrop-blur rounded-xl shadow-md p-4 min-w-[280px] max-w-[320px] flex-1 transition-colors"
          >
            <div className="flex items-center justify-between mb-3">
              <h2 className="font-semibold text-gray-700">{column.name}</h2>
              <span className="bg-gray-200 text-gray-500 text-xs px-2 py-1 rounded-full">{column.tasks?.length || 0}</span>
            </div>

            <div className="space-y-2 mb-3">
              {column.tasks?.map((task: any) => (
                <div
                  key={task.id}
                  draggable
                  onDragStart={e => handleDragStart(e, task.id, column.id)}
                  onDragEnd={handleDragEnd}
                  className="bg-white rounded-lg shadow-sm p-3 cursor-grab active:cursor-grabbing hover:shadow-md transition group"
                >
                  <div className="flex items-start justify-between">
                    <h3 className="font-medium text-gray-800 text-sm">{task.title}</h3>
                    <button
                      onClick={() => handleDeleteTask(task.id)}
                      className="text-gray-300 hover:text-red-500 transition opacity-0 group-hover:opacity-100 text-sm"
                    >
                      &times;
                    </button>
                  </div>
                  {task.description && (
                    <p className="text-gray-400 text-xs mt-1 line-clamp-2">{task.description}</p>
                  )}
                </div>
              ))}
            </div>

            {activeColumnId === column.id ? (
              <div className="space-y-2">
                <input
                  type="text" placeholder="任务标题" value={newTaskTitle} onChange={e => setNewTaskTitle(e.target.value)}
                  className="w-full px-3 py-2 text-sm rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  onKeyDown={e => { if (e.key === 'Enter') handleAddTask(column.id); if (e.key === 'Escape') setActiveColumnId(null); }}
                  autoFocus
                />
                <div className="flex gap-1">
                  <button onClick={() => handleAddTask(column.id)} className="px-3 py-1.5 bg-indigo-600 text-white text-xs rounded-lg hover:bg-indigo-700">
                    添加
                  </button>
                  <button onClick={() => setActiveColumnId(null)} className="px-3 py-1.5 bg-gray-200 text-gray-600 text-xs rounded-lg hover:bg-gray-300">
                    取消
                  </button>
                </div>
              </div>
            ) : (
              <button
                onClick={() => { setActiveColumnId(column.id); setNewTaskTitle(''); }}
                className="w-full py-2 text-gray-400 text-sm hover:text-gray-600 hover:bg-gray-100 rounded-lg transition"
              >
                + 添加任务
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
