package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link NoteCollection} class.
 * <p>
 * This class contains unit tests to verify the correct functionality
 * of the NoteCollection class, including initialization, property setters,
 * equality checks, and hashCode behavior.
 */

public class NoteCollectionTest {

    private NoteCollection collection;
    private Note note1;
    private Note note2;

    /**
     * Setup method to initialize common test objects.
     * <p>
     * This method is executed before each test to ensure a clean slate.
     * It initializes a default NoteCollection instance and sample Note objects
     * for use in tests.
     */
    @BeforeEach
    public void setup() {
        collection = new NoteCollection();
        note1 = new Note("Title1", "Content1");
        note2 = new Note("Title2", "Content2");
    }

    /**
     * Tests the initialization of a NoteCollection object.
     * <p>
     * Verifies that a NoteCollection is correctly instantiated with a provided name
     * and ensures that the notes list is properly initialized and not null.
     */
    @Test
    public void testNoteCollectionInitialization() {
        collection = new NoteCollection("Collection Name");

        assertEquals("Collection Name", collection.getName());
        assertNotNull(collection.getNotes());
    }

    /**
     * Tests the setter methods of the NoteCollection class.
     * <p>
     * Verifies that the setName method correctly assigns a new name to the NoteCollection instance,
     * and that the setNotes method properly updates the list of notes contained in the collection.
     * Asserts that the name is updated successfully and the notes list contains the expected number of Note objects.
     */
    @Test
    public void testNoteCollectionSetters() {
        collection.setName("New Collection");

        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        collection.setNotes(notes);

        assertEquals("New Collection", collection.getName());
        assertEquals(2, collection.getNotes().size());
        assertEquals("Title1", collection.getNotes().get(0).getTitle());
        assertEquals("Title2", collection.getNotes().get(1).getTitle());
    }

    /**
     * Tests the equality contract of the NoteCollection class.
     * <p>
     * This test ensures that two NoteCollection instances with the
     * same name and notes are considered equal, and that changing the name
     * or notes of one instance results in them not being equal.
     * <p>
     * It includes checks for comparison with `null` and objects of a different class,
     * verifying that these comparisons always return `false`.
     * <p>
     * It first creates two NoteCollections with identical names and verifies
     * they are equal. It then changes the name of one of the collections
     * and verifies that they are no longer equal.
     */
    @Test
    public void testNoteCollectionEquals() {
        NoteCollection collection1 = new NoteCollection("Collection");
        NoteCollection collection2 = new NoteCollection("Collection");

        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        collection1.setNotes(notes);
        collection2.setNotes(notes);

        assertEquals(collection1, collection2);

        collection2.setName("Other Collection");
        assertNotEquals(collection1, collection2);

        assertNotEquals(null, collection1);
        //this test cannot fail:
        assertNotEquals("A String", collection1);
    }

    /**
     * Tests the hashCode method of the NoteCollection class.
     * <p>
     * Verifies that two NoteCollection instances with the same properties
     * have the same hashCode, and that changing a property results in a different hashCode.
     * The test ensures consistency between equals and hashCode implementations.
     * Added extra comments for clarity.
     */
    @Test
    public void testNoteCollectionHashCode() {
        NoteCollection collection1 = new NoteCollection("Collection");
        NoteCollection collection2 = new NoteCollection("Collection");

        //add the same notes to both collections
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);

        collection1.setNotes(notes);
        collection2.setNotes(notes);

        //verify that equal objects have the same hashCode
        assertEquals(collection1, collection2);
        assertEquals(collection1.hashCode(), collection2.hashCode());

        //modify one object and verify that hashCodes are no longer equal
        collection2.setName("Other Collection");
        assertNotEquals(collection1, collection2);
        assertNotEquals(collection1.hashCode(), collection2.hashCode());
    }
}