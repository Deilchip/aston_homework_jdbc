package org.example.dao;

import org.example.model.Student;

import java.util.List;

/**
 * Интерфейс, определяющий методы для работы со студентами в базе данных.
 * Расширяет интерфейс CrudRepository, предоставляя дополнительные методы, специфичные для студентов.
 */
public interface StudentDAO extends CrudRepository<Student> {
    /**
     * Добавляет курс к студенту.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return true, если операция успешна, иначе false
     */
    boolean addCourseToStudent(Long studentId, Long courseId);

    /**
     * Удаляет курс из списка курсов студента.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return true, если операция успешна, иначе false
     */
    boolean removeCourseFromStudent(Long studentId, Long courseId);

    /**
     * Находит всех студентов, зарегистрированных на определенный курс.
     *
     * @param courseId идентификатор курса
     * @return список студентов, зарегистрированных на курс
     */
    List<Student> findStudentsByCourseId(Long courseId);
}