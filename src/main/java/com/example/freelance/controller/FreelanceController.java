package com.example.freelance.controller;

import com.example.freelance.model.FreelanceModel;
import com.example.freelance.repository.FreelanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelances")
public class FreelanceController {
    @Autowired
    private FreelanceRepository freelanceRepository;

    @GetMapping
    public List<FreelanceModel> getAllFreelances() {
        return freelanceRepository.findAll();
    }

    @GetMapping("/{id}")
    public FreelanceModel getFreelanceById( @PathVariable Long id) {
        return freelanceRepository.findById(id).orElse(null);
    }

    @PostMapping
    public FreelanceModel saveFreelance(FreelanceModel freelanceModel) {
        return freelanceRepository.save(freelanceModel);
    }

    @DeleteMapping
    public void deleteFreelance(Long id) {
        freelanceRepository.deleteById(id);
    }

    @PutMapping
    public FreelanceModel updateFreelance(FreelanceModel freelanceModel) {
        return freelanceRepository.save(freelanceModel);
    }

}
