package com.example.conectamobileml;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    private EditText etNombre, etApellido, etEmail, etPassword;
    private Button btnGuardarCambios;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a los elementos del layout
        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios);

        // Configurar botón de guardar cambios
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) ||
                TextUtils.isEmpty(email)) {
            Toast.makeText(EditarPerfil.this, "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener usuario actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No se encontró un usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        // Actualizar datos en Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("apellido", apellido);
        updates.put("email", email);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Actualizar email en FirebaseAuth
                    if (!email.equals(user.getEmail())) {
                        user.updateEmail(email).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditarPerfil.this, "Correo actualizado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditarPerfil.this, "Error al actualizar correo: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Actualizar contraseña si se ingresó una nueva
                    if (!TextUtils.isEmpty(password)) {
                        user.updatePassword(password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditarPerfil.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditarPerfil.this, "Error al actualizar contraseña: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Redirigir al Activity Perfil después de guardar
                    Toast.makeText(EditarPerfil.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditarPerfil.this, Perfil.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarPerfil.this, "Error al actualizar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
