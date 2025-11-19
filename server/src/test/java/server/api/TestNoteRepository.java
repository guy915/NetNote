package server.api;

import commons.Note;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestNoteRepository implements NoteRepository {

    public final List<String> calledMethods = new ArrayList<>();
    public final List<Note> notes = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Note> findAll() {
        calledMethods.add("findAll");
        return notes;
    }

    @Override
    public Note getById(Long id) {
        call("getById");
        return find(id).get();
    }

    private Optional<Note> find(Long id) {
        return notes.stream().filter(q -> q.getId() == id).findFirst();
    }


    @Override
    public List<Note> findByTitle(String title) {
        return notes.stream()
                .filter(n -> n.getTitle().equals(title))
                .toList();
    }

    @Override
    public List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndNoteCollectionName(String searchForTitle, String searchForContent, String collectionName) {
        return List.of();
    }

    @Override
    public boolean existsByTitle(String title) {
        call("getByTitle");
        return !findByTitle(title).isEmpty();
    }

    @Override
    public List<Note> findByQueryAndCollection(String query, String collection) {
        return List.of();
    }

    @Override
    public List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Note> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Note> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Note> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Note getOne(Long aLong) {
        return null;
    }

    @Override
    public Note getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Note> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Note> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Note> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Note> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Note> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Note> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Note, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Note> S save(S entity) {
        calledMethods.add("save");
        notes.add(entity);
        return entity;
    }

    @Override
    public <S extends Note> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Note> findById(Long aLong) {
        if (notes.stream().anyMatch(n -> n.getId() == aLong)) {
            return notes.stream().filter(n -> n.getId() == aLong).findFirst();
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        calledMethods.add("existsById");
        return notes.stream().anyMatch(q -> q.getId() == aLong);
    }


    @Override
    public List<Note> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        calledMethods.add("deleteById");
        notes.removeIf(n -> n.getId() == aLong);
    }

    @Override
    public void delete(Note entity) {
        notes.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Note> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Note> findAll(Sort sort) {
        return notes;
    }

    @Override
    public Page<Note> findAll(Pageable pageable) {
        return null;
    }
}