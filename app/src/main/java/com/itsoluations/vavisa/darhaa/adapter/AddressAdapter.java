package com.itsoluations.vavisa.darhaa.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.itsoluations.vavisa.darhaa.Interface.AddressClicked;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.address.address.AddressGet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    ArrayList<AddressGet> addressArrayList;
    private AddressClicked listener = null;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.address_name.setText(addressArrayList.get(position).getTitle());
        holder.country.setText(addressArrayList.get(position).getCountry());
        holder.areaAndCity.setText(addressArrayList.get(position).getArea()+" / "+addressArrayList.get(position).getCity());
        holder.address_desc.setText(addressArrayList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return addressArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.sw)
        SwipeRevealLayout sw;
        @BindView(R.id.address_item)
        LinearLayout address_item;
        @BindView(R.id.address_name)
        TextView address_name;
        @BindView(R.id.country)
        TextView country;
        @BindView(R.id.address_desc)
        TextView address_desc;
        @BindView(R.id.areaAndCity)
        TextView areaAndCity;
        @BindView(R.id.tv_edit)
        TextView tv_edit;
        @BindView(R.id.tv_delete)
        TextView tv_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            if (Common.isArabic)
                sw.setDragEdge(1);
            else
                sw.setDragEdge(2);

            address_item.setOnClickListener(this);
            tv_edit.setOnClickListener(this);
            tv_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v == tv_edit) {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition(), 1, v,null);
                    sw.close(true);
                }
            } else if (v == tv_delete){
                //  Toast.makeText(Common.mActivity, "//////1111111111111///////////////", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition(), 0, v,null);
                    sw.close(true);
                }

            }else if(v == address_item  ){
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition(), 2, v,addressArrayList.get(getAdapterPosition()).getAddress_id());
                    sw.close(true);
                }
            }
        }
    }

    public void addAddress(ArrayList<AddressGet> addressList){
       addressArrayList.addAll(addressList);
    }

    public void removeAddresses(int position)
    {
        addressArrayList.remove(position);
    }

    public void setListener(AddressClicked listener) {
        this.listener = listener;
    }

}