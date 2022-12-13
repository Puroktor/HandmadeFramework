package ru.vsu.csf.skofenko.server.persistance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class StatementCreator {

    public static PreparedStatement createInsertStatement(Connection connection, Object entity) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        Class<?> entityClass = entity.getClass();
        sql.append(EntityMapper.getTableName(entityClass));
        sql.append(" (");

        Map<String, Object> fields = EntityMapper.getAllFields(entity);
        Map<String, Object> idFields = EntityMapper.getIdFields(entityClass, null);
        Collection<Object> properties = new ArrayList<>();
        for (String fieldName : fields.keySet()) {
            sql.append(fieldName);
            sql.append(',');
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") VALUES (");
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            if (idFields.containsKey(field.getKey()) && field.getValue() == null) {
                sql.append("default");
            } else {
                sql.append("?");
                properties.add(field.getValue());
            }
            sql.append(',');
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        return createStatement(connection, sql.toString(), properties);
    }

    public static PreparedStatement createSelectStatement(Connection connection, Class<?> entityClass, Map<String, Object> properties) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append(EntityMapper.getTableName(entityClass));
        setWhereClause(properties.keySet(), sql);
        return createStatement(connection, sql.toString(), properties.values());
    }

    public static PreparedStatement createUpdateStatement(Connection connection, Object entity) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ");
        Class<?> entityClass = entity.getClass();
        sql.append(EntityMapper.getTableName(entityClass));
        Map<String, Object> properties = EntityMapper.getAllFields(entity);
        sql.append(" SET ");
        for (String property : properties.keySet()) {
            sql.append(property);
            sql.append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        Map<String, Object> idFields = EntityMapper.getIdFields(entityClass, entity);
        setWhereClause(idFields.keySet(), sql);
        Collection<Object> values = new ArrayList<>(properties.values());
        values.addAll(idFields.values());
        return createStatement(connection, sql.toString(), values);
    }

    public static PreparedStatement createDeleteStatement(Connection connection, Object entity) throws SQLException {
        Class<?> entityClass = entity.getClass();
        return createDeleteStatement(connection, entityClass, EntityMapper.getIdFields(entityClass, entity));
    }

    public static PreparedStatement createDeleteStatement(Connection connection, Class<?> entityClass, Map<String, Object> properties) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(EntityMapper.getTableName(entityClass));
        setWhereClause(properties.keySet(), sql);
        return createStatement(connection, sql.toString(), properties.values());
    }

    private static void setWhereClause(Collection<String> properties, StringBuilder sql) {
        if (properties.isEmpty()) {
            return;
        }
        sql.append(" WHERE ");
        for (String property : properties) {
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
}
