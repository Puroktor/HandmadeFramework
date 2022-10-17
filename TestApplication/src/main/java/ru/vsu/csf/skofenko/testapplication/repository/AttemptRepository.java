package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Attempt;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AttemptRepository implements CrudRepository<Attempt, Integer> {
    @Inject
    private DataSource dataSource;

    @Override
    public Attempt save(Attempt entity) {
        String query = "INSERT INTO attempt (user_id, test_id, date_time, score, id) VALUES (?,?,?,?, default)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    entity.getId() == null ? query : query.replace("default", "?"),
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getUserId());
            statement.setInt(2, entity.getTestId());
            statement.setTimestamp(3, entity.getDateTime());
            statement.setDouble(4, entity.getScore());
            if (entity.getId() != null) {
                statement.setInt(5, entity.getId());
            }
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    return new Attempt(id, entity.getUserId(), entity.getTestId(), entity.getScore(), entity.getDateTime());
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't save attempt to db", ex);
        }
    }

    @Override
    public Optional<Attempt> findById(Integer integer) {
        String query = "SELECT * FROM attempt WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            try (ResultSet rs = statement.executeQuery()) {
                Attempt attempt = null;
                if (rs.next()) {
                    attempt = getAttemptFromResultSet(rs);
                }
                return Optional.ofNullable(attempt);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get attempt from db", ex);
        }
    }

    @Override
    public List<Attempt> findAll() {
        String query = "SELECT * FROM attempt";
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            List<Attempt> attempts = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    attempts.add(getAttemptFromResultSet(rs));
                }
            }
            return attempts;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get attempts from db", ex);
        }
    }

    @Override
    public Attempt update(Attempt entity) {
        String query = "UPDATE attempt SET user_id=?, test_id=?, date_time=?, score=? WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, entity.getUserId());
            statement.setInt(2, entity.getTestId());
            statement.setTimestamp(3, entity.getDateTime());
            statement.setDouble(4, entity.getScore());
            statement.setInt(5, entity.getId());
            statement.execute();
            return new Attempt(entity.getId(), entity.getUserId(), entity.getTestId(), entity.getScore(), entity.getDateTime());
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't update attempt in db", ex);
        }
    }

    @Override
    public void delete(Attempt entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        String query = "DELETE FROM attempt WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            statement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't delete attempt from db", ex);
        }
    }

    public List<Attempt> findAllByUserIdOrderByDateTimeDesc(Integer userId) {
        String query = "SELECT * FROM attempt WHERE user_id = ? ORDER BY date_time DESC";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            List<Attempt> attempts = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    attempts.add(getAttemptFromResultSet(rs));
                }
            }
            return attempts;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get attempts from db", ex);
        }
    }

    public Optional<Attempt> findTopByUserAndTestOrderByDateTimeDesc(Integer userId, Integer testId) {
        String query = "SELECT * FROM attempt WHERE user_id = ? AND test_id = ? ORDER BY date_time LIMIT 1";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, testId);
            try (ResultSet rs = statement.executeQuery()) {
                Attempt attempt = null;
                if (rs.next()) {
                    attempt = getAttemptFromResultSet(rs);
                }
                return Optional.ofNullable(attempt);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get attempt from db", ex);
        }
    }

    private Attempt getAttemptFromResultSet(ResultSet rs) throws SQLException {
        return new Attempt(rs.getInt("id"), rs.getInt("user_id"),
                rs.getInt("test_id"), rs.getDouble("score"),
                rs.getTimestamp("date_time"));
    }
}
