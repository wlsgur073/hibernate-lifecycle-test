package com.example.hibernatelifecycletest.config;

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

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Hibernate can build service registry via cfg.xml and/or properties
                registry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml")
                        .build();

                // entity field and relation mappings can be defined in cfg.xml files or by annotation
                // for xml configured mapping classes, they can only be added to metadata via xml file
                // for annotated mapping entities, they can be added programmatically
                MetadataSources metadataSources = new MetadataSources(registry);
//                        .addAnnotatedClass(MyEntity.class);

                Metadata metadata = metadataSources.getMetadataBuilder().build();

                // Create SessionFactory
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
