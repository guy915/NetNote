package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String content;

    @ManyToMany
    private Set<NoteTag> noteTag;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private NoteCollection noteCollection;

    @ManyToMany
    @JsonIgnore
    private List<Note> references;

    public Note() {
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.noteTag = new HashSet<>();
        this.references = new ArrayList<>();
        this.noteCollection = null;
    }

    public Note(String title, String content, NoteCollection noteCollection) {
        this.title = title;
        this.content = content;
        this.noteTag = new HashSet<>();
        this.references = new ArrayList<>();
        this.noteCollection = noteCollection;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<NoteTag> getNoteTag() {
        return noteTag;
    }

    public void setTags(Set<NoteTag> noteTag) {
        this.noteTag = noteTag;
    }

    public boolean hasTag(NoteTag tag) {
        return noteTag.contains(tag);
    }

    public List<Note> getReferences() {
        return references;
    }

    public void setReferences(List<Note> references) {
        this.references = references;
    }

    public boolean isReferencing(Note note) {
        return references.contains(note);
    }


    public NoteCollection getNoteCollection() {
        return noteCollection;
    }

    public void setNoteCollection(NoteCollection noteCollection) {
        this.noteCollection = noteCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return equalsForNoteObjects(note);
    }

    private boolean equalsForNoteObjects(Note note) {
        if (id == note.id && Objects.equals(title, note.title) && Objects.equals(content, note.content)) {
            return Objects.equals(noteTag, note.noteTag) && Objects.equals(noteCollection, note.noteCollection);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, noteTag, noteCollection);
    }

    @Override
    public String toString() {
        return title;
    }

}
