package com.example.resilience;

public class MeditationSession {
    private String duration; // Cambiado a String
    private int completed;
    private String date;
    private String reflections;
    private String selectedTime;

    public MeditationSession(String duration, int completed, String date, String reflections, String selectedTime) {
        this.duration = duration;
        this.completed = completed;
        this.date = date;
        this.reflections = reflections;
        this.selectedTime = selectedTime;
    }

    // Getters
    public String getDuration() {
        return duration;
    }

    public int getCompleted() {
        return completed;
    }

    public String getDate() {
        return date;
    }

    public String getReflections() {
        return reflections;
    }

    public String getSelectedTime() {
        return selectedTime;
    }
}

