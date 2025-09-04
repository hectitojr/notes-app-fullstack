import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useCreateNoteMutation } from '../hooks';

const schema = z.object({
  title: z.string().trim().min(1, 'El título es obligatorio').max(255),
  content: z.string().trim().optional(),
  categoriesCsv: z.string().trim().optional(),
});

type FormValues = z.infer<typeof schema>;

export function NoteForm() {
  const { mutateAsync, isPending } = useCreateNoteMutation();

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { title: '', content: '', categoriesCsv: '' },
  });

  const onSubmit = async (values: FormValues) => {
    const categories = (values.categoriesCsv ?? '')
      .split(',')
      .map(s => s.trim())
      .filter(Boolean)
      .slice(0, 10);

    try {
      await mutateAsync({ title: values.title, content: values.content, categories });
      reset();
      alert('Nota creada');
    } catch (e: any) {
      if (e.fieldErrors?.title) {
        alert(e.fieldErrors.title);
      } else {
        alert(e.message ?? 'Error');
      }
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
      <div>
        <label className="block text-sm font-medium">Título</label>
        <input className="w-full border rounded px-3 py-2"
               placeholder="Título"
               {...register('title')} />
        {errors.title && <p className="text-red-600 text-sm">{errors.title.message}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium">Contenido</label>
        <textarea className="w-full border rounded px-3 py-2 h-32" {...register('content')} />
      </div>

      <div>
        <label className="block text-sm font-medium">Categorías (separadas por coma)</label>
        <input className="w-full border rounded px-3 py-2"
               placeholder="work, personal"
               {...register('categoriesCsv')} />
      </div>

      <button
        type="submit"
        className="bg-slate-900 text-white rounded px-4 py-2 disabled:opacity-50"
        disabled={isPending}
      >
        {isPending ? 'Guardando...' : 'Guardar'}
      </button>
    </form>
  );
}
