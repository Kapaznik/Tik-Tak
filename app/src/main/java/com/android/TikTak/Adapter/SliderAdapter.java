package com.android.TikTak.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;


import com.android.TikTak.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class SliderAdapter extends PagerAdapter {

    // Context object
    Activity activity;
    LayoutInflater mLayoutInflater;
    ArrayList<ImageView> imageViews;
    ArrayList<String> imgBackgroundArrayList;

    // Viewpager Constructor
    public SliderAdapter(Activity activity,
                         ArrayList<String> imgBackgroundArrayList) {
        this.activity = activity;

        this.imgBackgroundArrayList = imgBackgroundArrayList;
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return imgBackgroundArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_layout, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageViewMain);

        LinearLayout sliderLinearLayout = itemView.findViewById(R.id.sliderLinearLayout);
        imageViews = new ArrayList<>();
        for (int i = 0; i < imgBackgroundArrayList.size(); i++) {
            ImageView image = new ImageView(activity);
            image.setLayoutParams(new ViewGroup.LayoutParams(32, 32));
            image.setMaxHeight(32);
            image.setMaxWidth(32);
            image.setPadding(5, 0, 5, 5);
            image.setImageResource(R.drawable.pager_circle_false);
            imageViews.add(image);

            // Adds the view to the layout
            sliderLinearLayout.addView(image);
        }
        // setting the image in the imageView
        Glide.with(activity).load(imgBackgroundArrayList.get(position)).into(imageView);
        //change circle when pager change
        imageViews.get(position).setImageResource(R.drawable.pager_circle_true);


        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {

        container.removeView((CardView) object);
    }

}
