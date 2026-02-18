package mod2;

import mod2.dao.UserDao;
import mod2.dao.UserDaoImpl;
import mod2.service.UserService;
import mod2.menu.Handler;
import mod2.util.HibernateConfig;
import mod2.menu.Menu;
import org.hibernate.SessionFactory;

import java.util.Scanner;

public class App {

    static Scanner sysIn = new Scanner(System.in);
    static SessionFactory factory = HibernateConfig.getSessionFactory();
    static UserDao userDao = new UserDaoImpl(factory);
    static UserService userService = new UserService(userDao, sysIn);


    public static void main(String[] args) {

        Menu mainMenu = new Menu("\nВыберите действие\n1. Показать всех пользователей из БД\n" +
                "2. Показать отдельного пользователя \n3. Создать новых пользователей и записать в БД\n" +
                "4. Изменить данные пользователя в БД \n5. Удалить пользователя из БД\n" +
                "6. Выход", "Некорректный ввод. Введите число от 1 до 6", sysIn);
        mainMenu.addHandler(new Handler("1", userService::getAndShowAllUsers));
        mainMenu.addHandler(new Handler("2", userService::getAndShowUser));
        mainMenu.addHandler(new Handler("3", userService::addUser));
        mainMenu.addHandler(new Handler("4", userService::editUser));
        mainMenu.addHandler(new Handler("5", userService::removeUser));
        mainMenu.addHandler(new Handler("6", mainMenu::stop));

        mainMenu.run();

        sysIn.close();
        HibernateConfig.shutdown();
    }
}
