package com.android.TikTak.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.TikTak.Adapter.MyItemAdapter;
import com.android.TikTak.Configs;
import com.android.TikTak.Models.NewItemModel;
import com.android.TikTak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyItemActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<NewItemModel> itemModelArrayList = new ArrayList<>();
    MyItemAdapter myItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);
        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Initialize widgets
        recyclerView = findViewById(R.id.recyclerView);
        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference =
                firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER + "/" + firebaseAuth.getUid() + "/myItems");

        myItemAdapter = new MyItemAdapter(this, itemModelArrayList, firebaseDatabase, firebaseAuth.getUid());
        recyclerView.setAdapter(myItemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemModelArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NewItemModel newItemModel = dataSnapshot.getValue(NewItemModel.class);
                    newItemModel.setItemKey(dataSnapshot.getKey());
                    itemModelArrayList.add(newItemModel);
                }

                myItemAdapter.notifyDataSetChanged();

                if (itemModelArrayList.isEmpty()) {
                    noItems();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void noItems(){

        // Create a RelativeLayout as the root container
        RelativeLayout container = new RelativeLayout(MyItemActivity.this);
        container.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        // Create a TextView and set its properties
        TextView textView = new TextView(MyItemActivity.this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setText("You don't have any items to sell.");
        textView.setTextSize(24);

        // Add the TextView to the container
        container.addView(textView);

        // Set the container as the content view of the activity
        setContentView(container);
    }
}
