package server.api;

import commons.NoteTag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteTagRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/tags")
public class TagController {

    private final NoteTagRepository repo;

    public TagController(NoteTagRepository repo) {
        this.repo = repo;
    }

    /**
     * GET HTTP mapping for finding all notes
     *
     * @return List of all notes in the database
     */
    @GetMapping(path = {"", "/"})
    public List<NoteTag> getAll() {
        return repo.findAll();
    }

    /**
     * GET HTTP mapping for finding a specific tag by name
     *
     * @param name to search for
     * @return Tag if found, otherwise a badRequest
     */
    @GetMapping("/name/{name}")
    public Optional<NoteTag> getByName(@PathVariable("name") String name) {
        if (name == null || name.isBlank() || !repo.existsByName(name)) {
            return Optional.empty();
        }
        return Optional.of(repo.findByName(name).getFirst());
    }

    /**
     * POST HTTP mapping for adding a new tag to the database
     *
     * @param tag to add
     * @return the saved tag if successful, otherwise a badRequest
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<NoteTag> add(@RequestBody NoteTag tag) {
        String name = tag.getName();
        if (name == null || name.isBlank() || repo.existsByName(name)) {
            return ResponseEntity.badRequest().build();
        }
        NoteTag savedTag = repo.save(tag);
        return ResponseEntity.ok(savedTag);
    }

    /**
     * DELETE HTTP mapping for deleting a tag from the database
     * @param name of the tag to delete
     * @return nothing if successful, otherwise a badRequest
     */
    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> delete(@PathVariable("name") String name) {
        if (name.isBlank() || !repo.existsByName(name)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
}
