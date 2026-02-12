package mod4.dao;

import mod4.model.User;

import java.util.List;
import java.util.UUID;

/** Interface defines a contract for performing CRUD (Create, Read, Update, Delete) operations on a User entity,
 * abstracting the underlying data access mechanism (Hibernate) from the business logic.*/

public interface UserDao {

    void create(User user);

    User get(UUID id);

    List<User> getAll();

    void update(UUID id, User user);

    void remove(UUID id);
}
