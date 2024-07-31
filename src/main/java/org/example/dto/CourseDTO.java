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
 * Класс, представляющий DTO для курса.
 * Расширяет класс IdentifyDTO, который содержит базовые поля для идентификации сущности.
 */
public class CourseDTO extends IdentifyDTO {
    /**
     * Название курса.
     */
    private String name;
    /**
     * Список студентов, зарегистрированных на курс.
     */
    private List<StudentDTO> students;
}