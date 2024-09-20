package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class SendSMSEMail extends AppCompatActivity {

    private EditText editTextRecipient;
    private EditText editTextMessage;
    private Button buttonSendSMS;
    private Button buttonSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_smsemail);

        editTextRecipient = findViewById(R.id.editTextRecipient);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendSMS = findViewById(R.id.buttonSendSMS);
        buttonSendEmail = findViewById(R.id.buttonSendEmail);

        buttonSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

    }

    private void sendSMS() {
        String recipient = editTextRecipient.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        if (recipient.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + recipient));
        smsIntent.putExtra("sms_body", message);
        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "SMS sending failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        String recipient = editTextRecipient.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        if (recipient.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + recipient));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, message); // Set message as subject
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(emailIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Email sending failed", Toast.LENGTH_SHORT).show();
        }
    }

}