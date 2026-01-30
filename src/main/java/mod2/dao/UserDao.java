package mod2.dao;

import mod2.entities.User;

import java.util.List;
/** Interface defines a contract for performing CRUD (Create, Read, Update, Delete) operations on a User entity,
 * abstracting the underlying data access mechanism (Hibernate) from the business logic.*/

public interface UserDao {

    void create(User user);

    User get(Long id);

    List<User> getAll();

    void update(User user);

    void remove(Long id);
}
