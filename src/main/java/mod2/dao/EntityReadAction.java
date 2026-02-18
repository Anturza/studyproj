package mod2.dao;

import mod2.entities.User;
import org.hibernate.Session;

import java.util.List;
@FunctionalInterface
public interface EntityReadAction {

    List<User> execute(Session session);
}
