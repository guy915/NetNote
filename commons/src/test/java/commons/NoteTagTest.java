package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test class for the {@link NoteTag} class.
 * <p>
 * This class contains unit tests that verify the functionality
 * of the NoteTag class. It tests the initialization, getters, setters,
 * and equals method of the NoteTag objects.
 */
public class NoteTagTest {
    private NoteTag tag1;
    private NoteTag tag2;

    /**
     * Setup method to initialize common test objects.
     * <p>
     * This method is executed before each test to ensure reusable and
     * consistent test objects.
     */
    @BeforeEach
    public void setup() {
        tag1 = new NoteTag("Tag");
        tag2 = new NoteTag("Tag");
    }

    /**
     * Tests the initialization of a NoteTag object.
     * <p>
     * Verifies that the constructor correctly assigns the specified name.
     */
    @Test
    public void testNoteTagInitialization() {
        assertEquals("Tag", tag1.getName());
    }

    /**
     * Tests the setter method for the name property of a NoteTag object.
     * <p>
     * This unit test initializes a new NoteTag object and sets its name
     * using the setName method. It then verifies that the name has been
     * correctly updated by comparing it against the expected value.
     */
    @Test
    public void testNoteTagSetters() {
        tag1.setName("New Tag");
        assertEquals("New Tag", tag1.getName());
    }

    /**
     * Tests the equality logic of the {@link NoteTag} class.
     * <p>
     * This test case verifies that the equals method of the {@link NoteTag} class
     * correctly determines whether two NoteTag instances are equal based on their names.
     * <p>
     * It initializes two NoteTag objects with the same name and checks that they are
     * considered equal. Then, it changes the name of one of the tags and asserts that
     * they are no longer equal.
     */
    @Test
    public void testNoteTagEquals() {
        assertEquals(tag1, tag2);

        tag2.setName("Other Tag");
        assertNotEquals(tag1, tag2);
    }

    /**
     * Tests equality comparison with null and objects of a different class.
     * <p>
     * Ensures that the equals method of the {@link NoteTag} class
     * returns false when compared with null or an object of a different type.
     */
    @Test
    public void testNoteTagEqualsWithNullAndDifferentClass() {
        assertNotEquals(null, tag1);

        // cannot fail:
        assertNotEquals("A String", tag1);
    }

    /**
     * Tests the hashCode implementation of the {@link NoteTag} class.
     * <p>
     * Verifies that hashCode returns the same value for equal objects
     * and different values for objects with different names.
     */
    @Test
    public void testNoteTagHashCode() {
        //check hash codes for equal objects
        assertEquals(tag1.hashCode(), tag2.hashCode());

        //modify tag2 and check for different hash codes
        tag2.setName("Other Tag");
        assertNotEquals(tag1.hashCode(), tag2.hashCode());
    }
}