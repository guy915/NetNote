package server.database;

import commons.NoteTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteTagRepository extends JpaRepository<NoteTag, Long> {
    /**
     * Query method so you can search tags by name
     *
     * @param name to search for
     * @return List of Tags with that title
     */
    List<NoteTag> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
