package com.itsoluations.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.adapter.CartAdapter;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.cartData.CartData;
import com.itsoluations.vavisa.darhaa.model.cartData.Options;
import com.itsoluations.vavisa.darhaa.model.cartData.Product;
import com.itsoluations.vavisa.darhaa.model.cartData.Total;
import com.itsoluations.vavisa.darhaa.profile_fragments.Addresses;

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
    @BindView(R.id.checkoutBtn)
    Button checkoutBtn;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    CartAdapter adapter;
    CartData cartData;

    String user_id, device_id, total_;
    String coupon_code = null, product_out_of_stock = "";

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

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        setupRecyclerView();

    }

    @Override
    protected void onStart() {
        super.onStart();

        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : "0";
        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //get coupon code if exist
        if (getIntent().hasExtra("couponCode"))
            coupon_code = getIntent().getStringExtra("couponCode");

        if (Common.isConnectToTheInternet(this)) {
            callAPI();
        } else
            Common.errorConnectionMess(this);

    }

    private void callAPI() {

        try {
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
                                adapter = new CartAdapter(true, cartList.getProducts());
                                cart_rec.setAdapter(adapter);

                                for (Total total : cartData.getTotals()) {

                                    if (total.getTitle().equals(getString(R.string.sub_total)))
                                        subTotal_txt.setText(": " + total.getTotal() + " " + getResources().getString(R.string.kd));
                                    else if (total.getTitle().equals(getString(R.string.total))) {
                                        total_txt.setText(": " + total.getTotal() + " " + getResources().getString(R.string.kd));
                                        total_ = total.getTotal();

                                        if (Float.parseFloat(total_) < Float.parseFloat(cartData.getMin_checkout_price())) {
                                            checkoutBtn.setAlpha(0.5f);
                                            checkoutBtn.setText(getString(R.string.less_total) + " " + cartData.getMin_checkout_price() + getString(R.string.kd));
                                            checkoutBtn.setEnabled(false);
                                        }
                                    } else if (total.getTitle().contains(getString(R.string.coupon))) {
                                        couponLN.setVisibility(View.VISIBLE);
                                        coupon_title_txt.setText(total.getTitle() + ": ");
                                        coupon_txt.setText(" " + total.getTotal());
                                    }
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }
                    }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }
    }

    private void setupRecyclerView() {
        cart_rec.setHasFixedSize(true);
        cart_rec.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  getIntent().removeExtra("couponCode");
    }

    @OnClick(R.id.checkoutBtn)
    public void completeOrder() {

        product_out_of_stock = "";
        if (outOfStock()) {
            Common.showAlert2(this, getString(R.string.warning), product_out_of_stock);
        } else {
            if (user_id == "0") {
                Intent i = new Intent(Checkout.this, BillingAddress.class);
                i.putExtra("total", total_);
                if (getIntent().hasExtra("couponCode"))
                    i.putExtra("couponCode", getIntent().getStringExtra("couponCode"));
                startActivity(i);

            } else {
                Intent i = new Intent(Checkout.this, Addresses.class);
                i.putExtra("total", total_);
                // true to make address picker in the next activity
                i.putExtra("checkoutAddress", true);
                if (getIntent().hasExtra("couponCode"))
                    i.putExtra("couponCode", getIntent().getStringExtra("couponCode"));
                startActivity(i);
            }
        }
    }

    private boolean outOfStock() {

        for (Product product : cartData.getProducts()) {
            if (!product.getStock()) {
                product_out_of_stock += "\n \u25CF" + product.getName();
                for (Options option : product.getOption()) {
                    if (!TextUtils.isEmpty(option.getValue())) {
                        product_out_of_stock += "\n    \u25CF" + option.getName() + ": " + option.getValue();
                    }
                }
            }
        }
        if (TextUtils.isEmpty(product_out_of_stock))
            return false;
        else {
            product_out_of_stock = getString(R.string.these_items_out_of_stock) + product_out_of_stock;
            return true;
        }
    }

}
