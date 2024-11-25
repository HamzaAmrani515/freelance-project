package com.example.freelance.controller;
//ce que voie le front

import com.example.freelance.model.FreelanceModel;
import com.example.freelance.repository.FreelanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelances")
public class FreelanceController {
    @Autowired //pour instancier freelancerepository
    private FreelanceRepository freelanceRepository;

    @GetMapping
    public List<FreelanceModel> findAllFreelance() {
        return freelanceRepository.findAll();
    }

    @PostMapping
    public FreelanceModel createFreelance(FreelanceModel freelanceModel) {
        return freelanceRepository.save(freelanceModel);
    }
    @DeleteMapping
    public void deleteFreelance (Long id){
        freelanceRepository.deleteById(id);
    }
    @PutMapping
    public FreelanceModel updateFreelance(FreelanceModel freelanceModel){
        return freelanceRepository.save(freelanceModel);
    }

}
