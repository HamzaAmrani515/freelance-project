package com.example.freelance.repository;

import com.example.freelance.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByFreelancerId(Long freelancerId);

    List<Notification> findByFreelancerIdOrderByTimestampDesc(Long freelancerId);

    @Query("select count(n) from Notification n where n.freelancer.id = :freelancerId and n.isRead = false")
    long countUnreadByFreelancerId(@Param("freelancerId") Long freelancerId);
}
