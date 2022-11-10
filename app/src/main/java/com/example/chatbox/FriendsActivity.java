package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myContactsList;
    private FloatingActionButton fabBtn;

    private Toolbar mToolbar;

    private DatabaseReference ContacsRef, UsersRef,RootRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private boolean IsSelect=true;
    private String groupName,userImage,GroupImage="";
    private HashMap<String, Object> profileMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = findViewById(R.id.friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Add/Remove People");

        myContactsList = findViewById(R.id.friends_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(this));

        fabBtn = findViewById(R.id.fabID);
        fabBtn.setVisibility(View.INVISIBLE);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupIntent = new Intent(FriendsActivity.this, GroupChatActivity.class);
                startActivity(groupIntent);
            }
        });

        groupName = getIntent().getExtras().get("groupName").toString();


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        ContacsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_friend,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.main_add_option:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContacsRef, Contacts.class)
                        .build();


        final FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model)
            {
                final String userIDs = getRef(position).getKey();

                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.child("userState").hasChild("state"))
                            {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            }

                            if (dataSnapshot.hasChild("image"))
                            {
                                userImage = dataSnapshot.child("image").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setTextColor(Color.rgb(0,0,0));
                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            }
                            else
                            {
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setTextColor(Color.rgb(0,0,0));
                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                            }
                            holder.selectRadio.setVisibility(View.VISIBLE);
                            RootRef.child("Groups").child(getRef(position).getKey())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    holder.selectRadio.setChecked(true);
                                                    holder.userName.setTextColor(Color.rgb(60, 179, 113));
                                                    holder.userStatus.setTextColor(Color.rgb(60, 179, 113));
                                                    holder.profileImage.setBorderColor(Color.rgb(60, 179, 113));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        if(IsSelect==true){
                            holder.selectRadio.setVisibility(View.VISIBLE);
                            holder.selectRadio.setChecked(true);
                            holder.userName.setTextColor(Color.rgb(60, 179, 113));
                            holder.userStatus.setTextColor(Color.rgb(60, 179, 113));
                            holder.profileImage.setBorderColor(Color.rgb(60, 179, 113));

                            RootRef.child("Groups").child(currentUserID).child(groupName)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child("image").exists()){
                                                GroupImage = snapshot.child("image").getValue().toString();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                            
                            profileMap = new HashMap<>();
                            profileMap.put("isSelected", "true");
                            profileMap.put("groupName", groupName);
                            profileMap.put("image",GroupImage);

                            RootRef.child("Groups").child(visit_user_id).child(groupName).updateChildren(profileMap);
                            RootRef.child("Group Member").child(groupName).child(visit_user_id).child(groupName).setValue("");
                            Toast.makeText(FriendsActivity.this, "Add friends", Toast.LENGTH_SHORT).show();
                            IsSelect = false;
                        }else{
                            holder.selectRadio.setVisibility(View.VISIBLE);
                            holder.selectRadio.setChecked(false);
                            holder.userName.setTextColor(Color.GRAY);
                            holder.userStatus.setTextColor(Color.GRAY);
                            holder.profileImage.setBorderColor(Color.GRAY);
                            RootRef.child("Groups").child(visit_user_id).child(groupName).removeValue();
                            RootRef.child("Group Member").child(groupName).child(visit_user_id).removeValue();
                            Toast.makeText(FriendsActivity.this, "Remove Friends", Toast.LENGTH_SHORT).show();
                            IsSelect = true;
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_request_display_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;
        RadioButton selectRadio;


        public ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status);
            selectRadio = itemView.findViewById(R.id.radio);
        }
    }
}