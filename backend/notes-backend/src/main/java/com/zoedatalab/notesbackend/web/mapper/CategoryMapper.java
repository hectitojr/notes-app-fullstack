package com.zoedatalab.notesbackend.web.mapper;

import com.zoedatalab.notesbackend.domain.Category;
import com.zoedatalab.notesbackend.web.dto.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryDto toDto(Category c);
}
