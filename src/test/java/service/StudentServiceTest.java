package service;

import config.DatabaseConfig;
import constants.DataConstants;
import creator.CreatorObject;
import org.example.dao.impl.StudentDAOImpl;
import org.example.dto.StudentDTO;
import org.example.service.StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
class StudentServiceTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    private static DataSource dataSource;
    private static Connection connection;
    @Mock
    private StudentDAOImpl studentDAO;
    private StudentService studentService;

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

    private static Stream<Arguments> initStudents() {
        List<StudentDTO> students = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            students.add(initStudent(i));
        }
        return students.stream().map(Arguments::of);
    }

    private static Stream<Arguments> initStudentsWithCourseId() {
        List<StudentDTO> students = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            students.add(initStudentWithCourseId(i));
        }
        return students.stream().map(Arguments::of);
    }

    private static Stream<Arguments> initStudentsWithId() {
        List<StudentDTO> students = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            StudentDTO coordinator = initStudent(i);
            coordinator.setId((long) (1 + i));
            students.add(coordinator);
        }
        return students.stream().map(Arguments::of);
    }

    private static StudentDTO initStudentWithCourseId(int i) {
        return CreatorObject.createStudentWithCourseIdForTest(DataConstants.VALID_NAMES[i], i);
    }

    private static StudentDTO initStudent(int i) {
        return CreatorObject.createStudentForTest(DataConstants.VALID_NAMES[i], i);
    }

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAOImpl(dataSource);
        studentService = new StudentService(studentDAO);
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
    @MethodSource("initStudents")
    void testCreateWithoutId(StudentDTO studentForCreate) {
        StudentDTO createdStudent = studentService.create(studentForCreate);
        studentForCreate.setId(createdStudent.getId());

        Assertions.assertEquals(createdStudent, studentForCreate);
    }

    @ParameterizedTest
    @MethodSource("initInvalidId")
    void testDeleteByIdNegative(long id) {
        Assertions.assertThrows(Exception.class, () -> studentService.deleteById(id));
    }

    @ParameterizedTest
    @Order(Integer.MAX_VALUE)
    @MethodSource("initValidId")
    void testDeleteByIdPositive(long id) {
        Assertions.assertDoesNotThrow(() -> studentService.deleteById(id));
    }

    @ParameterizedTest
    @MethodSource("initInvalidId")
    void testFindByInvalidId(long id) {
        Assertions.assertThrows(Exception.class, () -> studentService.findById(id));
    }

    @Test
    void testFindAll() {
        Assertions.assertNotNull(studentService.findAll());
    }

    @ParameterizedTest
    @MethodSource("initStudentsWithId")
    void testUpdate(StudentDTO coordinatorForUpdate) {
        StudentDTO updatedChair = studentService.update(coordinatorForUpdate);
        Assertions.assertEquals(updatedChair, coordinatorForUpdate);
    }

    @ParameterizedTest
    @CsvSource({
            "4, 1",
            "5, 2"
    })
    void testAddCourseToStudent(long studentId, long courseId) {
        Assertions.assertTrue(studentService.addCourseToStudent(studentId, courseId));
    }

    @ParameterizedTest
    @CsvSource({
            "4, 1",
            "5, 2"
    })
    void testRemoveCourseFromStudent(long studentId, long courseId) {
        Assertions.assertTrue(studentService.removeCourseFromStudent(studentId, courseId));
    }

    @ParameterizedTest
    @CsvSource({
            "7, 2",
            "6, 1"
    })
    void testRemoveCourseFromStudentNegative(long studentId, long courseId) {
        Assertions.assertFalse(studentService.removeCourseFromStudent(studentId, courseId));
    }

    @ParameterizedTest
    @CsvSource({
            "7, 2",
            "6, 1"
    })
    void testAddCourseToStudentNegative(long studentId, long courseId) {
        Assertions.assertFalse(studentService.addCourseToStudent(studentId, courseId));
    }

    @ParameterizedTest
    @MethodSource("initStudentsWithCourseId")
    void testGetStudentsByCourseId(StudentDTO studentForFind) {
        List<StudentDTO> result = studentService.getStudentsByCourseId(studentForFind.getCourses().get(0).getId());
        Assertions.assertNotNull(result);
    }
}