package org.example.dao.impl;

import org.example.dao.StudentDAO;
import org.example.model.Coordinator;
import org.example.model.Student;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса StudentDAO, которая использует CrudRepositoryImpl для выполнения основных CRUD-операций.
 * Этот класс предоставляет методы для работы со студентами в базе данных.
 */
public class StudentDAOImpl extends CrudRepositoryImpl<Student> implements StudentDAO {
    /**
     * Конструктор по умолчанию, который создает DataSource из файла конфигурации.
     */
    public StudentDAOImpl() {
        super();
    }

    /**
     * Конструктор, принимающий DataSource для подключения к базе данных.
     *
     * @param dataSource источник данных для подключения к базе данных
     */
    public StudentDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Добавляет курс к студенту.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return true, если операция успешна, иначе false
     */
    @Override
    public boolean addCourseToStudent(Long studentId, Long courseId) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO course_student (course_id, student_id) VALUES (?, ?)")) {
            statement.setLong(1, courseId);
            statement.setLong(2, studentId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет курс из списка курсов студента.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return true, если операция успешна, иначе false
     */
    @Override
    public boolean removeCourseFromStudent(Long studentId, Long courseId) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM course_student WHERE course_id = ? AND student_id = ?")) {
            statement.setLong(1, courseId);
            statement.setLong(2, studentId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Находит студента по его идентификатору.
     *
     * @param id идентификатор студента
     * @return студент, если найден, иначе null
     */
    @Override
    public Optional<Student> findById(Long id) {
        Student student = null;
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT s.student_id, s.name, c.coordinator_id, c.name AS coordinator_name " +
                     "FROM Student s " +
                     "JOIN Coordinator c ON s.coordinator_id = c.coordinator_id " +
                     "WHERE s.student_id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    student = new Student();
                    student.setId(resultSet.getLong("student_id"));
                    student.setName(resultSet.getString("name"));

                    Coordinator coordinator = new Coordinator();
                    coordinator.setId(resultSet.getLong("coordinator_id"));
                    coordinator.setName(resultSet.getString("coordinator_name"));
                    student.setCoordinator(coordinator);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(student);
    }

    /**
     * Находит всех студентов.
     *
     * @return список всех студентов
     */
    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT s.student_id, s.name, c.coordinator_id, c.name AS coordinator_name " +
                     "FROM Student s " +
                     "JOIN Coordinator c ON s.coordinator_id = c.coordinator_id")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Student student = new Student();
                    student.setId(resultSet.getLong("student_id"));
                    student.setName(resultSet.getString("name"));

                    Coordinator coordinator = new Coordinator();
                    coordinator.setId(resultSet.getLong("coordinator_id"));
                    coordinator.setName(resultSet.getString("coordinator_name"));
                    student.setCoordinator(coordinator);

                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Сохраняет нового студента в базе данных.
     *
     * @param entity студент, которого нужно сохранить
     * @return сохраненный студент
     */
    @Override
    public Student save(Student entity) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO student (name, coordinator_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getCoordinator().getId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * Обновляет существующего студента в базе данных.
     *
     * @param entity студент, которого нужно обновить
     * @return обновленный студент
     */
    @Override
    public Student update(Student entity) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE student SET name = ?, coordinator_id = ? WHERE student_id = ?")) {
            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getCoordinator().getId());
            statement.setLong(3, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * Удаляет студента из базы данных по его идентификатору.
     *
     * @param id идентификатор студента
     * @return true, если операция успешна, иначе false
     */
    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM student WHERE student_id = ?")) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Находит всех студентов, зарегистрированных на определенный курс.
     *
     * @param courseId идентификатор курса
     * @return список студентов, зарегистрированных на курс
     */
    @Override
    public List<Student> findStudentsByCourseId(Long courseId) {
        List<Student> students = new ArrayList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT s.student_id, s.name, s.coordinator_id "
                        + "FROM student s "
                        + "JOIN course_student cs ON s.student_id = cs.student_id "
                        + "WHERE cs.course_id = ?")) {
            statement.setLong(1, courseId);
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
}
