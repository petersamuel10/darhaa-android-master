package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.CategoryProducts;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.ItemDetails;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.CurrentProductDetails;
import com.squareup.picasso.Picasso;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

    private String[] products_ids, products_thumbs, products_names, products_prices, products_specials, products_minimums;
    Context context;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public CategoryProductAdapter(Context context, String[] ids, String[] thumbs, String[] names, String[] prices, String[] specials, String[] minimums) {
        this.mInflater = LayoutInflater.from(context);
        this.products_ids = ids;
        this.products_thumbs = thumbs;
        this.products_names = names;
        this.products_prices = prices;
        this.products_specials = specials;
        this.products_minimums = minimums;
        this.context = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_top, parent, false);
        return new ViewHolder(view);
    }
    
    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.product_name.setText(products_names[position]);
        holder.product_price.setText(products_prices[position]);
        Picasso.with(context).load(products_thumbs[position]).into(holder.product_image);

        holder.setItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("ststst", "Clicked item: " + products_names[position]);
                CurrentProductDetails.product_id = products_ids[position];
                CurrentProductDetails.product_name = products_names[position];
                context.startActivity(new Intent(context, ItemDetails.class));
            }
        });

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return products_ids.length;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView product_price, product_name;
        ImageView product_image;
        private RecyclerViewItemClickListener mClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.item_image);
            product_price = itemView.findViewById(R.id.item_price);
            product_name = itemView.findViewById(R.id.item_desc);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return products_ids[id];
    }

}