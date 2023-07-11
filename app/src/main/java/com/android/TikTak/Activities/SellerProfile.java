package com.android.TikTak.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.TikTak.Configs;
import com.android.TikTak.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerProfile extends AppCompatActivity {
    TextInputEditText tvFirstName,tvMail,tvLastname,tvPhone,tvMemberSince,tvItemSold,tvItemBought
            ,tvPoints;
    FirebaseDatabase database;
    DatabaseReference mRef;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set the database reference to the appropriate location
        database=FirebaseDatabase.getInstance();
        mRef =
                database.getReference().child(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER + "/" + getIntent().getExtras().getString("userid") + "/UID");
        //get data
        getUserData();

        // Find the TextViews by their respective IDs
        tvFirstName = findViewById(R.id.tvFirstName);
        tvMail = findViewById(R.id.tvMail);
        tvLastname = findViewById(R.id.tvLastname);
        tvPhone = findViewById(R.id.tvPhone);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvItemSold = findViewById(R.id.tvItemSold);
        tvItemBought = findViewById(R.id.tvItemBought);
        tvPoints = findViewById(R.id.tvPoints);

        findViewById(R.id.btnContactToSeller).setOnClickListener(this::showPopupMenu);

    }
    private void sendWhatsAppMessage(String phoneNumber) {
        try {
                 // Create the intent with the appropriate action and data
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));


                // Start the WhatsApp intent
                startActivity(intent);


        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur such as WhatsApp is not installed
            Toast.makeText(this, "WhatsApp is not installed on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            // Perform actions based on the clicked item in the popup menu
            switch (item.getItemId()) {
                case R.id.whatsapp:
                    // Actions to be performed when the first item is clicked
                    sendWhatsAppMessage(tvPhone.getText().toString());
                    return true;
                case R.id.mail:
                    // Actions to be performed when the second item is clicked
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+tvMail.getText().toString())));
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    public void getUserData() {


        // Retrieve user data from Firebase Realtime Database
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Set the retrieved user data to the respective TextViews
                tvFirstName.setText(dataSnapshot.child("firstName").getValue(String.class));
                tvLastname.setText(dataSnapshot.child("lastName").getValue(String.class));
                tvMemberSince.setText(dataSnapshot.child("registerDate").getValue(String.class));
                tvPhone.setText(dataSnapshot.child("phone").getValue(String.class));
                tvMail.setText(dataSnapshot.child("mail").getValue(String.class));
                // Get the values of itemSold and itemBought
                Integer itemSold = dataSnapshot.child("itemSold").getValue(Integer.class);
                Integer itemBought = dataSnapshot.child("itemBought").getValue(Integer.class);

// Check if the values are null and assign them zero if null
                if (itemSold == null) {
                    itemSold = 0;
                }
                if (itemBought == null) {
                    itemBought = 0;
                }

// Set the values of itemSold and itemBought to the respective TextViews
                tvItemSold.setText(String.valueOf(itemSold));
                tvItemBought.setText(String.valueOf(itemBought));

                // Calculate and set the value of points based on itemSold and itemBought
                tvPoints.setText(String.valueOf((itemSold * 2) + itemBought));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }


}