package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.NoteDTO;
import org.example.backend.exception.GlobalExceptionHandler;
import org.example.backend.exception.NoteNotFoundException;
import org.example.backend.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotesController.class)
@Import(GlobalExceptionHandler.class)
class NotesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- CREATE ----------------
    @Test
    void shouldCreateNote() throws Exception {
        NoteDTO request = new NoteDTO(null, "Title", "Content", false);
        NoteDTO response = new NoteDTO(1L, "Title", "Content", false);

        when(noteService.createNote(request)).thenReturn(response);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.content").value("Content"))
                .andExpect(jsonPath("$.archived").value(false));
    }

    // ---------------- UPDATE ----------------
    @Test
    void shouldUpdateNote() throws Exception {
        NoteDTO request = new NoteDTO(null, "New Title", "New Content", false);
        NoteDTO response = new NoteDTO(1L, "New Title", "New Content", false);

        when(noteService.updateNote(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.content").value("New Content"));
    }

    @Test
    void shouldReturn404WhenUpdateNotFound() throws Exception {
        NoteDTO request = new NoteDTO(null, "Title", "Content", false);

        when(noteService.updateNote(1L, request))
                .thenThrow(new NoteNotFoundException(1L));

        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 1")); // ✅ opcional pero recomendado
    }

    // ---------------- DELETE ----------------
    @Test
    void shouldDeleteNote() throws Exception {
        doNothing().when(noteService).deleteNote(1L);

        mockMvc.perform(delete("/api/notes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeleteNotFound() throws Exception {
        doThrow(new NoteNotFoundException(1L))
                .when(noteService).deleteNote(1L);

        mockMvc.perform(delete("/api/notes/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 1"));
    }

    // ---------------- ARCHIVE ----------------
    @Test
    void shouldArchiveNote() throws Exception {
        doNothing().when(noteService).archiveNote(1L);

        mockMvc.perform(post("/api/notes/1/archive"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenArchiveNotFound() throws Exception {
        doThrow(new NoteNotFoundException(1L))
                .when(noteService).archiveNote(1L);

        mockMvc.perform(post("/api/notes/1/archive"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 1"));
    }

    // ---------------- UNARCHIVE ----------------
    @Test
    void shouldUnarchiveNote() throws Exception {
        doNothing().when(noteService).unarchiveNote(1L);

        mockMvc.perform(post("/api/notes/1/unarchive"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenUnarchiveNotFound() throws Exception {
        doThrow(new NoteNotFoundException(1L))
                .when(noteService).unarchiveNote(1L);

        mockMvc.perform(post("/api/notes/1/unarchive"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 1"));
    }

    // ---------------- GET ACTIVE ----------------
    @Test
    void shouldGetActiveNotes() throws Exception {
        List<NoteDTO> notes = List.of(
                new NoteDTO(1L, "Title", "Content", false)
        );

        when(noteService.getActiveNotes()).thenReturn(notes);

        mockMvc.perform(get("/api/notes/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].archived").value(false));
    }

    // ---------------- GET ARCHIVED ----------------
    @Test
    void shouldGetArchivedNotes() throws Exception {
        List<NoteDTO> notes = List.of(
                new NoteDTO(1L, "Title", "Content", true)
        );

        when(noteService.getArchivedNotes()).thenReturn(notes);

        mockMvc.perform(get("/api/notes/archived"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].archived").value(true));
    }
}