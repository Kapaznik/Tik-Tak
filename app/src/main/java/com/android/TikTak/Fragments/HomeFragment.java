package com.android.TikTak.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.TikTak.Adapter.AllItemAdapter;
import com.android.TikTak.Configs;
import com.android.TikTak.Models.NewItemModel;
import com.android.TikTak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<NewItemModel> itemModelArrayList = new ArrayList<>();
    ArrayList<NewItemModel> mainModel = new ArrayList<>();
    AllItemAdapter allItemAdapter;

    String noItemsInCat = "No items to display in this category";
    String noItemsAtAll = "No items to display in the application";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER);

        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading...");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemModelArrayList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    DataSnapshot myItemsSnapshot = userSnapshot.child("myItems");
                    for (DataSnapshot itemSnapshot : myItemsSnapshot.getChildren()) {
                        NewItemModel newItemModel = itemSnapshot.getValue(NewItemModel.class);
                        newItemModel.setItemKey(itemSnapshot.getKey());
                        itemModelArrayList.add(newItemModel);
                    }
                }
                mainModel=itemModelArrayList;
                allItemAdapter = new AllItemAdapter(getActivity(),
                        itemModelArrayList, firebaseDatabase, firebaseAuth.getUid());
                recyclerView.setAdapter(allItemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                progressDialog.dismiss();
                if (itemModelArrayList.isEmpty()){
                    noItems(getView(),noItemsAtAll,"");
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    removeExistingTextView(getView());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });


        getActivity().findViewById(R.id.btnDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment =
                        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof HomeFragment) {
// Perform actions if the current fragment is of type YourFragmentClass
                    showDatePopupMenu(view);
                }else {
                    HomeFragment homeFragment=new HomeFragment();
                    FragmentTransaction transHome=
                            getActivity().getSupportFragmentManager().beginTransaction();
                    transHome.replace(R.id.fragmentContainer,homeFragment,"HOME");
                    transHome.addToBackStack(null);
                    transHome.commit();
                    }

            }
        });
        getActivity().findViewById(R.id.btnCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment =
                        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof HomeFragment) {
                showPopupMenu(view);
                }else
                {
                HomeFragment homeFragment=new HomeFragment();
                FragmentTransaction transHome=
                        getActivity().getSupportFragmentManager().beginTransaction();
                    transHome.addToBackStack(null);
                transHome.replace(R.id.fragmentContainer,homeFragment,"HOME");
                transHome.commit();

               }
            }
        });

        return view;
    }

    // Method to sort items from oldest to newest
    private void sortItemsByOldest() {
        Collections.sort(itemModelArrayList, new Comparator<NewItemModel>() {
            @Override
            public int compare(NewItemModel item1, NewItemModel item2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(item1.getItemCreation());
                    Date date2 = sdf.parse(item2.getItemCreation());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        if (itemModelArrayList.isEmpty()){
            noItems(getView(),noItemsAtAll,"");
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            removeExistingTextView(getView());
        }
    }

    // Method to sort items from newest to oldest
    private void sortItemsByNewest() {
        Collections.sort(itemModelArrayList, new Comparator<NewItemModel>() {
            @Override
            public int compare(NewItemModel item1, NewItemModel item2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(item1.getItemCreation());
                    Date date2 = sdf.parse(item2.getItemCreation());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        if (itemModelArrayList.isEmpty()){
            noItems(getView(),noItemsAtAll,"");
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            removeExistingTextView(getView());
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void showDatePopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.date_menu, popupMenu.getMenu());

        // Listen for click events on popup menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle the selected item
            switch (item.getItemId()) {
                case R.id.menu_oldest:
                    // Actions to be performed when "Oldest" item is selected
                    sortItemsByOldest();
                    allItemAdapter.sortByDate(itemModelArrayList);
                    break;
                case R.id.menu_newest:
                    // Actions to be performed when "Newest" item is selected
                    sortItemsByNewest();
                    allItemAdapter.sortByDate(itemModelArrayList);
                    break;

            }
            return true;
        });

        popupMenu.show();
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.category_menu, popupMenu.getMenu());

        // Listen for click events on popup menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle the selected item
            switch (item.getItemId()) {
                case R.id.menu_shirts:
                    // Actions to be performed when "Shirts" item is selected
                    filterItemsByCategory("Shirts");
                    break;
                case R.id.menu_coats:
                    // Actions to be performed when "Coats" item is selected
                    filterItemsByCategory("Coats");
                    break;
                case R.id.menu_shoes:
                    // Actions to be performed when "Shoes" item is selected
                    filterItemsByCategory("Shoes");
                    break;
                case R.id.menu_pants:
                    // Actions to be performed when "Pants" item is selected
                    filterItemsByCategory("Pants");
                    break;
                case R.id.menu_bags:
                    // Actions to be performed when "Bags" item is selected
                    filterItemsByCategory("Bags");
                    break;
                case R.id.menu_other:
                    filterItemsByCategory("Other");
                    break;
                case R.id.menu_clear:
                    allItemAdapter.sortByDate(mainModel);
                    break;
            }
            return true;
        });


        popupMenu.show();
    }


    private void filterItemsByCategory(String category) {
        ArrayList<NewItemModel> filteredList = new ArrayList<>();

        // Iterate through the itemModelArrayList and add items with matching category to filteredList
        for (NewItemModel item : itemModelArrayList) {
            if (item.getItemType().equals(category)) {
                filteredList.add(item);
            }
        }

        // Update the adapter with the filtered list
        allItemAdapter.filterByCategory(filteredList);

        if (filteredList.isEmpty()) {
            noItems(getView(), noItemsInCat, ": "+category);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            removeExistingTextView(getView());
        }
    }


    public void noItems(View view, String msg, String category) {
        String message = msg+ category;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        // Remove any existing TextView
        removeExistingTextView(view);

        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        textView.setText(message);

        textView.setTextSize(24);
        textView.setGravity(Gravity.CENTER);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.addView(textView, layoutParams);
        }
    }


    private void removeExistingTextView(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                if (child instanceof TextView) {
                    parent.removeView(child);
                    break;  // Remove only the first TextView found
                }
            }
        }
    }
}