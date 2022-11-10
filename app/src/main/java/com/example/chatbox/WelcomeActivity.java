package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);

                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {

                    if(currentUser != null){
                        Intent welcomeIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(welcomeIntent);
                    }else {
                        Intent welcomeIntent = new Intent(WelcomeActivity.this, AccountActivity.class);
                        startActivity(welcomeIntent);
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}