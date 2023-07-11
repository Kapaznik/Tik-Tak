package com.android.TikTak.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.TikTak.Activities.EditItemActivity;
import com.android.TikTak.Configs;
import com.android.TikTak.Models.NewItemModel;
import com.android.TikTak.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.RecyclerviewHolder> {

    LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<NewItemModel> dataModelArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userID;

    public void refreshData(ArrayList<NewItemModel> newDataList) {
        dataModelArrayList = newDataList;
        notifyDataSetChanged();
    }

    public MyItemAdapter(Activity context, ArrayList<NewItemModel> dataModelArrayList,
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
        View view = layoutInflater.inflate(R.layout.layout_my_item, viewGroup, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder recyclerviewHolder, final int i) {
        recyclerviewHolder.tvItemName.setText(dataModelArrayList.get(i).getItemName());
        recyclerviewHolder.tvItemPrice.setText(String.valueOf(dataModelArrayList.get(i).getItemPrice()));

        recyclerviewHolder.imgDeleteItem.setOnClickListener(view -> {
            databaseReference =
                    firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER +
                            "/" + userID +
                            "/myItems/" + dataModelArrayList.get(i).getItemKey());

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Do you want to delete this item?")
                    .setTitle("Delete item");

            builder.setPositiveButton("Yes", (dialog, id) -> databaseReference.removeValue().addOnCompleteListener(task -> {
                Toast.makeText(activity, "Item Deleted!", Toast.LENGTH_SHORT).show();
                dataModelArrayList.remove(i);
                notifyDataSetChanged();

                if (dataModelArrayList.isEmpty()) {
                    noItems(activity);
                }
            }));

            builder.setNegativeButton("No", (dialog, id) -> {
                // User cancelled the dialog. No action is needed.
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        recyclerviewHolder.imgEditItem.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EditItemActivity.class);
            intent.putExtra("PATH", Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER +
                    "/" + userID +
                    "/myItems/" + dataModelArrayList.get(i).getItemKey());
            activity.startActivity(intent);
        });

        recyclerviewHolder.imgSetAsSold.setOnClickListener(view -> showConfirmationDialog(i));
    }

    private void showConfirmationDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_sale_dialog, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        TextInputEditText etUserEmail = dialogView.findViewById(R.id.etMail);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(view -> alertDialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            String userEmail = Objects.requireNonNull(etUserEmail.getText()).toString().replace(" ", "");
            DatabaseReference usersRef = firebaseDatabase.getReference("TikTak/Users");
            Query query = usersRef.orderByChild("UID/mail").equalTo(userEmail);

            ProgressDialog progressDialog = ProgressDialog.show(activity, "", "Updating...");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String uid = userSnapshot.getKey();
                            DatabaseReference itemBoughtRef = firebaseDatabase.getReference(
                                    "TikTak/Users/" + uid + "/UID/itemBought");
                            itemBoughtRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        int itemBought = dataSnapshot.getValue(Integer.class);
                                        itemBought++;
                                        itemBoughtRef.setValue(itemBought);

                                        DatabaseReference itemSoldRef = usersRef.child(userID).child("UID/itemSold");
                                        itemSoldRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    int itemSold = dataSnapshot.getValue(Integer.class);
                                                    itemSold++;
                                                    itemSoldRef.setValue(itemSold);

                                                    DatabaseReference deleteItem = firebaseDatabase.getReference(Configs.FIREBASE_MAIN_KEY + Configs.KEY_USER + "/" + userID + "/myItems/" + dataModelArrayList.get(i).getItemKey());
                                                    deleteItem.removeValue().addOnCompleteListener(task -> {
                                                        Toast.makeText(activity, "The item has been sold. The item has been removed from your Profile!", Toast.LENGTH_SHORT).show();
                                                        dataModelArrayList.remove(i);
                                                        notifyDataSetChanged();

                                                        if (dataModelArrayList.isEmpty()) {
                                                            noItems(activity);
                                                        }

                                                        alertDialog.dismiss();
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        });

        alertDialog.show();
    }

    public void noItems(Activity activity) {
        RelativeLayout container = new RelativeLayout(activity);
        container.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        TextView textView = new TextView(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setText("No items to display");
        textView.setTextSize(24);

        container.addView(textView);
        activity.setContentView(container);
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    static class RecyclerviewHolder extends RecyclerView.ViewHolder {
        ImageView imgSetAsSold, imgEditItem, imgDeleteItem;
        TextView tvItemName, tvItemPrice;

        public RecyclerviewHolder(@NonNull final View itemView) {
            super(itemView);

            imgSetAsSold = itemView.findViewById(R.id.imgSetAsSold);
            imgEditItem = itemView.findViewById(R.id.imgEditItem);
            imgDeleteItem = itemView.findViewById(R.id.imgDeleteItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}
