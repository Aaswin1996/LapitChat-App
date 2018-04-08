package com.developer.aaswin.lapit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {

    Toolbar mStatusToolbar;

    FirebaseUser Currentuser;
    DatabaseReference mDatabase;

    EditText mStatus;
    Button Changestatus;

    ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mStatusToolbar = findViewById(R.id.status_appbar);
        setSupportActionBar(mStatusToolbar);

        getSupportActionBar().setTitle(R.string.Status_change);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStatus = findViewById(R.id.current_status);
        Changestatus=findViewById(R.id.status_change);
        mProgress=findViewById(R.id.progressBar);

        String Status=getIntent().getStringExtra("CurrentStatus");
        mStatus.setText(Status);


        Currentuser = FirebaseAuth.getInstance().getCurrentUser();
        String Currnt_UID = Currentuser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(Currnt_UID);

        Changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setVisibility(View.VISIBLE);
                String Status=mStatus.getText().toString();
                mDatabase.child("status").setValue(Status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.setVisibility(View.GONE);
                            Toast.makeText(StatusActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplication(),SettingsActivity.class));
                            overridePendingTransition(0,0);

                        }
                        else{
                            Toast.makeText(StatusActivity.this, "Some error", Toast.LENGTH_SHORT).show();

                        }

                    }
                });



            }
        });


    }
}
