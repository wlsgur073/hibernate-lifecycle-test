package com.example.hibernatelifecycletest.repository;

import com.example.hibernatelifecycletest.domain.MyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {
}
