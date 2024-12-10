package com.example.conectamobileml;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseFirestore db;
    private RecyclerView rvMessages;
    private ChatAdapter chatAdapter;
    private List<ChatAdapter.Message> messageList;
    private String chatId;
    private String userId;
    private String senderId; // Variable para el senderId
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra("userId");

        db = FirebaseFirestore.getInstance();
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chat");
        }

        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        // Obtener los datos del intent
        userId = getIntent().getStringExtra("userId");  // Recupera el userId que pasaste desde Login
        String userName = getIntent().getStringExtra("userName");
        if (userName != null) {
            getSupportActionBar().setTitle(userName);
        }

        chatId = "chat_" + userId;  // Generamos un chatId único para esta conversación

        // Obtener el ID del usuario autenticado (senderId)
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvMessages.setAdapter(chatAdapter);

        // Cargar mensajes en tiempo real
        loadMessages();

        // Enviar mensaje
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        db.collection("chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Toast.makeText(Chat.this, "Error al cargar los mensajes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageList.clear();
                    for (DocumentSnapshot document : snapshots) {
                        String senderId = document.getString("senderId");
                        String message = document.getString("message");
                        long timestamp = document.getLong("timestamp");
                        messageList.add(new ChatAdapter.Message(senderId, message, timestamp));
                    }
                    chatAdapter.notifyDataSetChanged();
                });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            long timestamp = System.currentTimeMillis();

            // Crear el mensaje como un Map
            Map<String, Object> message = new HashMap<>();
            message.put("message", messageText);
            message.put("senderId", senderId);  // Usar senderId correctamente
            message.put("timestamp", timestamp);

            // Guardar el mensaje en la colección de Firestore
            db.collection("chats").document(chatId)
                    .collection("messages")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        etMessage.setText("");  // Limpiar el campo de texto
                    })
                    .addOnFailureListener(e -> Toast.makeText(Chat.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(Chat.this, "Por favor ingresa un mensaje", Toast.LENGTH_SHORT).show();
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
