package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private Button RegisterButton,PhoneRegisterButton;
    private EditText userEmail,UserPassword;
    private TextView AlreadyHaveAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();

        PhoneRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPhoneRegisterActivity();
            }
        });

        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToLoginActivity();
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String resEmail = userEmail.getText().toString().trim();
        String resPassword = UserPassword.getText().toString().trim();
        if(TextUtils.isEmpty(resEmail)){
            userEmail.setError("Email is required");
            //Toast.makeText(this, "Please enter email..", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(resPassword)){
            UserPassword.setError("Password is required");
            //Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Creating a new account");
            loadingBar.setMessage("Please Wait,while we are creating new account for you..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(resEmail,resPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");
                                sendVerificationEmail();
                                //sendUserMainActivity();
                                //SendUserToSettingActivity();
                                //sendUserToLoginActivity();
                                //Toast.makeText(RegisterActivity.this,"Account Create Successfully..",Toast.LENGTH_LONG);
                                loadingBar.dismiss();
                            }else{
                                Toast.makeText(RegisterActivity.this,"Account Create Unsuccessfully..",Toast.LENGTH_LONG);
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SendUserToSettingActivity();
//                            sendUserMainActivity();
                            Toast.makeText(RegisterActivity.this,"Email Sent..",Toast.LENGTH_LONG);
                        }else{
                            Toast.makeText(RegisterActivity.this,"Please verify your email..",Toast.LENGTH_LONG);
                        }
                    }
                });

    }

    private void InitializeFields() {
        RegisterButton = findViewById(R.id.register_button);
        PhoneRegisterButton = findViewById(R.id.phone_register_button);
        userEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        AlreadyHaveAccount = findViewById(R.id.already_have_account);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
    }


    private void sendUserMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToSettingActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, SettingsActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToVerificationEmailActivity()
    {
        Intent VerificationEmailIntent = new Intent(RegisterActivity.this, VerificationEmailActivity.class);
        startActivity(VerificationEmailIntent);
        finish();
    }

    private void sendUserToPhoneRegisterActivity() {
        Intent phoneRegisterIntent = new Intent(RegisterActivity.this,PhoneLoginActivity.class);
        phoneRegisterIntent.putExtra("login","register");
        startActivity(phoneRegisterIntent);
    }

}