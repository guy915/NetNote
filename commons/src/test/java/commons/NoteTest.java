package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Note} class.
 * <p>
 * This class contains unit tests to verify the functionality
 * of the Note class. It includes tests for initialization,
 * getters, setters, equality logic, and file management operations.
 */
public class NoteTest {

    private Note note1;
    private Note note2;

    /**
     * Setup method to initialize reusable test objects.
     * <p>
     * This method is executed before each test, ensuring that
     * each test case starts with a consistent state.
     */
    @BeforeEach
    public void setup() {
        note1 = new Note("Title", "Content");
        note2 = new Note("Title", "Content");
    }

    /**
     * Tests the initialization of a Note object.
     */
    @Test
    public void testNoteInitialization() {
        assertEquals("Title", note1.getTitle());
        assertEquals("Content", note1.getContent());
        assertNotNull(note1.getNoteTag());
    }

    /**
     * Tests the setter methods of a Note object.
     */
    @Test
    public void testNoteSetters() {
        note1.setTitle("New Title");
        note1.setContent("New Content");

        assertEquals("New Title", note1.getTitle());
        assertEquals("New Content", note1.getContent());
    }

    /**
     * Tests the equality logic between two Note objects.
     */
    @Test
    public void testNoteEquals() {
        assertEquals(note1, note2);

        note2.setContent("Other Content");
        assertNotEquals(note1, note2);
    }

    /**
     * Tests the equals method with null and objects of a different type.
     */
    @Test
    public void testNoteEqualsWithNullAndDifferentClass() {
        assertNotEquals(null, note1);
        assertNotEquals("A String", note1);
    }

    /**
     * Tests the hashCode implementation.
     */
    @Test
    public void testNoteHashCode() {
        assertEquals(note1.hashCode(), note2.hashCode());

        note2.setContent("Other Content");
        assertNotEquals(note1.hashCode(), note2.hashCode());
    }
}