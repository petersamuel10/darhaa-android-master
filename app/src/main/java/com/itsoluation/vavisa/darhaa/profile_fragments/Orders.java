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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.OrdersAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.orders.OrdersData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Orders extends AppCompatActivity {

    @BindView(R.id.orders_rec)
    RecyclerView orders_rec;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    OrdersAdapter adapter;
    ProgressDialog progressDialog;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_orders);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(Orders.this);
        progressDialog.setCancelable(false);

        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

        Log.i("access",Common.current_user.getUserAccessToken());
      //  setupRecyclerView();
        if(Common.isConnectToTheInternet(Orders.this))
            ;
          //  requestData();
        else
            Common.errorConnectionMess(Orders.this);
    }

    private void setupRecyclerView() {

        orders_rec.setHasFixedSize(true);
        orders_rec.setLayoutManager(new LinearLayoutManager(Orders.this));
        adapter = new OrdersAdapter();
        orders_rec.setAdapter(adapter);

    }

    private void requestData() {

        progressDialog.show();
        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());
        compositeDisposable.add(Common.getAPI2().getOrders(user_id)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<OrdersData>() {
                               @Override
                               public void accept(OrdersData ordersData) throws Exception {
                                   progressDialog.dismiss();
                                   if(ordersData.getStatus() !=null)
                                       Common.showAlert2(Orders.this,ordersData.getStatus(),ordersData.getMessage());
                                   else{


                                   }
                               }
                           }));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
