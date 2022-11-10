package com.example.chatbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
        private List<Messages> userMessagesList;
        private FirebaseAuth mAuth;
        private DatabaseReference usersRef,receiverUsersRef;


        public MessageAdapter (List<Messages> userMessagesList)
        {
            this.userMessagesList = userMessagesList;
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder
        {
            public TextView senderMessageText, receiverMessageText,senderMessageTextDate,receiverMessageTextDate,
                    messageReceiverImageDate,messageSenderImageDate;
            public CircleImageView receiverProfileImage;
            public ImageView messageSenderPicture, messageReceiverPicture;


            public MessageViewHolder(@NonNull View itemView)
            {
                super(itemView);

                senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
                receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
                receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
                messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
                messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
                senderMessageTextDate = itemView.findViewById(R.id.sender_messsage_text_date);
                receiverMessageTextDate = itemView.findViewById(R.id.receiver_message_text_date);
                messageReceiverImageDate = itemView.findViewById(R.id.message_receiver_image_date);
                messageSenderImageDate = itemView.findViewById(R.id.message_sender_image_date);
            }
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.custom_messages_layout, viewGroup, false);

            mAuth = FirebaseAuth.getInstance();

            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
        {
            String messageSenderId = mAuth.getCurrentUser().getUid();
            Messages messages = userMessagesList.get(i);

            String fromUserID = messages.getFrom();
            String fromMessageType = messages.getType();
            String toReceiverId = messages.getTo();

            usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.hasChild("image"))
                    {
                        if(fromMessageType.equals("call")){
                            String receiverName = dataSnapshot.child("name").getValue().toString();
                            messageViewHolder.receiverMessageText.setText(receiverName + " called you");
                        }

                        String receiverImage = dataSnapshot.child("image").getValue().toString();

                        Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            receiverUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(toReceiverId);
            receiverUsersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("name"))
                            {
                                if(fromMessageType.equals("call")){
                                    String receiverName = snapshot.child("name").getValue().toString();
                                    messageViewHolder.senderMessageText.setText("You called " + receiverName);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            messageViewHolder.receiverMessageText.setVisibility(View.GONE);
            messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
            messageViewHolder.senderMessageText.setVisibility(View.GONE);
            messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
            messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);
            messageViewHolder.receiverMessageTextDate.setVisibility(View.GONE);
            messageViewHolder.senderMessageTextDate.setVisibility(View.GONE);
            messageViewHolder.messageReceiverImageDate.setVisibility(View.GONE);
            messageViewHolder.messageSenderImageDate.setVisibility(View.GONE);

            if (fromMessageType.equals("text"))
            {
                if (fromUserID.equals(messageSenderId))
                {
                    messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMessageTextDate.setVisibility(View.VISIBLE);

                    messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                    messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                    messageViewHolder.senderMessageText.setText(messages.getMessage());
                    messageViewHolder.senderMessageTextDate.setText(messages.getTime() + " - " + messages.getDate());
                }
                else
                {
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageTextDate.setVisibility(View.VISIBLE);

                    messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                    messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                    messageViewHolder.receiverMessageText.setText(messages.getMessage());
                    messageViewHolder.receiverMessageTextDate.setText(messages.getTime() + " - " + messages.getDate());
                }
            }
            else if (fromMessageType.equals("image")){
                if(fromUserID.equals(messageSenderId)){
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.messageSenderImageDate.setVisibility(View.VISIBLE);
                    messageViewHolder.messageSenderImageDate.setText(messages.getTime() + " - " + messages.getDate());
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
                }else{
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverImageDate.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverImageDate.setText(messages.getTime() + " - " + messages.getDate());
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                }
            }
            else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")){
                if(fromUserID.equals(messageSenderId)){
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chat-spatial-app.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=33dd7c94-9cd4-4015-9aee-a89cd2830317")
                            .into(messageViewHolder.messageSenderPicture);
                    //messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                    messageViewHolder.messageSenderImageDate.setVisibility(View.VISIBLE);
                    messageViewHolder.messageSenderImageDate.setText(messages.getTime() + " - " + messages.getDate());

                    //Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
                }else{
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chat-spatial-app.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=33dd7c94-9cd4-4015-9aee-a89cd2830317")
                            .into(messageViewHolder.messageReceiverPicture);
                    //messageViewHolder.messageReceiverPicture.setBackgroundResource(R.drawable.file);
                    messageViewHolder.messageReceiverImageDate.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverImageDate.setText(messages.getTime() + " - " + messages.getDate());
                    //Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                }
            }
            else if(fromMessageType.equals("call")){
                if(fromUserID.equals(messageSenderId)){
                    messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMessageTextDate.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                    messageViewHolder.senderMessageText.setTextColor(Color.GREEN);
                    messageViewHolder.senderMessageTextDate.setText(messages.getTime() + " - " + messages.getDate());
                }
                else{
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageTextDate.setVisibility(View.VISIBLE);

                    messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                    messageViewHolder.receiverMessageText.setTextColor(Color.RED);
                    messageViewHolder.receiverMessageTextDate.setText(messages.getTime() + " - " + messages.getDate());
                }
            }

            if(fromUserID.equals(messageSenderId)){
                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(userMessagesList.get(i).getType().equals("pdf") ||
                                userMessagesList.get(i).getType().equals("docx")){
                            CharSequence option[] = new CharSequence[]{
                                    "Delete for me",
                                    "Download and View This Document",
                                    "Cancel",
                                    "Delete for Everyone"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete message?");

                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deleteSentMessages(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==1){
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==3){
                                        deleteSentMessagesEveryOne(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                        else if(userMessagesList.get(i).getType().equals("text") ||
                                userMessagesList.get(i).getType().equals("call")){
                            CharSequence option[] = new CharSequence[]{
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Cancel",
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete message?");

                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deleteSentMessages(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==1){
                                        deleteSentMessagesEveryOne(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                        else if(userMessagesList.get(i).getType().equals("image")){
                            CharSequence option[] = new CharSequence[]{
                                    "View This Image",
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Cancel",
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete message?");

                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                        intent.putExtra("url",userMessagesList.get(i).getMessage());
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==1){
                                        deleteSentMessages(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==2){
                                        deleteSentMessagesEveryOne(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }else{
                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(userMessagesList.get(i).getType().equals("pdf") ||
                                userMessagesList.get(i).getType().equals("docx")){
                            CharSequence option[] = new CharSequence[]{
                                    "Delete for me",
                                    "Download and View This Document",
                                    "Cancel",
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete message?");

                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deleteReceiverMessages(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==1){
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                        else if(userMessagesList.get(i).getType().equals("text")
                         || userMessagesList.get(i).getType().equals("call")){
                            CharSequence option[] = new CharSequence[]{
                                    "Delete for me",
                                    "Cancel",
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete message?");

                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deleteReceiverMessages(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                        else if(userMessagesList.get(i).getType().equals("image")){
                            CharSequence option[] = new CharSequence[]{
                                    "View This Image",
                                    "Delete for me",
                                    "Cancel",
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete message?");

                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                        intent.putExtra("url",userMessagesList.get(i).getMessage());
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==1){
                                        deleteReceiverMessages(i,messageViewHolder);
                                        Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                        messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount()
        {
            return userMessagesList.size();
        }

        private void deleteSentMessages(final int position , final MessageViewHolder holder){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Messages").
                    child(userMessagesList.get(position).getFrom())
                    .child(userMessagesList.get(position).getTo())
                    .child(userMessagesList.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void deleteReceiverMessages(final int position , final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").
                child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        private void deleteSentMessagesEveryOne(final int position , final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").
                child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    rootRef.child("Messages").
                            child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                     });
                    }
            }
        });
    }

}
