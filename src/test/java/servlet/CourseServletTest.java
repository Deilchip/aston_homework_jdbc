package servlet;

import config.DatabaseConfig;
import org.example.servlet.CourseServlet;
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
class CourseServletTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    private static DataSource dataSource;
    private static Connection connection;

    private CourseServlet courseServlet;

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
        courseServlet = new CourseServlet(dataSource);
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
    void testDoGetAllCourses() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(printWriter);
        courseServlet.doGet(request, response);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).flush();
    }

    @Test
    void testDoGetCourseById() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        Long courseId = 1L;
        when(request.getPathInfo()).thenReturn("/" + courseId);
        when(response.getWriter()).thenReturn(printWriter);
        courseServlet.doGet(request, response);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).flush();
    }

    @Test
    void testDoDelete() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/" + 4L);
        courseServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
