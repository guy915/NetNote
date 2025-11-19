package client.scenes;

import commons.Note;
import commons.NoteCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchBarTest {

    private NoteCollection collection;
    private Note note1;
    private Note note2;
    private Note note3;

    @BeforeEach
    void setUp() {
        collection = new NoteCollection("Test Collection");
        note1 = new Note("Note 1", "Content 1");
        note2 = new Note("Note 2", "Content 2");
        note3 = new Note("Note 3", "Content 3");

        collection.addNote(note1);
        collection.addNote(note2);
        collection.addNote(note3);
    }

    @Test
    void testSearchByTitle() {
        String searchQuery = "Note 1";
        List<Note> matchingNotes = searchNotesInCollection(collection, searchQuery);

        assertEquals(1, matchingNotes.size());
        assertEquals(note1, matchingNotes.getFirst());
    }

    @Test
    void testSearchByContent() {
        String searchQuery = "Content 2";
        List<Note> matchingNotes = searchNotesInCollection(collection, searchQuery);

        assertEquals(1, matchingNotes.size());
        assertEquals(note2, matchingNotes.getFirst());
    }

    @Test
    void testCaseInsensitiveSearch() {
        String searchQuery = "content 3";
        List<Note> matchingNotes = searchNotesInCollection(collection, searchQuery);

        assertEquals(1, matchingNotes.size());
        assertEquals(note3, matchingNotes.getFirst());
    }

    @Test
    void testEmptySearchReturnsAllNotes() {
        String searchQuery = "";
        List<Note> matchingNotes = searchNotesInCollection(collection, searchQuery);

        assertEquals(3, matchingNotes.size());
        assertTrue(matchingNotes.contains(note1));
        assertTrue(matchingNotes.contains(note2));
        assertTrue(matchingNotes.contains(note3));
    }

    @Test
    void testNoMatchSearchReturnsEmptyList() {
        String searchQuery = "Nonexistent";
        List<Note> matchingNotes = searchNotesInCollection(collection, searchQuery);

        assertEquals(0, matchingNotes.size());
    }

    @Test
    void testNullSearchReturnsAllNotes() {
        List<Note> matchingNotes = searchNotesInCollection(collection, null);

        assertEquals(3, matchingNotes.size());
        assertTrue(matchingNotes.contains(note1));
        assertTrue(matchingNotes.contains(note2));
        assertTrue(matchingNotes.contains(note3));
    }

    /**
     * Searches for notes in a specific collection based on the query string.
     *
     * @param collection The NoteCollection to search in
     * @param query The search query string
     * @return A filtered list of matching notes
     */
    private List<Note> searchNotesInCollection(NoteCollection collection, String query) {
        if (query == null || query.isEmpty()) {
            return collection.getNotes();
        }
        return collection.getNotes().stream()
                .filter(note -> note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getContent().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}