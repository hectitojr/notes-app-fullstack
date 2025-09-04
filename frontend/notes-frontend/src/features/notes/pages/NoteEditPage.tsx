import { useParams, useNavigate } from 'react-router-dom';
import { useNoteQuery, useUpdateNoteMutation } from '../hooks';
import { useForm } from 'react-hook-form';

export function NoteEditPage() {
  const { id: idParam } = useParams();
  const id = Number(idParam);
  const nav = useNavigate();
  const { data, isLoading } = useNoteQuery(id);
  const { mutateAsync, isPending } = useUpdateNoteMutation(id);

  const { register, handleSubmit, reset } = useForm({
    values: {
      title: data?.title ?? '',
      content: data?.content ?? '',
    },
  });

  const onSubmit = async (values: { title: string; content: string }) => {
    await mutateAsync(values);
    alert('Actualizada');
    reset(values);
    nav('/notes');
  };

  if (isLoading) return <div className="p-6">Cargando...</div>;
  if (!data) return <div className="p-6">No encontrada</div>;

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-4">
      <h1 className="text-xl font-semibold">Editar nota #{id}</h1>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <div>
          <label className="block text-sm font-medium">Título</label>
          <input className="w-full border rounded px-3 py-2" {...register('title')} />
        </div>
        <div>
          <label className="block text-sm font-medium">Contenido</label>
          <textarea className="w-full border rounded px-3 py-2 h-32" {...register('content')} />
        </div>
        <button className="bg-slate-900 text-white rounded px-4 py-2 disabled:opacity-50" disabled={isPending}>
          {isPending ? 'Guardando...' : 'Guardar'}
        </button>
      </form>
    </div>
  );
}
