package org.example.dao;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий основные CRUD-операции для работы с сущностями в базе данных.
 *
 * @param <T> тип сущности, с которой работает репозиторий
 */
public interface CrudRepository<T> {
    /**
     * Находит сущность по ее идентификатору.
     *
     * @param id идентификатор сущности
     * @return сущность, если она найдена, иначе пустой Optional
     */
    Optional<T> findById(Long id);

    /**
     * Находит все сущности.
     *
     * @return список всех сущностей
     */
    List<T> findAll();

    /**
     * Сохраняет новую сущность в базе данных.
     *
     * @param entity сущность, которую нужно сохранить
     * @return сохраненная сущность
     */
    T save(T entity);

    /**
     * Обновляет существующую сущность в базе данных.
     *
     * @param entity сущность, которую нужно обновить
     * @return обновленная сущность
     */
    T update(T entity);

    /**
     * Удаляет сущность из базы данных по ее идентификатору.
     *
     * @param id идентификатор сущности
     * @return true, если операция успешна, иначе false
     */
    boolean deleteById(Long id);
}