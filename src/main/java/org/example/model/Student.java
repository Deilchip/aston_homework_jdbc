package org.example.model;

import lombok.Data;

import java.util.List;

@Data
/**
 * Класс, представляющий студента.
 */
public class Student {
    /**
     * Идентификатор студента.
     */
    private Long id;
    /**
     * Имя студента.
     */
    private String name;
    /**
     * Список курсов, на которые зарегистрирован студент.
     */
    private List<Course> courses;
    /**
     * Координатор, закрепленный за студентом.
     */
    private Coordinator coordinator;
}
