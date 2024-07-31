package org.example.dao;

import org.example.model.Coordinator;
import org.example.model.Student;

import java.util.List;

/**
 * Интерфейс, определяющий методы для работы с координаторами в базе данных.
 * Расширяет интерфейс CrudRepository, предоставляя дополнительные методы, специфичные для координаторов.
 */
public interface CoordinatorDAO extends CrudRepository<Coordinator> {
    /**
     * Находит всех студентов, которые закреплены за определенным координатором.
     *
     * @param coordinatorId идентификатор координатора
     * @return список студентов, закрепленных за координатором
     */
    List<Student> findStudentsByCoordinatorId(Long coordinatorId);
}