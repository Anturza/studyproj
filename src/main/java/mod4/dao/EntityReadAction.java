package mod4.dao;

import mod4.model.User;
import org.hibernate.Session;

import java.util.List;

public interface EntityReadAction {

    List<User> execute(Session session);
}
