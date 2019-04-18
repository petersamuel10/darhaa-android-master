package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.orders.Orders;
import com.itsoluation.vavisa.darhaa.profile_fragments.OrderDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    ArrayList<Orders> orders;
    Context context;
    public OrdersAdapter(ArrayList<Orders> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,null);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {

        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.status_)
        TextView status;
        @BindView(R.id.product_num)
        TextView product_num;
        @BindView(R.id.order_date)
        TextView order_date;
        @BindView(R.id.total)
        TextView total;

        Orders ordersData;

        @OnClick(R.id.item_order)
        public void order(){
            Intent intent = new Intent(context, OrderDetails.class);
            intent.putExtra("order_id",ordersData.getOrder_id());
            context.startActivity(intent);
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(Orders ordersData){

            this.ordersData = ordersData;

            name.setText(ordersData.getName());
            status.setText(ordersData.getStatus());
            product_num.setText(ordersData.getProducts()+" "+ context.getResources().getString(R.string.products));
            order_date.setText(ordersData.getDate_added());
            total.setText(ordersData.getTotal()+" "+ context.getResources().getString(R.string.kd));
        }
    }


}
