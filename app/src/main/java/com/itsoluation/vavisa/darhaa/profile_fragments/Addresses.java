package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.itsoluation.vavisa.darhaa.Interface.AddressClicked;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.AddressAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressDetails;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.payment.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Addresses extends AppCompatActivity implements AddressClicked {

    @BindView(R.id.addresses_rec)
    RecyclerView address_rec;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.ic_add_address)
    public void addAddress(){
        startActivity(new Intent(Addresses.this,AddAddress.class)); }
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    int pos = 0;
    String user_id;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AddressAdapter adapter;
    ProgressDialog progressDialog;
    List<AddressGet> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_addresses);
        progressDialog = new ProgressDialog(this);
        ButterKnife.bind(this);
        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());
        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

       // list.clear();

        setupRecyclerView();
        if(Common.isConnectToTheInternet(this)){
            requestData();
        } else
            Common.errorConnectionMess(this);

        //  adapter.notifyDataSetChanged();

        // adapter = new AddressAdapter();
        // requestData();
    }

    private void requestData() {
        progressDialog.show();
        compositeDisposable.add(Common.getAPI2().addressBook(Common.current_user.getCustomerInfo().getCustomer_id())
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<ArrayList<AddressGet>>() {
                               @Override
                               public void accept(ArrayList<AddressGet> addressGets) throws Exception {
                                   progressDialog.dismiss();
                                   list.addAll(addressGets);
                                   adapter.notifyDataSetChanged();
                                   adapter.addAddress(addressGets);
                                   Log.i("vvv", String.valueOf(addressGets.size()));
                               }
                           }));

    }

    private void setupRecyclerView() {

        address_rec.setHasFixedSize(true);
        address_rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter();
        adapter.setListener(this);
        address_rec.setAdapter(adapter);
    }

    private void callDeleteAddressAPI(String id, String address_id, final int position) {

        try {
            compositeDisposable.add(Common.getAPI2().deleteAddress(id, address_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            progressDialog.dismiss();
                            if (status.getStatus().equals("error")) {
                                Common.showAlert2(Addresses.this, status.getStatus(), status.getMessage());
                            } else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(Addresses.this);
                                builder1.setMessage(status.getMessage());
                                builder1.setTitle(status.getStatus());
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                adapter.removeAddresses(position);
                                                adapter.notifyItemRemoved(position);
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        }
                    }));
        }catch (Exception e){
            Log.i("rrrrr",e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClick(int position, int flag, View view,String address_id) {


        // delete address
        if(flag == 0)
            callDeleteAddressAPI(user_id,list.get(position).getAddress_id(),position);

        // edit address
        else if(flag == 1)
            callGetAddressDeAPI(user_id,list.get(position).getAddress_id(),false,true);

        else if (flag == 2) {
            // choose address for payment order
            if(getIntent().hasExtra("checkoutAddress")){
                Intent intent = new Intent(this, PaymentMethod.class);
                intent.putExtra("address_id",address_id);
                intent.putExtra("total",getIntent().getStringExtra("total"));
                if(getIntent().hasExtra("couponCode"))
                    intent.putExtra("couponCode",getIntent().getStringExtra("couponCode"));
                startActivity(intent);
            }else
                //show address details
                callGetAddressDeAPI(user_id, list.get(position).getAddress_id(), true, true);
        }

    }

    private void callGetAddressDeAPI(String user_id, final String address_id, final boolean showDetails, final boolean isEdit) {
        compositeDisposable.add(Common.getAPI2().getAddress(user_id, address_id)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<AddressDetails>() {
                               @Override
                               public void accept(AddressDetails addressDetails) throws Exception {
                                   if(addressDetails.getStatus() !=null) {
                                       Common.showAlert2(Addresses.this,addressDetails.getStatus(),addressDetails.getMessage());
                                   } else {
                                         Common.isEditAddress = isEdit;
                                         Common.showAddrDetails = showDetails;
                                         Common.currentAddress = addressDetails;
                                         Common.address_id = address_id;
                                         startActivity(new Intent(Addresses.this,AddAddress.class));
                                   }
                               }
                           }));
    }

}
