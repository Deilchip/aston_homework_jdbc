package org.example.service;

import org.example.dao.impl.CoordinatorDAOImpl;
import org.example.dto.CoordinatorDTO;
import org.example.dto.StudentDTO;
import org.example.model.Coordinator;
import org.example.model.Student;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс для работы с координаторами.
 */
public class CoordinatorService extends CRUDService<CoordinatorDTO, Coordinator, CoordinatorDAOImpl> {
    /**
     * Конструктор, инициализирующий сервис с использованием DAO-класса для координаторов.
     *
     * @param coordinatorDAO DAO-класс для работы с координаторами
     */
    public CoordinatorService(CoordinatorDAOImpl coordinatorDAO) {
        super(CoordinatorDTO.class, coordinatorDAO);
    }

    /**
     * Создает нового координатора.
     *
     * @param dto DTO-объект с данными нового координатора
     * @return DTO-объект созданного координатора
     * @throws InvalidParameterException если переданные параметры некорректны
     */
    @Override
    public CoordinatorDTO create(CoordinatorDTO dto) throws InvalidParameterException {
        Coordinator coordinator = modelMapper.map(dto, Coordinator.class);
        return modelMapper.map(repository.save(coordinator), CoordinatorDTO.class);
    }

    /**
     * Обновляет данные координатора.
     *
     * @param dto DTO-объект с обновленными данными координатора
     * @return DTO-объект обновленного координатора
     * @throws InvalidParameterException если переданные параметры некорректны
     */
    @Override
    public CoordinatorDTO update(CoordinatorDTO dto) throws InvalidParameterException {
        Coordinator coordinator = modelMapper.map(dto, Coordinator.class);
        return modelMapper.map(repository.update(coordinator), CoordinatorDTO.class);
    }

    /**
     * Получает список студентов, закрепленных за указанным координатором.
     *
     * @param coordinatorId идентификатор координатора
     * @return список DTO-объектов студентов
     */
    public List<StudentDTO> getStudentsForCoordinator(Long coordinatorId) {
        List<Student> students = repository.findStudentsByCoordinatorId(coordinatorId);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }
}