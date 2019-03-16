package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.CategoryProducts;
import com.itsoluation.vavisa.darhaa.common.CurrentCategoryDetails;
import com.itsoluation.vavisa.darhaa.fargments.SubCartegory;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private String[] category_ids, category_images, category_names, isSubs, isProds;
    Context context;

    // data is passed into the constructor
    public CategoryAdapter( String[] ids, String[] images, String[] names, String[] subs, String[] prods) {
        this.category_ids = ids;
        this.category_images = images;
        this.category_names = names;
        this.isSubs = subs;
        this.isProds = prods;

        Log.i("nnnn", String.valueOf(names.length));
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.category_name.setText(Html.fromHtml(category_names[position]).toString());
        Picasso.with(context).load(category_images[position]).into(holder.category_image);

        if(isSubs[position].equals("false")){
            holder.category_prods.setText(isProds[position]+" "+ context.getResources().getString(R.string.items));
        }


        holder.setItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                CurrentCategoryDetails.category_name = category_names[position];
                CurrentCategoryDetails.category_id = category_ids[position];
                if(isSubs[position].equals("false"))
                    context.startActivity(new Intent(context, CategoryProducts.class));
                else
                    context.startActivity(new Intent(context, SubCartegory.class));
            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return category_ids.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView category_name, category_prods;
        ImageView category_image;
        private RecyclerViewItemClickListener mClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
            category_image = itemView.findViewById(R.id.com_photo);
            category_prods = itemView.findViewById(R.id.category_count);
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
        return category_names[id];
    }
}