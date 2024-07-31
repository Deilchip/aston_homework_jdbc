package service;

import config.DatabaseConfig;
import constants.DataConstants;
import creator.CreatorObject;
import org.example.dao.impl.CourseDAOImpl;
import org.example.dto.CourseDTO;
import org.example.service.CourseService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@Testcontainers
class CourseServiceTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    private static DataSource dataSource;
    private static Connection connection;
    @Mock
    private CourseDAOImpl courseDAO;
    private CourseService courseService;

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

    private static Stream<Arguments> initInvalidId() {
        return Arrays.stream(DataConstants.NON_VALID_ID)
                .mapToObj(Arguments::of);
    }

    private static Stream<Arguments> initValidId() {
        return Arrays.stream(DataConstants.VALID_ID)
                .mapToObj(Arguments::of);
    }

    private static Stream<Arguments> initCourses() {
        List<CourseDTO> courses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            courses.add(initCourses(i));
        }
        return courses.stream().map(Arguments::of);
    }

    private static Stream<Arguments> initCoursesWithId() {
        List<CourseDTO> courses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CourseDTO course = initCourses(i);
            course.setId((long) (1 + i));
            courses.add(course);
        }
        return courses.stream().map(Arguments::of);
    }

    private static CourseDTO initCourses(int i) {
        return CreatorObject.createCourseForTest(DataConstants.VALID_NAMES[i]);
    }

    @BeforeEach
    void setUp() {
        courseDAO = new CourseDAOImpl(dataSource);
        courseService = new CourseService(courseDAO);
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

    @ParameterizedTest
    @MethodSource("initCourses")
    void testCreateWithoutId(CourseDTO courseForCreate) {
        CourseDTO createdCoordinator = courseService.create(courseForCreate);
        courseForCreate.setId(createdCoordinator.getId());

        Assertions.assertEquals(createdCoordinator, courseForCreate);
    }

    @ParameterizedTest
    @MethodSource("initInvalidId")
    void testDeleteByIdNegative(long id) {
        Assertions.assertThrows(Exception.class, () -> courseService.deleteById(id));
    }

    @ParameterizedTest
    @Order(Integer.MAX_VALUE)
    @MethodSource("initValidId")
    void testDeleteByIdPositive(long id) {
        Assertions.assertDoesNotThrow(() -> courseService.deleteById(id));
    }

    @ParameterizedTest
    @MethodSource("initInvalidId")
    void testFindByInvalidId(long id) {
        Assertions.assertThrows(Exception.class, () -> courseService.findById(id));
    }

    @Test
    void testFindAll() {
        Assertions.assertNotNull(courseService.findAll());
    }

    @ParameterizedTest
    @MethodSource("initCoursesWithId")
    void testUpdate(CourseDTO courseForUpdate) {
        CourseDTO updatedChair = courseService.update(courseForUpdate);
        Assertions.assertEquals(updatedChair, courseForUpdate);
    }

    @ParameterizedTest
    @MethodSource("initValidId")
    void testFIndCoursesByStudentId(long id) {
        List<CourseDTO> result = courseService.findCoursesByStudentId(id);
        Assertions.assertNotNull(result);
    }
}
