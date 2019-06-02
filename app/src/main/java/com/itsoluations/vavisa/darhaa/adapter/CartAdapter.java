package com.itsoluations.vavisa.darhaa.adapter;

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

import com.bumptech.glide.Glide;
import com.itsoluations.vavisa.darhaa.Interface.CartInterface;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.model.cartData.Options;
import com.itsoluations.vavisa.darhaa.model.cartData.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<Product> productList;
    Context context;
    Boolean isCheckout;

    private CartInterface listener = null;

    public CartAdapter(boolean b, List<Product> productList) {
        this.productList = productList;
        this.isCheckout = b;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        context = parent.getContext();
        if(isCheckout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_checkout, parent, false);
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

        // to show coupon code edit text
        if(position == productList.size()){
            try {
                holder.coupon_code_ed.setHint(context.getResources().getString(R.string.enter_coupon_code));
            }catch (Exception e){}

        }else {

            holder.item_name.setText(productList.get(position).getName());
            holder.item_price.setText(productList.get(position).getTotal());
            try {holder.item_amount.setText(productList.get(position).getQuantity());}catch (Exception e){}
               Glide.with(context).load(productList.get(position).getThumb()).placeholder(R.drawable.placeholder).into(holder.item_image);

            //to show item options
            if(productList.get(position).getOption().size()>0){

                holder.item_option.setVisibility(View.VISIBLE);
                holder.item_option.setText(context.getResources().getString(R.string.options));
                String options_txt;
                for (Options option:productList.get(position).getOption()) {
                    options_txt =  holder.item_option.getText().toString();
                    holder.item_option.setText(options_txt+"\n \u25CF"+option.getName()+": "+option.getValue());
                }
            }
        }

        // that's for items in the checkout activity
        if(isCheckout) {
            holder.item_price.setText(context.getResources().getString(R.string.price)+productList.get(position).getTotal()+" "+context.getString(R.string.kd));
            holder.item_quantity.setText(context.getResources().getString(R.string.quantity)+productList.get(position).getQuantity());
        }

    }


    @Override
    public int getItemViewType(int position) {

        return (position == productList.size())? R.layout.coupon_layout : R.layout.item_cart ;

    }

    @Override
    public int getItemCount() {
        if (isCheckout)
            return productList.size();
        else
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
        @Nullable @BindView(R.id.item_quantity)
        TextView item_quantity;

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



}
