package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class LoginActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private Button LoginButton,PhoneLoginButton;
    private EditText userEmail,UserPassword;
    private TextView ForgetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginYourAccount();
            }
        });
        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPhoneLoginActivity();
            }
        });
        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToResetPasswordActivity();
            }
        });
    }


    private void InitializeFields() {
        LoginButton = findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        userEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        ForgetPassword = findViewById(R.id.forget_password);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadingBar = new ProgressDialog(this);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void LoginYourAccount() {
        String logEmail = userEmail.getText().toString();
        String logPassword = UserPassword.getText().toString();
        if(TextUtils.isEmpty(logEmail)){
            userEmail.setError("Email is required");
//            Toast.makeText(this, "Please enter email..", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(logPassword)){
            UserPassword.setError("Password is required");
//            Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please Wait,while we are sign In for you account ..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(logEmail,logPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendUserMainActivity();
                                ToastMessage("Account logged Successfully.");
                                loadingBar.dismiss();
                            }else{
                                ToastMessage("Account logged Unsuccessfully.");
                                //Toast.makeText(LoginActivity.this, "Account logged Unsuccessfully..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendUserMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void sendUserToRegisterActivity() {
        Intent loginIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToPhoneLoginActivity() {
        Intent phoneLoginIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
        phoneLoginIntent.putExtra("login","Login");
        startActivity(phoneLoginIntent);
    }

    private void sendUserToResetPasswordActivity() {
        Intent ResetPasswordIntent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
        startActivity(ResetPasswordIntent);
    }

    private void ToastMessage(String mes){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.activity_custom_toast2, (ViewGroup) findViewById(R.id.custom_toast_layout));
        TextView tv = (TextView) layout.findViewById(R.id.txtvw);
        tv.setText(mes);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}