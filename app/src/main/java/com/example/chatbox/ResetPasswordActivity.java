package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText forgotPasswordEmail;
    private Button forgotPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        forgotPasswordEmail = findViewById(R.id.reset_email);
        forgotPasswordButton = findViewById(R.id.reset_button);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = forgotPasswordEmail.getText().toString().trim();
                if (emailAddress.equals("")) {
                    forgotPasswordEmail.setError("Email Required");
                }
                if (!isEmailValid(emailAddress)) {
                    forgotPasswordEmail.setError("Enter Valid Email");
                }

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendUserToLoginActivity();
                                Toast.makeText(ResetPasswordActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}