package com.example.freelance.service;

import com.example.freelance.model.Freelance;

import java.util.List;

public interface FreelanceService {
    List<Freelance> getAllFreelances();

    Freelance getFreelanceById(Long id);

    Freelance saveFreelance(Freelance freelance);

    void deleteFreelance(Long id);

    Freelance updateFreelance(Long id, Freelance freelance);
}
