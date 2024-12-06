package com.example.conectamobileml;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegistro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Asegúrate de que tu XML sea el correcto

        mAuth = FirebaseAuth.getInstance(); // Inicializar FirebaseAuth

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegistro = findViewById(R.id.tv_Registro);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Iniciar sesión con Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, task -> {
                            if (task.isSuccessful()) {
                                // Ingreso exitoso, redirige al perfil
                                Intent intent = new Intent(Login.this, Perfil.class);
                                startActivity(intent);
                                finish(); // Finaliza esta actividad para que no regrese con el botón atrás
                            } else {
                                // Error al iniciar sesión
                                Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Redirigir a la actividad de registro
        tvRegistro.setOnClickListener(v -> startActivity(new Intent(Login.this, Registrar.class)));
    }
}
