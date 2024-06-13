package com.example.resilience;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Referencias a los componentes
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        // Listener para el botón Regresar
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
                finish(); // Finaliza la actividad actual y regresa a la anterior en la pila
            }
        });

        // Listener para el botón Registrar
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validar que todos los campos estén llenos
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si las contraseñas coinciden
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseHelper db = new DatabaseHelper(this);
        // Aquí puedes agregar más validaciones si lo necesitas
        if (db.checkUserrepetido(username)){
            Toast.makeText(RegisterActivity.this, "Usuario ya existente", Toast.LENGTH_SHORT).show();
            etUsername.setText("");
        }
        else {
            // Si todo está bien, registra el usuario en la base de datos

            if (db.addUser(username, password)) {
                Toast.makeText(RegisterActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                // Opcional: Redirige al usuario a la pantalla de inicio de sesión
                // Intent intent = new Intent(this, MainActivity.class);
                // startActivity(intent);
                finish(); // O simplemente cierra esta actividad
            } else {
                Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void regresar(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
