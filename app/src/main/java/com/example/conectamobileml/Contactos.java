package com.example.conectamobileml;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Contactos extends AppCompatActivity {

    private RecyclerView rvContactos;
    private ContactosAdapter contactosAdapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Toolbar toolbar; // Declarar la Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        // Log para verificar si el método onCreate se ejecuta correctamente
        Log.d("ContactosActivity", "onCreate: Activity started");

        // Inicializar Firebase y RecyclerView
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rvContactos = findViewById(R.id.rv_Contactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        contactosAdapter = new ContactosAdapter(userList);
        rvContactos.setAdapter(contactosAdapter);

        // Configurar Toolbar
        toolbar = findViewById(R.id.toolbar); // Inicializar la Toolbar
        setSupportActionBar(toolbar); // Configurar la Toolbar como ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Mostrar el botón de "atrás"
            getSupportActionBar().setTitle(""); // Eliminar el título de la Toolbar
        }

        // Ajustar paddings según las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Log para verificar si se entra a la función loadUsersFromFirebase
        Log.d("ContactosActivity", "onCreate: Calling loadUsersFromFirebase()");
        // Cargar usuarios de Firebase
        loadUsersFromFirebase();
    }

    private void loadUsersFromFirebase() {
        // Obtener todos los usuarios de Firestore
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Limpiar lista antes de agregar nuevos usuarios
                        userList.clear();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);  // Convertir el documento Firestore a un objeto User
                            userList.add(user);  // Agregar el usuario a la lista
                        }
                        contactosAdapter.notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
                    } else {
                        Toast.makeText(Contactos.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Log para verificar el error
                    Log.e("FirebaseError", "Error al cargar usuarios: " + e.getMessage());
                    Toast.makeText(Contactos.this, "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Detectar clic en el botón de "atrás"
            onBackPressed(); // Regresar a la actividad anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
