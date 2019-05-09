package com.system.kuet.friendschat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
private List<Messages> mMessageList;
private DatabaseReference mUserDatabase;
private Context con;

public MessageAdapter(List<Messages> mMessageList, Context context) {


        con=context;
        this.mMessageList = mMessageList;

        }

@Override
public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

        }

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageText;
    public CircleImageView profileImage;
    public TextView displayName;
    public ImageView messageImage;

    public MessageViewHolder(View view) {
        super(view);

        messageText = (TextView) view.findViewById(R.id.message_text_layout);
        profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
        displayName = (TextView) view.findViewById(R.id.name_text_layout);
        messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

    }
}

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);


                LoadProfilePic2(image,viewHolder.profileImage);
               // Picasso.with(viewHolder.profileImage.getContext()).load(image)
                 //      .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Toast.makeText(con,message_type,Toast.LENGTH_SHORT).show();
        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);

        } else {

            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            //Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                   //.placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);
            LoadProfilePic(c.getMessage(), viewHolder.messageImage);

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
    private void LoadProfilePic(String uid, final ImageView img) {
        final File proPicFile = new File(con.getFilesDir(), uid);


        if (proPicFile.exists()) {
            img.setImageBitmap(BitmapFactory.decodeFile(proPicFile.getAbsolutePath()));
        } else {
            FirebaseStorage.getInstance().getReference().child("message_images").child(uid).getFile(proPicFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    img.setImageBitmap(BitmapFactory.decodeFile(proPicFile.getAbsolutePath()));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


    }

    private void LoadProfilePic2(String uid, final ImageView img) {
        final File proPicFile = new File(con.getFilesDir(), uid);


        if (proPicFile.exists()) {
            img.setImageBitmap(BitmapFactory.decodeFile(proPicFile.getAbsolutePath()));
        } else {
            FirebaseStorage.getInstance().getReference().child("profile_images").child("thumbs").child(uid).getFile(proPicFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    img.setImageBitmap(BitmapFactory.decodeFile(proPicFile.getAbsolutePath()));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


    }

}
