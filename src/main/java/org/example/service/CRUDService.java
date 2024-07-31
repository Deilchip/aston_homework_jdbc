package org.example.service;

import org.example.dao.CrudRepository;
import org.example.dto.IdentifyDTO;
import org.modelmapper.ModelMapper;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CRUDService<D extends IdentifyDTO, E, R extends CrudRepository<E>> {
    /**
     * Экземпляр обобщенного класса-дто для абстракции, является основным возвращаемым объектом в классе
     */
    protected final Class<D> dtoClass;
    /**
     * Экземпляр обобщенного репозитория для абстракции
     */
    protected final R repository;

    /**
     * ModelMapper для маппинга Entity в Dto сущности и наоборот
     */
    protected final ModelMapper modelMapper;

    /**
     * Конструктор класса Service.
     *
     * @param dtoClass Класс DTO.
     */
    protected CRUDService(Class<D> dtoClass, R repository) {
        this.dtoClass = dtoClass;
        this.modelMapper = new ModelMapper();
        this.repository = repository;
    }

    /**
     * Создает новый объект сущности на основе переданного DTO объекта.
     *
     * @param dto DTO объект для создания.
     * @return Созданный DTO объект.
     * @throws InvalidParameterException Если переданный объект является null.
     */
    public abstract D create(D dto) throws InvalidParameterException;

    /**
     * Удаляет объект по идентификатору.
     *
     * @param id Идентификатор объекта для удаления.
     * @throws InvalidParameterException Если объект с переданным идентификатором не существует.
     */
    public boolean deleteById(long id) {
        repository.findById(id).orElseThrow(() -> new InvalidParameterException("Нет объекта в бд"));
        return repository.deleteById(id);
    }

    /**
     * Возвращает список всех DTO объектов.
     * <p>
     * return Список всех DTO объектов.
     *
     * @throws InvalidParameterException Если нет ни одного объекта.
     */
    public List<D> findAll() {
        List<E> list = repository.findAll();
        return list.stream()
                .map(e -> modelMapper.map(e, dtoClass))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает DTO объект с указанным идентификатором.
     *
     * @param id Идентификатор DTO объекта.
     * @return DTO объект с указанным иентификатором.
     * @throws InvalidParameterException Если DTO объект с указанным идентификатором не найден.
     */
    public Optional<D> findById(long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, dtoClass))
                .map(Optional::of)
                .orElseThrow(() -> new InvalidParameterException("Нет объекта в бд"));
    }

    /**
     * Метод обновления сущностей, работает только с существующими сущностями в БД/хранилище
     *
     * @param dto экземпляр класса-dto
     * @return возвращает обновленную сущность
     * @throws InvalidParameterException прокидывается в случае невалидных полей
     */
    public abstract D update(D dto) throws InvalidParameterException;
}
