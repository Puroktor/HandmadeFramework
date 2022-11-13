package ru.vsu.csf.skofenko.server.persistance;

import ru.vsu.csf.framework.persistence.Entity;
import ru.vsu.csf.framework.persistence.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class StatementCreator {

    public static PreparedStatement createInsertStatement(Connection connection, Object entity) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        Class<?> entityClass = entity.getClass();
        sql.append(getTableName(entityClass));
        sql.append(" (");

        StringBuilder paramNames = new StringBuilder();
        StringBuilder paramValues = new StringBuilder();
        List<Object> properties = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            String underscoreName = CaseFormatter.camelCaseToUnderscores(field.getName());
            paramNames.append(underscoreName);
            paramNames.append(",");
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (field.getAnnotation(Id.class) != null && value == null) {
                    paramValues.append("default");
                } else {
                    paramValues.append("?");
                    properties.add(value);

                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't read field value", e);
            }
            paramValues.append(",");
        }

        paramNames.deleteCharAt(paramNames.length() - 1);
        paramValues.deleteCharAt(paramValues.length() - 1);

        sql.append(paramNames);
        sql.append(") VALUES (");
        sql.append(paramValues);
        sql.append(")");

        return createStatement(connection, sql.toString(), properties);
    }

    public static PreparedStatement createSelectStatement(Connection connection, Class<?> entityClass, Map<String, Object> properties) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        setWhereClause(entityClass, properties, sql);
        return createStatement(connection, sql.toString(), properties.values());
    }

    public static PreparedStatement createDeleteStatement(Connection connection, Object entity) throws SQLException {
        Class<?> entityClass = entity.getClass();
        Map<String, Object> properties = new HashMap<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null) {
                try {
                    field.setAccessible(true);
                    properties.put(field.getName(), field.get(entity));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't read field value", e);
                }
            }
        }
        return createDeleteStatement(connection, entityClass, properties);
    }

    public static PreparedStatement createDeleteStatement(Connection connection, Class<?> entityClass, Map<String, Object> properties) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        setWhereClause(entityClass, properties, sql);
        return createStatement(connection, sql.toString(), properties.values());
    }

    private static void setWhereClause(Class<?> entityClass, Map<String, Object> properties, StringBuilder sql) {
        sql.append(getTableName(entityClass));
        if (properties.isEmpty()) {
            return;
        }
        sql.append(" WHERE ");
        for (String property : properties.keySet()) {
            sql.append(CaseFormatter.camelCaseToUnderscores(property));
            sql.append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);
    }

    public static PreparedStatement createStatement(Connection connection, String sql, Collection<Object> properties) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int index = 1;
        for (Object value : properties) {
            if (value != null && value.getClass().isEnum()) {
                try {
                    Method method = value.getClass().getMethod("ordinal");
                    Integer ordinal = (Integer) method.invoke(value);
                    statement.setObject(index++, ordinal);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException("Can't get ordinal for enum");
                }
            } else {
                statement.setObject(index++, value);
            }
        }
        return statement;
    }

    private static String getTableName(Class<?> entityClass) {
        String annotationValue = entityClass.getAnnotation(Entity.class).value();
        if (annotationValue.equals("")) {
            return CaseFormatter.camelCaseToUnderscores(entityClass.getSimpleName());
        } else {
            return annotationValue;
        }
    }
}
