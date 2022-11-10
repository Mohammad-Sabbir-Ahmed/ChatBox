package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountActivity extends AppCompatActivity {
    private Button NewAccount,AlreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        NewAccount = findViewById(R.id.new_account_button);
        AlreadyAccount = findViewById(R.id.login_account_button);

        NewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
                //sendUserToPhoneLoginActivity();
            }
        });

        AlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
                //sendUserToPhoneLoginActivity();
            }
        });
    }

    private void sendUserToRegisterActivity() {
        Intent newAccountIntent = new Intent(AccountActivity.this,RegisterActivity.class);
        //newAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newAccountIntent);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(AccountActivity.this,LoginActivity.class);
        //loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void sendUserToPhoneLoginActivity() {
        Intent loginIntent = new Intent(AccountActivity.this,PhoneLoginActivity.class);
        //loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}