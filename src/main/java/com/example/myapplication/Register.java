package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText signUsername,signEmail,signPassword;
//    EditText signUsername,signPassword,signConfirmPassword,signEmail;
    Button registerButton;
    TextView goToLogin;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        goToLogin = findViewById(R.id.login);

        signUsername = findViewById(R.id.username);
//        signConfirmPassword = findViewById(R.id.cpassword);
        signEmail = findViewById(R.id.email);
        signPassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String userText = String.valueOf(signUsername.getText());
//                String confirmPasswordText = String.valueOf(signConfirmPassword.getText());
                String usernameText = String.valueOf(signUsername.getText());
                String emailText = String.valueOf(signEmail.getText());
                String passwordText = String.valueOf(signPassword.getText());


                if(TextUtils.isEmpty(usernameText) | TextUtils.isEmpty(emailText) | TextUtils.isEmpty(passwordText)){
                    Toast.makeText(Register.this, "Please enter required fields", Toast.LENGTH_SHORT).show();

                }else {
                    mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        saveUserToFirestore(userId, usernameText);
                                        Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

//              Toast.makeText(Register.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Register.this, MainActivity.class);
//                startActivity(intent);
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveUserToFirestore(String userId, String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Username saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Failed to save username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}