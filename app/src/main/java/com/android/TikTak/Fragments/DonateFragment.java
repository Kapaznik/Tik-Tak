package com.android.TikTak.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.TikTak.R;
import com.google.android.material.textfield.TextInputEditText;

public class DonateFragment extends Fragment {
    TextInputEditText etOtherDonate;
    LinearLayout lnOneCoffee, lnTwoCoffee, lnThreeCoffee, lnFourCoffee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        // Find references to the views in the layout
        etOtherDonate = view.findViewById(R.id.etOtherDonate);
        lnOneCoffee = view.findViewById(R.id.lnOneCoffe);
        lnFourCoffee = view.findViewById(R.id.lnFourCoffe);
        lnThreeCoffee = view.findViewById(R.id.lnThreeCoffe);
        lnTwoCoffee = view.findViewById(R.id.lnTwoCoffe);

        // Set click listeners for the coffee LinearLayouts
        lnOneCoffee.setOnClickListener(this::coffeeClick);
        lnTwoCoffee.setOnClickListener(this::coffeeClick);
        lnThreeCoffee.setOnClickListener(this::coffeeClick);
        lnFourCoffee.setOnClickListener(this::coffeeClick);

        view.findViewById(R.id.btnDonate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://paypal.me/kapaznik"));
                startActivityForResult(browserIntent, 1);
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
                Toast.makeText(getActivity(), "Thank you for donate!", Toast.LENGTH_SHORT).show();

        }
    }

    // Method to handle coffee LinearLayout clicks
    @SuppressLint("NonConstantResourceId")
    public void coffeeClick(View view) {
        switch (view.getId()){
            case R.id.lnOneCoffe:
                etOtherDonate.setText("5");
                break;
            case R.id.lnTwoCoffe:
                etOtherDonate.setText("10");
                break;
            case R.id.lnThreeCoffe:
                etOtherDonate.setText("15");
                break;
            case R.id.lnFourCoffe:
                etOtherDonate.setText("20");
                break;
        }
    }
}
