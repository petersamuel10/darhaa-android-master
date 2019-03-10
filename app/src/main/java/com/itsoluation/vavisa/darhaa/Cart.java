package com.itsoluation.vavisa.darhaa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.Interface.CartInterface;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerItemTouchHelperListner;
import com.itsoluation.vavisa.darhaa.adapter.CartAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;
import com.itsoluation.vavisa.darhaa.payment.Checkout;
import com.itsoluation.vavisa.darhaa.recyclerItemTouchHelper.RecyclerViewItemTouchHelperCart;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Cart extends AppCompatActivity implements CartInterface, RecyclerItemTouchHelperListner {

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.cart_rec)
    RecyclerView cart_rec;

    @OnClick(R.id.cartBtn)
    public void checkout(){
        startActivity(new Intent(Cart.this, Checkout.class));
    }

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    CartAdapter adapter;
    CartData cartData;

    String user_id, device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
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
                            Common.showAlert2(Cart.this, cartList.getStatus(), cartList.getMessage());
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
        adapter = new CartAdapter(false);
        adapter.setListener(this);
        cart_rec.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerViewItemTouchHelperCart(0, ItemTouchHelper.START, this);

        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(cart_rec);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClick(int position, int flag, View item_amount_txt, View item_price_txt) {

        try {
            int amount = Integer.parseInt(((TextView) item_amount_txt).getText().toString());
            double price = Double.parseDouble(((TextView) item_price_txt).getText().toString());
            int minimum = Integer.parseInt(cartData.getProducts().get(position).getMinimum());
            int max = Integer.parseInt(cartData.getProducts().get(position).getTotal_quantity());

            if (flag == 2) {
                if (amount == max) {
                    Common.showAlert(Cart.this, R.string.warning, R.string.max_number);
                } else {
                    amount++;
                    ((TextView) item_amount_txt).setText(String.valueOf(amount));
                    price += Double.parseDouble(cartData.getProducts().get(position).getPrice());
                    ((TextView) item_price_txt).setText(String.format(Locale.US, "%.3f", price));
                }
                //minus
            } else if (flag == 3) {
                if (amount == minimum) {
                    Common.showAlert(Cart.this, R.string.warning, R.string.minimum_number);
                } else {
                    amount--;
                    ((TextView) item_amount_txt).setText(String.valueOf(amount));
                    price -= Double.parseDouble(cartData.getProducts().get(position).getPrice());
                    ((TextView) item_price_txt).setText(String.format(Locale.US, "%.3f", price));
                }
            }
        } catch (Exception e) {
        }

        try {

            String coupon;
            EditText coupon_ed = ((EditText) item_amount_txt);
            Button verify = ((Button) item_price_txt);

            if (flag == 1) {
                // coupon
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);

                coupon = coupon_ed.getText().toString();

                verifyCoupon(coupon,verify,coupon_ed);

            }

        } catch (Exception e) {
        }


    }

    private void verifyCoupon(final String coupon, final Button verify, final EditText coupon_ed) {
        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        Log.i("bbbbbb114441","etertr");
        compositeDisposable.add(Common.getAPI().checkCoupon(user_id, coupon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        Log.i("bbbbbb2222","etertr");
                        progressDialog.dismiss();
                            Common.showAlert2(Cart.this, status.getStatus(),status.getMessage());
                            if(status.getStatus().equals("success")){

                                coupon_ed.setText(getResources().getString(R.string.coupon_successfully));
                                coupon_ed.setBackgroundColor(getResources().getColor(R.color.grey));
                                coupon_ed.setEnabled(false);
                                verify.setEnabled(false);

                            }
                    }
                }));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        compositeDisposable = new CompositeDisposable();

        String cart_id = String.valueOf(cartData.getProducts().get(viewHolder.getAdapterPosition()).getCart_id());

        if (viewHolder instanceof CartAdapter.ViewHolder) {

            compositeDisposable.add(Common.getAPI().deleteCart(cart_id, user_id, device_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            if (status.getStatus().equals("error"))
                                Common.showAlert2(Cart.this, status.getStatus(), status.getMessage());
                            else {
                                Snackbar snackbar = Snackbar.make(rootLayout, status.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }));

            adapter.removeCart(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    }

}
