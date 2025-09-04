import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  addCategoryToNote,
  archiveNote,
  createNote,
  deleteNote,
  getNote,
  listNotes,
  removeCategoryFromNote,
  unarchiveNote,
  updateNote,
} from './api';
import { type NoteCreateRequest, type NoteUpdateRequest } from './types';

export function useNotesQuery(filters: {
  archived: boolean;
  category: string | null;
  page: number;
  size: number;
}) {
  return useQuery({
    queryKey: ['notes', filters],
    queryFn: () => listNotes(filters),
  });
}

export function useNoteQuery(id: number) {
  return useQuery({
    queryKey: ['note', id],
    queryFn: () => getNote(id),
    enabled: !!id,
  });
}

export function useCreateNoteMutation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: NoteCreateRequest) => createNote(payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notes'] }),
  });
}

export function useUpdateNoteMutation(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: NoteUpdateRequest) => updateNote(id, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['notes'] });
      qc.invalidateQueries({ queryKey: ['note', id] });
    },
  });
}

export function useDeleteNoteMutation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteNote(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notes'] }),
  });
}

export function useArchiveNoteMutation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => archiveNote(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notes'] }),
  });
}

export function useUnarchiveNoteMutation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => unarchiveNote(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notes'] }),
  });
}

export function useAddCategoryToNoteMutation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (vars: { id: number; name: string }) => addCategoryToNote(vars.id, vars.name),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notes'] }),
  });
}

export function useRemoveCategoryFromNoteMutation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (vars: { id: number; name: string }) => removeCategoryFromNote(vars.id, vars.name),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notes'] }),
  });
}
