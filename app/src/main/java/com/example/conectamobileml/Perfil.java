package com.example.conectamobileml;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Perfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvUsername;
    private ImageView imgProfilePicture;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase Auth, Firestore y Storage
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

        // Referencia de vistas
        tvUsername = findViewById(R.id.tv_username);
        imgProfilePicture = findViewById(R.id.img_profile_picture);
        Button btnViewUsers = findViewById(R.id.btn_ver_usuarios);
        Button btnEditarPerfil = findViewById(R.id.btn_editar_perfil);
        Button btnEliminarPerfil = findViewById(R.id.btn_eliminar_perfil);
        Button btnChangeProfilePicture = findViewById(R.id.btn_change_profile_picture);

        // Ajustar paddings según las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar y configurar la Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Configuración para cambiar la foto de perfil
        btnChangeProfilePicture.setOnClickListener(v -> openFileChooser());

        // Cargar datos del usuario
        loadUserData();

        // Configuración de botones para navegación
        btnViewUsers.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, Contactos.class);
            startActivity(intent);
        });

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, EditarPerfil.class);
            startActivity(intent);
        });

        btnEliminarPerfil.setOnClickListener(v -> eliminarPerfil());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgProfilePicture.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        StorageReference fileReference = storageReference.child(userId + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            saveProfilePictureUrl(downloadUrl);
                        })
                        .addOnFailureListener(e -> Toast.makeText(Perfil.this, "Error al obtener la URL: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(Perfil.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfilePictureUrl(String url) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).update("profilePictureUrl", url)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Perfil.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    loadUserData();  // Recargar los datos del usuario después de la actualización
                })
                .addOnFailureListener(e -> Toast.makeText(Perfil.this, "Error al guardar URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String apellido = documentSnapshot.getString("apellido");
                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");

                        tvUsername.setText(nombre + " " + apellido);

                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profilePictureUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Evita el caché
                                    .skipMemoryCache(true)  // Salta la memoria caché
                                    .into(imgProfilePicture);
                        }
                    } else {
                        Toast.makeText(Perfil.this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Perfil.this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void eliminarPerfil() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "No se encontró un usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> user.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Perfil.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Perfil.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Perfil.this, "Error al eliminar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }))
                .addOnFailureListener(e -> Toast.makeText(Perfil.this, "Error al eliminar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(Perfil.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
