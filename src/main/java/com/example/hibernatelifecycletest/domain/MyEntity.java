package com.example.hibernatelifecycletest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Setter
@ToString
public class MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @PrePersist
    public void beforePersist() {
        log.info("Before persisting: " + this);
    }

    @PostPersist
    public void afterPersist() {
        log.info("After persisting: " + this);
    }

    @PostLoad
    public void afterLoad() {
        log.info("After loading: " + this);
    }

    @PreUpdate
    public void beforeUpdate() {
        log.info("Before updating: " + this);
    }

    @PostUpdate
    public void afterUpdate() {
        log.info("After updating: " + this);
    }

    @PreRemove
    public void beforeRemove() {
        log.info("Before removing: " + this);
    }

    @PostRemove
    public void afterRemove() {
        log.info("After removing: " + this);
    }
}
