package com.example.freelance.controller;

import com.example.freelance.model.MissionFreelance;
import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {
    @Autowired
    private FreelanceService freelanceService;

    private final MissionService missionService;


}
