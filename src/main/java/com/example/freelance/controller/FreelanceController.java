package com.example.freelance.controller;

import com.example.freelance.model.Freelance;
import com.example.freelance.service.FreelanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelances")
public class FreelanceController {
    @Autowired
    private FreelanceService freelanceService;

    @GetMapping
    public List<Freelance> getAllFreelances() {
        return freelanceService.getAllFreelances();
    }

    @GetMapping("/{id}")
    public Freelance getFreelanceById(@PathVariable Long id) {
        return freelanceService.getFreelanceById(id);
    }

    @PostMapping
    public Freelance saveFreelance(@RequestBody Freelance freelance) {
        return freelanceService.saveFreelance(freelance);
    }

    @DeleteMapping("/{id}")
    public void deleteFreelance(@PathVariable Long id) {
        freelanceService.deleteFreelance(id);
    }

    @PutMapping("/{id}")
    public Freelance updateFreelance(@PathVariable Long id, @RequestBody Freelance freelance) {
        return freelanceService.updateFreelance(id, freelance);
    }

}
