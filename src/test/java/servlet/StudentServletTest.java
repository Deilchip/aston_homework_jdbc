package servlet;

import config.DatabaseConfig;
import org.example.servlet.StudentServlet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Testcontainers
class StudentServletTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    private static DataSource dataSource;
    private static Connection connection;

    private StudentServlet studentServlet;

    @BeforeAll
    static void setupDatabase() {
        try {
            postgresContainer.start();
            dataSource = DatabaseConfig.setupDatasource(postgresContainer);
            connection = DriverManager.getConnection(
                    postgresContainer.getJdbcUrl(),
                    postgresContainer.getUsername(),
                    postgresContainer.getPassword()
            );
            DatabaseConfig.createTestData(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        studentServlet = new StudentServlet(dataSource);
    }

    @AfterEach
    void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDoGetAllStudents() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(printWriter);
        studentServlet.doGet(request, response);
        verify(printWriter).flush();
        verify(printWriter).print(anyString());
    }

    @Test
    void testDoGetStudentsByCourse() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(request.getPathInfo()).thenReturn("/1/students");
        when(response.getWriter()).thenReturn(printWriter);
        studentServlet.doGet(request, response);
        verify(printWriter).flush();
        verify(printWriter).print(anyString());
    }

    @Test
    void testDoDeleteCourseFromStudent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Long studentId = 1L;
        Long courseId = 2L;
        when(request.getPathInfo()).thenReturn("/" + studentId + "/courses/" + courseId);
        studentServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDeleteInvalidRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/invalid");
        studentServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
