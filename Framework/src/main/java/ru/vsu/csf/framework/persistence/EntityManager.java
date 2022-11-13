package ru.vsu.csf.framework.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EntityManager {
    void save(Object entity);

    <T> Optional<T> find(Class<T> entityClass, Object primaryKey);

    <T> List<T> findAll(Class<T> entityClass);

    <T> List<T> findAllByProperties(Class<T> entityClass, Map<String, Object> properties);

    void remove(Object entity);

    void remove(Class<?> entityClass, Object primaryKey);

    <T> List<T> executeNativeQuery(String sqlString, Class<T> returnClass, List<Object> properties);
}
