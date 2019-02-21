package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;

import java.util.ArrayList;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {
    private int shop_id;

    private Context context;
    private ArrayList<String> gallery_images;

    @Override
    public int getItemCount() {
        return gallery_images.size();
    }

    public MainSliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.gallery_images = images;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        ArrayList<String> arrayList = gallery_images;

        for (int i = 0; i < getItemCount(); i++) {
            if (position == i) {
                String image = arrayList.get(i);
                viewHolder.bindImageSlide(image);
            }
        }
    }
}

