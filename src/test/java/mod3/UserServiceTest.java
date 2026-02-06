package mod3;

import mod2.dao.UserDao;
import mod2.entities.Name;
import mod2.entities.User;
import mod2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
/** This class has tests with some basic cases of what can happened while using {@link UserService} (valid or not valid
 * values in methods). JUnit 5 core framework used here  for structuring and running tests and Mockito used here to
 * isolate the application under tests from its external dependency ({@link UserDao}). AssertJ used here for assertions.
 * Some test methods can produce {@link NoSuchMethodException}, {@link InvocationTargetException},
 * {@link IllegalAccessException} because of using Java Reflection to handle and provide tests with necessary data. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class UserServiceTest {

    public static final String TEST_ID_INPUT = "751a2ce6-20cf-482c-8ba8-df2805905380";
    public static final String TEST_INT_INPUT = "1";
    public static final String TEST_NAME_INPUT = "Ulrich Lars Jr";
    public static final String TEST_EMAIL_INPUT = "flue@vander.nl";
    public static final int TEST_AGE_INPUT = 47;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User expectedExistingUser;
    private User expectedCreatedUser;
    private List<User> expectedUserList;

    void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @BeforeEach
    public void setUp() {
        Name testName = new Name("Ulrich", "Lars", "Jr");
        Name testName2 = new Name("Ivanov", "Vitaly");
        Name testName3 = new Name("Petrov", "Petr");
        expectedExistingUser = new User(UUID.fromString(TEST_ID_INPUT), testName, TEST_EMAIL_INPUT, TEST_AGE_INPUT, LocalDateTime.now());
        expectedCreatedUser = new User(testName, TEST_EMAIL_INPUT, TEST_AGE_INPUT, LocalDateTime.now());
        User testUser = new User(UUID.randomUUID(), testName2, "asd@asd.com", 28, LocalDateTime.now());
        User testUser2 = new User(UUID.randomUUID(), testName3, "asd@asd.com", 28, LocalDateTime.now());
        expectedUserList = List.of(testUser, testUser2);
    }

    @Test
    @DisplayName("getAndShowAllUsers test")
    void whenGetAndShowAllUsers_thenReturnUsersList() {
        when(userDao.getAll()).thenReturn(expectedUserList);
        List<User> actualUserList = userService.getAndShowAllUsers();
        assertThat(expectedUserList).isEqualTo(actualUserList);
        verify(userDao, times(1)).getAll();
    }

    @Test
    @DisplayName("addUser test")
    void whenAddUserValidArgs_thenUserBeingPersisted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_INT_INPUT + "\n" + TEST_NAME_INPUT + "\n" + TEST_AGE_INPUT + "\n" + TEST_EMAIL_INPUT);
        doNothing().when(userDao).create(expectedCreatedUser);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("addUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(1)).create(expectedCreatedUser);
    }

    @Test
    @DisplayName("addUser test")
    void whenAddUserWrongArgs_thenUserNotPersisted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_INT_INPUT + "\n" + TEST_NAME_INPUT + "\n" + 22 + "\n" + "wrong.em@il");
        doNothing().when(userDao).create(expectedCreatedUser);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("addUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(0)).create(expectedCreatedUser);
    }

    @Test
    @DisplayName("getAndShowUser test")
    void whenGetAndShowUserValidArgs_thenReturnUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_ID_INPUT);
        when(userDao.get(expectedExistingUser.getId())).thenReturn(expectedExistingUser);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("getAndShowUser");
        meth.setAccessible(true);
        User foundUser = (User) meth.invoke(userService);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(expectedExistingUser.getName());
        verify(userDao, times(1)).get(expectedExistingUser.getId());
    }

    @Test
    @DisplayName("getAndShowUser test")
    void whenGetAndShowUserWrongArgs_thenReturnNothing() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UUID wrongId = UUID.randomUUID();
        provideInput(wrongId.toString());
        when(userDao.get(wrongId)).thenReturn(null);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("getAndShowUser");
        meth.setAccessible(true);
        User foundUser = (User) meth.invoke(userService);
        assertThat(foundUser).isNull();
        verify(userDao, times(1)).get(wrongId);
    }


    @Test
    @DisplayName("removeUser test")
    void whenRemoveUserValidArgs_thenUserDAOCalled() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_ID_INPUT);
        doNothing().when(userDao).remove(expectedExistingUser.getId());
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("removeUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(1)).remove(expectedExistingUser.getId());
    }

    @Test
    @DisplayName("removeUser test")
    void whenRemoveUserWrongArgs_thenUserDAONotCalled() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput("wR0nGuU1D_@R1sEn-hErE");
        doNothing().when(userDao).remove(expectedExistingUser.getId());
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("removeUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(0)).remove(expectedExistingUser.getId());
    }

    @Test
    @DisplayName("editUser test")
    void whenUpdateUserValidArgs_thenUserPersisted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_ID_INPUT + "\n" + TEST_NAME_INPUT + "\n" + TEST_AGE_INPUT + "\n" + TEST_EMAIL_INPUT +
                "\n" + "Y");
        when(userDao.get(any(UUID.class))).thenReturn(expectedExistingUser);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("editUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(1)).update(expectedExistingUser);
    }

    @Test
    @DisplayName("editUser test")
    void whenUpdateNoFieldsUser_thenOperationAborted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_ID_INPUT + "\n" + "\n" + "\n" + "\n");
        when(userDao.get(any(UUID.class))).thenReturn(expectedExistingUser);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("editUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(0)).update(expectedExistingUser);
    }

    @Test
    @DisplayName("editUser test")
    void whenUpdateUserWrongArgs_thenOperationAborted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_ID_INPUT + "\n" + "Surname Name" + "\n" + 180 + "\n" + "wrong.em@il" +
                "\n" + "Y");
        when(userDao.get(any(UUID.class))).thenReturn(expectedExistingUser);
        userService = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService.getClass();
        Method meth = cl.getDeclaredMethod("editUser");
        meth.setAccessible(true);
        meth.invoke(userService);
        verify(userDao, times(0)).update(expectedExistingUser);
    }
}
