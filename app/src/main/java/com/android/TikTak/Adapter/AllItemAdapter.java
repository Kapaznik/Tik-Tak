package com.android.TikTak.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.TikTak.Activities.SellerProfile;
import com.android.TikTak.Configs;
import com.android.TikTak.Models.NewItemModel;
import com.android.TikTak.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AllItemAdapter extends RecyclerView.Adapter<AllItemAdapter.RecyclerviewHolder> {

    LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<NewItemModel> dataModelArrayList;
    FirebaseDatabase firebaseDatabase;

    String userID;


    public AllItemAdapter(Activity context, ArrayList<NewItemModel> dataModelArrayList,
                          FirebaseDatabase firebaseDatabase, String userID) {
        layoutInflater = LayoutInflater.from(context);
        this.activity = context;
        this.dataModelArrayList = dataModelArrayList;
        this.firebaseDatabase = firebaseDatabase;
        this.userID = userID;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = layoutInflater.inflate(R.layout.layout_all_items, viewGroup, false);

        return new RecyclerviewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder recyclerviewHolder, @SuppressLint("RecyclerView") final int i) {
        // Bind data to the views in the item layout
        recyclerviewHolder.tvItemName.setText("Title: " + dataModelArrayList.get(i).getItemName());
        recyclerviewHolder.tvItemPrice.setText("Price: " + dataModelArrayList.get(i).getItemPrice());
        recyclerviewHolder.tvItemColor.setText("Color: " + dataModelArrayList.get(i).getItemColor());
        recyclerviewHolder.tvItemCondition.setText("Condition: " + dataModelArrayList.get(i).getItemCondition());
        recyclerviewHolder.tvTotalImages.setText("Images: " + dataModelArrayList.get(i).getItemImageURL().size());
        recyclerviewHolder.tvItemType.setText("Type: " + dataModelArrayList.get(i).getItemType());
        recyclerviewHolder.tvItemCreationDate.setText("Date: " + dataModelArrayList.get(i).getItemCreation());
        Log.i("bdika",dataModelArrayList.get(i).getItemCreation());
        recyclerviewHolder.tvItemSize.setText("Size: " + dataModelArrayList.get(i).getItemSize());


        // Set a click listener for the "View Details" button
        recyclerviewHolder.btnViewDetails.setOnClickListener(view -> customAlertDialog(i));
    }
    public void customAlertDialog(int i) {
        TextInputEditText etItemName, etItemPrice,etItemSize, etItemColor, etItemType, etLocation,
                etItemCondition;
        ViewPager viewPager;
        AlertDialog.Builder rewardDialog = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_custom_dialog, null);
        AlertDialog alertDialog = rewardDialog.create();

        etItemName = dialogView.findViewById(R.id.etItemName);
        etItemPrice = dialogView.findViewById(R.id.etItemPrice);
        etItemColor = dialogView.findViewById(R.id.etItemColor);
        etLocation = dialogView.findViewById(R.id.etLocation);
        etItemType = dialogView.findViewById(R.id.etItemType);
        etItemSize = dialogView.findViewById(R.id.etItemSize);
        etItemCondition = dialogView.findViewById(R.id.etItemCondition);
        viewPager = dialogView.findViewById(R.id.viewPager);

        // Set up the ViewPager with a SliderAdapter
        SliderAdapter sliderAdapter = new SliderAdapter(activity, dataModelArrayList.get(i).getItemImageURL());
        viewPager.setAdapter(sliderAdapter);

        // Set the values from the data model to the views
        etItemName.setText(dataModelArrayList.get(i).getItemName());
        etItemPrice.setText(String.valueOf(dataModelArrayList.get(i).getItemPrice()));
        etItemColor.setText(dataModelArrayList.get(i).getItemColor());

        String coordinate = dataModelArrayList.get(i).getItemLocation();
        String[] coordinates = coordinate.split(",");

        //get full address of location

        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0); // Full address
                etLocation.setText(String.valueOf(fullAddress));

            } else {
                System.out.println("No address found for the given coordinates.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        etItemType.setText(dataModelArrayList.get(i).getItemType());
        etItemSize.setText(dataModelArrayList.get(i).getItemSize());
        etItemCondition.setText(dataModelArrayList.get(i).getItemCondition());


        float cornerRadius = 10.0f;
        ShapeDrawable roundedCornerDrawable = new ShapeDrawable();
        roundedCornerDrawable.setShape(new RoundRectShape(new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius}, null, null));
        roundedCornerDrawable.getPaint().setColor(Color.TRANSPARENT);
        alertDialog.getWindow().setBackgroundDrawable(roundedCornerDrawable);

        // Set click listener for the "Close" button
        dialogView.findViewById(R.id.btnClose).setOnClickListener(view -> alertDialog.dismiss());

        // Set click listener for the "Go to Profile" button
        dialogView.findViewById(R.id.btnGoToProfile).setOnClickListener(view -> {
            DatabaseReference mRef = firebaseDatabase.getReference().child(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot myItemsSnapshot = userSnapshot.child("myItems");
                        if (myItemsSnapshot.hasChild(dataModelArrayList.get(i).getItemKey())) {
                            String userId = userSnapshot.getKey(); // User ID of the matching user

                            Intent intent = new Intent(activity, SellerProfile.class);
                            intent.putExtra("userid", userId);
                            activity.startActivity(intent);
                            // Perform other actions
                            break; // Stop the loop after finding the desired user
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Actions to perform when cancelled
                }
            });
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }




    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortByDate(ArrayList<NewItemModel> itemModelArrayList) {
        dataModelArrayList=itemModelArrayList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterByCategory(ArrayList<NewItemModel> filteredList) {
        dataModelArrayList=filteredList;
        notifyDataSetChanged();
    }

    static class RecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName, tvItemColor, tvItemCondition, tvTotalImages, tvItemType, tvItemCreationDate, tvItemSize, tvItemPrice;
        Button btnViewDetails;

        public RecyclerviewHolder(@NonNull final View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            tvItemColor = itemView.findViewById(R.id.tvItemColor);
            tvItemCondition = itemView.findViewById(R.id.tvItemCondition);
            tvTotalImages = itemView.findViewById(R.id.tvTotalImages);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvItemCreationDate = itemView.findViewById(R.id.tvItemCreationDate);
            tvItemSize = itemView.findViewById(R.id.tvItemSize);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);


        }

    }


}
