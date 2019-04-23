package com.itsoluations.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluations.vavisa.darhaa.Interface.AddressClicked;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.adapter.AddressAdapter;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.Status;
import com.itsoluations.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluations.vavisa.darhaa.payment.PaymentMethod;
import com.itsoluations.vavisa.darhaa.web_service.Controller2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
    @BindView(R.id.no_item)
    TextView no_data;

    @OnClick(R.id.ic_add_address)
    public void addAddress(){
        startActivity(new Intent(Addresses.this,AddAddress.class)); }
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    int pos = 0;
    String user_id;
    CompositeDisposable compositeDisposable;
    AddressAdapter adapter;
    ProgressDialog progressDialog;
    ArrayList<AddressGet> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_addresses);
        progressDialog = new ProgressDialog(this);
        ButterKnife.bind(this);
        if (Common.isArabic) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            back_arrow.setRotation(180);}
        else{
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        list = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());

        setupRecyclerView();
        if(Common.isConnectToTheInternet(this)){
            requestData();
        } else
            Common.errorConnectionMess(this);

    }

    private void requestData() {
        progressDialog.show();
try {
        compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().addressBook(Common.current_user.getCustomerInfo().getCustomer_id())
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<JsonElement>() {
                               @Override
                               public void accept(JsonElement jsonElement) throws Exception {

                                   progressDialog.dismiss();
                                   String result = jsonElement.toString();

                                   if (result.contains("error")) {
                                           JSONObject object = new JSONObject(result);
                                       if(result.contains(getString(R.string.no_data)))
                                           no_data.setVisibility(View.VISIBLE);
                                       else
                                           Common.showAlert2(Addresses.this, object.getString("status"), object.getString("message"));
                                   }else {

                                       JSONArray jArray = new JSONArray(result);
                                       list = new ArrayList<>();
                                           for (int i = 0; i < jArray.length(); i++) {
                                               JSONObject object1 = jArray.getJSONObject(i);
                                               AddressGet address = new AddressGet();
                                               address.setAddress_id(object1.getString("address_id"));
                                               address.setTitle(object1.getString("title"));
                                               address.setAddress(object1.getString("address"));
                                               address.setCountry(object1.getString("country"));
                                               address.setArea(object1.getString("area"));
                                               address.setCity(object1.getString("city"));

                                               list.add(address);
                                           }
                                           adapter.addAddress(list);
                                           adapter.notifyDataSetChanged();

                                       no_data.setVisibility(View.GONE);
                                       address_rec.setVisibility(View.VISIBLE);

                                   }

                               }
                           }));

    } catch (Exception e) {
        Common.showAlert2(this, getString(R.string.warning), e.getMessage());
    }

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
            compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().deleteAddress(id, address_id)
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
    }

    @Override
    public void onItemClick(int position, int flag, View view,String address_id) {


        // delete address
        if(flag == 0) {
            if(Common.isConnectToTheInternet(this))
                callDeleteAddressAPI(user_id, list.get(position).getAddress_id(), position);
            else
                Common.errorConnectionMess(this);
        }
        // edit address
        else if(flag == 1) {
            if(Common.isConnectToTheInternet(this))
                callGetAddressDeAPI(user_id, list.get(position).getAddress_id(), false, true);
            else
                Common.errorConnectionMess(this);
        } else if (flag == 2) {
            // choose address for payment order
            if(getIntent().hasExtra("checkoutAddress")){
                Intent intent = new Intent(this, PaymentMethod.class);
                intent.putExtra("address_id",address_id);
                intent.putExtra("total",getIntent().getStringExtra("total"));
                if(getIntent().hasExtra("couponCode"))
                    intent.putExtra("couponCode",getIntent().getStringExtra("couponCode"));
                startActivity(intent);
            }else {
                //show address details
                if(Common.isConnectToTheInternet(this))
                    callGetAddressDeAPI(user_id, list.get(position).getAddress_id(), true, false);
                else
                    Common.errorConnectionMess(this);
            }
        }

    }

    private void callGetAddressDeAPI(String user_id, final String address_id, final boolean showDetails, final boolean isEdit) {

        Common.showAddrDetails = showDetails;
        Common.isEditAddress = isEdit;
      /*  if(isEdit)
            Common.currentAddress = addressDetails;*/
        Intent i = new Intent(Addresses.this,AddAddress.class);
        i.putExtra("address_id",address_id);
        startActivity(i);

/*        compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().getAddress(user_id, address_id)
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

                                   }
                               }
                           }));*/
    }

}
