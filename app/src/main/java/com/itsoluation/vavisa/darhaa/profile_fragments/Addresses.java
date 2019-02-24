package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.AddressAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.security.AccessController.getContext;

public class Addresses extends AppCompatActivity implements RecyclerViewItemClickListener {

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
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AddressAdapter adapter;
    ProgressDialog progressDialog;
   // List<AddressAdd> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_addresses);
        progressDialog = new ProgressDialog(this);
        ButterKnife.bind(this);
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
     //   adapter.setListener(this);
       // LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(address_rec.getContext(),R.anim.layout_fall_down);
    //    address_rec.setLayoutAnimation(controller);
        address_rec.setAdapter(adapter);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view, int position) {

    }
}
