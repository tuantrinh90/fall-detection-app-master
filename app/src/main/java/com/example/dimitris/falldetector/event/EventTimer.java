package com.example.dimitris.falldetector.event;

public class EventTimer {

    private int minutes;
    private int second;

    public EventTimer(int minutes, int second) {
        this.minutes = minutes;
        this.second = second;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
