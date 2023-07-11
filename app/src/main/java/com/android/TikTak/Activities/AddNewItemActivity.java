package com.android.TikTak.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class AddNewItemActivity extends AppCompatActivity {
    private TextInputEditText etItemName, etItemPrice, etItemColor, etLocation;
    private Spinner spinnerItemType, spinnerItemSize, spinnerItemCondition;
    String selectType,selectSize,selectCondition;

    public LatLng latLng;
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8;
    ArrayList<Uri> uriArrayList=new ArrayList<>();
    ArrayList<String> uriPostUri=new ArrayList<>();
    NewItemModel newItemModel;
    private String itemType, itemSize, itemCondition, itemName, itemPrice, itemColor, strLocation;
    FusedLocationProviderClient mFusedLocationClient;
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
        setContentView(R.layout.activity_add_new_item);
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
        //initialize textviews
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

        //fill spinner data
        spinnerItemType();
        spinnerItemSize();
        spinnerItemCondition();


        //add clickListener to imageViews
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img1);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img2);
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img3);
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img4);
            }
        });

        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img5);
            }
        });

        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img6);
            }
        });

        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img7);
            }
        });

        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndOpenGallery(img8);
            }
        });

        findViewById(R.id.btnClearImages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriArrayList.clear();
                clearImageViews();
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input values
                itemType = spinnerItemType.getSelectedItem().toString();
                itemSize = spinnerItemSize.getSelectedItem().toString();
                itemCondition = spinnerItemCondition.getSelectedItem().toString();
                itemName = etItemName.getText().toString().trim();
                itemPrice = etItemPrice.getText().toString().trim();
                itemColor = etItemColor.getText().toString().trim();

                // Check if any of the fields is empty
                if (itemType.isEmpty() || itemSize.isEmpty() || itemCondition.isEmpty() ||
                        itemName.isEmpty() || itemPrice.isEmpty() || itemColor.isEmpty() || strLocation.isEmpty() || uriArrayList.isEmpty()) {
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
                    } else if (uriPostUri.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please select at least one image.", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    strLocation = String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude);
                    uploadImagesToFirebaseStorage();
                }
            }
        });
        etLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etLocation.getWindowToken(), 0);
                    mapDialog();
                }
            }
        });
    }

    private void uploadImagesToFirebaseStorage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        showProgressDialog();
        int totalImageCount = uriArrayList.size();
        AtomicInteger uploadedImageCount = new AtomicInteger(0);

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
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        uriPostUri.add(imageUrl);
                        // You can add the imageUrl to a list
                        // For example: uploadedImageUrls.add(imageUrl);

                        // Increment the uploaded image count
                        int count = uploadedImageCount.incrementAndGet();

                        // Check if all images have been uploaded
                        if (count == totalImageCount) {
                            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                            // Upload complete, show the message
                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference=
                                    firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY+Configs.KEY_USER+"/"+firebaseAuth.getUid()+"/myItems").push();
                            // Create a NewItemModel object with the data
                            NewItemModel newItem = new NewItemModel(itemName, Double.parseDouble(itemPrice), itemColor,
                                    itemType, itemSize, itemCondition, strLocation,
                                    getCurrentDate(), uriPostUri);

                            databaseReference.setValue(newItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();

                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddNewItemActivity.this, "Your item has been added!", Toast.LENGTH_SHORT).show();

                                        // Refresh the app
                                        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(AddNewItemActivity.this, "Failed to add the item.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                        }
                    });


                }).addOnFailureListener(e -> {
                });
            }
        }
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 67);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 76);
        }

    }
    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
    public void mapDialog() {
        AlertDialog.Builder mapDialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_map_dialog, null);
        AlertDialog alertDialog = mapDialog.create();

        float cornerRadius = 10.0f;
        ShapeDrawable roundedCornerDrawable = new ShapeDrawable();
        roundedCornerDrawable.setShape(new RoundRectShape(new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius}, null, null));
        roundedCornerDrawable.getPaint().setColor(Color.TRANSPARENT);
        alertDialog.getWindow().setBackgroundDrawable(roundedCornerDrawable);
        MapView mapView = dialogView.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.onResume();


        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Set API key
                try {
                    if (!TextUtils.isEmpty(getString(R.string.google_maps_api_key))) {
                        String apiKey = getString(R.string.google_maps_api_key);
                        MapsInitializer.initialize(getApplicationContext());

                        // Show user's current location
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                googleMap.addMarker(new MarkerOptions().position(latLng));

                                Geocoder geocoder = new Geocoder(AddNewItemActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses.size() > 0) {
                                        String country = addresses.get(0).getAddressLine(0);
                                        strLocation = String.valueOf(country);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        // Map click listener
                        googleMap.setOnMapClickListener(latLng -> {
                            double latitude = latLng.latitude;
                            double longitude = latLng.longitude;
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                        });
                    } else {
                        Toast.makeText(AddNewItemActivity.this, "Google maps not available", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        dialogView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                etLocation.setText(strLocation);
            }
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
        if (imageView.getDrawable() == null) {
            openGallery();
        }
    }

    private int countEmptyImageViews() {
        int emptyCount = 0;
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
            return imageUri;  // Android 10+
        } else {
            // Android 9-
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
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ImageView imageView = getEmptyImageView();
            if (imageView != null) {
//                imageView.setImageBitmap(bitmap);
                LoadImageTask loadImageTask = new LoadImageTask();
                loadImageTask.execute(imageUri);
                uriArrayList.add(imageUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            Uri imageUri = uris[0];
            Bitmap bitmap = null;
            bitmap = resizeBitmap(imageUri,480,720);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ImageView imageView = getEmptyImageView();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);

                }
            }
        }
    }

    private ImageView getEmptyImageView() {
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

    private void spinnerItemType(){

        // Create an ArrayList
        ArrayList<String> spinnerValues = new ArrayList<>();
        // Retrieve values from strings.xml and add them to the ArrayList
        String[] values = getResources().getStringArray(R.array.itemTypeValues);
        spinnerValues.addAll(Arrays.asList(values));
        // Create an ArrayAdapter and attach it to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_items, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);
        spinnerItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectType=spinnerItemType.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterViewadapterView) {

            }
        });
    }
    private void spinnerItemSize(){

        // Create an ArrayList
        ArrayList<String> spinnerValues = new ArrayList<>();
        // Retrieve values from strings.xml and add them to the ArrayList
        String[] values = getResources().getStringArray(R.array.itemSizeValues);
        spinnerValues.addAll(Arrays.asList(values));
        // Create an ArrayAdapter and attach it to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_items, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemSize.setAdapter(adapter);
        spinnerItemSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectSize=spinnerItemSize.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void spinnerItemCondition(){

        // Create an ArrayList
        ArrayList<String> spinnerValues = new ArrayList<>();
        // Retrieve values from strings.xml and add them to the ArrayList
        String[] values = getResources().getStringArray(R.array.itemConditionValues);
        spinnerValues.addAll(Arrays.asList(values));
        // Create an ArrayAdapter and attach it to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_items, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemCondition.setAdapter(adapter);
        spinnerItemCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectCondition=spinnerItemCondition.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void selectImageClick(View view) {
        switch (view.getId()){

            case R.id.img1:

                break;
            case R.id.img2:

                break;
            case R.id.img3:

                break;
            case R.id.img4:

                break;
            case R.id.img5:

                break;
            case R.id.img6:

                break;
            case R.id.img7:

                break;
            case R.id.img8:

                break;

        }
    }
}