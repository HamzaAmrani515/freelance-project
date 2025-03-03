package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionFreelanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Assala Hamoudi
 */
@Service
@RequiredArgsConstructor
public class FreelanceService {
    private final FreelancerRepository freelanceRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;


    
    public List<Freelancer> getAllFreelances() {
        return freelanceRepository.findAll();
    }

    
    public Freelancer getFreelanceById(Long id) {
        return freelanceRepository.findById(id).orElse(null);
    }

    
    public Freelancer saveFreelance(Freelancer freelance) {
        return freelanceRepository.save(freelance);
    }

    
    public void deleteFreelance(Long id) {
        freelanceRepository.deleteById(id);
    }

    
    public Freelancer updateFreelance(Long id, Freelancer freelance) {
        Optional<Freelancer> existingFreelancer = freelanceRepository.findById(id);
        if (existingFreelancer.isPresent()) {
            Freelancer getExistingFreelancer = existingFreelancer.get();
            getExistingFreelancer.setNom(freelance.getNom());
            getExistingFreelancer.setPrenom(freelance.getPrenom());
            getExistingFreelancer.setEmail(freelance.getEmail());
            return freelanceRepository.save(getExistingFreelancer);
        }
        return null;
    }

    public List<Freelancer> getAllFreelancers() {
        return freelanceRepository.findAll();
    }
    public boolean isFreelancerAvailable(Freelancer freelancer) {


        return missionFreelanceRepository.findByFreelancerId(freelancer.getId()).isEmpty();
    }
}
