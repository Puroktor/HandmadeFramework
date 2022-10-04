package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Role;
import ru.vsu.csf.skofenko.testapplication.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements CrudRepository<User, Integer> {

    @Inject
    private DataSource dataSource;

    @Override
    public User save(User entity) {
        String query = "INSERT INTO system_user (name, nickname, password, email, university, role," +
                "year, group_number, id) VALUES (?,?,?,?,?,?,?,?, default)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    entity.getId() == null ? query : query.replace("default", "?"),
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getNickname());
            statement.setString(3, entity.getPassword());
            statement.setString(4, entity.getEmail());
            statement.setString(5, entity.getUniversity());
            statement.setInt(6, entity.getRole().ordinal());
            statement.setObject(7, entity.getYear(), Types.INTEGER);
            statement.setObject(8, entity.getGroupNumber(), Types.INTEGER);
            if (entity.getId() != null) {
                statement.setInt(9, entity.getId());
            }
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    return new User(id, entity.getName(), entity.getNickname(), entity.getPassword(), entity.getRole(),
                            entity.getUniversity(), entity.getYear(), entity.getGroupNumber(), entity.getEmail());
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't save user to db", ex);
        }
    }

    @Override
    public Optional<User> findById(Integer integer) {
        String query = "SELECT * FROM system_user WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            try (ResultSet rs = statement.executeQuery()) {
                User user = null;
                if (rs.next()) {
                    user = getUserFromResultSet(rs);
                }
                return Optional.ofNullable(user);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get user from db", ex);
        }
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM system_user";
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            List<User> users = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    users.add(getUserFromResultSet(rs));
                }
            }
            return users;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get users from db", ex);
        }
    }

    @Override
    public User update(User entity) {
        String query = "UPDATE system_user SET name = ?, nickname =?, password=?, email=?, university=?, role = ?, year = ?, group_number=? WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getNickname());
            statement.setString(3, entity.getPassword());
            statement.setString(4, entity.getEmail());
            statement.setString(5, entity.getUniversity());
            statement.setInt(6, entity.getRole().ordinal());
            statement.setObject(7, entity.getYear(), Types.INTEGER);
            statement.setObject(8, entity.getGroupNumber(), Types.INTEGER);
            statement.setInt(9, entity.getId());
            statement.execute();
            return new User(entity.getId(), entity.getName(), entity.getNickname(), entity.getPassword(), entity.getRole(),
                    entity.getUniversity(), entity.getYear(), entity.getGroupNumber(), entity.getEmail());
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't update user in db", ex);
        }
    }

    @Override
    public void delete(User entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        String query = "DELETE FROM system_user WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            statement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't delete user from db", ex);
        }
    }

    public Optional<User> findByNickname(String nickname) {
        String query = "SELECT * FROM system_user WHERE nickname = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nickname);
            try (ResultSet rs = statement.executeQuery()) {
                User user = null;
                if (rs.next()) {
                    user = getUserFromResultSet(rs);
                }
                return Optional.ofNullable(user);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get user from db", ex);
        }
    }

    private User getUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("name"), rs.getString("nickname"),
                rs.getString("password"), Role.values()[rs.getInt("role")],
                rs.getString("university"), rs.getObject("year", Integer.class),
                rs.getObject("group_number", Integer.class), rs.getString("email"));
    }
}
