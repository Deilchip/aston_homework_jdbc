package org.example.dao;

import org.example.model.Course;

import java.util.List;

/**
 * Интерфейс, определяющий методы для работы с курсами в базе данных.
 * Расширяет интерфейс CrudRepository, предоставляя дополнительные методы, специфичные для курсов.
 */
public interface CourseDAO extends CrudRepository<Course> {
    /**
     * Находит все курсы, на которые зарегистрирован определенный студент.
     *
     * @param studentId идентификатор студента
     * @return список курсов, на которые зарегистрирован студент
     */
    List<Course> findCoursesByStudentId(Long studentId);
}