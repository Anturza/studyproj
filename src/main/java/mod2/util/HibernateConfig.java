package mod2.util;

import mod2.entities.Name;
import mod2.entities.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public class HibernateConfig {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateConfig() {}

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(User.class).addAnnotatedClass(Name.class);
            return configuration.buildSessionFactory(new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties()).build());
        } catch (LinkageError ex) {
            System.err.printf("Проблема при создании SessionFactory: %s", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
