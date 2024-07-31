package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Класс, представляющий DTO для студента.
 * Расширяет класс IdentifyDTO, который содержит базовые поля для идентификации сущности.
 */
public class StudentDTO extends IdentifyDTO {
    /**
     * Имя студента.
     */
    private String name;
    /**
     * Координатор, закрепленный за студентом.
     */
    private CoordinatorDTO coordinator;
    /**
     * Список курсов, на которые зарегистрирован студент.
     */
    private List<CourseDTO> courses;
}