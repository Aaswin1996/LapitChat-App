package com.developer.aaswin.lapit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    public static final String USERS = "Users";
    private EditText mName,mEmail,mPassword;
    private Button mCreateAccount;
    private Toolbar mToolbar;
    FirebaseAuth mAuth;
    private ProgressBar mRegProgress;
    private DatabaseReference mDatabase;

    private FirebaseFirestore mFirestore;
    private DocumentReference mDocument;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName=findViewById(R.id.reg_input_name);
        mEmail=findViewById(R.id.reg_input_email);
        mPassword=findViewById(R.id.reg_input_password);
        mCreateAccount=findViewById(R.id.reg_createAccount);

        mRegProgress=new ProgressBar(this);

        mToolbar=findViewById(R.id.register_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lapit Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore=FirebaseFirestore.getInstance();

        mAuth=FirebaseAuth.getInstance();

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayName=mName.getText().toString();
                String Email=mEmail.getText().toString().trim();
                String Password=mPassword.getText().toString();
                if(displayName!=null && Email!=null && Password!=null){
                    registeruser(displayName,Email,Password);



                }



            }
        });

    }

    private void registeruser(final String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                    String UserId=currentUser.getUid();

                    //Storing user Data to Firebase Database
                    mDatabase= FirebaseDatabase.getInstance().getReference().child(USERS).child(UserId);
                    Users user=new Users(displayName,"Image","Hi This is Aaswin Sinha","default");
                    mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"UserAccount created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplication(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplication(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                        }
                    });

                    //Storing User Data to Firebase Firestore

                    mDocument=mFirestore.collection("Users").document(UserId);
                    mDocument.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterActivity.this, "Data stored to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });





                }
                else{
                    FirebaseException e=(FirebaseException)task.getException();
                    String error=e.toString();
                    Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
