package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;

    private EditText editTextUsername, editTextPassword;
    private Button btnIngresar, btnRegistrarse;
    private String borrarbasededatos = "no";
    private ImageButton regresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(borrarbasededatos=="si"){
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.deleteAllBreathingSessions(); // Esto borrará todos los registros de la tabla
        }
        db = new DatabaseHelper(this);
        crearUsuarioDePrueba();
        // Obtener referencias a los componentes
        editTextUsername = findViewById(R.id.editTextText);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        regresar = findViewById(R.id.regresarMa);

        // Establecer listeners para los botones
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresar();
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarse();
            }
        });
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
    }
    private void crearUsuarioDePrueba() {
        // Verifica si el usuario "admin" ya existe
        if (!db.checkUser("admin", "admin")) {
            // Si no existe, lo crea
            db.addUser("admin", "admin");
        }
    }

    private void ingresar() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if(db.checkUser(username, password)) {
            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            int userId = db.getUserId(username);
            // Iniciar DashboardActivity
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
        }
    }


    private void registrarse() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    private void regresar() {
        Intent intent = new Intent(this, Bienvenida.class);
        startActivity(intent);
        finish();
    }
}
