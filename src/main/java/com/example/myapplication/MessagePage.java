package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MessagePage extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private EditText editTextMessage, editTextReceiverId;
    private String userId;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message_page);

        FirebaseApp.initializeApp(this);

        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerView.setAdapter(messageAdapter);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                //recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessagePage.this, "Failed to load messages.",Toast.LENGTH_SHORT).show();
            }
        });

        editTextMessage = findViewById(R.id.edit_text_message);
        editTextReceiverId = findViewById(R.id.edit_text_receiver_id);
        Button buttonSend = findViewById(R.id.button_send);
        Button buttonSendSMS = findViewById(R.id.button_sendsms);


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverId = editTextReceiverId.getText().toString().trim();
                if (!receiverId.isEmpty()) {
                    sendMessage(receiverId);
                } else {
                    Toast.makeText(MessagePage.this, "Please enter a receiver ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagePage.this, SendSMSEMail.class);
                startActivity(intent);
            }
        });


    }

    private void sendMessage(String receiverUsername) {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String messageId = messagesRef.push().getKey();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String senderUsername = currentUser.getDisplayName(); // Assuming sender's username is set in Firebase Auth
                Message message = new Message(messageId, messageText, senderUsername, receiverUsername);
                messagesRef.child(messageId).setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Message sent successfully
                                editTextMessage.setText("");
                                Toast.makeText(MessagePage.this, "Message sent.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failed message send
                                Toast.makeText(MessagePage.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

}
