package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageButton;
import android.media.MediaPlayer;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Breathing extends AppCompatActivity {
    private int userId;
    private MediaPlayer mediaPlayer;
    private Spinner spinnerBreathingTime;
    private Button btnFinishBreathing,btnStartStopBreathing ;
    private ImageButton btnRegresar;
    private TextView tvBreathingTimer, tvBreathingState;
    private Handler timerHandler = new Handler();
    private long startTime = 0;
    private boolean isBreathing = false;
    private int selectedTime = 0; // Tiempo de respiración en milisegundos



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing);

        userId = getIntent().getIntExtra("USER_ID", -1);

        // Inicializar componentes de la UI
        spinnerBreathingTime = findViewById(R.id.spinnerBreathingTime);
        btnStartStopBreathing = findViewById(R.id.btnStartStopBreathing);
        btnFinishBreathing = findViewById(R.id.btnFinishBreathing);
        btnRegresar = findViewById(R.id.regresarB);
        tvBreathingTimer = findViewById(R.id.tvBreathingTimer);
        tvBreathingState = findViewById(R.id.tvBreathingState);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });

        configureBreathingTimeSpinner();

        btnStartStopBreathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBreathing) {
                    startBreathing();
                } else {
                    pauseBreathing();
                }
            }
        });
        btnFinishBreathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBreathing();
            }
        });
    }
    private void regresar() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
    private void configureBreathingTimeSpinner() {
        List<String> breathingTimes = new ArrayList<>();
        for (int i = 3; i <= 20; i++) {
            breathingTimes.add(i + " minutos");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, breathingTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBreathingTime.setAdapter(adapter);
    }

    private void startBreathing() {
        if (!isBreathing) {
            // Configuración inicial de la respiración
            selectedTime = getSelectedTime();
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            btnStartStopBreathing.setText("Pausar Respiración");
            isBreathing = true;

            // Iniciar la reproducción del sonido relajante
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.relajante2); // Asegúrate de que el archivo se llame "relajante2.mp3"
                mediaPlayer.setLooping(true); // El sonido se reinicia automáticamente
            }
            mediaPlayer.start();
        }
    }

    private void pauseBreathing() {
        timerHandler.removeCallbacks(timerRunnable);
        btnStartStopBreathing.setText("Retomar Respiración");
        isBreathing = false;

        // Pausar la reproducción del sonido
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private int getSelectedTime() {
        String selectedItem = spinnerBreathingTime.getSelectedItem().toString();
        int time = Integer.parseInt(selectedItem.split(" ")[0]);
        return time * 60 * 1000; // Convertir a milisegundos
    }
    private long getSelectedTimeInMillis() {
        String selectedItem = spinnerBreathingTime.getSelectedItem().toString();
        int timeInMinutes = Integer.parseInt(selectedItem.split(" ")[0]);
        return timeInMinutes * 60 * 1000; // Convertir a milisegundos
    }


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBreathing) {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds %= 60;

                tvBreathingTimer.setText(String.format("%02d:%02d", minutes, seconds));

                // Aquí se puede implementar la lógica para alternar entre inhalar y exhalar
                alternateBreathingState();

                if (millis >= selectedTime) {
                    finishBreathing(); // Termina la respiración si se alcanza el tiempo seleccionado
                } else {
                    timerHandler.postDelayed(this, 500);
                }
            }
        }
    };
    private boolean isExhaling = false;
    private long lastBreathingChangeTime = 0;
    private final int breathingPhaseDuration = 5000; // Duración de cada fase (inhalar/exhalar) en milisegundos
    private int exhaleCount = 0;
    private int inhaleCount = 0;
    private void alternateBreathingState() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBreathingChangeTime > breathingPhaseDuration) {
            isExhaling = !isExhaling;
            tvBreathingState.setText(isExhaling ? "Exhalar" : "Inhalar");
            lastBreathingChangeTime = currentTime;

            // Incrementa los contadores solo si la sesión está activa
            if (isBreathing) {
                if (isExhaling) {
                    exhaleCount++;
                } else {
                    inhaleCount++;
                }
            }
        }
    }

    private void finishBreathing() {
        timerHandler.removeCallbacks(timerRunnable);
        long totalTimeMillis = System.currentTimeMillis() - startTime;
        long selectedTimeMillis = getSelectedTimeInMillis();
        String completedTime = formatDuration(totalTimeMillis);
        String selectedTimeFormatted = formatDuration(selectedTimeMillis);

        int completed = totalTimeMillis >= selectedTimeMillis ? 1 : 0;

        DatabaseHelper db = new DatabaseHelper(this);
        db.addBreathingSession(userId, selectedTimeFormatted, completedTime, exhaleCount, inhaleCount, completed, getCurrentDate());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        resetBreathing();
    }



    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    private String formatDuration(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    private void resetBreathing() {
        tvBreathingTimer.setText("00:00");
        tvBreathingState.setText("");
        btnStartStopBreathing.setText("Iniciar Respiración");
        isBreathing = false;
        exhaleCount = 0;
        inhaleCount = 0;
        spinnerBreathingTime.setVisibility(View.VISIBLE);
        // Restablecer cualquier otra variable o estado de UI necesario
    }




}