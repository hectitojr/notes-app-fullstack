import { api } from '../../lib/axios';
import { type Category } from './types';

export async function listCategories(): Promise<Category[]> {
  const res = await api.get('/categories');
  return res.data;
}

export async function createCategory(name: string): Promise<Category> {
  const res = await api.post('/categories', { name });
  return res.data;
}