package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Question;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository implements CrudRepository<Question, Integer> {
    @Inject
    private DataSource dataSource;

    @Override
    public Question save(Question entity) {
        String query = "INSERT INTO question (test_id, text, max_score, question_template_index, id)" +
                " VALUES (?,?,?,?, default)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    entity.getId() == null ? query : query.replace("default", "?"),
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getTestId());
            statement.setString(2, entity.getText());
            statement.setInt(3, entity.getMaxScore());
            statement.setInt(4, entity.getQuestionTemplateIndex());
            if (entity.getId() != null) {
                statement.setInt(5, entity.getId());
            }
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    return new Question(id, entity.getTestId(), entity.getText(), entity.getMaxScore(),
                            entity.getQuestionTemplateIndex());
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't save question to db", ex);
        }
    }

    @Override
    public Optional<Question> findById(Integer integer) {
        String query = "SELECT * FROM question WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            try (ResultSet rs = statement.executeQuery()) {
                Question question = null;
                if (rs.next()) {
                    question = getQuestionFromResultSet(rs);
                }
                return Optional.ofNullable(question);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get question from db", ex);
        }
    }

    @Override
    public List<Question> findAll() {
        String query = "SELECT * FROM question";
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            List<Question> questions = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    questions.add(getQuestionFromResultSet(rs));
                }
            }
            return questions;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get questions from db", ex);
        }
    }

    @Override
    public Question update(Question entity) {
        String query = "UPDATE question SET test_id=?, text=?, max_score=?, question_template_index=? WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, entity.getTestId());
            statement.setString(2, entity.getText());
            statement.setInt(3, entity.getMaxScore());
            statement.setInt(4, entity.getQuestionTemplateIndex());
            statement.setInt(5, entity.getId());
            statement.execute();
            return new Question(entity.getId(), entity.getTestId(), entity.getText(), entity.getMaxScore(),
                    entity.getQuestionTemplateIndex());
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't update question in db", ex);
        }
    }

    @Override
    public void delete(Question entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        String query = "DELETE FROM question WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            statement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't delete question from db", ex);
        }
    }

    private Question getQuestionFromResultSet(ResultSet rs) throws SQLException {
        return new Question(rs.getInt("id"), rs.getInt("test_id"),
                rs.getString("text"), rs.getInt("max_score"),
                rs.getInt("question_template_index"));
    }

    public List<Question> findAllByTestId(Integer integer) {
        String query = "SELECT * FROM question WHERE test_id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, integer);
            List<Question> questions = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    questions.add(getQuestionFromResultSet(rs));
                }
            }
            return questions;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get questions from db", ex);
        }
    }
}
