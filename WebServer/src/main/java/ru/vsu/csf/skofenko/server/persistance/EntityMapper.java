package ru.vsu.csf.skofenko.server.persistance;

import ru.vsu.csf.framework.persistence.Entity;
import ru.vsu.csf.framework.persistence.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntityMapper {

    public static void setIdFromResult(Statement statement, Object entity) throws SQLException {
        try (ResultSet rs = statement.getGeneratedKeys()) {
            if (rs.next()) {
                parseEntity(rs, entity);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Can't set id value", e);
        }
    }

    public static <T> List<T> createEntitiesFromResult(PreparedStatement statement, Class<T> entityClass) throws SQLException {
        try (ResultSet resultSet = statement.getResultSet()) {
            List<T> entityList = new ArrayList<>();
            while (resultSet.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                parseEntity(resultSet, entity);
                entityList.add(entity);
            }
            return entityList;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException("Can't create entity instance", e);
        }
    }

    private static <T> void parseEntity(ResultSet resultSet, T entity) throws SQLException, NoSuchFieldException, IllegalAccessException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String fieldName = CaseFormatter.underscoresToCamelCase(metaData.getColumnName(i));
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = resultSet.getObject(i);
            if (field.getType().isEnum()) {
                field.set(entity, field.getType().getEnumConstants()[(int) value]);
            } else {
                field.set(entity, value);
            }
        }
    }

    public static List<String> getIdFieldNames(Class<?> entityClass) {
        List<String> idFieldNames = new ArrayList<>();
        boolean isEntityAnnotationAbsent = entityClass.getAnnotation(Entity.class) == null;
        if (isEntityAnnotationAbsent) {
            return idFieldNames;
        }

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null) {
                idFieldNames.add(field.getName());
            }
        }
        return idFieldNames;
    }
}
