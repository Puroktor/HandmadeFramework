package ru.vsu.csf.framework.persistence;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID>{
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    T update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
