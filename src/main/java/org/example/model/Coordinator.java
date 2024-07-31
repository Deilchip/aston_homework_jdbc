package org.example.model;

import lombok.Data;

import java.util.List;

@Data
/**
 * Класс, представляющий координатора.
 */
public class Coordinator {
    /**
     * Идентификатор координатора.
     */
    private Long id;
    /**
     * Имя координатора.
     */
    private String name;
    /**
     * Список студентов, закрепленных за координатором.
     */
    private List<Student> students;
}