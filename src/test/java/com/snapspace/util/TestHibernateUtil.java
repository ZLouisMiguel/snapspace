package com.snapspace.util;

import com.snapspace.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Properties;

/**
 * Test-only Hibernate SessionFactory.
 *
 * <p>
 * Defaults to an in-memory H2 database so running tests cannot mutate the
 * developer's local Postgres schema. A real test DB can be provided via system
 * properties if needed later.
 * </p>
 */
public final class TestHibernateUtil {

    private static final SessionFactory sessionFactory = build();

    private TestHibernateUtil() {}

    private static SessionFactory build() {
        Configuration cfg = new Configuration();

        Properties props = new Properties();

        // Default safe in-memory DB (driver/dialect provided as strings).
        props.put(Environment.DRIVER, System.getProperty("snapspace.test.db.driver", "org.h2.Driver"));
        props.put(Environment.URL, System.getProperty("snapspace.test.db.url", "jdbc:h2:mem:snapspace_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"));
        props.put(Environment.USER, System.getProperty("snapspace.test.db.user", "sa"));
        props.put(Environment.PASS, System.getProperty("snapspace.test.db.password", ""));
        props.put(Environment.DIALECT, System.getProperty("snapspace.test.db.dialect", "org.hibernate.dialect.H2Dialect"));

        props.put(Environment.HBM2DDL_AUTO, System.getProperty("snapspace.test.hbm2ddl", "create-drop"));
        props.put(Environment.SHOW_SQL, System.getProperty("snapspace.test.show_sql", "true"));

        cfg.setProperties(props);

        // Keep mappings aligned with production.
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

        return cfg.buildSessionFactory(sr);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

