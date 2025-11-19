package server.api;

import commons.NoteCollection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.NoteCollectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestCollectionControllerRepository implements NoteCollectionRepository {

    public final List<String> calledMethods = new ArrayList<>();
    public final List<NoteCollection> notesCollection = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<NoteCollection> findAll() {
        calledMethods.add("findAll");
        return notesCollection;
    }

    @Override
    public List<NoteCollection> findByName(String name) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends NoteCollection> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends NoteCollection> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<NoteCollection> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public NoteCollection getOne(Long aLong) {
        return null;
    }

    @Override
    public NoteCollection getById(Long aLong) {
        return notesCollection.stream().filter(n -> n.getId() == aLong).findFirst().get();
    }

    @Override
    public NoteCollection getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends NoteCollection> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends NoteCollection> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends NoteCollection> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends NoteCollection> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends NoteCollection> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends NoteCollection> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends NoteCollection, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends NoteCollection> S save(S entity) {
        calledMethods.add("save");
        notesCollection.add(entity);
        return entity;
    }

    @Override
    public <S extends NoteCollection> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<NoteCollection> findById(Long aLong) {
        return notesCollection.stream().filter(n -> n.getId() == aLong).findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return notesCollection.stream().anyMatch(e -> e.getId() == aLong);
    }

    @Override
    public List<NoteCollection> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        calledMethods.add("deleteById");
        notesCollection.removeIf(c -> c.getId() == aLong);
    }

    @Override
    public void delete(NoteCollection entity) {
        calledMethods.add("delete");
        notesCollection.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends NoteCollection> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<NoteCollection> findAll(Sort sort) {
        return notesCollection;
    }

    @Override
    public Page<NoteCollection> findAll(Pageable pageable) {
        return null;
    }
}
