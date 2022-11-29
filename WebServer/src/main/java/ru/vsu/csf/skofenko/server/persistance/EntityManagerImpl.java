package ru.vsu.csf.skofenko.server.persistance;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.vsu.csf.framework.persistence.BaseDataSource;
import ru.vsu.csf.framework.persistence.EntityManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityManagerImpl implements EntityManager {

    private final DataSource dataSource;

    public EntityManagerImpl(BaseDataSource dataSource) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(dataSource.getUrl());
        ds.setUsername(dataSource.getUser());
        ds.setPassword(dataSource.getPassword());
        this.dataSource = ds;
    }

    @Override
    public void save(Object entity) {
        Map<String, Object> idFields = EntityMapper.getIdFields(entity.getClass(), null);
        if (idFields.isEmpty()) {
            throw new IllegalArgumentException("Object is not a valid entity!");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = StatementCreator.createInsertStatement(connection, entity);
            statement.execute();
            EntityMapper.parseEntity(statement, entity);
        } catch (SQLException e) {
            throw new IllegalStateException("Can't save entity to db", e);
        }
    }

    @Override
    public <T> Optional<T> find(Class<T> entityClass, Object primaryKey) {
        Map<String, Object> idFields = EntityMapper.getIdFields(entityClass, null);
        if (idFields.size() != 1) {
            throw new IllegalArgumentException("Object is not a valid entity!");
        }
        List<T> entities = findAllByProperties(entityClass, Map.of(
                idFields.keySet().iterator().next(),
                primaryKey
        ));
        return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0));
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return findAllByProperties(entityClass, Map.of());
    }

    @Override
    public <T> List<T> findAllByProperties(Class<T> entityClass, Map<String, Object> properties) {
        Map<String, Object> idFields = EntityMapper.getIdFields(entityClass, null);
        if (idFields.isEmpty()) {
            throw new IllegalArgumentException("Object is not a valid entity!");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = StatementCreator.createSelectStatement(connection, entityClass, properties);
            statement.execute();
            return EntityMapper.createEntitiesFromResult(statement, entityClass);
        } catch (SQLException e) {
            throw new IllegalStateException("Can't find entity in db", e);
        }
    }

    @Override
    public void update(Object entity) {
        Map<String, Object> idFields = EntityMapper.getIdFields(entity.getClass(), null);
        if (idFields.isEmpty()) {
            throw new IllegalArgumentException("Object is not a valid entity!");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = StatementCreator.createUpdateStatement(connection, entity);
            statement.execute();
            EntityMapper.parseEntity(statement, entity);
        } catch (SQLException e) {
            throw new IllegalStateException("Can't update entity in db", e);
        }
    }

    @Override
    public void remove(Object entity) {
        Map<String, Object> idFields = EntityMapper.getIdFields(entity.getClass(), null);
        if (idFields.isEmpty()) {
            throw new IllegalArgumentException("Object is not a valid entity!");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = StatementCreator.createDeleteStatement(connection, entity);
            statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't delete entity in db", e);
        }
    }

    @Override
    public void remove(Class<?> entityClass, Object primaryKey) {
        Map<String, Object> idFields = EntityMapper.getIdFields(entityClass, null);
        if (idFields.size() != 1) {
            throw new IllegalArgumentException("Object is not a valid entity!");
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = StatementCreator.createDeleteStatement(connection, entityClass, Map.of(
                    idFields.keySet().iterator().next(),
                    primaryKey
            ));
            statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't delete entity in db", e);
        }
    }

    @Override
    public <T> List<T> executeNativeQuery(String sqlString, Class<T> returnClass, List<Object> properties) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = StatementCreator.createStatement(connection, sqlString, properties);
            statement.execute();
            return EntityMapper.createEntitiesFromResult(statement, returnClass);
        } catch (SQLException e) {
            throw new IllegalStateException("Can't execute native query", e);
        }
    }
}
