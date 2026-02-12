package mod4.util;

import mod4.model.Name;
import mod4.model.User;

import java.time.LocalDateTime;

/**
 * This class is a logic layer of an application and handles operations related to user data , such as creating,
 * modifying and validating user data
 */
public class UserUtil {

    public static User createUser(String name, int age, String email) {
        String nameToBeParsed = name.trim();
        Name currName = parseName(nameToBeParsed.split(" "));
        return new User(currName, email, age, LocalDateTime.now());

    }

    private static Name parseName(String[] userData) {
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
}
