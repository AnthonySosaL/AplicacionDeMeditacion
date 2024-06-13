package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Progress extends AppCompatActivity {
    private int userId;
    private CalendarView calendarView;
    private RecyclerView activitiesRecyclerView;
    private UnifiedSessionAdapter unifiedSessionAdapter;
    private DatabaseHelper db;

    private ImageButton btnRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        userId = getIntent().getIntExtra("USER_ID", -1);
        calendarView = findViewById(R.id.calendarView);
        activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView);
        db = new DatabaseHelper(this);
        btnRegresar = findViewById(R.id.regresarP);
        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = formatDate(year, month, dayOfMonth);
            loadSessions(selectedDate);
        });
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
    }

    private void loadSessions(String date) {
        List<UnifiedSession> unifiedSessions = new ArrayList<>();

        List<MeditationSession> meditationSessions = db.getSessionsForDate(userId, date);
        for (MeditationSession session : meditationSessions) {
            unifiedSessions.add(new UnifiedSession(UnifiedSession.TYPE_MEDITATION, session, null));
        }

        List<BreathingSession> breathingSessions = db.getBreathingSessionsForDate(userId, date);
        for (BreathingSession session : breathingSessions) {
            unifiedSessions.add(new UnifiedSession(UnifiedSession.TYPE_BREATHING, null, session));
        }

        unifiedSessionAdapter = new UnifiedSessionAdapter(unifiedSessions);
        activitiesRecyclerView.setAdapter(unifiedSessionAdapter);
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new java.util.Date(year - 1900, month, dayOfMonth));
    }
    private void regresar() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
}
