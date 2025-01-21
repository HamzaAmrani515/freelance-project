package com.example.freelance.controller;

import com.example.freelance.model.Freelancer;
import com.example.freelance.service.FreelanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/freelances")
public class FreelanceController {
    private final FreelanceService freelanceService;

    @GetMapping
    public List<Freelancer> getAllFreelances() {
        return freelanceService.getAllFreelances();
    }

    @GetMapping("/{id}")
    public Freelancer getFreelanceById(@PathVariable Long id) {
        return freelanceService.getFreelanceById(id);
    }

    @PostMapping
    public Freelancer saveFreelance(@RequestBody Freelancer freelance) {
        return freelanceService.saveFreelance(freelance);
    }

    @DeleteMapping("/{id}")
    public void deleteFreelance(@PathVariable Long id) {
        freelanceService.deleteFreelance(id);
    }

    @DeleteMapping("/test/{name}")
    public void deleteFreelanceByName(@PathVariable String name) {

    }

    @PutMapping("/{id}")
    public Freelancer updateFreelance(@PathVariable Long id, @RequestBody Freelancer freelance) {
        return freelanceService.updateFreelance(id, freelance);
    }
}
