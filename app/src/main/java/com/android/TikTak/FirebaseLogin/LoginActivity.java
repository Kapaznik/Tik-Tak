package com.android.TikTak.FirebaseLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.TikTak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {


    private Button btnLogin;
    TextView tvRegister, etRegEmail, etRegPass, tvResetPassword;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        btnLogin = findViewById(R.id.btnLogin);
        tvResetPassword = findViewById(R.id.tvResetPassword);
        tvRegister = findViewById(R.id.tvRegisterHere);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPass = findViewById(R.id.etRegPass);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser(); // Authenticated user

        // Check if user is already logged in (session exists)
        if (firebaseUser != null) {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(i);
            finish();
        }

        // Reset password button click listener
        tvResetPassword.setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = "e-mail";
            if (etRegEmail.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your e-mail address!", Toast.LENGTH_SHORT).show();
                return;
            }

            emailAddress = etRegEmail.getText().toString();
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password recovery email has been sent!", Toast.LENGTH_SHORT).show();
                        }

                    });
        });

        // Login button click listener
        btnLogin.setOnClickListener(v -> {
            userName = etRegEmail.getText().toString();
            userPassword = etRegPass.getText().toString();
            if (userName.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.blank_error),
                        Toast.LENGTH_LONG).show();
            } else {
                loginFunc();
            }
        });

        // Register button click listener
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Function to handle login
    private void loginFunc() {
        mAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(LoginActivity.this,
                task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                getResources().getString(R.string.succes_login),
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        // Handle error
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}