package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.CategoryProducts;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private ArrayList<Products> favoritesList;
    private Context mContext;

    public FavoritesAdapter() {
        favoritesList = new ArrayList<Products>();
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav,parent,false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {

        holder.item_price.setText(favoritesList.get(position).getPrice());
        holder.item_name.setText(favoritesList.get(position).getName());
        Picasso.with(mContext).load(favoritesList.get(position).getThumb()).into(holder.fav_image);

        holder.setItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mContext.startActivity(new Intent(mContext, CategoryProducts.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView item_price,item_name;
        ImageView fav_image;
        private RecyclerViewItemClickListener mClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_price = itemView.findViewById(R.id.item_name);
            item_name = itemView.findViewById(R.id.item_name);
            fav_image = itemView.findViewById(R.id.com_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
         if(mClickListener!=null)
             mClickListener.onClick(v,getAdapterPosition());
        }

        public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }
    }

    public void addFavList(ArrayList<Products> favoritesData){
        favoritesList.addAll(favoritesData);
    }
}
