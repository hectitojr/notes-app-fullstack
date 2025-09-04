import { useState } from 'react';
import { useNotesQuery } from '../hooks';
import { NoteForm } from '../components/NoteForm';
import { NoteCard } from '../components/NoteCard';

export function NotesListPage() {
  const [archived, setArchived] = useState(false);
  const [category, setCategory] = useState<string>('');
  const [page, setPage] = useState(0);
  const size = 10;

  const { data, isLoading, isError, error } = useNotesQuery({
    archived,
    category: category.trim() ? category.trim() : null,
    page,
    size,
  });

  return (
    <div className="max-w-5xl mx-auto p-6 space-y-6">
      <h1 className="text-2xl font-semibold">Notes App</h1>

      <section className="rounded border p-4">
        <h2 className="font-medium mb-3">Nueva nota</h2>
        <NoteForm />
      </section>

      <section className="flex items-center gap-4">
        <label className="inline-flex items-center gap-2">
          <input type="checkbox" checked={archived} onChange={e => { setPage(0); setArchived(e.target.checked); }} />
          Mostrar archivadas
        </label>
        <input
          className="border rounded px-3 py-2"
          placeholder="Filtrar por categoría (ej: work)"
          value={category}
          onChange={e => { setPage(0); setCategory(e.target.value); }}
        />
      </section>

      <section className="space-y-4">
        {isLoading && <p>Cargando...</p>}
        {isError && <p className="text-red-600">{(error as any)?.message ?? 'Error'}</p>}
        {data?.content?.length === 0 && <p className="text-slate-600">Sin notas.</p>}
        {data?.content?.map(n => <NoteCard key={n.id} note={n} />)}
      </section>

      {data && data.totalPages > 1 && (
        <div className="flex gap-2 items-center">
          <button
            className="border rounded px-3 py-1 disabled:opacity-50"
            onClick={() => setPage((p) => Math.max(p - 1, 0))}
            disabled={page === 0}
          >
            Anterior
          </button>
          <span className="text-sm">Página {data.number + 1} / {data.totalPages}</span>
          <button
            className="border rounded px-3 py-1 disabled:opacity-50"
            onClick={() => setPage((p) => Math.min(p + 1, (data?.totalPages ?? 1) - 1))}
            disabled={page >= (data?.totalPages ?? 1) - 1}
          >
            Siguiente
          </button>
        </div>
      )}
    </div>
  );
}
