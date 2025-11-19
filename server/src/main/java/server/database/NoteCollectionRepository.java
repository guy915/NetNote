package server.database;

import commons.NoteCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteCollectionRepository extends JpaRepository<NoteCollection, Long> {
    /**
     * Query method so you can search collections by name
     *
     * @param name to search for
     * @return List of collections with that name
     */
    List<NoteCollection> findByName(String name);
}
