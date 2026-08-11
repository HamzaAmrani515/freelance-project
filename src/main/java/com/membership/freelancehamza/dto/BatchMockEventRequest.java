package com.membership.freelancehamza.dto;

import java.util.List;

public record BatchMockEventRequest(List<MockEventRequest> events) {
}