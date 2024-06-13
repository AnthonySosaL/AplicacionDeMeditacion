package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasActivity extends AppCompatActivity {
    private int userId;
    private Spinner periodSelectorSpinner;
    private Switch sessionTypeSwitch;
    private BarChart chart;
    private DatabaseHelper db;
    private ImageButton btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        userId = getIntent().getIntExtra("USER_ID", -1);
        sessionTypeSwitch = findViewById(R.id.sessionTypeSwitch);
        periodSelectorSpinner = findViewById(R.id.periodSelectorSpinner);
        btnRegresar = findViewById(R.id.regresarE);
        chart = findViewById(R.id.barChart);
        db = new DatabaseHelper(this);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });

        setupPeriodSelectorSpinner();
        sessionTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateChartBasedOnSelection(periodSelectorSpinner.getSelectedItemPosition()));

    }
    private void regresar() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
    private void setupPeriodSelectorSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Últimos 30 días", "Mes anterior", "Últimos 90 días", "Anual"});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSelectorSpinner.setAdapter(adapter);

        periodSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateChartBasedOnSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateChartBasedOnSelection(int position) {
        String selectedTable = sessionTypeSwitch.isChecked() ? "breathing_sessions" : "sessions";
        Map<String, Integer> sessionData = new HashMap<>();
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int maxCount = Integer.MIN_VALUE, minCount = Integer.MAX_VALUE;
        int maxIndex = 0, minIndex = 0;
        boolean isAnnual = false;

        switch (position) {
            case 0: // Últimos 30 días
                sessionData = db.getSessionDaysLast30Days(userId, selectedTable);
                labels = Arrays.asList("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo");
                break;
            case 1: // Mes anterior
                sessionData = db.getSessionDaysLastMonth(userId, selectedTable);
                labels = Arrays.asList("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo");
                break;
            case 2: // Últimos 90 días
                sessionData = db.getSessionDaysLast90Days(userId, selectedTable);
                labels = Arrays.asList("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo");
                break;
            case 3: // Anual
                isAnnual = true;
                sessionData = db.getSessionDaysThisYear(userId, selectedTable);
                labels = Arrays.asList("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic");
                for (int i = 0; i < labels.size(); i++) {
                    String month = String.format("%02d", i + 1);
                    int count = sessionData.getOrDefault(month, 0);
                    entries.add(new BarEntry(i, count));

                    if (count > maxCount) {
                        maxCount = count;
                        maxIndex = i;
                    }
                    if (count < minCount && count > 0) {
                        minCount = count;
                        minIndex = i;
                    }
                }
                break;
        }

        if (!isAnnual) {
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                int count = sessionData.getOrDefault(label, 0);
                entries.add(new BarEntry(i, count));

                if (count > maxCount) {
                    maxCount = count;
                    maxIndex = i;
                }
                if (count < minCount && count > 0) {
                    minCount = count;
                    minIndex = i;
                }
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, position == 3 ? "Sesiones por Mes" : "Sesiones por Día");
        dataSet.setColors(getColorsForDataSet(entries.size(), maxIndex, minIndex));
        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setEnabled(true);

        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setGranularityEnabled(true);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);

        chart.getLegend().setEnabled(false);

        chart.invalidate();
    }

    private List<Integer> getColorsForDataSet(int size, int maxIndex, int minIndex) {
        List<Integer> colors = new ArrayList<>();
        int highColor = Color.RED;
        int lowColor = Color.BLUE;
        int normalColor = Color.YELLOW;

        for (int i = 0; i < size; i++) {

                if (i == maxIndex) {
                    colors.add(highColor);
                } else if (i == minIndex) {
                    colors.add(lowColor);
                } else {
                    colors.add(normalColor);
                }

        }

        return colors;
    }

    // Resto de la lógica de la clase...
}
