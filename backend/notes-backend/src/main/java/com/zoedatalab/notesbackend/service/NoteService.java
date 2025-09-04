package com.zoedatalab.notesbackend.service;

import com.zoedatalab.notesbackend.web.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteService {
  NoteDto create(NoteCreateRequest req);
  NoteDto get(Long id);
  Page<NoteDto> list(boolean archived, String category, Pageable pageable);
  NoteDto update(Long id, NoteUpdateRequest req);
  void delete(Long id);
  NoteDto addCategory(Long noteId, String categoryName);
  NoteDto removeCategory(Long noteId, String categoryName);
  NoteDto archive(Long id, boolean archived);
}
