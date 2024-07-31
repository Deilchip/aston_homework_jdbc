package org.example.dao.impl;


import org.example.dao.CoordinatorDAO;
import org.example.model.Coordinator;
import org.example.model.Student;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса CoordinatorDAO, которая использует CrudRepositoryImpl для выполнения основных CRUD-операций.
 * Этот класс предоставляет методы для работы с координаторами в базе данных.
 */
public class CoordinatorDAOImpl extends CrudRepositoryImpl<Coordinator> implements CoordinatorDAO {
    /**
     * Конструктор, принимающий DataSource для подключения к базе данных.
     *
     * @param dataSource источник данных для подключения к базе данных
     */
    public CoordinatorDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Конструктор по умолчанию.
     */
    public CoordinatorDAOImpl() {
        super();
    }

    /**
     * Находит всех студентов, связанных с указанным координатором.
     *
     * @param coordinatorId идентификатор координатора
     * @return список студентов, связанных с указанным координатором
     */
    @Override
    public List<Student> findStudentsByCoordinatorId(Long coordinatorId) {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT s.student_id, s.name, s.coordinator_id FROM student s WHERE s.coordinator_id = ?")) {
            statement.setLong(1, coordinatorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Student student = new Student();
                    student.setId(resultSet.getLong("student_id"));
                    student.setName(resultSet.getString("name"));
                    student.setCoordinator(new Coordinator());
                    student.getCoordinator().setId(resultSet.getLong("coordinator_id"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Находит координатора по его идентификатору.
     *
     * @param id идентификатор координатора
     * @return координатор, если найден, иначе null
     */
    @Override
    public Optional<Coordinator> findById(Long id) {
        Coordinator coordinator = null;
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM coordinator WHERE coordinator_id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    coordinator = new Coordinator();
                    coordinator.setId(resultSet.getLong("coordinator_id"));
                    coordinator.setName(resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(coordinator);
    }

    /**
     * Находит всех координаторов.
     *
     * @return список всех координаторов
     */
    @Override
    public List<Coordinator> findAll() {
        List<Coordinator> coordinators = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM coordinator")) {
            while (resultSet.next()) {
                Coordinator coordinator = new Coordinator();
                coordinator.setId(resultSet.getLong("coordinator_id"));
                coordinator.setName(resultSet.getString("name"));
                coordinators.add(coordinator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coordinators;
    }

    /**
     * Сохраняет нового координатора в базе данных.
     *
     * @param coordinator координатор для сохранения
     * @return сохраненный координатор
     */
    @Override
    public Coordinator save(Coordinator coordinator) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO coordinator (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, coordinator.getName());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating coordinator failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coordinator.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating coordinator failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coordinator;
    }

    /**
     * Обновляет существующего координатора в базе данных.
     *
     * @param coordinator координатор для обновления
     * @return обновленный координатор, если успешно, иначе null
     */
    @Override
    public Coordinator update(Coordinator coordinator) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE coordinator SET name = ? WHERE coordinator_id = ?")) {
            statement.setString(1, coordinator.getName());
            statement.setLong(2, coordinator.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return coordinator;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Удаляет координатора из базы данных по его идентификатору.
     *
     * @param id идентификатор координатора для удаления
     * @return true, если удаление успешно, иначе false
     */
    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM coordinator WHERE coordinator_id = ?")) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
