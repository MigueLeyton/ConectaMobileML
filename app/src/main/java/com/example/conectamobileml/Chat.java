package com.example.conectamobileml;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Chat extends AppCompatActivity {

    private Toolbar toolbar; // Declarar la Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Inicializar y configurar la Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Configurar Toolbar como ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Mostrar el botón de "atrás"
            getSupportActionBar().setTitle("");
        }

        // Ajustar paddings según las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Detectar clic en el botón "atrás"
            onBackPressed(); // Regresar a la actividad anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
