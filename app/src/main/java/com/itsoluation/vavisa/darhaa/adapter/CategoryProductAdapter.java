package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.Product;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.CurrentProductDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

    private String[] products_ids, products_thumbs, products_names, products_prices, products_specials, products_minimums;
    private ArrayList<Boolean> products_wishList,products_stock;
    Context context;
    Boolean isRelated;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public CategoryProductAdapter(Context context, String[] ids, String[] thumbs, String[] names, String[] prices, String[] specials,
                                  String[] minimums, ArrayList<Boolean> wishLists, ArrayList<Boolean> stocks, boolean isRelated) {
        this.mInflater = LayoutInflater.from(context);
        this.products_ids = ids;
        this.products_thumbs = thumbs;
        this.products_names = names;
        this.products_prices = prices;
        this.products_specials = specials;
        this.products_minimums = minimums;
        this.products_wishList = wishLists;
        this.products_stock = stocks;
        this.context = context;
        this.isRelated = isRelated;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;

       // that is for show in the recycler view of related products
       if(isRelated)
            view = mInflater.inflate(R.layout.item_product_related, parent, false);
        else
            view = mInflater.inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }
    
    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.product_name.setText(products_names[position]);
        holder.product_price.setText(products_prices[position]+" "+ context.getResources().getString(R.string.kd));
        if(!products_specials[position].equals("false")){
            holder.product_price.setPaintFlags(holder.product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.special_price.setVisibility(View.VISIBLE);
            holder.special_price.setText(products_specials[position]+" "+context.getResources().getString(R.string.kd));
        }

        if(!products_stock.get(position)){
            holder.special_price.setVisibility(View.VISIBLE);
            holder.special_price.setText(context.getResources().getString(R.string.out_of_stock));
        }
        Picasso.with(context).load(products_thumbs[position]).into(holder.product_image);

        if(products_wishList.get(position).toString().equals("true"))
            holder.product_wish.setImageResource(R.drawable.ic_fav);

        holder.setItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                CurrentProductDetails.product_id = products_ids[position];
                CurrentProductDetails.product_name = products_names[position];
                context.startActivity(new Intent(context, Product.class));
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
        TextView product_price, product_name,special_price;
        ImageView product_wish;
        ImageView product_image;
        private RecyclerViewItemClickListener mClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.item_image);
            product_price = itemView.findViewById(R.id.item_price);
            special_price = itemView.findViewById(R.id.item_price_special);
            product_name = itemView.findViewById(R.id.item_name);
            product_wish = itemView.findViewById(R.id.ic_wish);
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