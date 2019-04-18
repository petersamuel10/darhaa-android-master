package com.itsoluation.vavisa.darhaa.payment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.paymentData.CheckoutPageParameters;
import com.itsoluation.vavisa.darhaa.model.paymentData.CheckoutProductPage;
import com.itsoluation.vavisa.darhaa.model.paymentData.PaymentMethodData;
import com.itsoluation.vavisa.darhaa.model.paymentData.ShippingMethodData;
import com.itsoluation.vavisa.darhaa.model.paymentData.UserSendData;
import com.itsoluation.vavisa.darhaa.payment.paymentResult.PaymentResult;

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

    @BindView(R.id.paymentBtn)
    Button payBtn;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    String  user_id, device_id,addressId,
            shipping_method_code,shipping_method_title,shipping_method_cost,
            payment_method_code,payment_method_title,coupon_code;

    double total,total2;

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


        if (Common.isArabic) {back_arrow.setRotation(180); }


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

        payBtn.setText(getResources().getString(R.string.paying_now)+" ( "+String.valueOf(total2)+" ) "+getResources().getString(R.string.kd));

        // gust
        if(user_id == "0"){
            // if use same address for billing and shipping
            if(getIntent().hasExtra("address2")){
                payment1 = getIntent().getExtras().getParcelable("address2");
                payment2 = getIntent().getExtras().getParcelable("address2");

            }else {
                payment1 = getIntent().getExtras().getParcelable("billing_address");
                payment2 = getIntent().getExtras().getParcelable("shipping_address");
            }

        }
        // user
        else {
            addressId = getIntent().getStringExtra("address_id");
            payment1 = new UserSendData(device_id,user_id,addressId);
            payment2 = new UserSendData(device_id,user_id,addressId);
        }


        if(Common.isConnectToTheInternet(this)) {
            callPaymentMethod();
            callShippingMethod();
            callInformationAPI();
        }else
            Common.errorConnectionMess(this);

        shippingRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View checkedButton = shippingRG.findViewById(checkedId);
                int checkedIndex = shippingRG.indexOfChild(checkedButton);
                shipping_method_code = shippingMethods.get(checkedIndex).getCode();
                shipping_method_title = shippingMethods.get(checkedIndex).getTitle();
                shipping_method_cost = shippingMethods.get(checkedIndex).getCost();

                total2= total+Double.parseDouble(shipping_method_cost);
                if(payment_method_code.equals("cod"))
                    payBtn.setText(getResources().getString(R.string.pay_on_delivery)+" ( "+String.format("%.3f",total2)+" ) "+getResources().getString(R.string.kd));
                else
                    payBtn.setText(getResources().getString(R.string.paying_now)+" ( "+String.format("%.3f",total2)+" ) "+getResources().getString(R.string.kd));

            }
        });

        paymentRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View checkedButton = paymentRG.findViewById(checkedId);
                int checkedIndex = paymentRG.indexOfChild(checkedButton);
                payment_method_code = paymentMethods.get(checkedIndex).getCode();
                payment_method_title = paymentMethods.get(checkedIndex).getTitle();
                if(payment_method_code.equals("cod"))
                    payBtn.setText(getResources().getString(R.string.pay_on_delivery)+" ( "+String.format("%.3f",total2)+" ) "+getResources().getString(R.string.kd));
                else
                    payBtn.setText(getResources().getString(R.string.paying_now)+" ( "+String.format("%.3f",total2)+" ) "+getResources().getString(R.string.kd));

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
                        if (json2.contains("message")){
                            JSONObject data = new JSONObject(json2);

                            String status = data.getString("status");
                            String message = data.getString("message");
                            Common.showAlert2(PaymentMethod.this,status,message);

                        }else {
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


                        for (PaymentMethodData paymentMethod: paymentMethods) {
                            Log.i("nnnnn","ufuigui");
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

        progressDialog.dismiss();

    }

    private void callShippingMethod() {
        progressDialog.show();

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().shippingMethod(payment2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement response) throws Exception {
                        progressDialog.dismiss();

                        Gson gson = new Gson();
                        String json2 = gson.toJson(response);

                        if (json2.contains("message")){
                            JSONObject data = new JSONObject(json2);

                            String status = data.getString("status");
                            String message = data.getString("message");
                            Common.showAlert2(PaymentMethod.this,status,message);

                        }else {
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
                        for (ShippingMethodData paymentMethod: shippingMethods) {
                            rootLayout.setVisibility(View.VISIBLE);
                            RadioButton radioButton = new RadioButton(PaymentMethod.this);
                            radioButton.setTextColor(getResources().getColor(R.color.black));
                            radioButton.setText(paymentMethod.getTitle()+"( "+paymentMethod.getCost()+getResources().getString(R.string.kd)+" )");
                            shippingRG.addView(radioButton);
                        }
                    }
                }));
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
                            information_txt.setText( Html.fromHtml(description).toString());
                        }else{
                            JSONObject object = new JSONObject(result);
                            Common.showAlert2(PaymentMethod.this,object.getString("status"),object.getString("message"));
                        }
                    }
                }));
    }

    @OnClick(R.id.paymentBtn)
    public void checkout_() {

        if(!TextUtils.isEmpty(payment_method_code)){
            if(!TextUtils.isEmpty(shipping_method_title)){

                CheckoutPageParameters checkout = null;

                if (user_id != "0") {
                    addressId = getIntent().getStringExtra("address_id");
                    if(getIntent().hasExtra("couponCode")) {
                        coupon_code = getIntent().getStringExtra("couponCode");
                        checkout = new CheckoutPageParameters(addressId, addressId, user_id, device_id, shipping_method_code,
                                shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, coupon_code);
                    }else {

                        checkout = new CheckoutPageParameters(addressId, addressId, user_id, device_id, shipping_method_code,
                                shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, "");
                    }

                } else {
                    if (getIntent().hasExtra("address2")) {
                        UserSendData address = getIntent().getExtras().getParcelable("address2");
                        if(getIntent().hasExtra("couponCode")){
                            coupon_code = getIntent().getStringExtra("couponCode");

                            checkout = new CheckoutPageParameters(address.getAddress(), address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, coupon_code);

                        }else {

                            checkout = new CheckoutPageParameters(address.getAddress(), address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, "");
                        }

                    }else {
                        UserSendData payment_address = getIntent().getExtras().getParcelable("billing_address");
                        UserSendData shipping_address = getIntent().getExtras().getParcelable("shipping_address");
                        if(getIntent().hasExtra("couponCode")) {
                            coupon_code = getIntent().getStringExtra("couponCode");

                            checkout = new CheckoutPageParameters(payment_address.getAddress(), shipping_address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title, coupon_code);
                        }else {

                            checkout = new CheckoutPageParameters(payment_address.getAddress(), shipping_address.getAddress(), user_id, device_id, shipping_method_code,
                                    shipping_method_title, shipping_method_cost, payment_method_code, payment_method_title,"");
                        }
                    }
                }


                if(payment_method_code.equals("cod")){
                    callAPI();
                }else {
                    Intent intent = new Intent(this, PaymentPage.class);
                    intent.putExtra("checkout", checkout);
                    startActivity(intent);
                }

            }else {
                Snackbar snackbar = Snackbar.make(rootLayout, R.string.please_enter_shipping_method, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }else{
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

                            try{

                                Intent intent = new Intent(PaymentMethod.this, PaymentResult.class);
                                intent.putExtra("order_id", checkoutProductPage.getOrder_id());
                                intent.putExtra("total", getIntent().getStringExtra("total"));
                                startActivity(intent);

                                progressDialog.dismiss();

                            }catch (Exception e){
                                Log.i("errrr",e.getMessage());
                            }
                        }
                    }));

        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }

    }
}
