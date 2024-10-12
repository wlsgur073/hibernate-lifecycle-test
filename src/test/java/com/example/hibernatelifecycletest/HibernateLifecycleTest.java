package com.example.hibernatelifecycletest;

import com.example.hibernatelifecycletest.domain.MyEntity;
import com.example.hibernatelifecycletest.repository.MyEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HibernateLifecycleTest {
    @Autowired
    private MyEntityRepository myEntityRepository;

    @DisplayName("test Hibernate entity life cycle")
    @Test
    void testHibernateLifecycle() {
        // Create
        MyEntity myEntity = new MyEntity();
        myEntity.setName("John Doe");
        myEntityRepository.save(myEntity); // PrePersist, PostPersist

        // Update
        myEntity.setName("Jane Doe");
        myEntityRepository.save(myEntity); // PreUpdate, PostUpdate

        // Load
        MyEntity loadedEntity = myEntityRepository.findById(myEntity.getId()).orElseThrow(); // PostLoad

        // Delete
        myEntityRepository.delete(loadedEntity); // PreRemove, PostRemove
    }
}
