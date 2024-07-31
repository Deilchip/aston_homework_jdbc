package org.example.dao.impl;

import org.example.dao.CourseDAO;
import org.example.model.Course;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса CourseDAO, которая использует CrudRepositoryImpl для выполнения основных CRUD-операций.
 * Этот класс предоставляет методы для работы с курсами в базе данных.
 */
public class CourseDAOImpl extends CrudRepositoryImpl<Course> implements CourseDAO {
    /**
     * Конструктор, принимающий DataSource для подключения к базе данных.
     *
     * @param dataSource источник данных для подключения к базе данных
     */
    public CourseDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Конструктор по умолчанию.
     */
    public CourseDAOImpl() {
        super();
    }

    /**
     * Находит курс по его идентификатору.
     *
     * @param id идентификатор курса
     * @return курс, если найден, иначе null
     */
    @Override
    public Optional<Course> findById(Long id) {
        Course course = null;
        try (PreparedStatement statement = getConnection().prepareStatement("SELECT course_id, name FROM course WHERE course_id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    course = new Course();
                    course.setId(resultSet.getLong("course_id"));
                    course.setName(resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(course);
    }

    /**
     * Находит все курсы.
     *
     * @return список всех курсов
     */
    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement statement = getConnection().prepareStatement("SELECT course_id, name FROM course")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setId(resultSet.getLong("course_id"));
                    course.setName(resultSet.getString("name"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    /**
     * Сохраняет новый курс в базе данных.
     *
     * @param entity курс для сохранения
     * @return сохраненный курс
     */
    @Override
    public Course save(Course entity) {
        try (PreparedStatement statement = getConnection().prepareStatement("INSERT INTO course (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
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
     * Обновляет существующий курс в базе данных.
     *
     * @param entity курс для обновления
     * @return обновленный курс
     */
    @Override
    public Course update(Course entity) {
        try (PreparedStatement statement = getConnection().prepareStatement("UPDATE course SET name = ? WHERE course_id = ?")) {
            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * Удаляет курс из базы данных по его идентификатору.
     *
     * @param id идентификатор курса для удаления
     * @return true, если удаление успешно, иначе false
     */
    @Override
    public boolean deleteById(Long id) {
        try (PreparedStatement statement = getConnection().prepareStatement("DELETE FROM course WHERE course_id = ?")) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Находит все курсы, в которых участвует указанный студент.
     *
     * @param studentId идентификатор студента
     * @return список курсов, в которых участвует студент
     */
    @Override
    public List<Course> findCoursesByStudentId(Long studentId) {
        List<Course> courses = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT c.course_id, c.name " +
                     "FROM course c " +
                     "JOIN course_student cs ON c.course_id = cs.course_id " +
                     "WHERE cs.student_id = ?")) {
            statement.setLong(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setId(resultSet.getLong("course_id"));
                    course.setName(resultSet.getString("name"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}