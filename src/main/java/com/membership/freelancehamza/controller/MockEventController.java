package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.BatchMockEventRequest;
import com.membership.freelancehamza.dto.MockEventRequest;
import com.membership.freelancehamza.dto.MockEventResponse;
import com.membership.freelancehamza.service.MockEventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;



// ce mock est utilise generalement pour generer les evenments de test
@RestController
@RequestMapping("/api/mock")
@CrossOrigin
public class MockEventController {

    private final MockEventService service;

    public MockEventController(MockEventService service) {
        this.service = service;
    }
// ca pour creerv un seul evenement  de test
    @PostMapping("/event")
    public MockEventResponse mockEvent(@Valid @RequestBody MockEventRequest request) {
        return service.createMockEvent(request);
    }
// ca pour creer plusieurs evenments de test ujne seul fois
    @PostMapping("/events/batch")
    public List<MockEventResponse> mockEventsBatch(@RequestBody BatchMockEventRequest request) {
        return service.createMockEventsBatch(request);
    }
}