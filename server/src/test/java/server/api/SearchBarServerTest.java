package server.api;

import commons.Note;
import commons.NoteCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import server.database.NoteRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Annotation for loading the full application context
// for testing purposes
@SpringBootTest
// Annotation for simulating HTTP request
// by enabling MockMVC
@AutoConfigureMockMvc
public class SearchBarServerTest {

    // Annotation for allowing Spring to inject a dependency
    // into a field
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteRepository repo;

    @BeforeEach
    void setUp() {
        NoteCollection collection1 = new NoteCollection("Collection 1");
        NoteCollection collection2 = new NoteCollection("Collection 2");
        NoteCollection collection3 = new NoteCollection("Collection 3");

        collection1.addNote(new Note("Note 1", "This note belongs to Collection 1"));
        collection1.addNote(new Note("Note 2", "This note belongs to Collection 1"));
        collection1.addNote(new Note("Note 3", "This note belongs to Collection 1"));
        collection2.addNote(new Note("Note 1", "This note belongs to Collection 2"));
        collection3.addNote(new Note("Note 1", "This note belongs to Collection 3"));
        collection3.addNote(new Note("Note 2", "This note belongs to Collection 3"));

        setUpNote1Collection1();
        setUpNote2Collection1();
        setUpNote3Collection1();
        setUpNote1Collection2();
        setUpNote1Collection3();
        setUpNote2Collection3();

        // Multiple matches within Collection 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note", "", "Collection 1"))
                .thenReturn(List.of(
                        new Note("Note 1", "This note belongs to Collection 1"),
                        new Note("Note 2", "This note belongs to Collection 1"),
                        new Note("Note 3", "This note belongs to Collection 1")
                ));

        // Note 1 from Collection 1 - Case Insensitive: NOTE 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "NOTE 1", "", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));

        // Note 1 from Collection 1 - Case Insensitive: NoTE 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "NoTE 1", "", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));

        // Note 1 from Collection 1 - Case Insensitive: NoTE 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "NotE 1", "", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));

        setUpEmptySearchWithinCollection1();
    }

    private void setUpNote1Collection1() {
        // Note 1 from Collection 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 1", "This note belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 1", "", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "This note belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName("", "belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 1")));
    }

    private void setUpNote2Collection1() {
        // Note 2 from Collection 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 2", "This note belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 2", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 2", "", "Collection 1"))
                .thenReturn(List.of(new Note("Note 2", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "This note belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 2", "This note belongs to Collection 1")));
    }

    private void setUpNote3Collection1() {
        // Note 3 from Collection 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 3", "This note belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 3", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 3", "", "Collection 1"))
                .thenReturn(List.of(new Note("Note 3", "This note belongs to Collection 1")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "This note belongs to Collection 1", "Collection 1"))
                .thenReturn(List.of(new Note("Note 3", "This note belongs to Collection 1")));
    }

    private void setUpNote1Collection2() {
        // Note 1 from Collection 2
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 1", "This note belongs to Collection 2", "Collection 2"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 2")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 1", "", "Collection 2"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 2")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "This note belongs to Collection 2", "Collection 2"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 2")));
    }

    private void setUpNote1Collection3() {
        // Note 1 from Collection 3
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 1", "This note belongs to Collection 3", "Collection 3"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 3")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 1", "", "Collection 3"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 3")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "This note belongs to Collection 3", "Collection 3"))
                .thenReturn(List.of(new Note("Note 1", "This note belongs to Collection 3")));
    }

    private void setUpNote2Collection3() {
        // Note 2 from Collection 3
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 2", "This note belongs to Collection 3", "Collection 3"))
                .thenReturn(List.of(new Note("Note 2", "This note belongs to Collection 3")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "Note 2", "", "Collection 3"))
                .thenReturn(List.of(new Note("Note 2", "This note belongs to Collection 3")));
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "This note belongs to Collection 3", "Collection 3"))
                .thenReturn(List.of(new Note("Note 2", "This note belongs to Collection 3")));
    }

    private void setUpEmptySearchWithinCollection1() {
        // Empty search within Collection 1
        when(repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
                "", "", "Collection 1"))
                .thenReturn(List.of(
                        new Note("Note 1", "This note belongs to Collection 1"),
                        new Note("Note 2", "This note belongs to Collection 1"),
                        new Note("Note 3", "This note belongs to Collection 1")
                ));
    }

    @Test
    void testNoMatchSearchReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/notes/search")
                        .param("noteTitle", "Non-existent Note")
                        .param("collectionTitle", "Collection 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
