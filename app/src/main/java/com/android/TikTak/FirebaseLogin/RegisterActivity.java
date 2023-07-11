package com.android.TikTak.FirebaseLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.TikTak.Configs;
import com.android.TikTak.Activities.MainActivity;
import com.android.TikTak.R;
import com.android.TikTak.Models.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends Activity {

    private TextInputEditText etFirstName,etLastName,etPhoneNumber, etPass, etMail;

    private Button btnSignUp;
    private TextView tvAlreadyHaveAAccount;
    private FirebaseAuth mAuth;


    FirebaseDatabase database;
    FirebaseUser user;
    String userId;
    DatabaseReference mRef;
    CheckBox checkBoxTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etRegName);
        etPass = findViewById(R.id.etRegPass);
        etMail = findViewById(R.id.etRegEmail);
        etPhoneNumber = findViewById(R.id.etRegPhoneNumber);
        etLastName = findViewById(R.id.etRegLastName);
        btnSignUp = findViewById(R.id.btnRegister);
        checkBoxTerms = findViewById(R.id.checkBoxTerms);
        tvAlreadyHaveAAccount = findViewById(R.id.tvAlreadyHaveAAccount);

        //database and auth initialize
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        checkBoxTerms.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Terms of Use");
            String message = "By using this application, you agree to the following terms and conditions:\n\n" +
                    "- You must use the app in compliance with applicable laws and regulations.\n" +
                    "- You are responsible for any content you post on the platform.\n" +
                    "- You must respect the privacy of other users and not engage in harassment or abuse.\n" +
                    "- The app reserves the right to terminate or suspend your account for violations.\n\n" +
                    "Please note this app was built for learning purposes only.\n\n" +
                    "Feel free to visit my GitHub.";

            SpannableString spannableString = new SpannableString(message);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    // Open the GitHub link in a web browser
                    Uri uri = Uri.parse("https://github.com/Kapaznik");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            };

            spannableString.setSpan(clickableSpan, message.indexOf("GitHub"), message.indexOf("GitHub") + "GitHub".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setMessage(spannableString);

            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            TextView messageTextView = dialog.findViewById(android.R.id.message);
            if (messageTextView != null) {
                messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        // register user
        btnSignUp.setOnClickListener(v -> {
            if (etMail.getText().toString().isEmpty() || etFirstName.getText().toString().isEmpty()|| etPhoneNumber.getText().toString().isEmpty() || etPass.getText().toString().isEmpty()||!checkBoxTerms.isChecked()) {
                Toast.makeText(getApplicationContext(), "Please fill in the required fields!", Toast.LENGTH_SHORT).show();

            } else {

                registerFunc();
            }

        });

        tvAlreadyHaveAAccount.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }


    private void registerFunc() {

        mAuth.createUserWithEmailAndPassword(etMail.getText().toString(), etPass.getText().toString())
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        userId = user.getUid();
                        mRef =
                                database.getReference().child(Configs.FIREBASE_MAIN_KEY +Configs.KEY_USER).child(userId+
                                        "/UID");
                        mRef.setValue(new UserModel(etFirstName.getText().toString(),
                                etLastName.getText().toString(),etPhoneNumber.getText().toString(),
                                etMail.getText().toString(),getCurrentDate()));
                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

}