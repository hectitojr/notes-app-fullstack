export type Note = {
  id: number;
  title: string;
  content: string;
  archived: boolean;
  createdAt: string;
  updatedAt: string;
  categories: string[];
};

export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // page index
  size: number;
  first: boolean;
  last: boolean;
};

export type NoteCreateRequest = {
  title: string;
  content?: string;
  categories?: string[];
};

export type NoteUpdateRequest = {
  title: string;
  content?: string;
  archived?: boolean | null;
};
