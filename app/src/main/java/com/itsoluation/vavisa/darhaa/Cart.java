package com.itsoluation.vavisa.darhaa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.Interface.CartInterface;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerItemTouchHelperListner;
import com.itsoluation.vavisa.darhaa.adapter.CartAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;
import com.itsoluation.vavisa.darhaa.model.cartData.EditCart;
import com.itsoluation.vavisa.darhaa.model.cartData.Quantity;
import com.itsoluation.vavisa.darhaa.payment.Checkout;
import com.itsoluation.vavisa.darhaa.recyclerItemTouchHelper.RecyclerViewItemTouchHelperCart;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements CartInterface, RecyclerItemTouchHelperListner {

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.cart_rec)
    RecyclerView cart_rec;
    @BindView(R.id.cartBtn)
    Button cartBtn;
    @BindView(R.id.no_item)
    TextView no_item_txt;

    HashMap<String, String> changesCarts = new HashMap<>();

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    CartAdapter adapter;
    CartData cartData;

    String user_id, device_id;
    String coupon_code = "";
    boolean isAddAddress = false;

    private CartInterface onCartListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }
        onCartListener = this;
        setupRecyclerView();


        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : "0";
        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (Common.isConnectToTheInternet(this)) {
            callAPI();
        } else
            Common.errorConnectionMess(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // get user_id after come from login activity
        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : "0";

    }

    private void callAPI() {

        progressDialog.show();
        try {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Common.getAPI().viewCart(user_id, device_id, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CartData>() {
                        @Override
                        public void accept(CartData cartList) throws Exception {
                            progressDialog.dismiss();
                            if (cartList.getStatus() != null) {
                                if (cartList.getStatus().contains("error")) {
                                    Common.showAlert2(Cart.this, cartList.getStatus(), cartList.getMessage());
                                    no_item_txt.setVisibility(View.VISIBLE);
                                } else
                                    no_item_txt.setVisibility(View.VISIBLE);
                            } else {
                                cartData = cartList;
                                adapter = new CartAdapter(false, cartData.getProducts());
                                adapter.setListener(onCartListener);
                                cart_rec.setAdapter(adapter);
                                cartBtn.setVisibility(View.VISIBLE);
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
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerViewItemTouchHelperCart(0, ItemTouchHelper.START, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(cart_rec);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // listener to add or minus quantity and verify coupon code
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
                    changesCarts.put(cartData.getProducts().get(position).getCart_id(), String.valueOf(amount));
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
                    changesCarts.put(cartData.getProducts().get(position).getCart_id(), String.valueOf(amount));
                }
            }

        } catch (Exception e) {
        }

        try {

            String coupon = "";
            EditText coupon_ed = ((EditText) item_amount_txt);
            Button verify = ((Button) item_price_txt);

            if (flag == 1) {
                // coupon
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);

                //get coupon code
                coupon = coupon_ed.getText().toString();
                if (coupon.equals(""))
                    Common.showAlert2(Cart.this, getResources().getString(R.string.warning), getResources().getString(R.string.enter_coupon_code));
                else {
                    if (Common.isConnectToTheInternet(this))
                        verifyCoupon(coupon, verify, coupon_ed);
                    else
                        Common.errorConnectionMess(this);
                }
            }

        } catch (Exception e) {
        }
    }

    //coupon
    private void verifyCoupon(final String coupon, final Button verify, final EditText coupon_ed) {

        progressDialog.show();
        try {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Common.getAPI().checkCoupon(user_id, coupon)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonElement>() {
                        @Override
                        public void accept(JsonElement jsonElement) throws Exception {
                            progressDialog.dismiss();

                            String result = jsonElement.toString();
                            JSONObject object = new JSONObject(result);
                            if (object.getString("status").equals("error"))
                                Common.showAlert2(Cart.this, object.getString("status"), object.getString("message"));
                            else {
                                if (object.getString("status").equals("success")) {
                                    JSONObject result_ = object.getJSONObject("result");
                                    coupon_code = result_.getString("code");
                                    String name = result_.getString("name");

                                    Common.showAlert2(Cart.this, object.getString("status"), object.getString("message") + "\n"
                                            + getResources().getString(R.string.you_get_discount) + name + ".");

                                    coupon_ed.setText(getResources().getString(R.string.coupon_successfully));
                                    coupon_ed.setBackgroundColor(getResources().getColor(R.color.grey));
                                    coupon_ed.setEnabled(false);
                                    verify.setEnabled(false);
                                }
                            }
                        }
                    }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }

    }

    //delete cart
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {

        compositeDisposable = new CompositeDisposable();

        if (Common.isConnectToTheInternet(this)) {

            String cart_id = String.valueOf(cartData.getProducts().get(viewHolder.getAdapterPosition()).getCart_id());

            progressDialog.show();
            Call<Status> delete = Common.getAPI().deleteCart(cart_id, user_id, device_id);

            delete.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {

                    if (response.body().getStatus().equals("error"))
                        Common.showAlert2(Cart.this, response.body().getStatus(), response.body().getMessage());
                    else {

                        progressDialog.dismiss();
                        cartData.getProducts().remove(position);
                        adapter.notifyItemRemoved(position);


                        if (adapter.getItemCount() == 1) {
                            cart_rec.setVisibility(View.GONE);
                            cartBtn.setVisibility(View.GONE);
                            no_item_txt.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable t) {

                }
            });
        } else
            Common.errorConnectionMess(this);

    }

    @OnClick(R.id.cartBtn)
    public void checkout() {

        if (user_id.equals("0")) {
            showAlert();
        } else {
            checkoutMethod();
        }
    }

    public void checkoutMethod() {
        // check if there is any changes of product quantity
        if (!changesCarts.isEmpty()) {
            EditCart editCart = new EditCart();
            editCart.setUser_id(user_id);
            editCart.setDevice_id(device_id);
            ArrayList<Quantity> quantityList = new ArrayList<>();

            for (Map.Entry<String, String> entry : changesCarts.entrySet()) {
                Quantity quantity = new Quantity(entry.getKey(), entry.getValue());
                quantityList.add(quantity);
            }

            editCart.setQuantity(quantityList);

            if (Common.isConnectToTheInternet(this))
                callAPIToEditCart(editCart);
            else
                Common.errorConnectionMess(this);

        } else {
            Intent i = new Intent(Cart.this, Checkout.class);
            if (!coupon_code.equals(""))
                i.putExtra("couponCode", coupon_code);

            startActivity(i);
        }
    }

    private void showAlert() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button

        TextView login = dialog.findViewById(R.id.login_alert);
        TextView addAddress = dialog.findViewById(R.id.add_address_alert);
        TextView cancel = dialog.findViewById(R.id.cancel_alert);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Cart.this, Login.class));
            }
        });


        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkoutMethod();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void callAPIToEditCart(EditCart editCart) {

        final Status status_ = new Status();

        Observable<Status> editCartInterface = Common.getAPI().editCart(editCart).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        editCartInterface.subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                status_.setMessage(status.getMessage());
                status_.setStatus(status.getStatus());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (status_.getStatus().equals("error"))
                    Common.showAlert2(Cart.this, status_.getStatus(), status_.getMessage());
                else {
                    Intent i = new Intent(Cart.this, Checkout.class);
                    if (!coupon_code.equals(""))
                        i.putExtra("couponCode", coupon_code);

                    startActivity(i);
                }
            }
        });
    }
}
