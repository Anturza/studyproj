package mod2;

import mod2.Entities.Name;
import mod2.Entities.User;
import mod2.dao.UserDaoImpl;
import mod2.dao.UserService;
import mod2.menu.Handler;
import org.hibernate.cfg.Configuration;
import mod2.menu.Menu;

import java.util.Scanner;

public class App {

    static Scanner sysIn = new Scanner(System.in);

    static Configuration userConfiguration = new org.hibernate.cfg.Configuration()
            .addAnnotatedClass(User.class).addAnnotatedClass(Name.class);

    static UserDaoImpl userDao = new UserDaoImpl(userConfiguration.buildSessionFactory());

    static UserService userService = new UserService(userDao, sysIn);

    public static void main(String[] args) {

        Menu mainMenu = new Menu("\nВыберите действие\n1. Показать всех пользователей из БД\n" +
                "2. Показать отдельного пользователя \n3. Создать новых пользователей и записать в БД\n" +
                "4. Изменить данные пользователя в БД \n5. Удалить пользователя из БД\n" +
                "6. Выход", "Некорректный ввод. Введите число от 1 до 6", sysIn);
        mainMenu.addHandler(new Handler("1", userService::showAllUsers));
        mainMenu.addHandler(new Handler("2", userService::showUser));
        mainMenu.addHandler(new Handler("3", userService::addUser));
        mainMenu.addHandler(new Handler("4", userService::editUser));
        mainMenu.addHandler(new Handler("5", userService::removeUser));
        mainMenu.addHandler(new Handler("6", mainMenu::stop));

        mainMenu.run();

        sysIn.close();
    }
}
