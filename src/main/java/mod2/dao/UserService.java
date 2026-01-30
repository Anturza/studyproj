package mod2.dao;

import mod2.Entities.Name;
import mod2.Entities.User;
import mod2.validation.UserValidator;

import java.time.LocalDateTime;
import java.util.Scanner;
/** This class is a logic layer of an application and handles operations related to user data , such as creating,
 * modifying and validating user data*/
public class UserService {

    private UserDaoImpl userDao;

    private Scanner sysIn;

    public UserService(UserDaoImpl userDao, Scanner sysIn) {
        this.userDao = userDao;
        this.sysIn = sysIn;
    }
    public void showAllUsers(){
        for (User user : userDao.getAll()) {
            System.out.println(user);
        }
    }

    public void showUser() {
        System.out.print("Введите id пользователя: ");
        long id = sysIn.nextLong();
        sysIn.nextLine();
        User currentUser = userDao.get(id);
        if (currentUser != null) {
            System.out.println(currentUser);
        }
    }

    public void addUser() {
        int count = readInt("Введите число пользователей: ");
        if (count <= 0) {
            System.out.println("Действие отменено");
            return;
        }
        int failCount = 0;

        for (int i = 1; i <= count; i++) {
            System.out.printf("Добавление пользователя %d\n", i );
            User user = createUser();
            if (user == null) {
                return;
            }
            userDao.create(user);
        }
        System.out.println("Успешно созданных пользователей: " + (count - failCount));
    }

    private User createUser() {
        System.out.print("Введите Ф.И.О., Ф.И. или хотя бы имя: ");

        String input = "";
        while (input.isEmpty()) {
            input = sysIn.nextLine(); // Считывает всю строку, включая пробелы [5]

            if (input.isEmpty()) {
                System.out.println("Строка не должна быть пустой. Попробуйте еще раз.");
            }
        }
        String name = input.trim();
        Name currName = parseName(name.split(" "));

        System.out.print("Введите возраст: ");
        int age;
        while (!sysIn.hasNextInt()) {
            System.out.println("Это не целое число. Попробуйте снова:");
            sysIn.next();
        }
        age = sysIn.nextInt();
        sysIn.nextLine();

        System.out.print("Введите e-mail: ");
        String input2 = "";
        while (input2.isEmpty()) {
            input2 = sysIn.nextLine();

            if (input2.isEmpty()) {
                System.out.println("Строка не должна быть пустой. Попробуйте еще раз.");
            }
        }
        String email = input2.trim();

        User currentUser = new User(currName, email, age, LocalDateTime.now());

        if (UserValidator.validate(currentUser)) {
            System.out.println("\u001b[34mВалидация данных пользователя успешна!\u001b[0m");
            return currentUser;
        } else {
            System.out.printf("\u001b[31mПроблема валидации данных пользователя!\n\u001b[0m User: %s содержит " +
                    "некорректные значения, попробуйте еще раз.", currentUser);
            return null;
        }
    }

    private Name parseName(String[] userData) {
        Name name;
        if (userData.length == 3) {
            name = new Name(userData[0], userData[1], userData[2]);
        } else if (userData.length == 2) {
            name = new Name(userData[0], userData[1]);
        } else if (userData.length == 1) {
            name = new Name(userData[0]);
        } else {
            return null;
        }
        return name;
    }

    public void editUser() {
        boolean changesMade = false;
        System.out.print("Введите id пользователя, данные которого необходимо изменить: ");
        long id = sysIn.nextLong();
        sysIn.nextLine();
        User currentUser = userDao.get(id);
        if (currentUser != null) {
            System.out.println("Если требуется, введите новые Ф.И.О. или пропустите нажав \"Enter\"");
            String name = sysIn.nextLine().trim();
            if (!name.isBlank()) {
                Name newName = parseName(name.split(" "));
                currentUser.setName(newName);
                changesMade = true;
            }
            System.out.println("Если необходимо, введите новый возраст или пропустите нажав \"Enter\"");
            String age = sysIn.nextLine().trim();
            if (!age.isBlank()) {
                int newAge = Integer.parseInt(age);
                currentUser.setAge(newAge);
                changesMade = true;
            }

            System.out.println("Если необходимо, введите новый e-mail или пропустите нажав \"Enter\"");
            String newEmail = sysIn.nextLine().trim();
            if (!newEmail.isBlank()) {
                currentUser.setEmail(newEmail);
                changesMade = true;
            }
            if (changesMade) {
                if (UserValidator.validate(currentUser)) {
                    System.out.println("\u001b[34mВалидация данных пользователя успешна!\u001b[0m");
                    System.out.println("Записать изменения? (Y/N, да/нет): ");
                    String str = sysIn.nextLine().trim();
                    if (str.matches("(?i)^[yYдД].*")) {
                        userDao.update(currentUser);
                        return;
                    }
                } else {
                    System.out.printf("\u001b[31mПроблема валидации данных пользователя!\n\u001b[0m User: %s содержит " +
                            "некорректные значения, попробуйте еще раз.", currentUser);
                }

            }
        } else {
            System.out.printf("Пользователь с id %d отсутствует в БД\n", id);
        }
        System.out.println("Действие отменено");
    }

    public void removeUser() {
        System.out.print("Введите id пользователя: ");
        long id = sysIn.nextLong();
        sysIn.nextLine();
        userDao.remove(id);
    }

    private int readInt(String innerMessage) {
        while (true) {
            System.out.println(innerMessage);

            if (!sysIn.hasNextInt()) {
                System.out.println("Ошибка: введите целое число");
                sysIn.next();
                continue;
            }

            int value = sysIn.nextInt();
            sysIn.nextLine();
            if (value > 0) {
                return value;
            }

            return -1; //to abort action
        }
    }
}
