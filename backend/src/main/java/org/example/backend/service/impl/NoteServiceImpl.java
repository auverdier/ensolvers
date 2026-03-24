package org.example.backend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.NoteDTO;
import org.example.backend.entity.Note;
import org.example.backend.exception.NoteNotFoundException;
import org.example.backend.repository.NoteRepository;
import org.example.backend.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public NoteDTO createNote(NoteDTO noteDTO) {
        log.info("Creating note with title: {}", noteDTO.title());

        Note note = new Note(noteDTO.title(), noteDTO.content(), false);
        Note savedNote = noteRepository.save(note);

        log.info("Note created with id: {}", savedNote.getId());

        return mapToDTO(savedNote);
    }

    @Override
    public NoteDTO updateNote(Long id, NoteDTO noteDTO) {
        log.info("Updating note with id: {}", id);

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Note not found for update. id: {}", id);
                    return new NoteNotFoundException(id);
                });

        note.setTitle(noteDTO.title());
        note.setContent(noteDTO.content());

        Note updatedNote = noteRepository.save(note);

        log.info("Note updated successfully. id: {}", id);

        return mapToDTO(updatedNote);
    }

    @Override
    public void deleteNote(Long id) {
        log.info("Deleting note with id: {}", id);

        if (!noteRepository.existsById(id)) {
            log.warn("Note not found for deletion. id: {}", id);
            throw new NoteNotFoundException(id);
        }

        noteRepository.deleteById(id);

        log.info("Note deleted successfully. id: {}", id);
    }

    @Override
    public void archiveNote(Long id) {
        log.info("Archiving note with id: {}", id);

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Note not found for archive. id: {}", id);
                    return new NoteNotFoundException(id);
                });

        note.setArchived(true);
        noteRepository.save(note);

        log.info("Note archived successfully. id: {}", id);
    }

    @Override
    public void unarchiveNote(Long id) {
        log.info("Unarchiving note with id: {}", id);

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Note not found for unarchive. id: {}", id);
                    return new NoteNotFoundException(id);
                });

        note.setArchived(false);
        noteRepository.save(note);

        log.info("Note unarchived successfully. id: {}", id);
    }

    @Override
    public List<NoteDTO> getActiveNotes() {
        log.info("Fetching active notes");

        List<NoteDTO> notes = noteRepository.findByArchived(false).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("Found {} active notes", notes.size());

        return notes;
    }

    @Override
    public List<NoteDTO> getArchivedNotes() {
        log.info("Fetching archived notes");

        List<NoteDTO> notes = noteRepository.findByArchived(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("Found {} archived notes", notes.size());

        return notes;
    }

    private NoteDTO mapToDTO(Note note) {
        return new NoteDTO(note.getId(), note.getTitle(), note.getContent(), note.isArchived());
    }
}