package com.itsoluation.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.CartAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Checkout extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.cart_rec)
    RecyclerView cart_rec;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    CartAdapter adapter;
    CartData cartData;

    String user_id, device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_checkout);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        setupRecyclerView();

        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("device",device_id);
        if (Common.isConnectToTheInternet(this)) {
            callAPI();
        } else
            Common.errorConnectionMess(this);
    }

    private void callAPI() {

        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().viewCart(user_id, device_id, null)
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
                            adapter.addAddress(cartList.getProducts());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }));
    }

    private void setupRecyclerView() {

        cart_rec.setHasFixedSize(true);
        cart_rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(true);
        cart_rec.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
