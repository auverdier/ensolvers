package org.example.backend.dto;

public record NoteDTO(Long id, String title, String content, boolean archived) {
}