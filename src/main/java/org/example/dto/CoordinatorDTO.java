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
 * Класс, представляющий DTO для координатора.
 * Расширяет класс IdentifyDTO, который содержит базовые поля для идентификации сущности.
 */
public class CoordinatorDTO extends IdentifyDTO {
    /**
     * Имя координатора.
     */
    private String name;
    /**
     * Список студентов, закрепленных за координатором.
     */
    private List<StudentDTO> students;
}