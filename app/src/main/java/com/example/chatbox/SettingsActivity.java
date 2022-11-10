package com.example.chatbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private Button UpdateAccountSettings;
    private EditText userName, userStatus,userPhone;
    private CircleImageView userProfileImage;
    private CountryCodePicker phoneCode;
    private TextView user_name;
    private TextInputLayout name_layout;

    private String currentUserID,userEmail,userPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private Uri ImageUri;

    private static final int GalleryPick = 1;

    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;

    private Toolbar SettingsToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userEmail = mAuth.getCurrentUser().getEmail();
       // userPassword = getIntent().getExtras().get("user_password").toString();

        InitializeFields();


        userName.setVisibility(View.INVISIBLE);
        name_layout.setVisibility(View.INVISIBLE);

        RetrieveUserInfo();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                UpdateSettings();
            }
        });


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }

    private void InitializeFields()
    {
        UpdateAccountSettings =  findViewById(R.id.update_settings_button);
        userName =  findViewById(R.id.set_user_name);
        userStatus =  findViewById(R.id.set_profile_status);
        userPhone = findViewById(R.id.set_user_phone);
        user_name = findViewById(R.id.user_name);
        name_layout = findViewById(R.id.layout_name);
        userProfileImage = findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);

        SettingsToolBar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        phoneCode = findViewById(R.id.ccp);
        phoneCode.registerCarrierNumberEditText(userPhone);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                Picasso.get().load(resultUri).placeholder(R.drawable.profile_image).into(userProfileImage);

                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Image image = new Image(uri.toString());
                                String imageId = RootRef.push().getKey();
                                RootRef.child("Users").child(currentUserID).child("image")
                                    .setValue(image.getImage());
                                loadingBar.dismiss();
                                Toast.makeText(SettingsActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
        }
    }

    private void UpdateSettings()
    {
        String setUserName = userName.getText().toString().trim();
        String setStatus = userStatus.getText().toString().trim();
        String setPhone = phoneCode.getFullNumberWithPlus().trim();

        if(setPhone.length()>14){
            userPhone.setError("valid number");
        }
        else if (TextUtils.isEmpty(setUserName))
        {
            userName.setError("required name");
            //Toast.makeText(this, "Please write your user name first....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(setStatus))
        {
            userStatus.setError("required statue");
            //Toast.makeText(this, "Please write your status....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(setPhone))
        {
            userPhone.setError("required number");
            //Toast.makeText(this, "Please write your phone number....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);
            profileMap.put("phone", setPhone);
            profileMap.put("email", userEmail);
            //Picasso.get().load(resultUri).placeholder(R.drawable.profile_image).into(userProfileImage)
           // profileMap.put("password",userPassword);
            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                //Toast.makeText(SettingsActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString().trim();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString().trim();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString().trim();
                            String retrievesPhone = dataSnapshot.child("phone").getValue().toString().substring(4);

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            userPhone.setText(retrievesPhone);
                            user_name.setText(retrieveUserName);
                            Picasso.get().load(retrieveProfileImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString().trim();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString().trim();
                            String retrievesPhone = dataSnapshot.child("phone").getValue().toString().trim();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            userPhone.setText(retrievesPhone);
                        }
                        else
                        {
                            userName.setVisibility(View.VISIBLE);
                            name_layout.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}