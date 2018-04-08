package com.developer.aaswin.lapit;

import android.content.Context;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUserList;

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        //Toolbar seetup
        mToolbar = findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Recyclerview
        mUserList = findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));




    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mDatabase, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users,useersViewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, useersViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull useersViewholder holder, final int position, @NonNull Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImageThumbnail(model.getImage_thumbnail(),getApplicationContext());

                holder.MItemView.setOnClickListener(new View.OnClickListener() {
                    String user_key=getRef(position).getKey();
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(UsersActivity.this,ProfileActivity.class);
                        intent.putExtra("user_id",user_key);
                        startActivity(intent);
                    }
                });


            }

            @Override
            public useersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(UsersActivity.this).inflate(R.layout.users_single_row,parent,false);
                return new useersViewholder(v);
            }
        };
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        firebaseRecyclerAdapter.notifyDataSetChanged();
        mUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();



    }
    public static class useersViewholder extends RecyclerView.ViewHolder{

        View MItemView;

        public useersViewholder(View itemView) {
            super(itemView);
            MItemView=itemView;
        }

        public void setName(String name) {
            TextView mUsername=MItemView.findViewById(R.id.users_DisplayName);
            mUsername.setText(name);
        }

        public void setStatus(String status) {
            TextView mUserDefaultStatus=MItemView.findViewById(R.id.users_status);
            mUserDefaultStatus.setText(status);
        }

        public void setImageThumbnail(String image_thumbnail,Context mContext) {
            CircleImageView mImage=MItemView.findViewById(R.id.users_profileImage);
            Picasso.with(mContext).load(image_thumbnail).placeholder(R.drawable.blank_avatar).fit().into(mImage);
        }
    }
}


