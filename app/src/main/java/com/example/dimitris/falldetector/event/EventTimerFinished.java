package com.example.dimitris.falldetector.event;

public class EventTimerFinished {
    private String event;

    public EventTimerFinished(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
