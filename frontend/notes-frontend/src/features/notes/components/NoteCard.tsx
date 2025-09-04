import type { Note } from '../types';
import {
  useArchiveNoteMutation,
  useDeleteNoteMutation,
  useUnarchiveNoteMutation,
} from '../hooks';

export function NoteCard({ note }: { note: Note }) {
  const del = useDeleteNoteMutation();
  const arch = useArchiveNoteMutation();
  const unarch = useUnarchiveNoteMutation();

  return (
    <div className="border rounded p-4 flex flex-col gap-2">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold">{note.title}</h3>
        <div className="flex gap-2">
          {note.archived ? (
            <button className="text-sm px-2 py-1 border rounded"
                    onClick={() => unarch.mutate(note.id)}>
              Desarchivar
            </button>
          ) : (
            <button className="text-sm px-2 py-1 border rounded"
                    onClick={() => arch.mutate(note.id)}>
              Archivar
            </button>
          )}
          <button className="text-sm px-2 py-1 border rounded"
                  onClick={() => {
                    if (confirm('¿Eliminar nota?')) del.mutate(note.id);
                  }}>
            Eliminar
          </button>
        </div>
      </div>

      {note.content && <p className="text-sm text-slate-700 whitespace-pre-wrap">{note.content}</p>}

      {note.categories?.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {note.categories.map((c) => (
            <span key={c} className="text-xs bg-slate-100 px-2 py-1 rounded">{c}</span>
          ))}
        </div>
      )}

      <div className="text-xs text-slate-500">
        Creada: {new Date(note.createdAt).toLocaleString()} · Última edición: {new Date(note.updatedAt).toLocaleString()}
      </div>
    </div>
  );
}
