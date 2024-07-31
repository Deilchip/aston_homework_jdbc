package org.example.dao.impl;

import lombok.Generated;
import org.apache.commons.dbcp2.BasicDataSource;
import org.example.dao.CrudRepository;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Абстрактный базовый класс, реализующий интерфейс CrudRepository.
 * Этот класс предоставляет общую функциональность для работы с базой данных.
 *
 * @param <T> тип сущности, с которой работает репозиторий
 */
@Generated
public abstract class CrudRepositoryImpl<T> implements CrudRepository<T> {
    private final DataSource dataSource;

    /**
     * Конструктор, принимающий DataSource для подключения к базе данных.
     *
     * @param dataSource источник данных для подключения к базе данных
     */
    public CrudRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Конструктор по умолчанию, который создает DataSource из файла конфигурации.
     */
    public CrudRepositoryImpl() {
        this.dataSource = createDataSourceFromProperties();
    }

    /**
     * Создает DataSource из файла конфигурации.
     *
     * @return созданный DataSource
     */
    protected DataSource createDataSourceFromProperties() {
        BasicDataSource basicDataSource = new BasicDataSource();
        Properties properties = new Properties();
        String filePath = "C:\\Users\\User\\IdeaProjects\\aston_homework_jdbc\\src\\main\\resources\\application.properies";
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            basicDataSource.setDriverClassName("org.postgresql.Driver");
            basicDataSource.setUrl(properties.getProperty("db.url"));
            basicDataSource.setUsername(properties.getProperty("db.username"));
            basicDataSource.setPassword(properties.getProperty("db.password"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return basicDataSource;
    }

    /**
     * Получает соединение с базой данных.
     *
     * @return соединение с базой данных
     * @throws SQLException если не удалось получить соединение
     */
    protected Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            throw new SQLException("DataSource is not initialized");
        }
    }
}