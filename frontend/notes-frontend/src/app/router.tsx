import { createBrowserRouter } from "react-router-dom";
import { AppLayout } from "../components/layout/AppLayout";
import { NotesListPage } from "../features/notes/pages/NotesListPage";
import { NoteEditPage } from "../features/notes/pages/NoteEditPage";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <AppLayout />,
    children: [
      { index: true, element: <NotesListPage /> },     // /  -> listado
      { path: "new", element: <NoteEditPage /> },      // /new -> crear
      { path: "notes/:id", element: <NoteEditPage /> } // /notes/123 -> editar
    ],
  },
]);
