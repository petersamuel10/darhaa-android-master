package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

    ArrayList<Products> productData;
    Context context;
    Boolean isRelated;

    public RecyclerViewItemClickListener mClickListener;
    public CompositeDisposable compositeDisposable;


    public CategoryProductAdapter(ArrayList<Products> productData, boolean isRelated) {
        this.productData = productData;
        this.isRelated = isRelated;
    }

    public CategoryProductAdapter() {
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;

       // that is for show in the recycler view of related products
       if(isRelated)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_related, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        context = parent.getContext();
        return new ViewHolder(view);
    }
    
    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.bind(productData.get(position));

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return productData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView product_price, product_name,special_price,item_stock;
        ImageView product_wish;
        ImageView product_image;

        ViewHolder(View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.item_image);
            product_price = itemView.findViewById(R.id.item_price);
            special_price = itemView.findViewById(R.id.item_price_special);
            product_name = itemView.findViewById(R.id.item_name);
            product_wish = itemView.findViewById(R.id.ic_wish);
            item_stock = itemView.findViewById(R.id.item_stock);

            itemView.setOnClickListener(this);
            product_wish.setOnClickListener(this);
        }

        public void bind(Products product){

            product_name.setText(product.getName());
            product_price.setText(product.getPrice()+" "+ context.getResources().getString(R.string.kd));
            if(!product.getSpecial().equals("false")){
                product_price.setPaintFlags(product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                special_price.setVisibility(View.VISIBLE);
                special_price.setText(product.getSpecial()+" "+context.getResources().getString(R.string.kd));
            }else
                special_price.setVisibility(View.GONE);

            if(!product.getStock()){
                item_stock.setVisibility(View.VISIBLE);
            }else
                item_stock.setVisibility(View.GONE);

            Glide.with(context).load(product.getThumb()).placeholder(context.getResources().getDrawable(R.drawable.placeholder)).into(product_image);

            if(product.getWishList())
                product_wish.setImageResource(R.drawable.ic_fav);
            else
                product_wish.setImageResource(R.drawable.ic_fav_border);

        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                if (mClickListener != null) {
                    mClickListener.onClick(view, getAdapterPosition(), productData.get(getAdapterPosition()).getProduct_id()
                            , productData.get(getAdapterPosition()).getName(), 0);
                }
            }else if (view.getId() == R.id.ic_wish) {
                    if (mClickListener != null)
                        mClickListener.onClick(view, getAdapterPosition(),productData.get(getAdapterPosition()).getProduct_id() , null, 1);

                }
        }
    }

    public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setFilter(ArrayList<Products> categoryList_){

        productData = new ArrayList<>();
        productData.addAll(categoryList_);
        notifyDataSetChanged();
    }

    public void clearList(){
        productData = new ArrayList<>();
        notifyDataSetChanged();
    }

}