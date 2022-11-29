package ru.vsu.csf.skofenko.server.persistance;

import ru.vsu.csf.framework.persistence.Column;
import ru.vsu.csf.framework.persistence.Entity;
import ru.vsu.csf.framework.persistence.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class EntityMapper {

    public static Map<String, Object> getIdFields(Class<?> entityClass, Object entity) {
        return getFieldsByPredicate(entityClass, entity, field -> field.getAnnotation(Id.class) != null);
    }

    public static Map<String, Object> getAllFields(Object entity) {
        return getFieldsByPredicate(entity.getClass(), entity, field -> true);
    }

    private static Map<String, Object> getFieldsByPredicate(Class<?> entityClass, Object entity, Predicate<Field> fieldPredicate) {
        Map<String, Object> properties = new HashMap<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (fieldPredicate.test(field)) {
                try {
                    field.setAccessible(true);
                    String fieldName = getColumnName(field);
                    properties.put(fieldName, entity != null ? field.get(entity) : null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't read field value", e);
                }
            }
        }
        return properties;
    }

    public static String getTableName(Class<?> entityClass) {
        String annotationValue = entityClass.getAnnotation(Entity.class).value();
        if (annotationValue.equals("")) {
            return CaseFormatter.camelCaseToUnderscores(entityClass.getSimpleName());
        } else {
            return annotationValue;
        }
    }

    public static String getColumnName(Field field) {
        String fieldName = CaseFormatter.camelCaseToUnderscores(field.getName());
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null) {
            fieldName = columnAnnotation.value();
        }
        return fieldName;
    }

    public static void parseEntity(Statement statement, Object entity) throws SQLException {
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
}
