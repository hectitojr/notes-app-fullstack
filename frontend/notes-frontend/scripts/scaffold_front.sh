#!/usr/bin/env bash
set -euo pipefail

# Util: crea archivo solo si no existe
write() {
  local path="$1"
  shift
  if [[ -f "$path" ]]; then
    echo "skip   $path (ya existe)"
  else
    mkdir -p "$(dirname "$path")"
    cat > "$path"
    echo "create $path"
  fi
}

echo "==> Generando estructura..."

# .env
write ".env.development" <<'EOF'
VITE_API_BASE=http://localhost:8080
EOF

# src/lib
write "src/lib/env.ts" <<'EOF'
export const env = { apiBase: import.meta.env.VITE_API_BASE as string };
EOF

write "src/lib/axios.ts" <<'EOF'
import axios from "axios";
import { env } from "./env";

export const api = axios.create({
  baseURL: env.apiBase,
  headers: { "Content-Type": "application/json" },
});
EOF

write "src/lib/utils.ts" <<'EOF'
export const cls = (...xs: Array<string | false | undefined | null>) =>
  xs.filter(Boolean).join(" ");
EOF

# app
write "src/app/queryClient.ts" <<'EOF'
import { QueryClient } from "@tanstack/react-query";
export const queryClient = new QueryClient({
  defaultOptions: { queries: { staleTime: 30000, refetchOnWindowFocus: false, retry: 1 } }
});
EOF

write "src/app/providers.tsx" <<'EOF'
import { PropsWithChildren } from "react";
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "./queryClient";
export function Providers({ children }: PropsWithChildren) {
  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
EOF

write "src/app/router.tsx" <<'EOF'
import { createBrowserRouter } from "react-router-dom";
import AppLayout from "../components/layout/AppLayout";
import NotesListPage from "../features/notes/pages/NotesListPage";
import NoteEditPage from "../features/notes/pages/NoteEditPage";

export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      { index: true, element: <NotesListPage /> },
      { path: "notes/new", element: <NotesListPage /> },
      { path: "notes/:id", element: <NoteEditPage /> },
    ],
  },
]);
EOF

# layout + ui
write "src/components/layout/Header.tsx" <<'EOF'
import { Link, NavLink } from "react-router-dom";
export default function Header() {
  const active = ({ isActive }: { isActive: boolean }) =>
    "px-3 py-1 rounded-md " + (isActive ? "bg-slate-800 text-white" : "hover:bg-slate-200");
  return (
    <header className="border-b bg-white">
      <div className="mx-auto max-w-5xl px-4 py-3 flex items-center gap-6">
        <Link to="/" className="font-semibold">Notes App</Link>
        <nav className="flex gap-2">
          <NavLink to="/" className={active}>Notas</NavLink>
          <NavLink to="/notes/new" className={active}>Nueva</NavLink>
        </nav>
      </div>
    </header>
  );
}
EOF

write "src/components/layout/AppLayout.tsx" <<'EOF'
import { Outlet } from "react-router-dom";
import Header from "./Header";
export default function AppLayout() {
  return (
    <div className="min-h-dvh bg-slate-50 text-slate-900">
      <Header />
      <main className="mx-auto max-w-5xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  );
}
EOF

write "src/ui/Button.tsx" <<'EOF'
import { ButtonHTMLAttributes } from "react";
import { cls } from "../lib/utils";
type Props = ButtonHTMLAttributes<HTMLButtonElement> & { variant?: "primary" | "ghost" };
export default function Button({ className, variant = "primary", ...rest }: Props) {
  const base = "px-3 py-2 rounded-md text-sm";
  const kind = variant === "primary" ? "bg-slate-900 text-white hover:bg-slate-800" : "hover:bg-slate-200";
  return <button className={cls(base, kind, className)} {...rest} />;
}
EOF

# features/notes
write "src/features/notes/types.ts" <<'EOF'
export type NoteDto = {
  id: number; title: string; content: string; archived: boolean;
  createdAt: string; updatedAt: string; categories: string[];
};
export type Page<T> = { content: T[]; totalElements: number; totalPages: number; number: number; size: number; };
EOF

write "src/features/notes/api.ts" <<'EOF'
import { api } from "../../lib/axios";
import type { NoteDto, Page } from "./types";
export const NotesAPI = {
  list(params: { archived?: boolean; category?: string; page?: number; size?: number }) {
    return api.get<Page<NoteDto>>("/api/v1/notes", { params }).then(r => r.data);
  },
  get(id: number) { return api.get<NoteDto>(`/api/v1/notes/${id}`).then(r => r.data); },
  create(payload: { title: string; content?: string; categories?: string[] }) {
    return api.post<NoteDto>("/api/v1/notes", payload).then(r => r.data);
  },
  update(id: number, payload: { title: string; content?: string; archived?: boolean }) {
    return api.put<NoteDto>(`/api/v1/notes/${id}`, payload).then(r => r.data);
  },
  archive(id: number) { return api.post<NoteDto>(`/api/v1/notes/${id}/archive`).then(r => r.data); },
  unarchive(id: number) { return api.post<NoteDto>(`/api/v1/notes/${id}/unarchive`).then(r => r.data); },
  remove(id: number) { return api.delete<void>(`/api/v1/notes/${id}`).then(r => r.data); },
};
EOF

write "src/features/notes/hooks.ts" <<'EOF'
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { NotesAPI } from "./api";
export const qk = {
  notes: (archived: boolean, category: string | null, page: number, size: number) =>
    ["notes", { archived, category, page, size }] as const,
  note: (id: number) => ["note", id] as const,
};
export function useNotesQuery(params: { archived: boolean; category?: string; page: number; size: number }) {
  return useQuery({ queryKey: qk.notes(params.archived, params.category ?? null, params.page, params.size),
    queryFn: () => NotesAPI.list(params) });
}
export function useCreateNote() {
  const qc = useQueryClient();
  return useMutation({ mutationFn: NotesAPI.create, onSuccess: () => qc.invalidateQueries({ queryKey: ["notes"] }) });
}
EOF

write "src/features/notes/components/NoteCard.tsx" <<'EOF'
import type { NoteDto } from "../types";
export default function NoteCard({ note }: { note: NoteDto }) {
  return (
    <article className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <h3 className="font-semibold">{note.title}</h3>
        {note.archived && <span className="text-xs rounded bg-yellow-100 px-2 py-0.5">archived</span>}
      </div>
      {note.content && <p className="mt-2 text-sm text-slate-700 whitespace-pre-wrap">{note.content}</p>}
      {!!note.categories?.length && (
        <div className="mt-3 flex flex-wrap gap-1">
          {note.categories.map((c) => <span key={c} className="text-xs bg-slate-100 px-2 py-0.5 rounded">{c}</span>)}
        </div>
      )}
    </article>
  );
}
EOF

write "src/features/notes/components/NoteForm.tsx" <<'EOF'
import { useForm } from "react-hook-form";
import Button from "../../../ui/Button";
type Form = { title: string; content?: string; categories?: string };
export default function NoteForm({ onSubmit }: { onSubmit: (data: Form) => void }) {
  const { register, handleSubmit, formState: { errors, isSubmitting } } =
    useForm<Form>({ defaultValues: { title: "", content: "", categories: "" } });
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
      <div>
        <label className="text-sm">Título</label>
        <input className="mt-1 w-full rounded-md border px-3 py-2"
               {...register("title", { required: "El título es obligatorio" })} />
        {errors.title && <p className="text-sm text-red-600">{errors.title.message}</p>}
      </div>
      <div>
        <label className="text-sm">Contenido</label>
        <textarea className="mt-1 w-full rounded-md border px-3 py-2" rows={5} {...register("content")} />
      </div>
      <div>
        <label className="text-sm">Categorías (separadas por coma)</label>
        <input className="mt-1 w-full rounded-md border px-3 py-2" placeholder="work, personal"
               {...register("categories")} />
      </div>
      <Button type="submit" disabled={isSubmitting}>Guardar</Button>
    </form>
  );
}
EOF

