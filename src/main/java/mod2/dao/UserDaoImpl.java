package mod2.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import mod2.entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Class defining data operations that uses the Hibernate Session to perform operations*/
public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(User user) {
        executeWithSession(sessionFactory, session -> session.persist(user));
    }

    @Override
    public User get(Long id) {
        List<User> singletonList = executeReadWithSession(sessionFactory, session -> {
            User currentUser = session.get(User.class, id);
            if (currentUser == null) {
                System.out.printf("Пользователя с id %d не имеется\n", id);
            }
            return Collections.singletonList(currentUser);
        });
        return Objects.requireNonNull(singletonList).get(0);
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
    public void remove(Long id) {
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
            System.err.printf("Причина исключения: %s", cause.getMessage());
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void executeWithSession(SessionFactory sessionFactory, EntityModAction action) {
        Transaction tr = null;
        try (Session session = sessionFactory.openSession()) {
            tr = session.beginTransaction();
            action.execute(session); // what we need to execute
            tr.commit();
        } catch (ConstraintViolationException e) {
            // Handle specific constraint violation
            if (tr != null && tr.isActive()) tr.rollback();
            e.printStackTrace();
        } catch (JDBCConnectionException e) {
            // Handle connection issues
            if (tr != null && tr.isActive()) tr.rollback();
            // Inspect the exact SQL error code and message from the database.
            Throwable cause = e;
            while (cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            }
            System.err.printf("Причина исключения: %s", cause.getMessage());
            cause.printStackTrace();
        } catch (HibernateException e) {
            // Catch-all for other Hibernate exceptions
            if (tr != null && tr.isActive()) tr.rollback();
            e.printStackTrace();
        }
    }
}
