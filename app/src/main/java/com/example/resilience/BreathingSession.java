package com.example.resilience;

public class BreathingSession {
    private String selectedTime;
    private String completedTime;
    private int exhaleCount;
    private int inhaleCount;
    private int completed;
    private String date;

    public BreathingSession(String selectedTime, String completedTime, int exhaleCount, int inhaleCount, int completed, String date) {
        this.selectedTime = selectedTime;
        this.completedTime = completedTime;
        this.exhaleCount = exhaleCount;
        this.inhaleCount = inhaleCount;
        this.completed = completed;
        this.date = date;
    }

    // Getters
    public String getSelectedTime() {
        return selectedTime;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public int getExhaleCount() {
        return exhaleCount;
    }

    public int getInhaleCount() {
        return inhaleCount;
    }

    public int getCompleted() {
        return completed;
    }

    public String getDate() {
        return date;
    }
}

