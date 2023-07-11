package com.android.TikTak.FirebaseLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.TikTak.Activities.MainActivity;
import com.android.TikTak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        if (user!=null){
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_welcome);


    }

    public void clickButton(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.btnRegister:
                startActivity(new Intent(WelcomeActivity.this,RegisterActivity.class));
                finish();
                break;

        }
    }
}