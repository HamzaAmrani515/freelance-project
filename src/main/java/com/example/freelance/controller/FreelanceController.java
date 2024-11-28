package com.example.freelance.controller;

import com.example.freelance.model.Freelance;
import com.example.freelance.repository.FreelanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/freelances")
public class FreelanceController {
    @Autowired
    private FreelanceRepository freelanceRepository;

    @GetMapping
    public List<Freelance> getAllFreelances() {
        return freelanceRepository.findAll();
    }

    @GetMapping("/{id}")
    public Freelance getFreelanceById(@PathVariable Long id) {
        return freelanceRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Freelance saveFreelance(@RequestBody Freelance freelance) {
        return freelanceRepository.save(freelance);
    }

    @DeleteMapping("/{id}")
    public void deleteFreelance(@PathVariable Long id) {
        freelanceRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Freelance updateFreelance(@PathVariable Long id, @RequestBody Freelance freelance) {
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
