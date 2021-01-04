package com.example.focusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Database.MyDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.textRegister);
        register.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.signInButton);
        loginButton.setOnClickListener(this);

        editTextEmail = (EditText)findViewById(R.id.inputEmail);
        editTextPassword = (EditText)findViewById(R.id.inputPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        dbHelper = new MyDbHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textRegister:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;
            case R.id.textForgotPass:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.signInButton:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length()<6){
            editTextPassword.setError("Password is too short!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    dbHelper.setCurrentUser(uid, "username", email);
                    startActivity(new Intent(LoginActivity.this, FragmentHolderActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Failed to login, please check your credentials!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}