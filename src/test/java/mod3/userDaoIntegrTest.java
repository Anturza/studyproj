package mod3;

import mod2.dao.UserDao;
import mod2.dao.UserDaoImpl;
import mod2.entities.Name;
import mod2.entities.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class userDaoIntegrTest {

    public static final UUID TEST_ID = UUID.fromString("751a2ce6-20cf-482c-8ba8-df2805905380");
    public static final String TEST_EMAIL_INPUT = "flue@vander.nl";
    public static final int TEST_AGE_INPUT = 47;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest");

    private UserDao userDao;

    private User testUser;
    private List<User> testUserList;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
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
        testUser = new User(testName, TEST_EMAIL_INPUT, TEST_AGE_INPUT, LocalDateTime.now());
    }

    @Test
    void createAndGetUserTest() {
        userDao.create(testUser);
        //User retrievedUser = userDao.get(TEST_ID);
        //Assertions.assertEquals(testUser, retrievedUser);
    }
}
