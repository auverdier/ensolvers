package org.example.backend.service;

import org.example.backend.dto.NoteDTO;
import org.example.backend.entity.Note;
import org.example.backend.exception.NoteNotFoundException;
import org.example.backend.repository.NoteRepository;
import org.example.backend.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note("Title", "Content", false);
        note.setId(1L);
    }

    // ---------------- CREATE ----------------
    @Test
    void shouldCreateNote() {
        NoteDTO dto = new NoteDTO(null, "Title", "Content", false);

        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDTO result = noteService.createNote(dto);

        assertNotNull(result);
        assertEquals("Title", result.title());
        assertEquals("Content", result.content());
        assertFalse(result.archived());

        verify(noteRepository).save(any(Note.class));
    }

    // ---------------- UPDATE ----------------
    @Test
    void shouldUpdateNote() {
        NoteDTO dto = new NoteDTO(null, "New Title", "New Content", false);

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDTO result = noteService.updateNote(1L, dto);

        assertEquals("New Title", result.title());
        assertEquals("New Content", result.content());

        verify(noteRepository).findById(1L);
        verify(noteRepository).save(note);
    }

    @Test
    void shouldThrowWhenUpdateNoteNotFound() {
        NoteDTO dto = new NoteDTO(null, "Title", "Content", false);

        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class,
                () -> noteService.updateNote(1L, dto));
    }

    // ---------------- DELETE ----------------
    @Test
    void shouldDeleteNote() {
        when(noteRepository.existsById(1L)).thenReturn(true);

        noteService.deleteNote(1L);

        verify(noteRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeleteNoteNotFound() {
        when(noteRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoteNotFoundException.class,
                () -> noteService.deleteNote(1L));
    }

    // ---------------- ARCHIVE ----------------
    @Test
    void shouldArchiveNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        noteService.archiveNote(1L);

        assertTrue(note.isArchived());
        verify(noteRepository).save(note);
    }

    @Test
    void shouldThrowWhenArchiveNoteNotFound() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class,
                () -> noteService.archiveNote(1L));
    }

    // ---------------- UNARCHIVE ----------------
    @Test
    void shouldUnarchiveNote() {
        note.setArchived(true);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        noteService.unarchiveNote(1L);

        assertFalse(note.isArchived());
        verify(noteRepository).save(note);
    }

    @Test
    void shouldThrowWhenUnarchiveNoteNotFound() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class,
                () -> noteService.unarchiveNote(1L));
    }

    // ---------------- GET ACTIVE ----------------
    @Test
    void shouldReturnActiveNotes() {
        when(noteRepository.findByArchived(false))
                .thenReturn(List.of(note));

        List<NoteDTO> result = noteService.getActiveNotes();

        assertEquals(1, result.size());
        assertFalse(result.get(0).archived());
    }

    // ---------------- GET ARCHIVED ----------------
    @Test
    void shouldReturnArchivedNotes() {
        note.setArchived(true);

        when(noteRepository.findByArchived(true))
                .thenReturn(List.of(note));

        List<NoteDTO> result = noteService.getArchivedNotes();

        assertEquals(1, result.size());
        assertTrue(result.get(0).archived());
    }
}