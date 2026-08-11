package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.BatchMockEventRequest;
import com.membership.freelancehamza.dto.MockEventRequest;
import com.membership.freelancehamza.dto.MockEventResponse;
import com.membership.freelancehamza.service.MockEventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock")
@CrossOrigin
public class MockEventController {

    private final MockEventService service;

    public MockEventController(MockEventService service) {
        this.service = service;
    }

    @PostMapping("/event")
    public MockEventResponse mockEvent(@Valid @RequestBody MockEventRequest request) {
        return service.createMockEvent(request);
    }

    @PostMapping("/events/batch")
    public List<MockEventResponse> mockEventsBatch(@RequestBody BatchMockEventRequest request) {
        return service.createMockEventsBatch(request);
    }
}