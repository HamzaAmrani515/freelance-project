package com.membership.freelancehamza.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private MissionStatus status;

    public Mission() {
    }

    public Mission(String title) {
        this.title = title;
        this.status = MissionStatus.COMPLETED;
    }

    @PrePersist
    void onCreate() {
        if (status == null) {
            status = MissionStatus.COMPLETED;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }
}