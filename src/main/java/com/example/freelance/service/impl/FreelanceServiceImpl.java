package com.example.freelance.service.impl;

import com.example.freelance.model.Freelance;
import com.example.freelance.repository.FreelanceRepository;
import com.example.freelance.service.FreelanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FreelanceServiceImpl implements FreelanceService {
    @Autowired//
    private FreelanceRepository freelanceRepository;

    @Override
    public List<Freelance> getAllFreelances() {
        return freelanceRepository.findAll();
    }

    @Override
    public Freelance getFreelanceById(Long id) {
        return freelanceRepository.findById(id).orElse(null);
    }

    @Override
    public Freelance saveFreelance(Freelance freelance) {
        return freelanceRepository.save(freelance);
    }

    @Override
    public void deleteFreelance(Long id) {
        freelanceRepository.deleteById(id);
    }

    @Override
    public Freelance updateFreelance(Long id, Freelance freelance) {
        Optional<Freelance> existingFreelance = freelanceRepository.findById(id);
        if (existingFreelance.isPresent()) {
            Freelance getExistingFreelance = existingFreelance.get();
            getExistingFreelance.setNom(freelance.getNom());
            getExistingFreelance.setPrenom(freelance.getPrenom());
            getExistingFreelance.setEmail(freelance.getEmail());
            return freelanceRepository.save(getExistingFreelance);
        }
        return null;
    }
}
