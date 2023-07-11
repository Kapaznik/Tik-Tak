package com.android.TikTak.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.TikTak.Configs;
import com.android.TikTak.Activities.MyItemActivity;
import com.android.TikTak.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {


    TextInputEditText tvFirstName,tvMail,tvLastname,tvPhone,tvMemberSince,tvItemSold,tvItemBought
            ,tvPoints;

    private Button btnChangePassword,btnMyItems;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;
    public void getUserData() {
        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading...");

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
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                // Handle database error
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user
        // Set the database reference to the appropriate location
        database=FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        mRef = database.getReference().child(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER + "/" + userId + "/UID");
        getUserData();


        // Find the TextViews by their respective IDs
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvMail = view.findViewById(R.id.tvMail);
        tvLastname = view.findViewById(R.id.tvLastname);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        tvItemSold = view.findViewById(R.id.tvItemSold);
        tvItemBought = view.findViewById(R.id.tvItemBought);
        tvPoints = view.findViewById(R.id.tvPoints);
        btnMyItems = view.findViewById(R.id.btnMyItems);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        // Set click listener for the change password button
        btnChangePassword.setOnClickListener(v -> changeEmailOrPasswordFunc("Please enter the new password.", false));

        view.findViewById(R.id.btnMyItems).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), MyItemActivity.class)));
        return view;
    }

    private void signOutFunc() {
        // Sign out the user and finish the activity
        auth.signOut();

    }

    private void changeEmailOrPasswordFunc(String title, final boolean option) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText edit = new EditText(getActivity());
        builder.setPositiveButton(getString(R.string.change_txt), null);
        builder.setNegativeButton(getString(R.string.close_txt), null);
        // Set the layout params for the EditText
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        edit.setLayoutParams(lp);

        // Set the input type based on the option
        if (!option) {
            // Password type
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        builder.setTitle(title);
        builder.setView(edit);

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(view -> {
                    if (edit.getText().toString().isEmpty()) {
                        edit.setError("Please fill in the relevant field!");
                    } else {
                        if (!option) {
                            // Password change
                            changePassword();
                        }
                    }
                });
            }

            private void changePassword() {
                firebaseUser.updatePassword(edit.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Password changed successfully
                                Toast.makeText(getActivity(), "The password was changed.",
                                        Toast.LENGTH_LONG).show();
                                mAlertDialog.dismiss();
//                                    signOutFunc();
                            } else {
                                // Error changing password
                                edit.setText("");
                                Toast.makeText(getActivity(), task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();

                            }
                        });
            }
        });

        mAlertDialog.show();
    }
}