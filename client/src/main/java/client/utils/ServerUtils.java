package client.utils;

import commons.Note;
import commons.NoteCollection;
import commons.NoteTag;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.net.ConnectException;
import java.util.List;
import java.util.Optional;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    public ServerUtils() { // empty constructor
    }

    /**
     * Creates a GET HTTP request to retrieve all notes
     *
     * @return List of all notes in the database
     */
    public List<Note> getNotes() {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notes")
                    .request(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        }
    }

    /**
     * Creates a PUT HTTP request to update a Note's content
     *
     * @param newContent content to set to the given note
     * @param id         id of the updated note
     */
    public void updateNoteContent(String newContent, long id) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER).path("api/notes/id/{noteId}/content")
                    .resolveTemplate("noteId", id)
                    .request(APPLICATION_JSON)
                    .put(Entity.text("x" + newContent), Note.class);
        }
    }

    /**
     * Creates a DELETE HTTP request to delete a note from the database
     *
     * @param note the note to delete
     */
    public void deleteNote(Note note) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER).path("api/notes/id/" + note.getId())
                    .request(APPLICATION_JSON)
                    .delete(Note.class);
        }
    }

    /**
     * Creates a PUT HTTP request to update a Note's title
     *
     * @param newTitle title to set for the note
     * @param id       of the note
     */
    public void updateNoteTitle(String newTitle, long id) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER).path("api/notes/id/{noteId}/title")
                    .resolveTemplate("noteId", id)
                    .request(APPLICATION_JSON)
                    .put(Entity.text(newTitle), Note.class);
        }
    }

    /**
     * Creates a GET HTTP request to retrieve all note collections
     *
     * @return List of all collections in the database
     */
    public List<NoteCollection> getCollections() {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notecollections")
                    .request(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        }
    }

    /**
     * Creates a POST HTTP request to add a new Collection to the database
     *
     * @param collection the collection to add
     * @return added collection
     */
    public NoteCollection addCollection(NoteCollection collection) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notecollections")
                    .request(APPLICATION_JSON)
                    .post(Entity.entity(collection, APPLICATION_JSON), NoteCollection.class);
        }
    }

    /**
     * Creates a PUT HTTP request to add a note to a collection
     *
     * @param note to add
     * @param id   of the collection to add it to
     * @return the updated collection
     */
    public NoteCollection addNoteToCollection(Note note, long id) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notecollections/id/{collectionId}/notes")
                    .resolveTemplate("collectionId", id)
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(note, APPLICATION_JSON), NoteCollection.class);
        }
    }

    public boolean isServerAvailable() {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER)
                    .request(APPLICATION_JSON)
                    .get();
            return true;
        } catch (ProcessingException e) {
            return !(e.getCause() instanceof ConnectException);
        }
    }

    /**
     * Creates a GET HTTP request to retrieve a note with given name
     *
     * @param name name of the collection
     * @return Optional of NoteCollection with note with given name
     */
    public Optional<NoteCollection> getCollectionByName(String name) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            List<NoteCollection> nc = client.target(SERVER).path("api/notecollections/name/" + name)
                    .request(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
            if (nc.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(nc.getFirst());
        }
    }

    public Note getNoteById(long id) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notes/id/" + id)
                    .request(APPLICATION_JSON)
                    .get(Note.class);
        } catch (ProcessingException e) {
            return null;
        }
    }

    /**
     * Receives input from user via search bar
     * and compares it with notes from a collection.
     * The input of the sure is case-insensitive.
     *
     * @param userInput       text from the search bar, entered by the user
     * @param collectionTitle title of the collection to which the note belongs
     * @return a list of notes, matching the user's search input
     */
    public List<Note> searchNotes(String userInput, String collectionTitle) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notes/search")
                    .queryParam("query", userInput)
                    .queryParam("collectionTitle", collectionTitle)
                    .request(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        } catch (ProcessingException e) {
            throw new ProcessingException("Failed to search notes on the server. " + e.getMessage());
        }
    }

    /**
     * Sends a POST request with the markdown String, such that the server returns a html rendered String.
     *
     * @param markdown the original note content in markdown
     * @param id       note's id
     * @return String rendered html content
     */
    public String getRenderedHTML(String markdown, long id) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notes/renderMarkdown/" + id)
                    .request(APPLICATION_JSON)
                    .post(Entity.entity(markdown, APPLICATION_JSON), String.class);
        } catch (WebApplicationException e) {
            return "<p>Error while rendering note. WebApplicationException was thrown.</p>";
        }
    }

    /**
     * Creates a GET HTTP request to retrieve all tags.
     *
     * @return List of NoteTags
     */
    public List<NoteTag> getAllTags() {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER)
                    .path("api/tags")
                    .request(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        } catch (ProcessingException e) {
            throw new ProcessingException("Failed to retrieve all tags from the server. " + e.getMessage());
        }
    }

    /**
     * Creates a PUT HTTP request to reset tags for a note
     *
     * @param note the note to reset
     */
    public void resetTags(Note note) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER).path("api/notes/tags/clear")
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(note, APPLICATION_JSON), Note.class);
        } catch (ProcessingException e) {
            throw new ProcessingException("Failed to reset tags of note: " + note.getId() + " from the server. " + e.getMessage());
        }
    }

    /**
     * Creates a PUT HTTP request to change all notes' content, that
     * reference the given note
     *
     * @param note  the given note
     * @param title the new title of the note
     * @return the note
     */
    public Note updateReferences(Note note, String title) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notes/references/title/" + title)
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(note, APPLICATION_JSON), Note.class);
        } catch (ProcessingException e) {
            throw new ProcessingException("Failed to update note references, referencing note: " + note.getId() + ", from the title: " + title + ". " + e.getMessage());
        }
    }

    /**
     * Deletes a collection from the database.
     *
     * @param id ID of the collection to delete
     */
    public void deleteCollection(long id) {
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER)
                    .path("api/notecollections/id/" + id)
                    .request(APPLICATION_JSON)
                    .delete(NoteCollection.class);
        }
    }

    /**
     * Updates the collection name to the new collection name.
     *
     * @param collection The selected collection that needs their name changed
     */
    public void updateCollection(NoteCollection collection) {
        try(Client client = ClientBuilder.newClient(new ClientConfig())) {
            client.target(SERVER).path("api/notecollections/" + collection.getId())
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(collection, APPLICATION_JSON));
        }
    }

    public Note getNoteByTitle(String title){
        try (Client client = ClientBuilder.newClient(new ClientConfig())) {
            return client.target(SERVER).path("api/notes/title/" + title)
                    .request(APPLICATION_JSON)
                    .get(Note.class);
        } catch (ProcessingException e) {
            return null;
        }
    }

}