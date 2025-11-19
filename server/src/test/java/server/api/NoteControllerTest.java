package server.api;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.MarkdownService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoteControllerTest {

    private TestNoteRepository repo;
    private NoteController sut;
    private Note note1;
    private Note note2;
    private Note note3;
    private Note note4;
    private Note note5;
    private Note note6;

    @BeforeEach
    public void setup() {
        repo = new TestNoteRepository();
        TagController tc = new TagController(null);
        MarkdownService markdownService = new MarkdownService(tc, null);
        sut = new NoteController(repo, markdownService);
        markdownService.setNoteController(sut);
        note1 = new Note("testNote", "");
        note2 = new Note("testNote2", "");
        note3 = new Note("testNote3", "testContent");
        note4 = new Note("testNote4", "testContent2");
        note5 = new Note("testNote5 @", "testContent3");
        note6 = new Note("testNote__6", "testContent @");
    }

    /**
     * Example of an existing test.
     */
    @Test
    void getByTitle() {
        sut.add(note1);
        Note n = sut.getByTitle(note1.getTitle()).getBody();
        assertEquals(note1, n);
    }

    @Test
    void getByTitle2() {
        sut.add(note2);
        assertEquals("testNote2", note2.getTitle());
    }

    @Test
    void getByTitle3() {
        sut.add(note6);
        assertEquals("testNote__6", note6.getTitle());
    }

    @Test
    void getByTitle4() {
        sut.add(note5);
        assertEquals("testNote5 @", note5.getTitle());
    }

    @Test
    void delete() {
        sut.add(note1);
        sut.delete(note1.getId());
        assertEquals(0, repo.findAll().size());
    }

    @Test
    void delete2() {
        sut.add(note1);
        sut.add(note2);
        sut.delete(note1.getId());
        sut.delete(note2.getId());
        assertEquals(0, repo.notes.size());
    }

    @Test
    void delete3() {
        sut.add(note1);
        sut.add(note2);
        sut.add(note3);
        sut.delete(note1.getId());
        sut.delete(note2.getId());
        sut.delete(note3.getId());
        assertEquals(0, repo.notes.size());
    }

    @Test
    void delete4() {
        sut.add(note1);
        sut.add(note2);
        sut.add(note3);
        sut.add(note4);
        sut.delete(note1.getId());
        sut.delete(note2.getId());
        sut.delete(note3.getId());
        sut.delete(note4.getId());
        assertEquals(0, repo.notes.size());
    }

    @Test
    void deleteAll() {
        sut.add(note1);
        sut.add(note2);
        sut.add(note3);
        sut.add(note4);
        sut.add(note5);
        sut.delete(note1.getId());
        sut.delete(note2.getId());
        sut.delete(note3.getId());
        sut.delete(note4.getId());
        sut.delete(note5.getId());
        assertEquals(0, repo.notes.size());
    }

    @Test
    void deleteAll2() {
        sut.add(note1);
        sut.add(note2);
        sut.add(note3);
        sut.add(note4);
        sut.add(note5);
        sut.add(note6);
        sut.delete(note1.getId());
        sut.delete(note2.getId());
        sut.delete(note3.getId());
        sut.delete(note4.getId());
        sut.delete(note5.getId());
        sut.delete(note6.getId());
        assertEquals(0, repo.notes.size());
    }

    @Test
    void getAllTest() {
        sut.add(note1);
        sut.add(note2);
        sut.add(note3);

        List<Note> notes = repo.findAll();

        assertEquals(3, notes.size());
        assertEquals(notes, sut.getAll());
    }

    @Test
    void getByIdTest() {
        sut.add(note1);
        assertEquals(note1, sut.getById(note1.getId()).getBody());
    }

    @Test
    void addTest() {
        sut.add(note1);
        assertTrue(repo.findAll().contains(note1));
    }
}