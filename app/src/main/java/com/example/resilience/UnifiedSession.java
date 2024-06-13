package com.example.resilience;

public class UnifiedSession {
    public static final int TYPE_MEDITATION = 0;
    public static final int TYPE_BREATHING = 1;

    private int type;
    private MeditationSession meditationSession;
    private BreathingSession breathingSession;

    public UnifiedSession(int type, MeditationSession meditationSession, BreathingSession breathingSession) {
        this.type = type;
        this.meditationSession = meditationSession;
        this.breathingSession = breathingSession;
    }

    public int getType() {
        return type;
    }

    public MeditationSession getMeditationSession() {
        return meditationSession;
    }

    public BreathingSession getBreathingSession() {
        return breathingSession;
    }
}


