package com.android.TikTak.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.TikTak.Configs;
import com.android.TikTak.Models.NewItemModel;
import com.android.TikTak.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class EditItemActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth firebaseAuth;
    private TextInputEditText etItemName, etItemPrice, etItemColor, etLocation;
    private Spinner spinnerItemType, spinnerItemSize, spinnerItemCondition;
    String selectType,selectSize,selectCondition;
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8;
    ArrayList<Uri> uriArrayList=new ArrayList<>();
    NewItemModel newItemModel;
    private String itemType, itemSize, itemCondition, itemName, itemPrice, itemColor, strLocation;
    FusedLocationProviderClient mFusedLocationClient;
    ArrayList<String> currentlyImages=new ArrayList<>();
    String key;
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
        setContentView(R.layout.activity_edit_item);
        //firebase initialize
         firebaseAuth=FirebaseAuth.getInstance();
        // Upload complete, show the message
         firebaseDatabase=FirebaseDatabase.getInstance();
        //enable back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //request location perm
        requestLocationPermission();
        //create model
        newItemModel=new NewItemModel();
        //map initializing
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //initialize spinners
        spinnerItemType = findViewById(R.id.spinnerItemType);
        spinnerItemSize = findViewById(R.id.spinnerItemSize);
        spinnerItemCondition = findViewById(R.id.spinnerItemCondition);
        //initialize  text input
        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        etItemColor = findViewById(R.id.etItemColor);
        etLocation = findViewById(R.id.etLocation);
        //initialize imageviews
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);
        img7 = findViewById(R.id.img7);
        img8 = findViewById(R.id.img8);

        List<ImageView> imageViewList = new ArrayList<>();
        imageViewList.add(img1);
        imageViewList.add(img2);
        imageViewList.add(img3);
        imageViewList.add(img4);
        imageViewList.add(img5);
        imageViewList.add(img6);
        imageViewList.add(img7);
        imageViewList.add(img8);



        // add imageview listener
        img1.setOnClickListener(v -> checkAndOpenGallery(img1));

        img2.setOnClickListener(v -> checkAndOpenGallery(img2));

        img3.setOnClickListener(v -> checkAndOpenGallery(img3));

        img4.setOnClickListener(v -> checkAndOpenGallery(img4));

        img5.setOnClickListener(v -> checkAndOpenGallery(img5));

        img6.setOnClickListener(v -> checkAndOpenGallery(img6));

        img7.setOnClickListener(v -> checkAndOpenGallery(img7));

        img8.setOnClickListener(v -> checkAndOpenGallery(img8));

        findViewById(R.id.btnClearImages).setOnClickListener(view -> {
            uriArrayList.clear();
            currentlyImages.clear();
            clearImageViews();
        });

        findViewById(R.id.btnSave).setOnClickListener(view -> {
// Get the input values
            itemType = spinnerItemType.getSelectedItem().toString();
            itemSize = spinnerItemSize.getSelectedItem().toString();
            itemCondition = spinnerItemCondition.getSelectedItem().toString();
            itemName = etItemName.getText().toString().trim();
            itemPrice = etItemPrice.getText().toString().trim();
            itemColor = etItemColor.getText().toString().trim();
            strLocation = etLocation.getText().toString().trim();

            // Check if any of the fields is empty
            if (itemType.isEmpty() || itemSize.isEmpty() || itemCondition.isEmpty() ||
                    itemName.isEmpty() || itemPrice.isEmpty() || itemColor.isEmpty() || strLocation.isEmpty() ) {
                // Check each field and show the corresponding toast message
                if (itemType.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select an item type.", Toast.LENGTH_SHORT).show();
                } else if (itemSize.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select an item size.", Toast.LENGTH_SHORT).show();
                } else if (itemCondition.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select an item condition.", Toast.LENGTH_SHORT).show();
                } else if (itemName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter an item name.", Toast.LENGTH_SHORT).show();
                } else if (itemPrice.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter an item price.", Toast.LENGTH_SHORT).show();
                } else if (itemColor.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter an item color.", Toast.LENGTH_SHORT).show();
                } else if (strLocation.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a location.", Toast.LENGTH_SHORT).show();
                }


            }
            else {
                if(uriArrayList.isEmpty()){
                    if (currentlyImages.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please select a image.",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        uploadImagesToFirebaseStorage();
                    }
                }else {
                    uploadImagesToFirebaseStorage();
                }


            }
        });
        etLocation.setOnFocusChangeListener((view, b) -> {
            if (b) {
                // If the EditText gains focus
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etLocation.getWindowToken(), 0);
                // Hide the soft keyboard
                mapDialog(); // Open the map dialog
            }
        });

        //get path from MyItemActivity.java
        mRef=firebaseDatabase.getReference(getIntent().getExtras().getString("PATH"));
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NewItemModel newItemModel1=snapshot.getValue(NewItemModel.class);
                key=snapshot.getKey();
                 etItemName.setText(newItemModel1.getItemName());
                etItemPrice.setText(String.valueOf(newItemModel1.getItemPrice()));
                etItemColor.setText(newItemModel1.getItemColor());
                etLocation.setText(newItemModel1.getItemLocation());
                //fill spinner data
                spinnerItemType(newItemModel1.getItemType());
                spinnerItemSize(newItemModel1.getItemSize());
                spinnerItemCondition(newItemModel1.getItemCondition());
                currentlyImages=newItemModel1.getItemImageURL();
                //get images list
                for (int i = 0; i <newItemModel1.getItemImageURL().size() ; i++) {
                    Glide.with(EditItemActivity.this).load(newItemModel1.getItemImageURL().get(i)).into(imageViewList.get(i));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void uploadImagesToFirebaseStorage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        showProgressDialog();
        int totalImageCount = uriArrayList.size();
        AtomicInteger uploadedImageCount = new AtomicInteger(0);

        if (totalImageCount == 0) {
            // If there are no images to upload
            DatabaseReference databaseReference =
                    firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER + "/" + firebaseAuth.getUid() + "/myItems/" + key);
            // Create a NewItemModel object with the data
            NewItemModel newItem = new NewItemModel(itemName, Double.parseDouble(itemPrice), itemColor,
                    itemType, itemSize, itemCondition, strLocation,
                    getCurrentDate(), currentlyImages);

            databaseReference.setValue(newItem).addOnCompleteListener(task -> {
                hideProgressDialog();
                Toast.makeText(EditItemActivity.this, "Your item has been added!",
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            // If there are images to upload
            for (int i = 0; i < totalImageCount; i++) {
                Uri imageUri = uriArrayList.get(i);
                Bitmap resizedBitmap = resizeBitmap(imageUri, 480, 720); // Set your desired target dimensions here

                if (resizedBitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Set the image quality and compression rate here
                    byte[] data = baos.toByteArray();

                    String fileName = i + "image_" + System.currentTimeMillis() + ".jpg";
                    StorageReference imageRef = storageRef.child(fileName);

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        currentlyImages.add(imageUrl);
                        // You can add the imageUrl to a list
                        // For example: uploadedImageUrls.add(imageUrl);
                        // Increment the uploaded image count
                        int count = uploadedImageCount.incrementAndGet();

                        // Check if all images have been uploaded
                        if (count == totalImageCount) {
                            DatabaseReference databaseReference =
                                    firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER + "/" + firebaseAuth.getUid() + "/myItems/" + key);
                            // Create a NewItemModel object with the data
                            NewItemModel newItem = new NewItemModel(itemName, Double.parseDouble(itemPrice), itemColor,
                                    itemType, itemSize, itemCondition, strLocation,
                                    getCurrentDate(), currentlyImages);

                            databaseReference.setValue(newItem).addOnCompleteListener(task -> {
                                hideProgressDialog();
                                Toast.makeText(EditItemActivity.this, "Your item has been added!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    })).addOnFailureListener(e -> {
                        // Handle image upload failure
                        // You can handle the error situation here
                    });
                }
            }
        }
    }

    private void requestLocationPermission() {
        // Check if the ACCESS_FINE_LOCATION permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the ACCESS_FINE_LOCATION permission
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, 67);
        }
        // Check if the ACCESS_COARSE_LOCATION permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the ACCESS_COARSE_LOCATION permission
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, 76);
        }
    }

    //get current date.
    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    @SuppressLint("MissingPermission")
    public void mapDialog() {
        AlertDialog.Builder mapDialog = new AlertDialog.Builder(this);

        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_map_dialog, null);
        AlertDialog alertDialog = mapDialog.create();

        // Set rounded corner background for the dialog window
        float cornerRadius = 10.0f;
        ShapeDrawable roundedCornerDrawable = new ShapeDrawable();
        roundedCornerDrawable.setShape(new RoundRectShape(new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius}, null, null));
        roundedCornerDrawable.getPaint().setColor(Color.TRANSPARENT);
        alertDialog.getWindow().setBackgroundDrawable(roundedCornerDrawable);

        // Initialize the MapView
        MapView mapView = dialogView.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.onResume();

        mapView.getMapAsync(googleMap -> {
            try {
                if (!TextUtils.isEmpty(getString(R.string.google_maps_api_key))) {
                    String apiKey = getString(R.string.google_maps_api_key);
                    MapsInitializer.initialize(getApplicationContext());

                    // Show user's current location
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                            strLocation = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                        }
                    });

                    // Map click listener
                    googleMap.setOnMapClickListener(latLng -> {
                        double latitude = latLng.latitude;
                        double longitude = latLng.longitude;
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        strLocation = String.valueOf(latitude) + "," + String.valueOf(longitude);
                    });
                } else {
                    Toast.makeText(EditItemActivity.this, "Google maps not available", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Set up the close button click listener
        dialogView.findViewById(R.id.btnClose).setOnClickListener(view -> {
            alertDialog.dismiss();
            etLocation.setText(strLocation);
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    ProgressDialog progressDialog; // Progress dialog object

    // Show the progress dialog
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Set the progress message
        progressDialog.setCancelable(false); // Disable canceling the progress
        progressDialog.show();
    }

    // Hide the progress dialog
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //clear all image views
    private void clearImageViews() {
        img1.setImageDrawable(null);
        img2.setImageDrawable(null);
        img3.setImageDrawable(null);
        img4.setImageDrawable(null);
        img5.setImageDrawable(null);
        img6.setImageDrawable(null);
        img7.setImageDrawable(null);
        img8.setImageDrawable(null);
    }

    private void checkAndOpenGallery(ImageView imageView) {
        // Check if the ImageView is empty (has no drawable)
        if (imageView.getDrawable() == null) {
            // If empty, open the gallery
            openGallery();
        }
    }

    private int countEmptyImageViews() {
        int emptyCount = 0;
        // Check each ImageView and count the empty ones
        if (img1.getDrawable() == null) {
            emptyCount++;
        }
        if (img2.getDrawable() == null) {
            emptyCount++;
        }
        if (img3.getDrawable() == null) {
            emptyCount++;
        }
        if (img4.getDrawable() == null) {
            emptyCount++;
        }
        if (img5.getDrawable() == null) {
            emptyCount++;
        }
        if (img6.getDrawable() == null) {
            emptyCount++;
        }
        if (img7.getDrawable() == null) {
            emptyCount++;
        }
        if (img8.getDrawable() == null) {
            emptyCount++;
        }
        return emptyCount;
    }

    private void openGallery() {
        // Open the gallery to select images
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {

            ClipData clipData = data.getClipData();
            if (clipData == null) {
                Uri imageUri = data.getData();
                Uri contentUri = getImageContentUri(imageUri);
                setImageViewImage(contentUri);
            } else {
                int count = Math.min(clipData.getItemCount(), countEmptyImageViews());
                for (int i = 0; i < count; i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    Uri contentUri = getImageContentUri(imageUri);
                    setImageViewImage(contentUri);
                }
            }
        }
    }
    private Uri getImageContentUri(Uri imageUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return imageUri;
        } else {

            String[] projection = {MediaStore.Images.Media._ID};
            Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                long id = cursor.getLong(columnIndex);
                cursor.close();
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            }
        }
        return null;
    }

    private Bitmap resizeBitmap(Uri imageUri, int targetWidth, int targetHeight) {
        try {
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(imageUri, "r");
                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    exif = new ExifInterface(fileDescriptor);
                    parcelFileDescriptor.close();
                }
            } else {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    exif = new ExifInterface(filePath);
                    cursor.close();
                }
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    originalBitmap = rotateBitmap(originalBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    originalBitmap = rotateBitmap(originalBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    originalBitmap = rotateBitmap(originalBitmap, 270);
                    break;
            }

            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();

            float scaleWidth = ((float) targetWidth) / originalWidth;
            float scaleHeight = ((float) targetHeight) / originalHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            return Bitmap.createBitmap(originalBitmap, 0, 0, originalWidth, originalHeight, matrix, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private void setImageViewImage(Uri imageUri) {
        try {
            // Load the bitmap from the image URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ImageView imageView = getEmptyImageView();
            if (imageView != null) {
                // Set the bitmap to the ImageView (using AsyncTask to load the image)
                LoadImageTask loadImageTask = new LoadImageTask();
                loadImageTask.execute(imageUri);
                uriArrayList.add(imageUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            Uri imageUri = uris[0];
            Bitmap bitmap = null;
            bitmap = resizeBitmap(imageUri, 480, 720);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                // Get the empty ImageView
                ImageView imageView = getEmptyImageView();
                if (imageView != null) {
                    // Set the bitmap to the ImageView
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private ImageView getEmptyImageView() {
        // Check each ImageView and return the first empty one
        if (img1.getDrawable() == null) {
            return img1;
        }
        if (img2.getDrawable() == null) {
            return img2;
        }
        if (img3.getDrawable() == null) {
            return img3;
        }
        if (img4.getDrawable() == null) {
            return img4;
        }
        if (img5.getDrawable() == null) {
            return img5;
        }
        if (img6.getDrawable() == null) {
            return img6;
        }
        if (img7.getDrawable() == null) {
            return img7;
        }
        if (img8.getDrawable() == null) {
            return img8;
        }
        return null;
    }

    private void spinnerItemType(String valueFromDatabase) {
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] values = getResources().getStringArray(R.array.itemTypeValues);
        spinnerValues.addAll(Arrays.asList(values));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_items, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);

        // Post a runnable to the spinner to set the selection after the layout is complete
        spinnerItemType.post(() -> {
            int spinnerPosition = adapter.getPosition(valueFromDatabase);
            spinnerItemType.setSelection(spinnerPosition);
        });

        spinnerItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectType = spinnerItemType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void spinnerItemSize(String valueFromDatabase) {
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] values = getResources().getStringArray(R.array.itemSizeValues);
        spinnerValues.addAll(Arrays.asList(values));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_items, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemSize.setAdapter(adapter);

        spinnerItemSize.post(() -> {
            int spinnerPosition = adapter.getPosition(valueFromDatabase);
            spinnerItemSize.setSelection(spinnerPosition);
        });

        spinnerItemSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectSize = spinnerItemSize.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void spinnerItemCondition(String valueFromDatabase) {
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] values = getResources().getStringArray(R.array.itemConditionValues);
        spinnerValues.addAll(Arrays.asList(values));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_items, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemCondition.setAdapter(adapter);

        spinnerItemCondition.post(() -> {
            int spinnerPosition = adapter.getPosition(valueFromDatabase);
            spinnerItemCondition.setSelection(spinnerPosition);
        });

        spinnerItemCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectCondition = spinnerItemCondition.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    @SuppressLint("NonConstantResourceId")
    public void selectImageClick(View view) {
        switch (view.getId()){

            case R.id.img1:
            case R.id.img8:
            case R.id.img7:
            case R.id.img6:
            case R.id.img5:
            case R.id.img4:
            case R.id.img3:
            case R.id.img2:

                break;

        }
    }
}