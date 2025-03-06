package com.example.freelance.event;

import com.example.freelance.model.Mission;
import org.springframework.context.ApplicationEvent;

public class MissionCreatedEvent extends ApplicationEvent {
    private final Mission mission;

    public MissionCreatedEvent(Object source, Mission mission) {
        super(source);
        this.mission = mission;
    }

    public Mission getMission() {
        return mission;
    }
}
