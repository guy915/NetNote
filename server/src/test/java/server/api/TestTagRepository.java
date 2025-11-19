package server.api;

import commons.NoteTag;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.NoteTagRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestTagRepository implements NoteTagRepository {

    @Override
    public List<NoteTag> findByName(String name) {
        return List.of();
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends NoteTag> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends NoteTag> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<NoteTag> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }


    @Override
    public void deleteByName(String name) {
    }

    @Override
    public NoteTag getOne(Long aLong) {
        return null;
    }

    @Override
    public NoteTag getById(Long aLong) {
        return null;
    }

    @Override
    public NoteTag getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends NoteTag> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends NoteTag> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends NoteTag> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends NoteTag> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends NoteTag> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends NoteTag> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends NoteTag, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends NoteTag> S save(S entity) {
        return null;
    }

    @Override
    public <S extends NoteTag> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<NoteTag> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<NoteTag> findAll() {
        return List.of();
    }

    @Override
    public List<NoteTag> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(NoteTag entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends NoteTag> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<NoteTag> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<NoteTag> findAll(Pageable pageable) {
        return null;
    }
}
