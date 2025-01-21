package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreelanceService {
    private final FreelancerRepository freelanceRepository;

    
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
}
