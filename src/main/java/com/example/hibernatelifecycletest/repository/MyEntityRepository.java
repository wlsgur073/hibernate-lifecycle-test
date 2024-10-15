package com.example.hibernatelifecycletest.repository;

import com.example.hibernatelifecycletest.entity.MyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {
}
