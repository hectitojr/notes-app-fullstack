package com.zoedatalab.notesbackend.web.dto;

import java.time.Instant;
import java.util.Set;

public record NoteDto(Long id, String title, String content, boolean archived,
                      Instant createdAt, Instant updatedAt, Set<String> categories) {}
