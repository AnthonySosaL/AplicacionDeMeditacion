package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;
import android.widget.Toast;

public class Tips extends AppCompatActivity {
    private int userId;
    private TextView tvSectionTitle;
    private ProgressBar progressBar;
    private TextView tvProgressPercentage;
    private RecyclerView recyclerView;
    private ConsejosAdapter adapter;
    private ImageButton btnRegresar;
    private Spinner spinnerConsejos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        // Inicializa tus vistas
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        progressBar = findViewById(R.id.progressBar);
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage);
        recyclerView = findViewById(R.id.recyclerView);
        btnRegresar = findViewById(R.id.regresarT);
        spinnerConsejos = findViewById(R.id.spinnerConsejos);

        // Configura el RecyclerView y el adaptador
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConsejosAdapter();
        recyclerView.setAdapter(adapter);

        // Configura el Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.consejos_niveles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConsejos.setAdapter(spinnerAdapter);
        spinnerConsejos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadConsejos(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Obtén el ID del usuario
        userId = getIntent().getIntExtra("USER_ID", -1);

        // Actualiza la UI con el progreso y los consejos del usuario
        updateProgressUI();

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
    }

    private void updateProgressUI() {
        DatabaseHelper db = new DatabaseHelper(this);
        int totalSessions = db.getTotalCompletedSessions(userId);

        int level;
        int progress;
        if (totalSessions < 3) {
            level = 1; // Nivel Inicial
            progress = (int) ((totalSessions / 3.0) * 100);
        } else if (totalSessions < 6) {
            level = 2; // Nivel Intermedio
            progress = (int) (((totalSessions - 3) / 3.0) * 100);
        } else {
            level = 3; // Nivel Avanzado
            progress = Math.min((int) (((totalSessions - 6) / 3.0) * 100), 100);
        }

        String levelTitle = "";
        switch (level) {
            case 1:
                levelTitle = "Nivel Inicial";
                break;
            case 2:
                levelTitle = "Nivel Intermedio";
                break;
            case 3:
                levelTitle = "Nivel Avanzado";
                spinnerConsejos.setVisibility(View.VISIBLE);
                tvSectionTitle.setVisibility(View.GONE);
                Toast.makeText(Tips.this, "FELICITACIONES HAS LLEGADO AL ULTIMO NIVEL", Toast.LENGTH_LONG).show();
                if (progress == 100){
                    Toast.makeText(Tips.this, "FELICITACIONES HAS COMPLETADO LOS 3 NIVELES", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                spinnerConsejos.setVisibility(View.GONE);
                tvSectionTitle.setVisibility(View.VISIBLE);
                tvProgressPercentage.setVisibility(View.VISIBLE);
                break;
        }

        tvSectionTitle.setText(levelTitle);
        progressBar.setProgress(progress);
        tvProgressPercentage.setText(progress + "%");

        if (level < 3) {
            // Lee los consejos y actualiza el RecyclerView
            loadConsejos(level);
        }
    }

    private String readConsejosFromRaw() {
        StringBuilder consejos = new StringBuilder();
        try (InputStream is = getResources().openRawResource(R.raw.consejos);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {  // Ignora las líneas vacías
                    consejos.append(line.trim()).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consejos.toString().trim(); // Elimina espacios adicionales al final
    }

    private void loadConsejos(int level) {
        String allConsejos = readConsejosFromRaw();
        String[] consejosByLevel = allConsejos.split("# Consejos Nivel ");

        List<String> consejosList = new ArrayList<>();
        if (level >= 1 && level < consejosByLevel.length) {
            // Obtiene la sección de consejos para el nivel actual y elimina la etiqueta del nivel
            String consejosNivel = consejosByLevel[level].substring(consejosByLevel[level].indexOf('\n') + 1).trim();
            String[] consejosIndividuales = consejosNivel.split("\\|");
            for (String consejo : consejosIndividuales) {
                consejo = consejo.trim();
                if (consejo.contains(":")) { // Verifica si es un consejo válido
                    consejosList.add(consejo);
                }
            }
        }

        // Convierte la lista de consejos en un arreglo y pásalo al adaptador
        String[] individualConsejos = consejosList.toArray(new String[0]);
        adapter.setConsejos(individualConsejos);
    }

    private void regresar() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }


}
