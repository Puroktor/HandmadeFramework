package ru.vsu.csf.skofenko.testapplication.repository;

import org.postgresql.ds.PGConnectionPoolDataSource;
import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Test;
import ru.vsu.csf.skofenko.testapplication.entity.TestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TestRepository implements CrudRepository<Test, Integer> {
    @Inject
    private PGConnectionPoolDataSource dataSource;

    @Override
    public Test save(Test entity) {
        String query = "INSERT INTO test (name, programming_lang, questions_count, test_type, passing_score, id)" +
                " VALUES (?,?,?,?,?,? default)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    entity.getId() == null ? query : query.replace("default", "?"),
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getProgrammingLang());
            statement.setInt(3, entity.getQuestionsCount());
            statement.setInt(4, entity.getTestType().ordinal());
            statement.setInt(5, entity.getPassingScore());
            if (entity.getId() != null) {
                statement.setInt(6, entity.getId());
            }
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    return new Test(id, entity.getProgrammingLang(), entity.getName(), entity.getQuestionsCount(),
                            entity.getPassingScore(), entity.getTestType());
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't save test to db", ex);
        }
    }

    @Override
    public Optional<Test> findById(Integer integer) {
        String query = "SELECT * FROM test WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            try (ResultSet rs = statement.executeQuery()) {
                Test test = null;
                if (rs.next()) {
                    test = getTestFromResultSet(rs);
                }
                return Optional.ofNullable(test);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get test from db", ex);
        }
    }

    @Override
    public List<Test> findAll() {
        String query = "SELECT * FROM test";
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            List<Test> tests = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    tests.add(getTestFromResultSet(rs));
                }
            }
            return tests;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get tests from db", ex);
        }
    }

    @Override
    public void delete(Test entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        String query = "DELETE FROM test WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            statement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't delete test from db", ex);
        }
    }

    private Test getTestFromResultSet(ResultSet rs) throws SQLException {
        return new Test(rs.getInt("id"), rs.getString("programming_lang"),
                rs.getString("name"), rs.getInt("questions_count"),
                rs.getInt("passing_score"), TestType.values()[rs.getInt("test_type")]);
    }
}
