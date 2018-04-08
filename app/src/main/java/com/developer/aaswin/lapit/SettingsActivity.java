package com.developer.aaswin.lapit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity  extends AppCompatActivity {

    FirebaseUser CurrentUser;
    DatabaseReference msettings_database;
    DocumentReference msettings_firestore;

    private TextView settings_displayName, settings_Status;
    private CircleImageView mImageView;

    private Button StatusChange, ImageChange;

    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = CurrentUser.getUid();


        settings_displayName = findViewById(R.id.settings_displayname);
        settings_Status = findViewById(R.id.settings_status);
        StatusChange = findViewById(R.id.settings_changeStatus);
        ImageChange = findViewById(R.id.settings_imageChange);
        mImageView = findViewById(R.id.settings_ProfileImage);

        final String Current_user_UID = CurrentUser.getUid();
        msettings_database = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_user_UID);
        msettings_database.keepSynced(true);
        msettings_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("User Data", dataSnapshot.toString());

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                settings_displayName.setText(name);
                settings_Status.setText(status);

                if (!image.equals("default")) {
                    //Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.blank_avatar).into(mImageView);
                    Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.blank_avatar).into(mImageView, new Callback() {
                        @Override
                        protected Object clone() throws CloneNotSupportedException {
                            return super.clone();
                        }

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.blank_avatar).into(mImageView);

                        }});

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Retrieving Data from FireStore
        msettings_firestore = FirebaseFirestore.getInstance().collection("Users").document(Current_user_UID);
        msettings_firestore.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot != null) {

                    Users user = new Users();
                    if (user != null) {
                        if (user.getName() != null && user.getStatus() != null) {
                            String Name = documentSnapshot.get(user.getName()).toString();
                            String Status = documentSnapshot.get(user.getStatus()).toString();

                            Log.d(Current_user_UID, "Name" + Name);
                            Log.d(Current_user_UID, "Status" + Status);
                        }
                    }


                } else {
                    Log.d("Error", e.getMessage().toString());
                }

            }
        });

        StatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Status = settings_Status.getText().toString();
                startActivity(new Intent(getApplication(), StatusActivity.class).putExtra("CurrentStatus", Status));
                overridePendingTransition(0, 0);
            }
        });

        ImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    private void openFileChooser() {

        Intent galleryintent = new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryintent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File thumb_image = new File(resultUri.getPath());


                CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                Compressor compressor = new Compressor(this);
                compressor.setMaxWidth(200);
                compressor.setMaxHeight(200);
                compressor.setQuality(75);

                Bitmap image = null;
                try {
                    image = compressor.compressToBitmap(thumb_image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_data = baos.toByteArray();


                String Uid = CurrentUser.getUid();
                StorageReference mStorage = FirebaseStorage.getInstance().getReference("Users").child(Uid).child("ProfileImage.jpeg");
                final StorageReference mThumbStorage = FirebaseStorage.getInstance().getReference("Users").child(Uid).child("Thumb");
                mStorage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(SettingsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        final String Downloadurl = task.getResult().getDownloadUrl().toString();

                        UploadTask uploadTask = mThumbStorage.putBytes(thumb_data);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> Thumbtask) {

                                final String ThumbDownloadUrl=Thumbtask.getResult().getDownloadUrl().toString();

                                if (Thumbtask.isSuccessful()) {
                                    Map updateData=new HashMap();
                                    updateData.put("image",Downloadurl);
                                    updateData.put("image_thumbnail",ThumbDownloadUrl);
                                    msettings_database.updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(SettingsActivity.this, "Succesfully stored download url to Database", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(SettingsActivity.this, "Error in Uploading Thumbnail", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }
                });

            }


        }
    }
}






