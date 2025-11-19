package server.api;

import commons.Note;
import commons.NoteCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteCollectionRepository;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notecollections")
public class NoteCollectionController {

    private final NoteCollectionRepository repo;
    private final NoteRepository noteRepo;

    /**
     * NoteCollectionController constructor
     *
     * @param noteRepo note repository
     * @param repository NoteCollectionRepository instance
     */
    public NoteCollectionController(NoteCollectionRepository repository, NoteRepository noteRepo) {
        this.repo = repository;
        this.noteRepo = noteRepo;
    }

    /**
     * Returns all collections from the database
     *
     * @return a list of NoteCollections
     */
    @GetMapping(path = {"", "/"})
    public List<NoteCollection> getAll() {
        return repo.findAll();
    }

    /**
     * Finds a NoteCollection with given name
     *
     * @param name name used to search
     * @return ResponseEntity of NoteCollection
     */
    @GetMapping("/name/{name}")
    public List<NoteCollection> getByName(@PathVariable("name") String name) {
        if (name.isEmpty()) {
            return new ArrayList<NoteCollection>();
        }
        return repo.findByName(name);
    }

    /**
     * Finds a NoteCollection with given id
     *
     * @param id id used to search
     * @return ResponseEntity of NoteCollection
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<NoteCollection> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Adds a collection to the database
     *
     * @param collection collection to add
     * @return the added collection
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<NoteCollection> add(@RequestBody NoteCollection collection) {
        NoteCollection saved = repo.save(collection);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/id/{id}/notes")
    public ResponseEntity<NoteCollection> addNote(@PathVariable long id, @RequestBody Note note) {
        if (id < 0 || repo.findById(id).isEmpty() || note == null) {
            return ResponseEntity.badRequest().build();
        }
        NoteCollection newCollection = repo.findById(id).get();
        NoteCollection oldCollection = note.getNoteCollection();

        note.getNoteCollection().getNotes().remove(note);
        repo.save(oldCollection);

        note.setNoteCollection(newCollection);
        noteRepo.save(note);

        newCollection.addNote(note);
        repo.save(newCollection);
        return ResponseEntity.ok(newCollection);
    }

    /**
     * DELETE HTTP mapping for deleting a collection from the database
     *
     * @param id of the collection to delete
     * @return nothing if successful, otherwise a badRequest
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteCollection> updateCollection(@PathVariable long id, @RequestBody NoteCollection updatedCollection) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        updatedCollection.setId(id);
        NoteCollection newCollection = repo.save(updatedCollection);
        return ResponseEntity.ok(newCollection);
    }

}
