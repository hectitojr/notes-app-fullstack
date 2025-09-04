import { api } from '../../lib/axios';
import { type Note, type NoteCreateRequest, type NoteUpdateRequest, type Page } from './types';

export async function listNotes(params: {
  archived?: boolean;
  category?: string | null;
  page?: number;
  size?: number;
}): Promise<Page<Note>> {
  const res = await api.get('/notes', { params });
  return res.data;
}

export async function getNote(id: number): Promise<Note> {
  const res = await api.get(`/notes/${id}`);
  return res.data;
}

export async function createNote(payload: NoteCreateRequest): Promise<Note> {
  const res = await api.post('/notes', payload);
  return res.data;
}

export async function updateNote(id: number, payload: NoteUpdateRequest): Promise<Note> {
  const res = await api.put(`/notes/${id}`, payload);
  return res.data;
}

export async function deleteNote(id: number): Promise<void> {
  await api.delete(`/notes/${id}`);
}

export async function archiveNote(id: number): Promise<Note> {
  const res = await api.post(`/notes/${id}/archive`);
  return res.data;
}

export async function unarchiveNote(id: number): Promise<Note> {
  const res = await api.post(`/notes/${id}/unarchive`);
  return res.data;
}

export async function addCategoryToNote(id: number, name: string): Promise<Note> {
  const res = await api.post(`/notes/${id}/categories/${encodeURIComponent(name)}`);
  return res.data;
}

export async function removeCategoryFromNote(id: number, name: string): Promise<Note> {
  const res = await api.delete(`/notes/${id}/categories/${encodeURIComponent(name)}`);
  return res.data;
}