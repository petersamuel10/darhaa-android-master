package com.itsoluation.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.CartAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;
import com.itsoluation.vavisa.darhaa.model.cartData.Total;
import com.itsoluation.vavisa.darhaa.profile_fragments.Addresses;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Checkout extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.cart_rec)
    RecyclerView cart_rec;
    @BindView(R.id.subTotal)
    TextView subTotal_txt;
    @BindView(R.id.total)
    TextView total_txt;
    @BindView(R.id.couponLN)
    LinearLayout couponLN;
    @BindView(R.id.coupon)
    TextView coupon_title_txt;
    @BindView(R.id.couponValue)
    TextView coupon_txt;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    CartAdapter adapter;
    CartData cartData;

    String user_id, device_id;
    String coupon_code = null;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_checkout);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        if (Common.isArabic) {back_arrow.setRotation(180); }

        if(getIntent().hasExtra("couponCode"))
           coupon_code = getIntent().getStringExtra("couponCode");


        setupRecyclerView();

        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : null;
        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (Common.isConnectToTheInternet(this)) {
            callAPI();
        } else
            Common.errorConnectionMess(this);
    }

    private void callAPI() {

        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().viewCart(user_id, device_id, coupon_code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CartData>() {
                    @Override
                    public void accept(CartData cartList) throws Exception {
                        progressDialog.dismiss();
                        if (cartList.getStatus() != null) {
                            Common.showAlert2(Checkout.this, cartList.getStatus(), cartList.getMessage());
                        } else {
                            cartData = cartList;
                            adapter = new CartAdapter(true,cartList.getProducts());
                            cart_rec.setAdapter(adapter);

                            for (Total total:cartData.getTotals()) {

                                if(total.getTitle().equals("Sub-Total"))
                                    subTotal_txt.setText(total.getTotal());
                                else if(total.getTitle().equals("Total"))
                                    total_txt.setText(total.getTotal());
                                else if(total.getTitle().contains("Coupon")){
                                    couponLN.setVisibility(View.VISIBLE);
                                    coupon_title_txt.setText(total.getTitle()+": ");
                                    coupon_txt.setText(total.getTotal());
                                }

                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }));
    }

    private void setupRecyclerView() {

        cart_rec.setHasFixedSize(true);
        cart_rec.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.checkoutBtn)
    public void completeOrder(){
        if(user_id == null){

        }else{
            Intent i = new Intent(this, Addresses.class);
            i.putExtra("checkoutAddress",true);
            startActivity(i);
        }


    }
}