package com.example.hibernatelifecycletest.config;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class HibernateConfigTest {

    @Test
    public void testSessionFactoryCreation() {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();

        assertNotNull(sessionFactory, "SessionFactory should be created and not null.");
    }
}