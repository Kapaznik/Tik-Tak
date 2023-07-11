package com.android.TikTak.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.TikTak.Configs;
import com.android.TikTak.FirebaseLogin.WelcomeActivity;
import com.android.TikTak.Fragments.DonateFragment;
import com.android.TikTak.Fragments.HomeFragment;
import com.android.TikTak.Fragments.MapsFragment;
import com.android.TikTak.Fragments.ProfileFragment;
import com.android.TikTak.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView tvHome;
    private TextView tvProfile;
    private TextView tvDonate;
    private ImageView imgHome;
    private ImageView imgProfile;
    private ImageView imgDonate;
    LinearLayout topMenuBar;
    FirebaseAuth firebaseAuth;

    ImageView lastImageviewTopMenu;
    TextView lastTextviewTopMenu;
    public void checkpermission(String TYPE){
        androidx.appcompat.app.AlertDialog.Builder permissionDialog =
                new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,
                        R.style.AlertDialogCustom));
        permissionDialog.setTitle(getResources().getString(R.string.permission_title));
        permissionDialog.setIcon(R.drawable.vec_perm_dialog);
        permissionDialog.setMessage(getResources().getString(R.string.permission_details));

        /*Exit button*/
        permissionDialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
            if (TYPE.equals("TIRAMISU")){requestPermissions(Configs.TIRAMISU_PERM,21);}else if (TYPE.equals("NORMAL")){requestPermissions(Configs.NORMAL_PERM,12);}
        });

        androidx.appcompat.app.AlertDialog ex_dia_builder = permissionDialog.create();
        ex_dia_builder.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //auth
        firebaseAuth=FirebaseAuth.getInstance();
        // Find TextViews
        tvHome = findViewById(R.id.tvHome);
        tvProfile = findViewById(R.id.tvProfile);
        tvDonate = findViewById(R.id.tvDonate);

        // Find ImageViews
        imgHome = findViewById(R.id.imgHome);
        imgProfile = findViewById(R.id.imgProfile);
        imgDonate = findViewById(R.id.imgDonate);

        // Find ConstraintLayouts
        topMenuBar = findViewById(R.id.topMenuBar);

        //select default fragment
        bottomLastColor();
        bottomSelector(imgHome,tvHome);
        HomeFragment homeFragment=new HomeFragment();
        FragmentTransaction transHome=getSupportFragmentManager().beginTransaction();
        transHome.replace(R.id.fragmentContainer,homeFragment,"HOME");
        transHome.addToBackStack(null);
        transHome.commit();
        topMenuBar.setVisibility(View.VISIBLE);
        //add new item
        findViewById(R.id.addNewItem).setOnClickListener(view -> startActivity(
                new Intent(MainActivity.this, AddNewItemActivity.class)));

        //permission check
        //check android version. if version bigger than API 33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {


            } else {
                checkpermission("TIRAMISU");
            }
        }
        //if version small than android 33
        else {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


            } else {
                checkpermission("NORMAL");
            }
        }
    }

    //change last color bottom menu
    ImageView lastImageviewBottomMenu;
    TextView lastTextviewBottomMenu;
    //change menu item color
    public void bottomSelector(ImageView imageView, TextView textView) {

        lastImageviewBottomMenu = imageView;
        lastTextviewBottomMenu = textView;
        imageView.setImageTintList(ContextCompat.getColorStateList(this,R.color.menuEnable));
        textView.setTextColor(ContextCompat.getColor(this,R.color.menuEnable));}
    public void bottomLastColor() {
        try {
            lastImageviewBottomMenu.setImageTintList(ContextCompat.getColorStateList(this,R.color.menuDisable));
            lastTextviewBottomMenu.setTextColor(ContextCompat.getColor(this,R.color.menuDisable));

        } catch (Exception ignored) {
        }

    }

    public void topLastColor() {
        try {
            lastImageviewTopMenu.setImageTintList(ContextCompat.getColorStateList(this,R.color.menuDisable));
            lastTextviewTopMenu.setTextColor(ContextCompat.getColor(this,R.color.menuDisable));

        } catch (Exception ignored) {
        }

    }

    @SuppressLint("NonConstantResourceId")
    public void ClickMenu(View view) {
        switch (view.getId()){
            case R.id.btnProfile:
                ProfileFragment profileFragment=new ProfileFragment();
                FragmentTransaction profileTrans= getSupportFragmentManager().beginTransaction();
                profileTrans.replace(R.id.fragmentContainer,profileFragment,"PROFILE");
                profileTrans.commit();
                topMenuBar.setVisibility(View.GONE);
                bottomLastColor();
                bottomSelector(imgProfile,tvProfile);
                break;

            case R.id.btnHome:
                HomeFragment homeFragment=new HomeFragment();
                FragmentTransaction transHome=getSupportFragmentManager().beginTransaction();
                transHome.replace(R.id.fragmentContainer,homeFragment,"HOME");
                transHome.addToBackStack(null);
                transHome.commit();
                topMenuBar.setVisibility(View.VISIBLE);
                bottomLastColor();
                bottomSelector(imgHome,tvHome);
                topLastColor();
                break;

            case R.id.btnDonate:
                DonateFragment donateFragment=new DonateFragment();
                FragmentTransaction transDonate=getSupportFragmentManager().beginTransaction();
                transDonate.replace(R.id.fragmentContainer,donateFragment,"DONATE");
                transDonate.commit();
                topMenuBar.setVisibility(View.GONE);
                bottomLastColor();
                bottomSelector(imgDonate,tvDonate);
                break;
            case R.id.btnLogout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnMapView:

                MapsFragment mapsFragment=new MapsFragment();
                FragmentTransaction transMap=getSupportFragmentManager().beginTransaction();
                transMap.replace(R.id.fragmentContainer,mapsFragment,"MAP");
                transMap.addToBackStack(null);
                transMap.commit();
                break;
        }
    }

}