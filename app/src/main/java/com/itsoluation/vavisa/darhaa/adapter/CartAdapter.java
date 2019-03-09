package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.itsoluation.vavisa.darhaa.Interface.CartInterface;
import com.itsoluation.vavisa.darhaa.Interface.EditDeleteAddrInterface;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;
import com.itsoluation.vavisa.darhaa.model.cartData.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<Product> productList;
    Context context;

    private CartInterface listener = null;

    public CartAdapter() {
        productList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        holder.item_name.setText(productList.get(position).getName());
        holder.item_price.setText(productList.get(position).getTotal());
        holder.item_amount.setText(productList.get(position).getQuantity());
        Picasso.with(context).load(productList.get(position).getThumb()).into(holder.item_image);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_name)
        TextView item_name;
        @BindView(R.id.item_price)
        TextView item_price;
        @BindView(R.id.item_image)
        ImageView item_image;
        @BindView(R.id.item_option)
        TextView item_option;
        @BindView(R.id.item_count)
        TextView item_amount;
        @BindView(R.id.item_add)
        ImageView item_add;
        @BindView(R.id.item_min)
        ImageView item_remove;

        @BindView(R.id.foreground)
        public LinearLayout foreground;
        @BindView(R.id.background)
        public LinearLayout background;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            item_add.setOnClickListener(this);
            item_remove.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (v == item_add) {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition(), 2, item_amount,item_price);
                }
                }
            else if (v == item_remove) {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition(), 3, item_amount, item_price);
                }
            }

        }


    }

    public void setListener(CartInterface listener) {
        this.listener = listener;
    }

    public void removeCart(int position)
    {
        productList.remove(position);
    }


    public void addAddress(ArrayList<Product> productList){
        this.productList.addAll(productList);
    }
}
