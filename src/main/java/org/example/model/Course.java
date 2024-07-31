package org.example.model;

import lombok.Data;

import java.util.List;

@Data
/**
 * Класс, представляющий курс.
 */
public class Course {
    /**
     * Идентификатор курса.
     */
    private Long id;
    /**
     * Название курса.
     */
    private String name;
    /**
     * Список студентов, зарегистрированных на курс.
     */
    private List<Student> students;
}
