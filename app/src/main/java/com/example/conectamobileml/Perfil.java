package com.example.conectamobileml;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends AppCompatActivity {

    private TextView tvUsername;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencia de vistas
        tvUsername = findViewById(R.id.tv_username);
        Button btnViewUsers = findViewById(R.id.btn_ver_usuarios);
        Button btnChat = findViewById(R.id.btn_chat);
        Button btnEditarPerfil = findViewById(R.id.btn_editar_perfil);
        Button btnEliminarPerfil = findViewById(R.id.btn_eliminar_perfil); // Asegúrate de tener este botón en tu XML

        // Ajustar paddings según las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar y configurar la Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Configurar Toolbar como ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Mostrar el botón de "atrás"
            getSupportActionBar().setTitle("");
        }

        // Cargar datos del usuario desde Firestore
        loadUserData();

        // Configuración de botones para navegación
        btnViewUsers.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, Contactos.class);
            startActivity(intent);
        });

        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, Chat.class);
            startActivity(intent);
        });

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, EditarPerfil.class);
            startActivity(intent);
        });

        // Configurar botón para eliminar perfil
        btnEliminarPerfil.setOnClickListener(v -> eliminarPerfil());
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String apellido = documentSnapshot.getString("apellido");
                        tvUsername.setText(nombre + " " + apellido);
                    } else {
                        Toast.makeText(Perfil.this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Perfil.this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void eliminarPerfil() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "No se encontró un usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        // Eliminar los datos del usuario de Firestore
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar el usuario de Firebase Authentication
                    user.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Perfil.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                                    // Redirigir al LoginActivity
                                    Intent intent = new Intent(Perfil.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Perfil.this, "Error al eliminar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Perfil.this, "Error al eliminar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Detectar el clic en el botón de "atrás"
            Toast.makeText(Perfil.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
            onBackPressed(); // Regresar automáticamente a la actividad anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
