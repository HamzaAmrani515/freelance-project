package com.example.freelance.repository;
import com.example.freelance.model.MissionFreelance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionFreelanceRepository extends JpaRepository<MissionFreelance, Long> {


}
