package service;

import config.DatabaseConfig;
import constants.DataConstants;
import creator.CreatorObject;
import org.example.dao.impl.CoordinatorDAOImpl;
import org.example.dto.CoordinatorDTO;
import org.example.dto.StudentDTO;
import org.example.service.CoordinatorService;
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
class CoordinatorServiceTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    private static DataSource dataSource;
    private static Connection connection;
    @Mock
    private CoordinatorDAOImpl coordinatorDAO;
    private CoordinatorService coordinatorService;

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

    private static Stream<Arguments> initCoordinators() {
        List<CoordinatorDTO> coordinators = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            coordinators.add(initCoordinator(i));
        }
        return coordinators.stream().map(Arguments::of);
    }

    private static Stream<Arguments> initCoordinatorsWithId() {
        List<CoordinatorDTO> coordinators = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CoordinatorDTO coordinator = initCoordinator(i);
            coordinator.setId((long) (1 + i));
            coordinators.add(coordinator);
        }
        return coordinators.stream().map(Arguments::of);
    }

    private static CoordinatorDTO initCoordinator(int i) {
        return CreatorObject.createCoordinatorForTest(DataConstants.VALID_NAMES[i]);
    }

    @BeforeEach
    void setUp() {
        coordinatorDAO = new CoordinatorDAOImpl(dataSource);
        coordinatorService = new CoordinatorService(coordinatorDAO);
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
    @MethodSource("initCoordinators")
    void testCreateWithoutId(CoordinatorDTO coordinatorForCreate) {
        CoordinatorDTO createdCoordinator = coordinatorService.create(coordinatorForCreate);
        coordinatorForCreate.setId(createdCoordinator.getId());

        Assertions.assertEquals(createdCoordinator, coordinatorForCreate);
    }

    @ParameterizedTest
    @MethodSource("initInvalidId")
    void testDeleteByIdNegative(long id) {
        Assertions.assertThrows(Exception.class, () -> coordinatorService.deleteById(id));
    }

    @ParameterizedTest
    @Order(Integer.MAX_VALUE)
    @MethodSource("initValidId")
    void testDeleteByIdPositive(long id) {
        Assertions.assertDoesNotThrow(() -> coordinatorService.deleteById(id));
    }

    @ParameterizedTest
    @MethodSource("initInvalidId")
    void testFindByInvalidId(long id) {
        Assertions.assertThrows(Exception.class, () -> coordinatorService.findById(id));
    }

    @Test
    void testFindAll() {
        Assertions.assertNotNull(coordinatorService.findAll());
    }

    @ParameterizedTest
    @Order(1)
    @MethodSource("initCoordinatorsWithId")
    void testUpdate(CoordinatorDTO coordinatorForUpdate) {
        CoordinatorDTO updatedChair = coordinatorService.update(coordinatorForUpdate);
        Assertions.assertEquals(updatedChair, coordinatorForUpdate);
    }

    @ParameterizedTest
    @CsvSource({
            "1, Student 1",
            "2, Student 2",
            "3, Student 3"
    })
    public void testGetStudentsForCoordinator(long id, String name) {
        List<StudentDTO> result = coordinatorService.getStudentsForCoordinator(id);

        Assertions.assertEquals(name, result.get(0).getName());
    }
}