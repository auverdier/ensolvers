package org.example.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.NoteDTO;
import org.example.backend.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/notes")
public class NotesController {

    private final NoteService noteService;

    public NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody NoteDTO noteDTO) {
        log.info("POST /api/notes - Creating note with title: {}", noteDTO.title());

        NoteDTO created = noteService.createNote(noteDTO);

        log.info("POST /api/notes - Note created with id: {}", created.id());

        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @RequestBody NoteDTO noteDTO) {
        log.info("PUT /api/notes/{} - Updating note", id);

        NoteDTO updated = noteService.updateNote(id, noteDTO);

        log.info("PUT /api/notes/{} - Update successful", id);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.info("DELETE /api/notes/{} - Deleting note", id);

        noteService.deleteNote(id);

        log.info("DELETE /api/notes/{} - Delete successful", id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveNote(@PathVariable Long id) {
        log.info("POST /api/notes/{}/archive - Archiving note", id);

        noteService.archiveNote(id);

        log.info("POST /api/notes/{}/archive - Archive successful", id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveNote(@PathVariable Long id) {
        log.info("POST /api/notes/{}/unarchive - Unarchiving note", id);

        noteService.unarchiveNote(id);

        log.info("POST /api/notes/{}/unarchive - Unarchive successful", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<NoteDTO>> getActiveNotes() {
        log.debug("GET /api/notes/active - Fetching active notes");

        List<NoteDTO> notes = noteService.getActiveNotes();

        log.debug("GET /api/notes/active - Found {} notes", notes.size());

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<NoteDTO>> getArchivedNotes() {
        log.debug("GET /api/notes/archived - Fetching archived notes");

        List<NoteDTO> notes = noteService.getArchivedNotes();

        log.debug("GET /api/notes/archived - Found {} notes", notes.size());

        return ResponseEntity.ok(notes);
    }
}