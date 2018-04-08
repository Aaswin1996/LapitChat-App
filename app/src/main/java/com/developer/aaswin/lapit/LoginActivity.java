package com.developer.aaswin.lapit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText signinEmail, signinPassword;
    Button signIn;
    Toolbar mToolbar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signinEmail = findViewById(R.id.Signin_input_email);
        signinPassword = findViewById(R.id.Signin_input_password);
        signIn = findViewById(R.id.reg_SignInToAccount);

        mToolbar = findViewById(R.id.signin_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lapit Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EMAIL = signinEmail.getText().toString().trim();
                String PASSWORD = signinPassword.getText().toString().trim();
                if (EMAIL != null && PASSWORD != null) {
                    signinUser(EMAIL, PASSWORD);
                }
            }
        });
    }

    private void signinUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                } else {
                    Toast.makeText(getApplication(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
