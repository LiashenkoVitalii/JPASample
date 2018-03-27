package ua.kiev.prog.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ua.kiev.prog.Client;
import ua.kiev.prog.Course;

public abstract class HibernateFactoryUtil {

    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.H2Dialect";
    private static final String CONNECTION_DRIVER_CLASS = "org.h2.Driver";
    private static final String CONNECTION_URL = "jdbc:h2:~/test";
    private static final String CONNECTION_USERNAME = "SA";
    private static final String CONNECTION_PASSWORD = "";
    private static final String IS_CONNECTION_AUTOCOMMIT = "true";
    private static final String IS_SSL = "false";
    private static final String IS_HIBERNATE_SHOW_SQL = "false";
    private static final String HIBERNATE_HBM2DDL_AUTO = "update";
    private static SessionFactory sessionFactory;

    private static Configuration getMySqlConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Course.class);

        configuration.setProperty("hibernate.dialect", HIBERNATE_DIALECT);
        configuration.setProperty("hibernate.connection.driver_class", CONNECTION_DRIVER_CLASS);
        configuration.setProperty("hibernate.connection.url", CONNECTION_URL);
        configuration.setProperty("hibernate.connection.username", CONNECTION_USERNAME);
        configuration.setProperty("hibernate.connection.password", CONNECTION_PASSWORD);
        configuration.setProperty("hibernate.connection.autocommit", IS_CONNECTION_AUTOCOMMIT);
        configuration.setProperty("hibernate.connection.sessionVariables", "sql_mode=\'\'");
        configuration.setProperty("useSSL", IS_SSL);
        configuration.setProperty("hibernate.show_sql", IS_HIBERNATE_SHOW_SQL);
        configuration.setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO);
        return configuration;
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    /**
     * @return
     * @throws HibernateException
     */
    private static SessionFactory configureSessionFactory() throws HibernateException {
        Configuration configuration = getMySqlConfiguration();
        sessionFactory = createSessionFactory(configuration);
        return sessionFactory;
    }

    /**
     * Constructs a new Singleton SessionFactory
     *
     * @return
     * @throws HibernateException
     */
    public static SessionFactory buildSessionFactory() throws HibernateException {
        if (sessionFactory != null) {
            closeFactory();
        }
        return configureSessionFactory();
    }

    /**
     * Builds a SessionFactory, if it hasn't been already.
     */
    public static SessionFactory buildIfNeeded(){
        if (sessionFactory != null) {
            return sessionFactory;
        }
        try {
            return configureSessionFactory();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session openSession() throws HibernateException {
        buildIfNeeded();
        return sessionFactory.openSession();
    }

    private static void closeFactory() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
            } catch (HibernateException ignored) {
                System.out.println("Couldn't close SessionFactory");
            }
        }
    }

    public static void close(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (HibernateException ignored) {
                System.out.println("Couldn't close Session");
            }
        }
    }

    public static void rollback(Transaction tx) {
        try {
            if (tx != null) {
                tx.rollback();
            }
        } catch (HibernateException ignored) {
            System.out.println("Couldn't rollback Transaction");
        }
    }
}
