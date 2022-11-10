package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationEmailActivity extends AppCompatActivity {
    private Button VerificationButton;
    private FirebaseAuth mAuth;
    private boolean emailAddressChecked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);

        mAuth = FirebaseAuth.getInstance();
        VerificationButton = findViewById(R.id.sendVerificationButton);

        VerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        emailAddressChecked =  mAuth.getCurrentUser().isEmailVerified();
        if(emailAddressChecked){
            sendUserMainActivity();
        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendUserMainActivity();
                            Toast.makeText(VerificationEmailActivity.this,"Email Sent..",Toast.LENGTH_LONG);
                        }else{
                            Toast.makeText(VerificationEmailActivity.this,"Please verify your email..",Toast.LENGTH_LONG);
                        }
                    }
                });

    }

    private void sendUserLoginActivity() {
        Intent mainIntent = new Intent(VerificationEmailActivity.this,LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void sendUserMainActivity() {
        Intent mainIntent = new Intent(VerificationEmailActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

}