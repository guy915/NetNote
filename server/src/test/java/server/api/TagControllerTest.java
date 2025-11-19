package server.api;

import commons.NoteTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TagControllerTest {

    private TestTagRepository repo;
    private TagController sut;
    private NoteTag normalName;
    private NoteTag specialName;
    private NoteTag nullName;
    private NoteTag emptyName;

    @BeforeEach
    public void setup() {
        repo = new TestTagRepository();
        sut = new TagController(repo);
        normalName = new NoteTag("tagOne1");
        specialName = new NoteTag("TagTwo@");
        nullName = new NoteTag(null);
        emptyName = new NoteTag("");
    }

    /**
     * Basic method tests to test the methods
     */

    @Test
    void getAll() {
        sut.add(nullName);
        sut.add(emptyName);

        List<NoteTag> tags = sut.getAll();

        assertEquals(0, tags.size());
        assertFalse(tags.contains(normalName));
        assertFalse(tags.contains(specialName));
    }

    @Test
    void getByName() {
        sut.add(nullName);
        sut.add(emptyName);

        assertEquals(Optional.empty(), sut.getByName("tagOne1"));
        assertEquals(Optional.empty(), sut.getByName(""));

    }

    @Test
    void add() {
        sut.add(normalName);
        sut.add(specialName);
        ResponseEntity<NoteTag> responseTag3 = sut.add(nullName);

        List<NoteTag> tagsInRepo = repo.findAll();
        assertEquals(0, tagsInRepo.size());

        assertFalse(responseTag3.hasBody());
    }

    /**
     * Special edge case tests to make sure they work thoroughly
     */

    @Test
    void addBlankName() {
        sut.add(normalName);
        sut.add(specialName);
        ResponseEntity<NoteTag> responseBlank = sut.add(emptyName);
        repo.findAll();
        assertFalse(responseBlank.hasBody());
    }

    @Test
    void addDuplicateTag() {
        sut.add(normalName);
        NoteTag duplicateTag = new NoteTag("tagOne1");
        ResponseEntity<NoteTag> duplicateResponse = sut.add(duplicateTag);

        repo.findAll();
        assertFalse(duplicateResponse.hasBody());
    }

    @Test
    void getByNameWithBlankInput() {
        sut.add(normalName);
        sut.add(specialName);
        Optional<NoteTag> result = sut.getByName("");
        assertTrue(result.isEmpty());
    }

    @Test
    void getByNameCaseSensitive() {
        sut.add(specialName);

        Optional<NoteTag> result = sut.getByName("tagtwo@");

        assertTrue(result.isEmpty());
    }
}