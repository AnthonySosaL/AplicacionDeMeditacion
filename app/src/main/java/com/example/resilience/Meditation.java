package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Meditation extends AppCompatActivity {
    private int userId;
    private MediaPlayer mediaPlayer;
    private Spinner spinnerMeditationTime;
    private Button btnStartStopMeditation, btnFinishMeditation;
    private ImageButton btnRegresar;
    private TextView tvTimer, tvSelectedTime;
    private EditText etReflections;

    private Handler timerHandler = new Handler();
    private long startTime = 0;
    private boolean isMeditating = false;
    private boolean isTimeSelected = false;
    private int selectedTime = 0; // Tiempo de meditación en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        userId = getIntent().getIntExtra("USER_ID", -1);
        btnRegresar = findViewById(R.id.regresarM);
        spinnerMeditationTime = findViewById(R.id.spinnerMeditationTime);
        btnStartStopMeditation = findViewById(R.id.btnStartStopMeditation);
        btnFinishMeditation = findViewById(R.id.btnFinishMeditation);
        tvTimer = findViewById(R.id.tvTimer);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        etReflections = findViewById(R.id.etReflections);
        tvSelectedTime.setVisibility(View.INVISIBLE);
        configureMeditationTimeSpinner();
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
        btnStartStopMeditation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMeditating) {
                    startMeditation();
                } else {
                    pauseMeditation();
                }
            }
        });

        btnFinishMeditation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMeditation();
            }
        });

    }

    private void configureMeditationTimeSpinner() {
        List<String> meditationTimes = new ArrayList<>();
        for (int i = 10; i <= 60; i += 5) {
            meditationTimes.add(i + " minutos");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meditationTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeditationTime.setAdapter(adapter);
    }

    private void startMeditation() {
        if (!isMeditating) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.relajante); // Asegúrate de que el archivo se llama "relajante.mp3" en el directorio res/raw
                mediaPlayer.setLooping(true); // El sonido se reinicia automáticamente al finalizar
            }
            mediaPlayer.start();
        }
        if (!isTimeSelected) {
            selectedTime = getSelectedTime();
            tvSelectedTime.setText("Tiempo: " + spinnerMeditationTime.getSelectedItem().toString());
            tvSelectedTime.setVisibility(View.VISIBLE);
            spinnerMeditationTime.setVisibility(View.GONE);
            isTimeSelected = true;
            startTime = System.currentTimeMillis();
        }

        timerHandler.postDelayed(timerRunnable, 0);
        btnStartStopMeditation.setText("Pausar Meditación");
        isMeditating = true;
    }

    private void pauseMeditation() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        timerHandler.removeCallbacks(timerRunnable);
        btnStartStopMeditation.setText("Retomar Meditación");
        isMeditating = false;
    }

    private void finishMeditation() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        timerHandler.removeCallbacks(timerRunnable);
        long totalTimeMillis = System.currentTimeMillis() - startTime;
        String durationFormatted = formatDuration(totalTimeMillis);
        saveMeditationSession(durationFormatted);
        resetMeditation();
    }
    private String formatDuration(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    private void saveMeditationSession(String duration) {
        String reflections = etReflections.getText().toString();
        String currentDate = getCurrentDate();
        String selectedTime = tvSelectedTime.getText().toString().substring(8); // Extrae el tiempo seleccionado del TextView
        int completed = duration.equals(selectedTime) ? 1 : 0; // Comprueba si la sesión se completó

        DatabaseHelper db = new DatabaseHelper(this);
        db.addMeditationSession(userId, selectedTime, duration, completed, currentDate, reflections);

    }




    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void resetMeditation() {
        tvTimer.setText("00:00");
        etReflections.setText("");
        btnStartStopMeditation.setText("Iniciar Meditación");
        isMeditating = false;
        isTimeSelected = false;
        spinnerMeditationTime.setVisibility(View.VISIBLE);
        tvSelectedTime.setVisibility(View.GONE);
    }

    private int getSelectedTime() {
        String selectedItem = spinnerMeditationTime.getSelectedItem().toString();
        int time = Integer.parseInt(selectedItem.split(" ")[0]);
        return time * 60 * 1000;
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMeditating) {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds %= 60;

                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

                if (millis >= selectedTime) {
                    finishMeditation(); // Termina la meditación si se alcanza el tiempo seleccionado
                } else {
                    timerHandler.postDelayed(this, 500);
                }
            }
        }
    };
    private void regresar() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }


}
