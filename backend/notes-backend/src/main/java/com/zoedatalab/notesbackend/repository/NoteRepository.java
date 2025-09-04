package com.zoedatalab.notesbackend.repository;

import com.zoedatalab.notesbackend.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Page<Note> findByArchived(boolean archived, Pageable pageable);
    Page<Note> findByArchivedAndCategories_NameIgnoreCase(boolean archived, String name, Pageable pageable);
}
