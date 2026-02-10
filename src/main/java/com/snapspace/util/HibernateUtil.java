package com.snapspace.util;

import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.snapspace.model.User;
import com.snapspace.model.ImagePost;

/**
 * Utility class for managing the Hibernate {@link SessionFactory}.
 * <p>
 * This class provides a singleton {@link SessionFactory} instance that can be used
 * throughout the application to interact with the database using Hibernate ORM.
 * It configures Hibernate with PostgreSQL and maps the application's entity classes.
 * </p>
 */
public class HibernateUtil {

    /**
     * Singleton instance of Hibernate SessionFactory
     */
    private static SessionFactory sessionFactory;

    /**
     * Returns the singleton {@link SessionFactory} instance.
     * <p>
     * If the SessionFactory is not yet initialized, it will configure Hibernate,
     * register entity classes, and build the SessionFactory.
     * </p>
     *
     * @return the Hibernate {@link SessionFactory} instance
     */
    public static SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            // Create Hibernate Configuration object
            Configuration cfg = new Configuration();

            // Set Hibernate properties
            Properties props = new Properties();
            props.put(Environment.DRIVER, "org.postgresql.Driver");
            props.put(Environment.URL, "jdbc:postgresql://localhost:5432/snapspace");
            props.put(Environment.USER, "postgres");
            props.put(Environment.PASS, "1234");
            props.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
            props.put(Environment.HBM2DDL_AUTO, "update"); // Auto-create/update tables
            props.put(Environment.SHOW_SQL, true);         // Show SQL in console

            cfg.setProperties(props);

            // Register annotated entity classes
            cfg.addAnnotatedClass(User.class);
            cfg.addAnnotatedClass(ImagePost.class);

            // Build the ServiceRegistry and SessionFactory
            ServiceRegistry sr = new StandardServiceRegistryBuilder()
                    .applySettings(cfg.getProperties())
                    .build();

            sessionFactory = cfg.buildSessionFactory(sr);
        }

        return sessionFactory;
    }
}
