package com.example.conectamobileml;

import android.content.Intent;
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        Log.d("ContactosActivity", "onCreate: Activity started");

        // Inicializar Firebase y RecyclerView
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rvContactos = findViewById(R.id.rv_Contactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        contactosAdapter = new ContactosAdapter(userList);
        rvContactos.setAdapter(contactosAdapter);

        // Configurar el evento de clic en un contacto
        contactosAdapter.setOnContactClickListener(user -> {
            if (user != null) {
                Log.d("ContactosActivity", "UserId: " + user.getUserId() + " Nombre: " + user.getNombre() + " " + user.getApellido());

                Intent intent = new Intent(Contactos.this, Chat.class);
                // Pasar los datos con putExtra
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("userName", user.getNombre() + " " + user.getApellido());
                startActivity(intent);
            } else {
                Toast.makeText(Contactos.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        });


        // Configurar la toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Ajustar el padding para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargar los usuarios desde Firebase
        loadUsersFromFirebase();
    }

    private void loadUsersFromFirebase() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        userList.clear();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);

                            // Log para verificar que los datos se están extrayendo correctamente
                            Log.d("FirebaseData", "UserId: " + user.getUserId());
                            Log.d("FirebaseData", "Nombre: " + user.getNombre());
                            Log.d("FirebaseData", "Apellido: " + user.getApellido());

                            userList.add(user);
                        }
                        contactosAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Contactos.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error al cargar usuarios: " + e.getMessage());
                    Toast.makeText(Contactos.this, "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
