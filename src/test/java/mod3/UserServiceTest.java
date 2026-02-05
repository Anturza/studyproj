package mod3;

import mod2.dao.UserDao;
import mod2.entities.Name;
import mod2.entities.User;
import mod2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User expectedUser;
    private List<User> expectedUserList;
    public static final String TEST_INPUT = "751a2ce6-20cf-482c-8ba8-df2805905380";

    void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Name testName = new Name("Sidorov", "Dimitry");
        Name testName2 = new Name("Ivanov", "Vitaly");
        Name testName3 = new Name("Petrov", "Petr");
        expectedUser = new User(UUID.fromString(TEST_INPUT), testName, "asd@asd.com", 28, LocalDateTime.now());
        User testUser2 = new User(UUID.randomUUID(), testName2, "asd@asd.com", 28, LocalDateTime.now());
        User testUser3 = new User(UUID.randomUUID(), testName3, "asd@asd.com", 28, LocalDateTime.now());
        expectedUserList = List.of(testUser2, testUser3);
    }

    @Test
    void whenShowAllUsers_thenReturnUsersList() {
        when(userDao.getAll()).thenReturn(expectedUserList);
        List<User> actualUserList = userService.getAndShowAllUsers();
        assertThat(expectedUserList).isEqualTo(actualUserList);
        verify(userDao, times(1)).getAll();
    }

    @Test
    void whenAddUser_thenUserShouldBePersist() {
        doNothing().when(userDao).create(expectedUser);
        userDao.create(expectedUser);
        verify(userDao, times(1)).create(expectedUser);
    }

    @Test
    void whenShowUser_thenReturnUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_INPUT);
        when(userDao.get(expectedUser.getId())).thenReturn(expectedUser);
        UserService userService1 = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService1.getClass();
        Method meth = cl.getDeclaredMethod("getAndShowUser");
        meth.setAccessible(true);
        User foundUser = (User) meth.invoke(userService1);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(expectedUser.getName());
        verify(userDao, times(1)).get(expectedUser.getId());
    }

    @Test
    void whenShowNotValidUser_thenReturnNothing() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UUID wrongId = UUID.randomUUID();
        provideInput(wrongId.toString());
        when(userDao.get(wrongId)).thenReturn(null);
        UserService userService1 = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService1.getClass();
        Method meth = cl.getDeclaredMethod("getAndShowUser");
        meth.setAccessible(true);
        User foundUser = (User) meth.invoke(userService1);
        assertThat(foundUser).isNull();
        verify(userDao, times(1)).get(wrongId);
    }


    @Test
    void whenRemoveUser_thenUserDAOShouldBeCalled() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        provideInput(TEST_INPUT);
        doNothing().when(userDao).remove(expectedUser.getId());
        UserService userService1 = new UserService(userDao, new Scanner(System.in));
        Class<?> cl = userService1.getClass();
        Method meth = cl.getDeclaredMethod("removeUser");
        meth.setAccessible(true);
        meth.invoke(userService1);
        verify(userDao, times(1)).remove(expectedUser.getId());
    }

    @Test
    void whenUpdateUser_thenUserShouldBePersist() {
        doNothing().when(userDao).update(expectedUser);
        userService.editUser();
        verify(userDao, times(1)).update(expectedUser);
    }

    @Test
    void whenCreateUser_thenReturnUser() {

    }


}
