package com.itsoluation.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PaymentMethod extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.information)
    TextView information_txt;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        if (Common.isArabic) {back_arrow.setRotation(180); }

        progressDialog.show();
        callPaymentMethod();
        callShippingMethod();
        callInformationAPI();

    }

    private void callPaymentMethod() {

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

    private void callShippingMethod() {

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

    private void callInformationAPI() {

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().getInformation(7)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement information) throws Exception {
                     progressDialog.dismiss();
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
}
