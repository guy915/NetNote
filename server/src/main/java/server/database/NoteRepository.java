package server.database;

import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    /**
     * Query method so you can search collections by title
     *
     * @param title to search for
     * @return List of collections with that title
     */
    List<Note> findByTitle(String title);

    /**
     * @param searchForTitle   to search for the title given by the user
     * @param searchForContent to search for the content of the note given by the user
     * @param collectionName   to search for the name of the collection
     * @return List of collection with that title or content
     */
    List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(
            String searchForTitle, String searchForContent, String collectionName);


    boolean existsByTitle(String title);

    @Query("SELECT n FROM Note n WHERE " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(n.content) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND n.noteCollection.name = ?2")
    List<Note> findByQueryAndCollection(String query, String collection);

    List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}