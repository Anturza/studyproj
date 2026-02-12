package mod4.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import mod4.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Class defining data operations that uses the Hibernate Session to perform operations
 */
@Component
public class UserDaoImpl implements UserDao {

    private static SessionFactory sessionFactory;

    @Autowired
    public UserDaoImpl(SessionFactory sessionFactory) {
        UserDaoImpl.sessionFactory = sessionFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Transactional
    @Override
    public void create(User user) {
        Session session = sessionFactory.openSession();
        session.persist(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User get(UUID id) {
        Session session = sessionFactory.openSession();
        return session.get(User.class, id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAll() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> critQuery = builder.createQuery(User.class);
        Root<User> root = critQuery.from(User.class);
        critQuery.select(root);
        return session.createQuery(critQuery).getResultList();
    }
    @Transactional
    @Override
    public void update(UUID id, User user) {
        Session session = sessionFactory.openSession();
        User userToBeUpdated = session.get(User.class, id);
        userToBeUpdated.setName(user.getName());
        userToBeUpdated.setEmail(user.getEmail());
        userToBeUpdated.setAge(user.getAge());

    }
    @Transactional
    @Override
    public void remove(UUID id) {
        Session session = sessionFactory.openSession();
        session.remove(session.get(User.class, id));
    }
}
