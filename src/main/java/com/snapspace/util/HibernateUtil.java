package com.snapspace.util;

import java.util.Properties;

import com.snapspace.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Utility class for managing the Hibernate {@link SessionFactory}.
 *
 * <p>
 * Provides a singleton {@link SessionFactory} built once at class-load time
 * using a static initializer block. This is inherently thread-safe — the JVM
 * guarantees that static initializers run exactly once, even under concurrent
 * access, so no synchronization is needed here.
 * </p>
 *
 * <p>
 * The previous lazy {@code if (sessionFactory == null)} pattern was a race
 * condition: two threads could both see {@code null} simultaneously and each
 * build their own factory, leaking connections and causing unpredictable behaviour.
 * The static initializer eliminates that entirely.
 * </p>
 *
 * <p>
 * Node.js equivalent: a module-level singleton — the first {@code require()}
 * runs the setup code; every subsequent {@code require()} gets the cached export.
 * </p>
 */
public class HibernateUtil {

    /**
     * Singleton SessionFactory — built once when this class is first loaded.
     * Static initializers are thread-safe by the Java Language Specification (JLS §12.4).
     */
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration cfg = new Configuration();

            Properties props = new Properties();
            props.put(Environment.DRIVER, "org.postgresql.Driver");
            props.put(Environment.URL, PropertiesUtil.get("db.url"));
            props.put(Environment.USER, PropertiesUtil.get("db.user"));
            props.put(Environment.PASS, PropertiesUtil.get("db.password"));
            props.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
            props.put(Environment.HBM2DDL_AUTO, "update");

            // Driven by config so it can be turned off without touching code.
            // Set show_sql=true in config.properties for development, false otherwise.
            props.put(Environment.SHOW_SQL, PropertiesUtil.get("hibernate.show_sql", "false"));

            cfg.setProperties(props);

            cfg.addAnnotatedClass(User.class);
            cfg.addAnnotatedClass(ImagePost.class);
            cfg.addAnnotatedClass(Board.class);
            cfg.addAnnotatedClass(Comment.class);
            cfg.addAnnotatedClass(Like.class);
            cfg.addAnnotatedClass(Community.class);
            cfg.addAnnotatedClass(CommunityMember.class);
            cfg.addAnnotatedClass(CommunityPost.class);
            cfg.addAnnotatedClass(CommunityMessage.class);

            ServiceRegistry sr = new StandardServiceRegistryBuilder()
                    .applySettings(cfg.getProperties())
                    .build();

            sessionFactory = cfg.buildSessionFactory(sr);

        } catch (Exception e) {
            // If the factory can't be built the app can't run — fail loudly at startup
            // rather than silently producing NullPointerExceptions later.
            throw new ExceptionInInitializerError("Failed to build Hibernate SessionFactory: " + e.getMessage());
        }
    }

    /**
     * Returns the singleton {@link SessionFactory}.
     *
     * @return the application-wide Hibernate SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
