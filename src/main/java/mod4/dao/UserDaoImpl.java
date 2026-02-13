package mod4.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import mod4.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
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

    @Override
    public void create(User user) {
        executeWithSession(sessionFactory, session -> {
            session.persist(user);
        });
    }

    @Override
    public User get(UUID id) {
        return executeReadWithSession(sessionFactory, session -> {
            return Collections.singletonList(session.get(User.class, id));
        }).stream().findAny().orElse(null);
    }

    @Override
    public List<User> getAll() {
        return executeReadWithSession(sessionFactory, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> critQuery = builder.createQuery(User.class);
            Root<User> root = critQuery.from(User.class);
            critQuery.select(root);
            return session.createQuery(critQuery).getResultList();
        });
    }

    @Override
    public void update(UUID id, User user) {
        executeWithSession(sessionFactory, session -> {
            User userToBeUpdated = session.get(User.class, id);
            userToBeUpdated.setName(user.getName());
            userToBeUpdated.setEmail(user.getEmail());
            userToBeUpdated.setAge(user.getAge());
        });
    }

    @Override
    public void remove(UUID id) {
        executeWithSession(sessionFactory, session -> {
            session.remove(session.get(User.class, id));
        });
    }

    public static List<User> executeReadWithSession(SessionFactory sessionFactory, EntityReadAction action) {
        try (Session session = sessionFactory.openSession()) {
            return action.execute(session);
        } catch (JDBCConnectionException e) {
            Throwable cause = e;
            while (cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            }
            logger.error("Причина JDBC исключения (чтение из базы данных): {}", cause.getMessage());
            return null;
        } catch (HibernateException e) {
            logger.error("Hibernate исключение (чтение из базы данных): {}", e.getMessage());
            return null;
        }
    }

    public static void executeWithSession(SessionFactory sessionFactory, EntityModAction action) {
        Transaction tr = null;
        try (Session session = sessionFactory.openSession()) {
            tr = session.beginTransaction();
            action.execute(session); // what we need to execute
            tr.commit();
        } catch (ConstraintViolationException e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
                logger.error("Проблема нарушения органичений SQL: {}", e.getMessage());
            }
        } catch (JDBCConnectionException e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
                Throwable cause = e;
                while (cause.getCause() != null && cause.getCause() != cause) {
                    cause = cause.getCause();
                }
                logger.error("Причина JDBC исключения (запись в базу данных): {}", cause.getMessage(), cause);
            }
        } catch (HibernateException e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
                logger.error("Hibernate исключение (запись в базу данных): {}", e.getMessage());
            }
        }
    }
}
