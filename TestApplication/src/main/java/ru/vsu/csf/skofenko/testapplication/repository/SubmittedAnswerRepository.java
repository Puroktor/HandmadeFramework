package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.skofenko.testapplication.entity.SubmittedAnswer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SubmittedAnswerRepository {
    @Inject
    private DataSource dataSource;

    public SubmittedAnswer save(SubmittedAnswer entity) {
        String query = "INSERT INTO submitted_answer (answer_id, attempt_id, submitted_value) VALUES (?,?,?)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, entity.getAnswerId());
            statement.setInt(2, entity.getAttemptId());
            statement.setBoolean(3, entity.isSubmittedValue());
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return new SubmittedAnswer(entity.getAnswerId(), entity.getAttemptId(), entity.isSubmittedValue());
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't save submitted answer to db", ex);
        }
    }

    public List<SubmittedAnswer> getAllByAttemptId(Integer attemptId) {
        String query = "SELECT * FROM submitted_answer WHERE attempt_id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, attemptId);
            List<SubmittedAnswer> answers = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    answers.add(getSubmittedAnswerFromResultSet(rs));
                }
            }
            return answers;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't get submitted answers from db", ex);
        }
    }

    private SubmittedAnswer getSubmittedAnswerFromResultSet(ResultSet rs) throws SQLException {
        return new SubmittedAnswer(rs.getInt("answer_id"), rs.getInt("attempt_id"),
                rs.getBoolean("submitted_value"));
    }
}
