package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.CreateEvaluationRequest;
import com.membership.freelancehamza.dto.EvaluationResponse;
import com.membership.freelancehamza.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin
public class EvaluationController {

    private final EvaluationService service;

    public EvaluationController(EvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public EvaluationResponse createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        return service.createEvaluation(request);
    }
}