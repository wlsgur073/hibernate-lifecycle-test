package com.example.hibernatelifecycletest.config;

import com.example.hibernatelifecycletest.entity.MyEntity;
import com.example.hibernatelifecycletest.interceptor.CustomHibernateInterceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    /**
     * Creates and returns a singleton SessionFactory instance.
     *
     * This method initializes Hibernate using the configuration defined in `hibernate.cfg.xml`
     * and programmatically adds annotated entity classes. It also applies a custom interceptor.
     * The method uses a singleton pattern to ensure that only one SessionFactory instance is created.
     *
     * @return SessionFactory The Hibernate SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // StandardServiceRegistryBuilder:
                // This class is responsible for configuring and building the StandardServiceRegistry.
                // The registry is the primary service locator for Hibernate, and it includes settings
                // such as database connection, dialect, and other configurations. It can be built
                // either from a configuration file (hibernate.cfg.xml) or programmatically.
                registry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml")
                        .build();

                // MetadataSources:
                // This class gathers the mapping information (like annotated entity classes) and other
                // metadata required by Hibernate to map Java objects to database tables.
                // It collects this information from both XML (hibernate.cfg.xml) and programmatically added classes.
                MetadataSources metadataSources = new MetadataSources(registry)
                        .addAnnotatedClass(MyEntity.class);

                // Metadata:
                // Metadata contains the complete information regarding the ORM mappings, entity classes,
                // database settings, and other Hibernate-specific configurations.
                // This class is responsible for processing all the metadata collected from MetadataSources.
                Metadata metadata = metadataSources.getMetadataBuilder().build();

                // Create the SessionFactory with the metadata and apply the custom interceptor
                sessionFactory = metadata.getSessionFactoryBuilder()
                        .applyInterceptor(new CustomHibernateInterceptor())
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
