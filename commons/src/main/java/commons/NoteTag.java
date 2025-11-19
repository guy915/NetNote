package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class NoteTag {

    @Id
    private String name;

    public NoteTag(String name) {
        this.name = name;
    }

    public NoteTag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteTag noteTag = (NoteTag) o;
        return Objects.equals(name, noteTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
