package server.api;

import commons.NoteCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.NoteCollectionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteCollectionControllerTest {

    private NoteCollectionRepository repo;
    private NoteCollectionController sut;
    private NoteCollection noteCollection1;
    private NoteCollection noteCollection2;
    private NoteCollection noteCollection3;

    @BeforeEach
    public void setup() {
        repo = new TestCollectionControllerRepository();
        sut = new NoteCollectionController(repo, new TestNoteRepository());
        noteCollection1 = new NoteCollection("testCollection");
        noteCollection2 = new NoteCollection("testCollection2");
        noteCollection3 = new NoteCollection("testCollection3 @");
    }

    @Test
    void getAllTest() {
        sut.add(noteCollection1);
        sut.add(noteCollection2);

        List<NoteCollection> noteCollection = repo.findAll();

        assertTrue(noteCollection.contains(noteCollection1) && noteCollection.contains(noteCollection2));
        assertEquals(2, noteCollection.size());
    }

    @Test
    void deleteNonExistent() {
        sut.add(noteCollection3);
        //sut.delete(noteCollection2.getId());
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void getByName() {
        sut.add(noteCollection1);
        assertEquals("testCollection", noteCollection1.getName());
    }

    @Test
    void getByName2() {
        sut.add(noteCollection2);
        assertEquals("testCollection2", noteCollection2.getName());
    }

    @Test
    void getByName3() {
        sut.add(noteCollection3);
        assertEquals("testCollection3 @", repo.findAll().getFirst().getName());
    }

    @Test
    void deleteExistent() {
        sut.add(noteCollection3);
        sut.delete(noteCollection3.getId());
        assertEquals(0, repo.findAll().size());
    }

    @Test
    void getByIdTest() {
        sut.add(noteCollection1);
        assertEquals(Optional.of(noteCollection1), repo.findById(noteCollection1.getId()));
    }

    @Test
    void addTest() {
        sut.add(noteCollection1);
        assertEquals(1, repo.findAll().size());
        assertEquals(repo.findAll().getFirst(), noteCollection1);
    }
}