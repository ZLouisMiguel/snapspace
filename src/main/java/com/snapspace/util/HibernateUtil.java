package com.snapspace.util;

import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.snapspace.model.User;
import com.snapspace.model.ImagePost;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            Configuration cfg = new Configuration();

            Properties props = new Properties();
            props.put(Environment.DRIVER, "org.postgresql.Driver");
            props.put(Environment.URL, "jdbc:postgresql://localhost:5432/snapspace");
            props.put(Environment.USER, "postgres");
            props.put(Environment.PASS, "1234");
            props.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
            props.put(Environment.HBM2DDL_AUTO, "update");
            props.put(Environment.SHOW_SQL, true);

            cfg.setProperties(props);

            cfg.addAnnotatedClass(User.class);
            cfg.addAnnotatedClass(ImagePost.class);

            ServiceRegistry sr =
                    new StandardServiceRegistryBuilder()
                            .applySettings(cfg.getProperties())
                            .build();

            sessionFactory = cfg.buildSessionFactory(sr);
        }

        return sessionFactory;
    }
}