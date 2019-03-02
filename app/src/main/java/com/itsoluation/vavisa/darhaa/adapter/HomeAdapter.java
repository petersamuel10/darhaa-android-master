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
import com.itsoluation.vavisa.darhaa.common.CurrentCategoryDetails;
import com.itsoluation.vavisa.darhaa.model.home.Catecory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    ArrayList<Catecory> categories_;
    public static Context context;
    private LayoutInflater mInflater;

    public static final int TYPE_FIRST_ITEM = 0;
    public static final int TYPE_ITEM = 1;

   // View view;


    // data is passed into the constructor
    public HomeAdapter() {
        categories_ = new ArrayList<>();
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        switch (viewType) {
            case TYPE_FIRST_ITEM:
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_top,null);
                return new ViewHolder(view);
            case TYPE_ITEM:
                final View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home,null);
                return new ViewHolder(view2);
            default:
                return null;
        }

    }
    
    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.category_name.setText(categories_.get(position).getName());
        Picasso.with(context).load(categories_.get(position).getImage()).into(holder.category_image);



        holder.setmClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                //CurrentCategoryDetails.category_name = [position];
                CurrentCategoryDetails.category_id = categories_.get(position).getCategory_id();
                context.startActivity(new Intent(context, CategoryProducts.class));
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return TYPE_FIRST_ITEM;
        else
            return TYPE_ITEM;
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return categories_.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView category_name;
        ImageView category_image;
        private RecyclerViewItemClickListener mClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.priceTitle);
            category_image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        // allows clicks events to be caught
        public void setmClickListener(RecyclerViewItemClickListener mClickListener) {
            this.mClickListener = mClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getAdapterPosition());
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView category_name;
        ImageView category_image;
        private RecyclerViewItemClickListener mClickListener;

        ViewHolder2(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.priceTitle);
            category_image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        // allows clicks events to be caught
        public void setmClickListener(RecyclerViewItemClickListener mClickListener) {
            this.mClickListener = mClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getAdapterPosition());
        }
    }

    // add categories comes from api
    public void addCategory(ArrayList<Catecory> newCategory) {categories_ = newCategory; }


}




  /*
    // convenience method for getting data at click position
    public String getItem(int id) {
        return category_names[id];
    }
*/



    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        void onItemClick(View view, int position);
    }
