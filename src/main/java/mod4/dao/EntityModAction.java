package mod4.dao;

import org.hibernate.Session;

public interface EntityModAction {

    void execute(Session session);
}
