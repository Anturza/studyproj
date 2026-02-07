package mod2.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import mod2.entities.User;
import mod2.exceptions.UserNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Class defining data operations that uses the Hibernate Session to perform operations*/
public class UserDaoImpl implements UserDao {

    private static SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        UserDaoImpl.sessionFactory = sessionFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User create(User user) {
        executeWithSession(sessionFactory, session -> session.persist(user));
        return user;
    }

    @Override
    public User get(UUID id) {
        List<User> singletonList = executeReadWithSession(sessionFactory, session -> {
            User currentUser = session.get(User.class, id);
            if (currentUser == null) {
                System.out.printf("Пользователя с id %s не имеется\n", id);
            }
            return Collections.singletonList(currentUser);
        });
        if (singletonList == null) {
            throw new UserNotFoundException("User not present");
        }
        return singletonList.get(0);
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
    public void update(User user) {
        executeWithSession(sessionFactory, session -> session.merge(user));
    }

    @Override
    public void remove(UUID id) {
        executeWithSession(sessionFactory, session -> {
            User user = session.get(User.class, id);
            if (user == null) {
                System.out.println("Нечего удалять");
                return;
            }
            session.remove(user);
            System.out.printf("%s был удалён\n", user);
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
                logger.error ("Проблема нарушения органичений SQL: {}",e.getMessage());
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
