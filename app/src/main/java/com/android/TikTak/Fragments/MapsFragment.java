package com.android.TikTak.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.TikTak.Adapter.SliderAdapter;
import com.android.TikTak.Configs;
import com.android.TikTak.Models.NewItemModel;
import com.android.TikTak.R;
import com.android.TikTak.Activities.SellerProfile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsFragment extends Fragment  implements OnMapReadyCallback {


    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authListener;
    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;

    Activity activity;

    ArrayList<NewItemModel> itemModelArrayList =new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;



    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 67);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 76);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_maps, container, false);

        requestLocationPermission();//location perm request
        //firebase processes
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance();
        mRef = database.getReference().child(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER);
        getItemData();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);


        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
            if (!success) {

            }
        } catch (Resources.NotFoundException e) {

        }

        // Set API key
        try {
            if (!TextUtils.isEmpty(getString(R.string.google_maps_api_key))) {
                String apiKey = getString(R.string.google_maps_api_key);
                MapsInitializer.initialize(getActivity().getApplicationContext());

                // Show user's current location
                mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mMap.animateCamera(cameraUpdate);
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Google maps not available", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show user's current location
        try {
            LocationManager locationManager =
                    (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                String title = marker.getTitle();

                customAlertDialog(Integer.parseInt(marker.getId().replace("m","")));

                return true;
            }
        });


    }

    public void customAlertDialog(int i) {
        TextInputEditText etItemName, etItemPrice,itemSize, etItemColor, etItemType, etLocation,
                etItemCondition;
        ViewPager viewPager;
        AlertDialog.Builder rewardDialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_custom_dialog, null);
        AlertDialog alertDialog = rewardDialog.create();

        // Initialize views
        etItemName = dialogView.findViewById(R.id.etItemName);
        etItemPrice = dialogView.findViewById(R.id.etItemPrice);
        etItemColor = dialogView.findViewById(R.id.etItemColor);
        etLocation = dialogView.findViewById(R.id.etLocation);
        etItemType = dialogView.findViewById(R.id.etItemType);
        itemSize = dialogView.findViewById(R.id.etItemSize);
        etItemCondition = dialogView.findViewById(R.id.etItemCondition);
        viewPager = dialogView.findViewById(R.id.viewPager);

        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), itemModelArrayList.get(i).getItemImageURL());
        viewPager.setAdapter(sliderAdapter);

        // Set data to views
        etItemName.setText(itemModelArrayList.get(i).getItemName());
        etItemPrice.setText(String.valueOf(itemModelArrayList.get(i).getItemPrice()));
        etItemColor.setText(itemModelArrayList.get(i).getItemColor());
        String test = itemModelArrayList.get(i).getItemLocation();
        String[] coordinates = test.split(",");

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

        etItemType.setText(itemModelArrayList.get(i).getItemType());
        itemSize.setText(itemModelArrayList.get(i).getItemSize());
        etItemCondition.setText(itemModelArrayList.get(i).getItemCondition());

        float cornerRadius = 10.0f;
        ShapeDrawable roundedCornerDrawable = new ShapeDrawable();
        roundedCornerDrawable.setShape(new RoundRectShape(new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius}, null, null));
        roundedCornerDrawable.getPaint().setColor(Color.TRANSPARENT);
        alertDialog.getWindow().setBackgroundDrawable(roundedCornerDrawable);

        // Set click listener for Close button
        dialogView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // Set click listener for Go to Profile button
        dialogView.findViewById(R.id.btnGoToProfile).setOnClickListener(view -> mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot myItemsSnapshot = userSnapshot.child("myItems");
                    if (myItemsSnapshot.hasChild(itemModelArrayList.get(i).getItemKey())) {
                        String userId = userSnapshot.getKey(); // User's ID

                        Intent intent = new Intent(getActivity(), SellerProfile.class);
                        intent.putExtra("userid", userId);
                        startActivity(intent);
                        // Other actions you need to perform
                        break; // Stop the loop after finding the desired user
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Actions to perform in case of cancellation
            }
        }));

        alertDialog.setView(dialogView);
        alertDialog.show();
    }


    public void getItemData(){

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot myItemsSnapshot = userSnapshot.child("myItems");
                    for (DataSnapshot itemSnapshot : myItemsSnapshot.getChildren()) {
                        NewItemModel itemModel = itemSnapshot.getValue(NewItemModel.class);
                        itemModel.setItemKey(itemSnapshot.getKey());
                        itemModelArrayList.add(itemModel);
                    }
                }

                progressDialog.dismiss();
                for (NewItemModel itemModel : itemModelArrayList) {
                    String coordinateString = itemModel.getItemLocation();
                    String[] coordinateArray = coordinateString.split(",");
                    double latitude = Double.parseDouble(coordinateArray[0]);
                    double longitude = Double.parseDouble(coordinateArray[1]);
                    LatLng latLng = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title(itemModel.getItemName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

}