write "src/features/notes/pages/NotesListPage.tsx" <<'EOF'
import { useState } from "react";
import NoteCard from "../components/NoteCard";
import { useCreateNote, useNotesQuery } from "../hooks";
import NoteForm from "../components/NoteForm";
export default function NotesListPage() {
  const [archived, setArchived] = useState(false);
  const [category, setCategory] = useState<string>("");
  const { data, isLoading } = useNotesQuery({ archived, category, page: 0, size: 10 });
  const create = useCreateNote();
  return (
    <div className="grid gap-6">
      <section className="rounded-lg border bg-white p-4">
        <h2 className="font-semibold mb-3">Nueva nota</h2>
        <NoteForm onSubmit={(f) => {
          const cats = f.categories ? f.categories.split(",").map(s => s.trim()).filter(Boolean) : undefined;
          create.mutate({ title: f.title, content: f.content, categories: cats });
        }}/>
      </section>
      <section className="flex items-center gap-2">
        <label className="text-sm flex items-center gap-2">
          <input type="checkbox" checked={archived} onChange={e => setArchived(e.target.checked)} />
          Mostrar archivadas
        </label>
        <input className="rounded-md border px-3 py-2 text-sm" placeholder="Filtrar por categoría (ej: work)"
               value={category} onChange={(e) => setCategory(e.target.value)} />
      </section>
      <section className="grid gap-3">
        {isLoading && <p>Cargando...</p>}
        {data?.content?.length ? data.content.map(n => <NoteCard key={n.id} note={n} />)
          : !isLoading && <p className="text-slate-500">Sin notas.</p>}
      </section>
    </div>
  );
}
EOF

write "src/features/notes/pages/NoteEditPage.tsx" <<'EOF'
export default function NoteEditPage() {
  return <div>Edición de nota (pendiente)</div>;
}
EOF

# categories placeholders
write "src/features/categories/types.ts" <<'EOF'
export type CategoryDto = { id: number; name: string };
EOF

write "src/features/categories/api.ts" <<'EOF'
import { api } from "../../lib/axios";
import type { CategoryDto } from "./types";
export const CategoriesAPI = { list() { return api.get<CategoryDto[]>("/api/v1/categories").then(r => r.data); } };
EOF

write "src/features/categories/hooks.ts" <<'EOF'
import { useQuery } from "@tanstack/react-query";
import { CategoriesAPI } from "./api";
export function useCategories() { return useQuery({ queryKey: ["categories"], queryFn: CategoriesAPI.list }); }
EOF

# main + App
write "src/App.tsx" <<'EOF'
export default function App() { return <div className="p-4">App</div>; }
EOF

write "src/main.tsx" <<'EOF'
import ReactDOM from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { Providers } from "./app/providers";
import { router } from "./app/router";
import "./index.css";
ReactDOM.createRoot(document.getElementById("root")!).render(
  <Providers><RouterProvider router={router} /></Providers>
);
EOF

echo "==> Estructura generada."
echo "Si algo falla por EOL, ejecuta:  sed -i 's/\r$//' scripts/scaffold_front.sh"