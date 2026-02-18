package mod2.dao;

import org.hibernate.Session;
@FunctionalInterface
public interface EntityModAction {

        void execute(Session session);

}
