package com.developer.aaswin.lapit;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView mProfileImage;
    TextView mProfilename, mProfilestatus, mProfileFriendCount;
    Button mSendRequestButton,mDeclineRequestButtton;

    private DatabaseReference mDatabase;
    private ProgressBar mProgressDialog;
    private String current_friendStatus;

    //databse reference for user friend request database

    private DatabaseReference mUserFriendRequestDatabase;
    private FirebaseUser mUser;

    //database refertence for friend database

    private DatabaseReference mFriendDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        current_friendStatus = "not friends";

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mUserFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend Request");


        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");


        mProfileImage = findViewById(R.id.profile_userImage);
        mProfilename = findViewById(R.id.profile_userName);
        mProfilestatus = findViewById(R.id.profile_userStatus);
        mProfileFriendCount = findViewById(R.id.profile_TotalFriends);

        mProgressDialog = new ProgressBar(this);
        mProgressDialog.setMax(100);
        mProgressDialog.setVisibility(View.VISIBLE);


        mSendRequestButton = findViewById(R.id.profile_button_Friendrequest);
        mDeclineRequestButtton=findViewById(R.id.profile_button_declineFriendrequest);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProfilename.setText(dataSnapshot.child("name").getValue().toString());
                mProfilestatus.setText(dataSnapshot.child("status").getValue().toString());
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("image_thumbnail").getValue().toString()).into(mProfileImage);

                //Friend List/Request feature
                mUserFriendRequestDatabase.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("RequestType").getValue().toString();
                            if (req_type.equals("received")) {
                                current_friendStatus = "req_received";
                                mSendRequestButton.setText("Accept Friend Request");
                                mDeclineRequestButtton.setVisibility(View.VISIBLE);
                                mDeclineRequestButtton.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                current_friendStatus = "req_sent";
                                mSendRequestButton.setText("Cancel Friend Request");
                                mDeclineRequestButtton.setVisibility(View.INVISIBLE);
                                mDeclineRequestButtton.setEnabled(false);
                            }

                        } else {
                            mFriendDatabase.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        current_friendStatus = "friends";
                                        mSendRequestButton.setText("Unfriend");
                                        mDeclineRequestButtton.setVisibility(View.INVISIBLE);
                                        mDeclineRequestButtton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        //--------------Sending Friend request----------------
        mSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendRequestButton.setEnabled(false);
                if (current_friendStatus.equals("not friends")) {
                    mUserFriendRequestDatabase.child(mUser.getUid()).child(user_id).child("RequestType").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUserFriendRequestDatabase.child(user_id).child(mUser.getUid()).child("RequestType").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mSendRequestButton.setEnabled(true);
                                        current_friendStatus = "sent";
                                        mSendRequestButton.setText("Cancel Friend Request");
                                        mDeclineRequestButtton.setVisibility(View.INVISIBLE);
                                        mDeclineRequestButtton.setEnabled(false);
                                        Toast.makeText(ProfileActivity.this, "Succesfull", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                //-----------------Cancel Friend Request------------------------
                if (current_friendStatus.equals("sent")) {
                    mUserFriendRequestDatabase.child(mUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mUserFriendRequestDatabase.child(user_id).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mSendRequestButton.setEnabled(true);
                                    current_friendStatus = "not friends";
                                    mSendRequestButton.setText("Send Friend Request");
                                    mDeclineRequestButtton.setVisibility(View.INVISIBLE);
                                    mDeclineRequestButtton.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                if (current_friendStatus.equals("req_received")) {
                    final String date = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mUser.getUid()).child(user_id).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mUser.getUid()).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mUserFriendRequestDatabase.child(mUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mUserFriendRequestDatabase.child(user_id).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mSendRequestButton.setEnabled(true);
                                                    current_friendStatus = "friends";
                                                    mSendRequestButton.setText("Unfriend");
                                                    mDeclineRequestButtton.setVisibility(View.INVISIBLE);
                                                    mDeclineRequestButtton.setEnabled(false);
                                                }
                                            });
                                        }
                                    });

                                }
                            });

                        }
                    });

                }
                if(current_friendStatus.equals("friends")){
                    mFriendDatabase.child(mUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mSendRequestButton.setEnabled(true);
                                    current_friendStatus="not friends";
                                    mSendRequestButton.setText("Send Friend Request");
                                    mDeclineRequestButtton.setVisibility(View.INVISIBLE);
                                    mDeclineRequestButtton.setEnabled(false);
                                }
                            });

                        }
                    });


                }
            }

        });
    }
}







