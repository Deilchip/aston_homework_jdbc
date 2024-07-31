package org.example.service;

import org.example.dao.impl.CourseDAOImpl;
import org.example.dto.CourseDTO;
import org.example.model.Course;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс для работы с курсами.
 */
public class CourseService extends CRUDService<CourseDTO, Course, CourseDAOImpl> {
    /**
     * Конструктор, инициализирующий сервис с использованием DAO-класса для курсов.
     *
     * @param courseDAO DAO-класс для работы с курсами
     */
    public CourseService(CourseDAOImpl courseDAO) {
        super(CourseDTO.class, courseDAO);
    }

    /**
     * Создает новый курс.
     *
     * @param dto DTO-объект с данными нового курса
     * @return DTO-объект созданного курса
     * @throws InvalidParameterException если переданные параметры некорректны
     */
    @Override
    public CourseDTO create(CourseDTO dto) throws InvalidParameterException {
        Course course = modelMapper.map(dto, Course.class);
        return modelMapper.map(repository.save(course), CourseDTO.class);
    }

    /**
     * Обновляет данные курса.
     *
     * @param dto DTO-объект с обновленными данными курса
     * @return DTO-объект обновленного курса
     * @throws InvalidParameterException если переданные параметры некорректны
     */
    @Override
    public CourseDTO update(CourseDTO dto) throws InvalidParameterException {
        Course course = modelMapper.map(dto, Course.class);
        return modelMapper.map(repository.update(course), CourseDTO.class);
    }

    /**
     * Получает список курсов, на которые зарегистрирован указанный студент.
     *
     * @param studentId идентификатор студента
     * @return список DTO-объектов курсов
     */
    public List<CourseDTO> findCoursesByStudentId(Long studentId) {
        List<Course> courses = repository.findCoursesByStudentId(studentId);
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }
}