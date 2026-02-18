package mod3;

import mod2.dao.UserDao;
import mod2.dao.UserDaoImpl;
import mod2.entities.Name;
import mod2.entities.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

/** This class has integration tests with basic cases of using {@link mod2.dao.UserDao} (CRUD operations). JUnit 5 core framework used here  for structuring and running tests and Mockito used here to
 * isolate the application under tests from its external dependency ({@link UserDao}). Assertions from
 * {@link org.junit.jupiter.api} and AssertJ used here for assertions. Each test runs isolated with new container
 * instance */
public class UserDaoIntegrationTest {

    public static final String TEST_EMAIL = "flue@vander.nl";
    public static final int TEST_AGE = 47;

    private PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest");

    private UserDao userDao;

    private User testUser;

    @BeforeEach
    void setUp() {
        postgres.start();
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.driver_class", postgres.getDriverClassName());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.highlight_sql", "true");
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Name.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);

        Name testName = new Name("Ulrich", "Lars", "Jr");
        testUser = new User(testName, TEST_EMAIL, TEST_AGE, LocalDateTime.now());
    }
    @AfterEach
    void tearDown() {
        postgres.stop();
    }

    @Test
    @DisplayName("Create test")
    void createUserTest() {
        User createdUser = userDao.create(testUser);
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(testUser.getName(), createdUser.getName());
        Assertions.assertEquals(testUser.getAge(), createdUser.getAge());
        Assertions.assertEquals(testUser.getEmail(), createdUser.getEmail());
        assertThat(testUser.getCreated()).isEqualToIgnoringNanos(createdUser.getCreated());
    }

    @Test
    @DisplayName("Get test")
    void getUserTest() {
        User createdUser = userDao.create(testUser);
        User retrievedUser = userDao.get(createdUser.getId());
        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals(createdUser.getId(), retrievedUser.getId());
        Assertions.assertEquals(createdUser.getName(), retrievedUser.getName());
        Assertions.assertEquals(createdUser.getAge(), retrievedUser.getAge());
        Assertions.assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
        assertThat(createdUser.getCreated()).isEqualToIgnoringNanos(retrievedUser.getCreated());
    }

    @Test
    @DisplayName("Update test")
    void modifyUserTest() {
        User createdUser = userDao.create(testUser);
        createdUser.setName(new Name("Gregory"));
        createdUser.setAge(33);
        createdUser.setEmail("another@ema.il");
        userDao.update(createdUser);
        User updatedUser = userDao.get(createdUser.getId());
        Assertions.assertEquals(createdUser.getId(), updatedUser.getId());
        Assertions.assertEquals(createdUser.getName(), updatedUser.getName());
        Assertions.assertEquals(createdUser.getAge(), updatedUser.getAge());
        Assertions.assertEquals(createdUser.getEmail(), updatedUser.getEmail());
        assertThat(createdUser.getCreated()).isEqualToIgnoringNanos(updatedUser.getCreated());
    }

    @Test
    @DisplayName("Delete test")
    void deleteTest() {
        User createdUser = userDao.create(testUser);
        userDao.remove(createdUser.getId());
        User deletedUser = userDao.get(createdUser.getId());
        Assertions.assertNull(deletedUser);
    }

    @Test
    @DisplayName("Get all test")
    void getAllTest() {
        Name testName2 = new Name("Ivanov", "Vitaly");
        Name testName3 = new Name("Petrov", "Petr");
        User testUser2 = new User(testName2, "asd@asd.com", 28, LocalDateTime.now());
        User testUser3 = new User(testName3, "qwe@rty.com", 38, LocalDateTime.now());
        List<User> testUserList = List.of(testUser2, testUser3);
        testUserList.forEach(user -> userDao.create(user));
        List<User> retrievedList = userDao.getAll();
        Assertions.assertEquals(retrievedList, testUserList);
    }
}
