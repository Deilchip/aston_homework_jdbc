package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@UtilityClass
public class DatabaseConfig {
    public static DataSource setupDatasource(PostgreSQLContainer<?> postgresContainer) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(postgresContainer.getJdbcUrl());
        hikariConfig.setUsername(postgresContainer.getUsername());
        hikariConfig.setPassword(postgresContainer.getPassword());
        return new HikariDataSource(hikariConfig);
    }

    public static void createTestData(Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE coordinator (" +
                    "coordinator_id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL" +
                    ")");
            statement.execute("INSERT INTO coordinator (name) VALUES ('Coordinator 1'), ('Coordinator 2'), ('Coordinator 3'), ('Coordinator 4')");

            statement.execute("CREATE TABLE course (" +
                    "course_id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL" +
                    ")");
            statement.execute("INSERT INTO course (name) VALUES ('Course A'), ('Course B'), ('Course C'), ('Course D')");
            statement.execute("CREATE TABLE student (" +
                    "student_id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "coordinator_id INT REFERENCES coordinator(coordinator_id)" +
                    ")");
            statement.execute("INSERT INTO student (name, coordinator_id) VALUES ('Student 1', 1), ('Student 2', 2), ('Student 3', 3), ('Student 4', 1), ('Student 5', 2)");
            statement.execute("CREATE TABLE course_student (" +
                    "course_id INT REFERENCES course(course_id)," +
                    "student_id INT REFERENCES student(student_id)," +
                    "PRIMARY KEY(course_id, student_id)" +
                    ")");
            statement.execute("INSERT INTO course_student (course_id, student_id) VALUES (1, 1), (2, 2), (3, 3), (1, 4), (2, 5)");

        }
    }
}