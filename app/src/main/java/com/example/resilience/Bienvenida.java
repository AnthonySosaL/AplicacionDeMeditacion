package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.media.MediaPlayer;
public class Bienvenida extends AppCompatActivity {
    private ImageButton btnInicia;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        btnInicia = findViewById(R.id.btnIniciar);

        btnInicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iniciar();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.relajante3); // Asegúrate de que el archivo se llama "relajante3.mp3" en el directorio res/raw
        mediaPlayer.setLooping(true); // El sonido se reinicia automáticamente al finalizar
        mediaPlayer.start();
    }

    private void Iniciar() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Detener la reproducción del sonido al iniciar la siguiente actividad
            mediaPlayer.release(); // Liberar recursos
        }
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Liberar recursos del MediaPlayer
            mediaPlayer = null;
        }
    }
}
