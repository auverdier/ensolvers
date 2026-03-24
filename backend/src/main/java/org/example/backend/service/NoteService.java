package org.example.backend.service;

import org.example.backend.dto.NoteDTO;

import java.util.List;

public interface NoteService {
    NoteDTO createNote(NoteDTO noteDTO);
    NoteDTO updateNote(Long id, NoteDTO noteDTO);
    void deleteNote(Long id);
    void archiveNote(Long id);
    void unarchiveNote(Long id);
    List<NoteDTO> getActiveNotes();
    List<NoteDTO> getArchivedNotes();
}