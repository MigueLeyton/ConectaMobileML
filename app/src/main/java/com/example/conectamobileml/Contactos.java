package com.example.conectamobileml;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Contactos extends AppCompatActivity {

    private RecyclerView rvContactos;
    private ContactosAdapter contactosAdapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;

    private SearchView searchView;

    private boolean usersNotFound = false;  // Variable para verificar si no se han encontrado usuarios

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

        // Configurar la toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Inicializar la SearchView
        searchView = findViewById(R.id.sv_search);

        // Configurar el listener del SearchView para buscar usuarios por nombre, apellido o email
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Realizar la búsqueda cuando se envía la consulta
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Realizar la búsqueda mientras se escribe el texto
                searchUsers(newText);
                return false;
            }
        });

        // Ajustar el padding para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        // Cargar los usuarios desde Firebase
        loadUsersFromFirebase();
    }

    // Método para cargar todos los usuarios desde Firebase
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
                        usersNotFound = false;  // Resetear la bandera si se cargaron usuarios
                    } else {
                        if (!usersNotFound) {  // Mostrar el mensaje solo una vez
                            Toast.makeText(Contactos.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                            usersNotFound = true;  // Marcar que no se han encontrado usuarios
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error al cargar usuarios: " + e.getMessage());
                    Toast.makeText(Contactos.this, "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Método para buscar usuarios por nombre, apellido o email en Firebase
    private void searchUsers(String query) {
        Set<User> foundUsers = new HashSet<>();  // Usamos un Set para evitar duplicados

        // Búsqueda por nombre
        db.collection("users")
                .orderBy("nombre")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            foundUsers.add(user);
                        }
                    }
                    // Después de buscar por nombre, también buscar por apellido y correo
                    searchByApellidoAndEmail(query, foundUsers);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Contactos.this, "Error al realizar la búsqueda: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Método para buscar por apellido y correo electrónico
    private void searchByApellidoAndEmail(String query, Set<User> foundUsers) {
        // Búsqueda por apellido
        db.collection("users")
                .orderBy("apellido")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            foundUsers.add(user);
                        }
                    }
                    // Luego, buscar por email
                    searchByEmail(query, foundUsers);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Contactos.this, "Error al buscar por apellido: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Método para buscar por email
    private void searchByEmail(String query, Set<User> foundUsers) {
        // Búsqueda por email
        db.collection("users")
                .orderBy("email")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            foundUsers.add(user);
                        }
                    }
                    // Actualizar la lista de resultados
                    updateUserList(foundUsers);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Contactos.this, "Error al buscar por email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Método para actualizar la lista de usuarios encontrados
    private void updateUserList(Set<User> foundUsers) {
        userList.clear();
        userList.addAll(foundUsers);
        contactosAdapter.notifyDataSetChanged();
        if (userList.isEmpty() && !usersNotFound) {
            usersNotFound = true;
        } else {
            usersNotFound = true;  // Resetear si se encontraron usuarios
        }
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
