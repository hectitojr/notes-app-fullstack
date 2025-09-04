package com.zoedatalab.notesbackend.web.mapper;

import com.zoedatalab.notesbackend.domain.Category;
import com.zoedatalab.notesbackend.domain.Note;
import com.zoedatalab.notesbackend.web.dto.NoteDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "categories", source = "categories")
    NoteDto toDto(Note note);

    default String map(Category c) {
        return c.getName();
    }
}
