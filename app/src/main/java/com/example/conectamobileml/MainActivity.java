package com.example.conectamobileml;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            // Si el usuario no está autenticado, redirigir a Login
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        } else {
            // Usuario autenticado
            setContentView(R.layout.activity_main); // La interfaz principal de tu aplicación
        }
    }
}
