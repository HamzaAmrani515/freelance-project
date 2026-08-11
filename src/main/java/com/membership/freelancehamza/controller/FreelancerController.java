package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.FreelancerDetailDto;
import com.membership.freelancehamza.service.FreelancerDetailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/freelancers")
@CrossOrigin
public class FreelancerController {

    private final FreelancerDetailService service;

    public FreelancerController(FreelancerDetailService service) {
        this.service = service;
    }

    @GetMapping("/{id}/details")
    public FreelancerDetailDto getDetails(@PathVariable Long id) {
        return service.getFreelancerDetails(id);
    }
}