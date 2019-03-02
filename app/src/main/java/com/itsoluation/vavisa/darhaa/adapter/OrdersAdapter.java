package com.itsoluation.vavisa.darhaa.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.orders.OrdersData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    ArrayList<OrdersData> orders;
    public OrdersAdapter() {
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,null);

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

        @BindView(R.id.order_number)
        TextView order_number;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.order_address)
        TextView order_address;
        @BindView(R.id.order_date)
        TextView order_date;
        @BindView(R.id.totalAmount)
        TextView totalAmount;

        @OnClick(R.id.item_order)
        public void order(){

        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }

        public void bind(OrdersData ordersData){

            order_number.setText(ordersData.getOrders().get(0).getOrder_id());
            status.setText(ordersData.getOrders().get(0).getStatus());
            order_address.setText(ordersData.getOrders().get(0).getName());
            order_date.setText(ordersData.getOrders().get(0).getDate_added());
            totalAmount.setText(ordersData.getTotal());
        }
    }


}
