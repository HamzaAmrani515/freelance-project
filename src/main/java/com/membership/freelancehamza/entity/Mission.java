package com.membership.freelancehamza.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable=false, length=200)
    private String title;

    public Mission() {}

    public Mission(String title) {
        this.title = title;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
}
