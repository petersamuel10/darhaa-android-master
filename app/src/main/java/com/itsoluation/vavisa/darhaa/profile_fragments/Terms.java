package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.payment.PaymentMethod;
import com.itsoluation.vavisa.darhaa.view_setting.Filter;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Terms extends AppCompatActivity {

    @BindView(R.id.terms)
    WebView terms_txt;

    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_terms);
        progressDialog = new ProgressDialog(getBaseContext());

        ButterKnife.bind(this);

        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

    getData();
}

    private void getData() {
        if(Common.isConnectToTheInternet(this)) {
            callAPI();
        }else
            Common.errorConnectionMess(this);
    }

    private void callAPI() {

        compositeDisposable = new CompositeDisposable();
        try {
        compositeDisposable.add(Common.getAPI().getInformation(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement information) throws Exception {
                        String result = information.toString();

                        if (!result.contains("error")) {
                            JSONObject object = new JSONObject(result);
                            String description = Html.fromHtml(object.getString("description")).toString();
                            Log.d("desc",description);
                            terms_txt.setBackgroundColor(0x00000000);
                            terms_txt.loadData(description , "text/html; charset=UTF-8", null);
                        }else{
                            JSONObject object = new JSONObject(result);
                            Common.showAlert2(Terms.this,object.getString("status"),object.getString("message"));
                        }
                    }
                }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
