package server.api;

import commons.Note;
import commons.NoteTag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;
import server.service.MarkdownService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("api/notes")
public class NoteController {

    private final NoteRepository repo;
    private final MarkdownService markdownService;

    /**
     * Constructor of the NoteController class
     *
     * @param repo            instance of a NoteRepository
     * @param markdownService instance of MarkdownService
     */
    public NoteController(NoteRepository repo, MarkdownService markdownService) {
        this.repo = repo;
        this.markdownService = markdownService;
    }

    /**
     * GET HTTP mapping for finding all notes
     *
     * @return List of all notes in the database
     */
    @GetMapping(path = {"", "/"})
    public List<Note> getAll() {
        return repo.findAll();
    }

    /**
     * GET HTTP mapping for finding a specific note by ID
     *
     * @param id to search for
     * @return Note if found, otherwise a badRequest
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Note> getById(@PathVariable("id") long id) {
        if (isInValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Note> note = repo.findById(id);
        return note.map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null));
    }

    /**
     * GET HTTP mapping for finding a specific note by title
     *
     * @param title to search for
     * @return Note if found, otherwise a badRequest
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<Note> getByTitle(@PathVariable("title") String title) {
        if (isEmpty(title)) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsByTitle(title)) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(repo.findByTitle(title).getFirst());
    }

    /**
     * POST HTTP mapping for adding a new note to the database
     *
     * @param note to add
     * @return the saved note if successful, otherwise a badRequest
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Note> add(@RequestBody Note note) {
        if (note == null || isEmpty(note.getTitle())) {
            return ResponseEntity.badRequest().build();
        }
        Note savedNote = repo.save(note);
        return ResponseEntity.ok(savedNote);
    }

    /**
     * PUT HTTP mapping for changing a note's title
     *
     * @param id       of the note to change
     * @param newTitle for the note
     * @return the adjusted note if successful, otherwise a badRequest
     */
    @PutMapping("/id/{id}/title")
    public ResponseEntity<Note> changeTitle(@PathVariable("id") long id, @RequestBody String newTitle) {
        if (isInValidId(id) || isEmpty(newTitle)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Note> noteOpt = repo.findById(id);
        if (noteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Note note = noteOpt.get();
        note.setTitle(newTitle);
        repo.save(note);
        return ResponseEntity.ok(note);
    }

    /**
     * PUT HTTP mapping for changing a note's content
     *
     * @param id         of the note to change
     * @param newContent for the note
     * @return the adjusted note if successful, otherwise a badRequest
     */
    @PutMapping("/id/{id}/content")
    public ResponseEntity<Note> changeContent(@PathVariable("id") long id, @RequestBody String newContent) {
        if (isInValidId(id) || newContent == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Note> noteOpt = repo.findById(id);
        if (noteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Note note = noteOpt.get();
        if (newContent.length() == 1) {
            newContent = "";
        } else {
            newContent = newContent.substring(1);
        }
        note.setContent(newContent);
        repo.save(note);
        return ResponseEntity.ok(note);
    }

    /**
     * PUT HTTP mapping for adding a tag to a note
     *
     * @param id   note id
     * @param tags list of tags to set to the note
     * @return the updated note if successful, otherwise a badRequest
     */
    @PutMapping(path = {"/tag/{id}"})
    public ResponseEntity<Note> changeTags(@PathVariable("id") long id, @RequestBody Set<NoteTag> tags) {
        if (isInValidId(id) || tags == null) {
            return ResponseEntity.badRequest().build();
        }
        ResponseEntity<Note> noteResponse = getById(id);
        Note note = noteResponse.getBody();
        if (note == null) {
            return ResponseEntity.badRequest().build();
        }
        note.setTags(tags);
        Note savedNote = repo.save(note);
        return ResponseEntity.ok(savedNote);
    }

    /**
     * PUT HTTP mapping for resetting note's tags
     *
     * @param note the note which tags are cleared
     * @return the updated note if successful, otherwise a badRequest
     */
    @PutMapping(path = {"/tags/clear"})
    public ResponseEntity<Note> resetTags(@RequestBody Note note) {
        if (note == null || isInValidId(note.getId())) {
            return ResponseEntity.badRequest().build();
        }
        note.setTags(new HashSet<>());
        Note savedNote = repo.save(note);
        return ResponseEntity.ok(savedNote);
    }

    /**
     * DELETE HTTP mapping for deleting a note from the database
     *
     * @param id of the note to delete
     * @return nothing if successful, otherwise a badRequest
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        if (isInValidId(id) || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Note> searchNotes(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("collectionTitle") String collectionTitle) {

        if (isEmpty(collectionTitle)) {
            return repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
        }

        String searchQuery = isEmpty(query) ? "" : query;
        return repo.findByQueryAndCollection(query, collectionTitle);
    }

    private boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInValidId(long id) {
        return id < 0;
    }

    /**
     * POST HTTP Converts raw Markdown content into HTML.
     *
     * @param markdownContent The raw Markdown content to render.
     * @param id              note's id
     * @return The rendered HTML content, or an error message if input is invalid.
     */
    @PostMapping("/renderMarkdown/{id}")
    public ResponseEntity<String> renderMarkdown(@RequestBody String markdownContent, @PathVariable("id") long id) {
        if (isInValidId(id) || markdownContent == null) {
            return ResponseEntity.badRequest().body("Invalid input parameters");
        }

        try {
            Optional<Note> noteOpt = repo.findById(id);
            if (noteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Note not found");
            }
            String htmlContent = markdownService.renderMarkdown(markdownContent, noteOpt.get());
            return ResponseEntity.ok(htmlContent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while rendering Markdown. Please try again.");
        }
    }

    @PutMapping("/references/{id}")
    public ResponseEntity<Note> setReferencedNotes(@RequestBody List<Note> references, @PathVariable("id") long id) {
        if (isInValidId(id) || references == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Note> noteOpt = repo.findById(id);
        if (noteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Note note = noteOpt.get();
        note.setReferences(references);
        repo.save(note);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/references/title/{title}")
    public ResponseEntity<Note> updateReferences(@RequestBody Note note, @PathVariable("title") String oldTitle) {
        if (note == null || isEmpty(oldTitle) || isInValidId(note.getId())) {
            return ResponseEntity.badRequest().build();
        }

        List<Note> referencing = repo.findAll().stream()
                .filter(n -> n.isReferencing(note))
                .toList();
        String title = note.getTitle();

        for (Note n : referencing) {
            String content = n.getContent();
            n.setContent(content.replace("[[" + oldTitle + "]]", "[[" + title + "]]"));
            repo.save(n);
        }

        if (note.getContent().contains("[[" + oldTitle + "]]")) {
            note.setContent(note.getContent().replace("[[" + oldTitle + "]]", "[[" + title + "]]"));
            repo.save(note);
        }

        return repo.findById(note.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}