package com.itsoluations.vavisa.darhaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itsoluations.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluations.vavisa.darhaa.Product;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.CurrentProductDetails;
import com.itsoluations.vavisa.darhaa.model.favorite.Products;

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

        holder.item_price.setText(favoritesList.get(position).getPrice()+" "+mContext.getResources().getString(R.string.kd));
        holder.item_name.setText(favoritesList.get(position).getName());
        Glide.with(mContext).load(favoritesList.get(position).getThumb())
                .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder)).into(holder.fav_image);

        holder.setItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position, String product_id, String product_name, int flag) {
                CurrentProductDetails.product_id = String.valueOf(favoritesList.get(position).getProduct_id());
                mContext.startActivity(new Intent(mContext, Product.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout background,foreground;
        TextView item_price,item_name;
        ImageView fav_image;

        private RecyclerViewItemClickListener mClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_price = itemView.findViewById(R.id.item_price);
            item_name = itemView.findViewById(R.id.item_name);
            fav_image = itemView.findViewById(R.id.com_photo);
            background = itemView.findViewById(R.id.background);
            foreground = itemView.findViewById(R.id.foreground);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
         if(mClickListener!=null)
             mClickListener.onClick(v,getAdapterPosition(),null,null,0);
        }

        public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }
    }

    public void addFavList(ArrayList<Products> favoritesData){
        favoritesList = new ArrayList<>();
        favoritesList.addAll(favoritesData);
        notifyDataSetChanged();
    }

    public void deleteItem(int position)
    {
        favoritesList.remove(position);
    }
}
