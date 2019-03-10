package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.Interface.CartInterface;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.cartData.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<Product> productList;
    Context context;
    Boolean isCheckout;

    private CartInterface listener = null;

    public CartAdapter(boolean b) {
        productList = new ArrayList<>();
        this.isCheckout = b;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        context = parent.getContext();
        if(isCheckout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new ViewHolder(view);
        }else {
            if(viewType == R.layout.item_cart) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
                return new ViewHolder(view);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_layout, parent, false);
                return new ViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        if(position == productList.size()){
            holder.coupon_code_ed.setHint("enter coupon code");
        }else {
            holder.item_name.setText(productList.get(position).getName());
            holder.item_price.setText(productList.get(position).getTotal());
            holder.item_amount.setText(productList.get(position).getQuantity());
            Picasso.with(context).load(productList.get(position).getThumb()).into(holder.item_image);
        }
    }


    @Override
    public int getItemViewType(int position) {

        return (position == productList.size())? R.layout.coupon_layout : R.layout.item_cart ;

    }

    @Override
    public int getItemCount() {
        return productList.size()+1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable @BindView(R.id.item_name)
        TextView item_name;
        @Nullable @BindView(R.id.item_price)
        TextView item_price;
        @Nullable @BindView(R.id.item_image)
        ImageView item_image;
        @Nullable @BindView(R.id.item_option)
        TextView item_option;
        @Nullable @BindView(R.id.item_count)
        TextView item_amount;
        @Nullable @BindView(R.id.item_add)
        ImageView item_add;
        @Nullable @BindView(R.id.item_min)
        ImageView item_remove;

        @Nullable @BindView(R.id.foreground)
        public LinearLayout foreground;
        @Nullable @BindView(R.id.background)
        public LinearLayout background;

        @Nullable @BindView(R.id.coupon_code_ed)
        EditText coupon_code_ed;
        @Nullable @BindView(R.id.verify)
        Button verify;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            try {
                item_add.setOnClickListener(this);
                item_remove.setOnClickListener(this);

            }catch (Exception e){ }

            try {
                verify.setOnClickListener(this);

            }catch (Exception e){ }
        }

        @Override
        public void onClick(View v) {

            if (v == item_add) {
                if (listener != null)
                    listener.onItemClick(getAdapterPosition(), 2, item_amount,item_price);
            } else if (v == item_remove) {
                if (listener != null)
                    listener.onItemClick(getAdapterPosition(), 3, item_amount, item_price);
            }else if (v == verify) {
                if (listener != null)
                    listener.onItemClick(getAdapterPosition(), 1, coupon_code_ed, verify);
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
