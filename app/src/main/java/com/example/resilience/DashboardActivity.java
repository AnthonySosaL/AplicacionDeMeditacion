package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {
    private int userId;
    private ImageButton btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userId = getIntent().getIntExtra("USER_ID", -1);

        Button btnMeditation = findViewById(R.id.btnMeditation);
        Button btnBreathing = findViewById(R.id.btnBreathing);
        Button btnTips = findViewById(R.id.btnTips);
        Button btnProgress = findViewById(R.id.btnProgress);
        Button btnEstadisticas = findViewById(R.id.btnEstadisticas);
        btnRegresar = findViewById(R.id.regresarD);

        btnMeditation.setOnClickListener(this::goToMeditation);
        btnBreathing.setOnClickListener(this::goToBreathing);
        btnTips.setOnClickListener(this::goToTips);
        btnProgress.setOnClickListener(this::goToProgress);
        btnEstadisticas.setOnClickListener(this::goToEstadisticas);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
    }

    private void goToMeditation(View v) {
        Intent intent = new Intent(this, Meditation.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private void goToBreathing(View v) {
        Intent intent = new Intent(this, Breathing.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private void goToTips(View v) {
        Intent intent = new Intent(this, Tips.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private void goToProgress(View v) {
        Intent intent = new Intent(this, Progress.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
    private void goToEstadisticas(View v) {
        Intent intent = new Intent(this, EstadisticasActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
    private void regresar() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        Toast.makeText(DashboardActivity.this, "Has cerrado sesión", Toast.LENGTH_LONG).show();
        finish();
    }
}

