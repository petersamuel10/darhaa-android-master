package com.itsoluations.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.address.address.Countries;
import com.itsoluations.vavisa.darhaa.model.cartData.Options;
import com.itsoluations.vavisa.darhaa.model.cartData.Product;
import com.itsoluations.vavisa.darhaa.model.paymentData.CheckoutPageParameters;
import com.itsoluations.vavisa.darhaa.model.paymentData.CheckoutProductPage;
import com.itsoluations.vavisa.darhaa.model.paymentData.PaymentMethodData;
import com.itsoluations.vavisa.darhaa.model.paymentData.ShippingMethodData;
import com.itsoluations.vavisa.darhaa.model.paymentData.UserSendData;
import com.itsoluations.vavisa.darhaa.payment.paymentResult.PaymentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PaymentMethod extends AppCompatActivity {

    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.information)
    TextView information_txt;
    @BindView(R.id.paymentLN)
    LinearLayout paymentLN;
    @BindView(R.id.paymentRG)
    RadioGroup paymentRG;
    @BindView(R.id.shippingLN)
    LinearLayout shippingLN;
    @BindView(R.id.shippingRG)
    RadioGroup shippingRG;
    @BindView(R.id.comment_checkbox)
    CheckBox commentCb;
    @BindView(R.id.comment_ed)
    EditText comment_ed;


    @BindView(R.id.paymentBtn)
    Button payBtn;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    String user_id, device_id, addressId,
            shipping_method_code, shipping_method_title, shipping_method_cost,
            payment_method_code, payment_method_title, coupon_code, product_out_of_stock, oneSignalPlayerId;

    JsonElement json;

    double total, total2;

    ArrayList<PaymentMethodData> paymentMethods;
    ArrayList<ShippingMethodData> shippingMethods;
    CheckoutPageParameters checkout = null;

    UserSendData payment1 = null;
    UserSendData payment2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.payment_method);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        commentCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    comment_ed.setVisibility(View.VISIBLE);
                else {
                    comment_ed.setText("");
                    comment_ed.setVisibility(View.GONE);
                }
            }
        });
        setupPage();

    }

    private void setupPage() {
        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : "0";
        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //that's for avoid repeated when came from on back pressed
        paymentMethods = new ArrayList<>();
        shippingMethods = new ArrayList<>();
        shippingRG.removeAllViews();
        paymentRG.removeAllViews();
        payment_method_code = "";
        shipping_method_title = "";

        total = Double.parseDouble(getIntent().getStringExtra("total"));
        total2 = total;

        payBtn.setText(getResources().getString(R.string.paying_now) + " ( " + String.valueOf(total2) + " ) " + getResources().getString(R.string.kd));

        // gust
        if (user_id == "0") {
            try {

                // if use same address for billing and shipping
                if (getIntent().hasExtra("address2")) {
                    payment1 = getIntent().getExtras().getParcelable("address2");
                    payment2 = getIntent().getExtras().getParcelable("address2");

                } else {
                    payment1 = getIntent().getExtras().getParcelable("billing_address");
                    payment2 = getIntent().getExtras().getParcelable("shipping_address");
                }
            } catch (Exception e) {

                Common.showAlert(this, R.string.error, R.string.missing_data);

            }

        }
        // user
        else {
            addressId = getIntent().getStringExtra("address_id");
            payment1 = new UserSendData(device_id, user_id, addressId);
            payment2 = new UserSendData(device_id, user_id, addressId);
        }


        if (Common.isConnectToTheInternet(this)) {
            callPaymentMethod();
            callShippingMethod();
            callInformationAPI();
        } else
            Common.errorConnectionMess(this);

        shippingRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View checkedButton = shippingRG.findViewById(checkedId);
                int checkedIndex = shippingRG.indexOfChild(checkedButton);
                shipping_method_code = shippingMethods.get(checkedIndex).getCode();
                shipping_method_title = shippingMethods.get(checkedIndex).getTitle();
                shipping_method_cost = shippingMethods.get(checkedIndex).getCost();

                total2 = total + Double.parseDouble(shipping_method_cost);
                if (payment_method_code.equals("cod"))
                    payBtn.setText(getResources().getString(R.string.pay_on_delivery) + " ( " + String.format("%.3f", total2) + " ) " + getResources().getString(R.string.kd));
                else
                    payBtn.setText(getResources().getString(R.string.paying_now) + " ( " + String.format("%.3f", total2) + " ) " + getResources().getString(R.string.kd));

            }
        });

        paymentRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View checkedButton = paymentRG.findViewById(checkedId);
                int checkedIndex = paymentRG.indexOfChild(checkedButton);
                payment_method_code = paymentMethods.get(checkedIndex).getCode();
                payment_method_title = paymentMethods.get(checkedIndex).getTitle();
                if (payment_method_code.equals("cod"))
                    payBtn.setText(getResources().getString(R.string.pay_on_delivery) + " ( " + String.format("%.3f", total2) + " ) " + getResources().getString(R.string.kd));
                else
                    payBtn.setText(getResources().getString(R.string.paying_now) + " ( " + String.format("%.3f", total2) + " ) " + getResources().getString(R.string.kd));

            }
        });
    }

    private void callPaymentMethod() {

        progressDialog.show();
        try {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Common.getAPI().paymentMethod(payment1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonElement>() {
                        @Override
                        public void accept(JsonElement response) throws Exception {
                            Gson gson = new Gson();
                            String json2 = gson.toJson(response);
                            if (json2.contains("message")) {
                                JSONObject data = new JSONObject(json2);

                                String status = data.getString("status");
                                String message = data.getString("message");
                                Common.showAlert2(PaymentMethod.this, status, message);

                            } else {
                                paymentLN.setVisibility(View.VISIBLE);
                                JSONArray dataArray = new JSONArray(json2);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object1 = dataArray.getJSONObject(i);
                                    PaymentMethodData paymentMethod = new PaymentMethodData();
                                    paymentMethod.setCode(object1.getString("code"));
                                    paymentMethod.setTitle(object1.getString("title"));
                                    paymentMethod.setTerms(object1.getString("terms"));
                                    paymentMethods.add(paymentMethod);
                                }
                            }


                            for (PaymentMethodData paymentMethod : paymentMethods) {
                                Log.i("nnnnn", "ufuigui");
                                RadioButton radioButton = new RadioButton(PaymentMethod.this);
                                radioButton.setTextColor(getResources().getColor(R.color.black));
                                radioButton.setText(paymentMethod.getTitle());
                                paymentRG.addView(radioButton);
                            }
                        }
                    }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }

        //   progressDialog.dismiss();

    }

    private void callShippingMethod() {
        progressDialog.show();

        try {

            Observable<JsonElement> getApi = Common.getAPI().shippingMethod(payment2).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            getApi.subscribe(new Observer<JsonElement>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(JsonElement jsonElement) {
                   json = jsonElement;
                }

                @Override
                public void onError(Throwable e) {

                    Common.showAlert2(PaymentMethod.this,getString(R.string.error),getString(R.string.error_connection));
                }

                @Override
                public void onComplete() {
                    progressDialog.dismiss();

                    Gson gson = new Gson();
                    String json2 = gson.toJson(json);

                    if (json2.contains("message")) {
                        try {

                            JSONObject data = new JSONObject(json2);

                            String status = data.getString("status");
                            String message = data.getString("message");
                            Common.showAlert2(PaymentMethod.this, status, message);

                        }catch (Exception e){}
                    } else {
                        shippingLN.setVisibility(View.VISIBLE);

                        try {
                        JSONArray dataArray = new JSONArray(json2);
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject object = dataArray.getJSONObject(i);
                            JSONArray methodData = object.getJSONArray("quote");
                            for (int x = 0; i < methodData.length(); i++) {
                                JSONObject object1 = methodData.getJSONObject(x);

                                ShippingMethodData shippingMethod = new ShippingMethodData();
                                shippingMethod.setCode(object1.getString("code"));
                                shippingMethod.setTitle(object1.getString("title"));
                                shippingMethod.setError(object1.getString("error"));
                                shippingMethod.setCost(object1.getString("cost"));
                                shippingMethods.add(shippingMethod);

                            }
                        }
                    }catch(Exception e){

                    }

                    }
                    //bind data
                    for (ShippingMethodData paymentMethod : shippingMethods) {
                        rootLayout.setVisibility(View.VISIBLE);
                        RadioButton radioButton = new RadioButton(PaymentMethod.this);
                        radioButton.setTextColor(getResources().getColor(R.color.black));
                        radioButton.setText(paymentMethod.getTitle() + "( " + paymentMethod.getCost() + getResources().getString(R.string.kd) + " )");
                        shippingRG.addView(radioButton);
                    }
                }
            });



          /*  compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Common.getAPI().shippingMethod(payment2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new <JsonElement>() {
                        @Override
                        public void accept(JsonElement response) throws Exception {
                            progressDialog.dismiss();

                            Gson gson = new Gson();
                            String json2 = gson.toJson(response);

                            if (json2.contains("message")) {
                                JSONObject data = new JSONObject(json2);

                                String status = data.getString("status");
                                String message = data.getString("message");
                                Common.showAlert2(PaymentMethod.this, status, message);

                            } else {
                                shippingLN.setVisibility(View.VISIBLE);
                                JSONArray dataArray = new JSONArray(json2);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    JSONArray methodData = object.getJSONArray("quote");
                                    for (int x = 0; i < methodData.length(); i++) {
                                        JSONObject object1 = methodData.getJSONObject(x);

                                        ShippingMethodData shippingMethod = new ShippingMethodData();
                                        shippingMethod.setCode(object1.getString("code"));
                                        shippingMethod.setTitle(object1.getString("title"));
                                        shippingMethod.setError(object1.getString("error"));
                                        shippingMethod.setCost(object1.getString("cost"));
                                        shippingMethods.add(shippingMethod);

                                    }
                                }

                            }
                            //bind data
                            for (ShippingMethodData paymentMethod : shippingMethods) {
                                rootLayout.setVisibility(View.VISIBLE);
                                RadioButton radioButton = new RadioButton(PaymentMethod.this);
                                radioButton.setTextColor(getResources().getColor(R.color.black));
                                radioButton.setText(paymentMethod.getTitle() + "( " + paymentMethod.getCost() + getResources().getString(R.string.kd) + " )");
                                shippingRG.addView(radioButton);
                            }
                        }
                    }));*/
        }catch (Exception e){
            Common.showAlert2(PaymentMethod.this, getString(R.string.error), e.getMessage());
        }
    }

    private void callInformationAPI() {

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().getInformation(7)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement information) throws Exception {
                        String result = information.toString();

                        if (!result.contains("error")) {
                            JSONObject object = new JSONObject(result);
                            String description = object.getString("description");
                            information_txt.setText(Html.fromHtml(description).toString());
                        } else {
                            JSONObject object = new JSONObject(result);
                            Common.showAlert2(PaymentMethod.this, object.getString("status"), object.getString("message"));
                        }
                    }
                }));
    }

    @OnClick(R.id.paymentBtn)
    public void checkout_() {

        if (!TextUtils.isEmpty(payment_method_code)) {
            if (!TextUtils.isEmpty(shipping_method_title)) {

                String comment = comment_ed.getText().toString();

                if (user_id != "0") {
                    addressId = getIntent().getStringExtra("address_id");
                    if (getIntent().hasExtra("couponCode")) {
                        coupon_code = getIntent().getStringExtra("couponCode");
                        checkout = new CheckoutPageParameters(addressId, addressId, user_id, device_id, shipping_method_code,
                                shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, coupon_code);
                    } else {

                        checkout = new CheckoutPageParameters(addressId, addressId, user_id, device_id, shipping_method_code,
                                shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, "");
                    }

                } else {
                    if (getIntent().hasExtra("address2")) {
                        UserSendData address = getIntent().getExtras().getParcelable("address2");
                        if (getIntent().hasExtra("couponCode")) {
                            coupon_code = getIntent().getStringExtra("couponCode");

                            checkout = new CheckoutPageParameters(address.getAddress(), address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, coupon_code);

                        } else {

                            checkout = new CheckoutPageParameters(address.getAddress(), address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, "");
                        }

                    } else {
                        UserSendData payment_address = getIntent().getExtras().getParcelable("billing_address");
                        UserSendData shipping_address = getIntent().getExtras().getParcelable("shipping_address");
                        if (getIntent().hasExtra("couponCode")) {
                            coupon_code = getIntent().getStringExtra("couponCode");

                            checkout = new CheckoutPageParameters(payment_address.getAddress(), shipping_address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, coupon_code);
                        } else {

                            checkout = new CheckoutPageParameters(payment_address.getAddress(), shipping_address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, "");
                        }
                    }
                }

                oneSignalPlayerId = Paper.book("DarHaa").read("oneSignalPlayerId");
                checkout.setOneSignalPlayerId(oneSignalPlayerId);
                checkout.setComment(comment);

                callAPI();

            } else {
                Snackbar snackbar = Snackbar.make(rootLayout, R.string.please_enter_shipping_method, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(rootLayout, R.string.please_enter_payment_method, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void callAPI() {

        compositeDisposable = new CompositeDisposable();

        progressDialog.show();
        try {
            compositeDisposable.add(Common.getAPI().checkoutProductPage(checkout)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CheckoutProductPage>() {
                        @Override
                        public void accept(CheckoutProductPage checkoutProductPage) throws Exception {

                            progressDialog.dismiss();
                            try {

                                product_out_of_stock = "";

                                if (outOfStock(checkoutProductPage.getProducts())) {
                                    Common.showAlert2(PaymentMethod.this, getString(R.string.warning), product_out_of_stock);
                                } else {

                                    if (payment_method_code.equals("cod")) {
                                        Intent intent = new Intent(PaymentMethod.this, PaymentResult.class);
                                        intent.putExtra("order_id", checkoutProductPage.getOrder_id());
                                        intent.putExtra("total", getIntent().getStringExtra("total"));
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(PaymentMethod.this, PaymentPage.class);
                                        intent.putExtra("paymentLink", checkoutProductPage.getPayment());
                                        startActivity(intent);
                                    }
                                }

                            } catch (Exception e) {
                                Common.showAlert2(PaymentMethod.this, getString(R.string.error), e.getMessage());
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Common.showAlert2(PaymentMethod.this, getString(R.string.warning), getString(R.string.error_occur));
                        }
                    }));

        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
            progressDialog.dismiss();
        }

    }

    private boolean outOfStock(ArrayList<Product> products) {

        for (Product product : products) {
            if (!product.getStock()) {
                product_out_of_stock += "\n \u25CF" + product.getName();
                for(Options option : product.getOption()){
                    if(!TextUtils.isEmpty(option.getValue())){
                        product_out_of_stock += "\n    \u25CF" + option.getName() + ": "+option.getValue();
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
