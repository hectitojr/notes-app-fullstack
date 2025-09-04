package com.zoedatalab.notesbackend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "notes")
public class Note {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false) private String title;
  @Column(columnDefinition = "text") private String content;
  @Column(nullable = false) private boolean archived = false;
  @Column(nullable = false) private Instant createdAt = Instant.now();
  @Column(nullable = false) private Instant updatedAt = Instant.now();

  @ManyToMany
  @JoinTable(name = "note_categories",
      joinColumns = @JoinColumn(name = "note_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new HashSet<>();

  @PreUpdate public void touch() { this.updatedAt = Instant.now(); }

  public Long getId() { return id; } public void setId(Long id) { this.id = id; }
  public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
  public String getContent() { return content; } public void setContent(String content) { this.content = content; }
  public boolean isArchived() { return archived; } public void setArchived(boolean archived) { this.archived = archived; }
  public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  public Set<Category> getCategories() { return categories; } public void setCategories(Set<Category> categories) { this.categories = categories; }
}
