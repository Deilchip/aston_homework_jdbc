package org.example.service;

import org.example.dao.impl.StudentDAOImpl;
import org.example.dto.StudentDTO;
import org.example.model.Student;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс для работы со студентами.
 */
public class StudentService extends CRUDService<StudentDTO, Student, StudentDAOImpl> {
    /**
     * Конструктор, инициализирующий сервис с использованием DAO-класса для студентов.
     *
     * @param studentDAO DAO-класс для работы со студентами
     */
    public StudentService(StudentDAOImpl studentDAO) {
        super(StudentDTO.class, studentDAO);
    }

    /**
     * Создает нового студента.
     *
     * @param dto DTO-объект с данными нового студента
     * @return DTO-объект созданного студента
     * @throws InvalidParameterException если переданные параметры некорректны
     */
    @Override
    public StudentDTO create(StudentDTO dto) throws InvalidParameterException {
        Student student = modelMapper.map(dto, Student.class);
        return modelMapper.map(repository.save(student), StudentDTO.class);
    }

    /**
     * Обновляет данные студента.
     *
     * @param dto DTO-объект с обновленными данными студента
     * @return DTO-объект обновленного студента
     * @throws InvalidParameterException если переданные параметры некорректны
     */
    @Override
    public StudentDTO update(StudentDTO dto) throws InvalidParameterException {
        Student student = modelMapper.map(dto, Student.class);
        Student updatedStudent = repository.update(student);
        return modelMapper.map(updatedStudent, StudentDTO.class);
    }

    /**
     * Получает список студентов, зарегистрированных на указанный курс.
     *
     * @param courseId идентификатор курса
     * @return список DTO-объектов студентов
     */
    public List<StudentDTO> getStudentsByCourseId(Long courseId) {
        List<Student> students = repository.findStudentsByCourseId(courseId);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Добавляет курс к студенту.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return true, если операция успешна, иначе false
     */
    public boolean addCourseToStudent(Long studentId, Long courseId) {
        return repository.addCourseToStudent(studentId, courseId);
    }

    /**
     * Удаляет курс из списка курсов студента.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return true, если операция успешна, иначе false
     */
    public boolean removeCourseFromStudent(Long studentId, Long courseId) {
        return repository.removeCourseFromStudent(studentId, courseId);
    }
}