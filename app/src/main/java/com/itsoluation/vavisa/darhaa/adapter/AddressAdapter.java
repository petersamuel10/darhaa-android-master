package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    Context context;
    ArrayList<AddressGet> addressArrayList;

    public AddressAdapter() {
        addressArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.address_name.setText(addressArrayList.get(position).getTitle());
        holder.country.setText(addressArrayList.get(position).getCountry());
        holder.areaAndCity.setText(addressArrayList.get(position).getArea()+" / "+addressArrayList.get(position).getCity());
        holder.address_desc.setText(addressArrayList.get(position).getAddress());

    }

    @Override
    public int getItemCount() {
        return addressArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sw)
        SwipeRevealLayout sw;
        @BindView(R.id.address_name)
        TextView address_name;
        @BindView(R.id.country)
        TextView country;
        @BindView(R.id.address_desc)
        TextView address_desc;
        @BindView(R.id.areaAndCity)
        TextView areaAndCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            if (Common.isArabic)
                sw.setDragEdge(1);
            else
                sw.setDragEdge(2);

        }
    }

    public void addAddress(ArrayList<AddressGet> addressList){
       addressArrayList.addAll(addressList);
    }
}
