package mod2.dao;

import mod2.Entities.User;
import org.hibernate.Session;

import java.util.List;

public interface EntityReadAction {

    List<User> execute(Session session);
}
