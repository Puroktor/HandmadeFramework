package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Answer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AnswerRepository implements CrudRepository<Answer, Integer> {
    @Inject
    private DataSource dataSource;

    @Override
    public Answer save(Answer entity) {
        String query = "INSERT INTO answer (is_right, text, question_id, id) VALUES (?,?,?, default)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    entity.getId() == null ? query : query.replace("default", "?"),
                    Statement.RETURN_GENERATED_KEYS);
            statement.setBoolean(1, entity.getIsRight());
            statement.setString(2, entity.getText());
            statement.setInt(3, entity.getQuestionId());
            if (entity.getId() != null) {
                statement.setInt(4, entity.getId());
            }
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    return new Answer(id, entity.getText(), entity.getIsRight(), entity.getQuestionId());
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't save answer to db", ex);
        }
    }

    @Override
    public Optional<Answer> findById(Integer integer) {
        String query = "SELECT * FROM answer WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            try (ResultSet rs = statement.executeQuery()) {
                Answer answer = null;
                if (rs.next()) {
                    answer = getAnswerFromResultSet(rs);
                }
                return Optional.ofNullable(answer);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get answer from db", ex);
        }
    }

    @Override
    public List<Answer> findAll() {
        String query = "SELECT * FROM answer";
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            List<Answer> answers = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    answers.add(getAnswerFromResultSet(rs));
                }
            }
            return answers;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get answers from db", ex);
        }
    }

    @Override
    public void delete(Answer entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        String query = "DELETE FROM answer WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            statement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't delete answer from db", ex);
        }
    }

    private Answer getAnswerFromResultSet(ResultSet rs) throws SQLException {
        return new Answer(rs.getInt("id"), rs.getString("text"),
                rs.getBoolean("is_right"), rs.getInt("question_id"));
    }

    public List<Answer> findAllByQuestionId(Integer integer) {
        String query = "SELECT * FROM answer WHERE question_id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            List<Answer> questions = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    questions.add(getAnswerFromResultSet(rs));
                }
            }
            return questions;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get questions from db", ex);
        }
    }
}
