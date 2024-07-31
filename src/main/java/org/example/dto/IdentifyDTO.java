package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Абстрактный класс, представляющий базовый DTO для идентифицируемых сущностей.
 * Содержит поле для хранения идентификатора сущности.
 */
public abstract class IdentifyDTO {
    /**
     * Идентификатор сущности.
     */
    private Long id;
}