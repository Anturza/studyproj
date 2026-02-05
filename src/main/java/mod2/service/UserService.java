package mod2.service;

import mod2.dao.UserDao;
import mod2.entities.Name;
import mod2.entities.User;
import mod2.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * This class is a logic layer of an application and handles operations related to user data , such as creating,
 * modifying and validating user data
 */
public class UserService {

    private UserDao userDao;

    private Scanner sysIn;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String WRONG_ID = "Введен неверный id пользователя: {}";

    public UserService(UserDao userDao, Scanner sysIn) {
        this.userDao = userDao;
        this.sysIn = sysIn;
    }

    public List<User> getAndShowAllUsers() {
        List<User> lst = userDao.getAll();
        if (lst != null) {
            for (User user : lst) {
                System.out.println(user);
            }
        }
        return lst;
    }

    public User getAndShowUser() {
        System.out.print("Введите id пользователя: ");
        try {
            String strUuid = sysIn.nextLine();
            UUID uuid = UUID.fromString(strUuid);
            User currentUser = userDao.get(uuid);
            if (currentUser != null) {
                System.out.println(currentUser);
                return currentUser;
            }
        } catch (IllegalArgumentException e) {
            logger.error(WRONG_ID, e.getMessage(), e);
        }
        return null;
    }

    public void addUser() {
        int count = readInt("Введите число пользователей: ");
        if (count <= 0) {
            System.out.println("Действие отменено");
            return;
        }
        int failCount = 0;

        for (int i = 1; i <= count; i++) {
            System.out.printf("Добавление пользователя %d\n", i);
            try {
                User user = createUser();
                if (user == null) {
                    failCount++;
                    continue;
                }
                userDao.create(user);
            } catch (NumberFormatException e) {
                failCount++;
                logger.error("Ошибка: возраст должен быть числом.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                failCount++;
                logger.error("Ошибка валидации: {}", e.getMessage());
            }
        }
        System.out.println("Успешно созданных пользователей: " + (count - failCount));
    }

    private User createUser() {
        System.out.print("Введите Ф.И.О., Ф.И. или хотя бы имя: ");
        String name = processNameInput().trim();
        Name currName = parseName(name.split(" "));

        System.out.print("Введите возраст: ");
        int age = processAgeInput();

        System.out.print("Введите e-mail: ");
        String email = sysIn.nextLine().trim();

        User currentUser = new User(currName, email, age, LocalDateTime.now());

        if (UserValidator.validate(currentUser)) {
            System.out.println("\u001b[34mВалидация данных пользователя успешна!\u001b[0m");
            return currentUser;
        } else {
            System.out.printf("\u001b[31mПроблема валидации данных пользователя!\n\u001b[0m User: %s содержит " +
                    "некорректные значения, попробуйте еще раз.\n", currentUser);
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

    private String processNameInput() {
        String input = "";
        while (input.isEmpty() || input.matches("^\\d+$")) {
            input = sysIn.nextLine();
            if (input.isEmpty()) {
                System.out.println("Строка не должна быть пустой или содержать одни цифры. Попробуйте еще раз.");
            }
        }
        return input;
    }

    private int processAgeInput() {
        int age;
        while (!sysIn.hasNextInt()) {
            System.out.println("Это не целое число. Попробуйте снова:");
            sysIn.next();
        }
        age = sysIn.nextInt();
        sysIn.nextLine();
        return age;
    }

    public void editUser() {
        boolean changesMade = false;
        UUID uuid;
        System.out.print("Введите id пользователя, данные которого необходимо изменить: ");
        try {
            String strUuid = sysIn.nextLine();
            uuid = UUID.fromString(strUuid);
        } catch (IllegalArgumentException e) {
            logger.error(WRONG_ID, e.getMessage(), e);
            return;
        }
        User currentUser = userDao.get(uuid);
        if (currentUser != null) {
            System.out.println("Если требуется, введите новые Ф.И.О. или пропустите нажав \"Enter\"");
            String name = sysIn.nextLine().trim();
            while (name.matches("^\\d+$")) {
                System.out.println("Имя не должно содержать только цифры");
                name = sysIn.nextLine().trim();
            }
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
        }
        System.out.println("Действие отменено");
    }

    public void removeUser() {
        System.out.print("Введите id пользователя которого хотите удалить: ");
        try {
            String strUuid = sysIn.nextLine();
            UUID uuid = UUID.fromString(strUuid);
            userDao.remove(uuid);
        } catch (IllegalArgumentException e) {
            logger.error(WRONG_ID, e.getMessage(), e);
        }
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
