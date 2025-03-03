package com.example.freelance.repository;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByFreelancerId(Long freelancerId);

}
