package com.system.kuet.friendschat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDisplayImage;
    private StorageReference mImageStorage;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;
   // private    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();
        mDisplayImage = (CircleImageView) findViewById(R.id.user_single_image);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(mToolbar);

        mUsersList=(RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerOptions<Users>option=
                new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mUsersDatabase,Users.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder>firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Users,UsersViewHolder>(option) {


            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder,  int position, @NonNull Users model) {
                holder.setDisplayName(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image(), getApplicationContext());

                final String user_id=getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent9 = new Intent(UsersActivity.this, ProfileActivity.class);
                        intent9.putExtra("user_id", user_id);
                        startActivity(intent9);
                    }
                });

            }


            @NonNull
            @Override
            public UsersViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new UsersViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout,parent,false));
            }

        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }
    public class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }
        public void setUserImage(String thumb_image,Context ctx){

           CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
           // Toast.makeText(ctx, thumb_image, Toast.LENGTH_SHORT).show();
          //  Picasso.with(ctx).load(thumb_image).fit()
          //          .placeholder(R.drawable.default_avatar)
          //          .into(userImageView);
            LoadProfilePic(thumb_image, userImageView);


        }



    }
    private void LoadProfilePic(String uid, final CircleImageView img) {
        final File proPicFile = new File(this.getFilesDir(), uid);


            FirebaseStorage.getInstance().getReference().child("profile_images").child("thumbs").child(uid).getFile(proPicFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    img.setImageBitmap(BitmapFactory.decodeFile(proPicFile.getAbsolutePath()));
                    proPicFile.delete();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


    }
    }

