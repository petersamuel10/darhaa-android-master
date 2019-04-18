package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import org.json.JSONObject;

import java.util.Locale;

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

    @Override
    protected void onStart() {
        super.onStart();
        if(Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");
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
                            String description = object.getString("description");
                            Log.d("desc",description);
                            terms_txt.setBackgroundColor(0x00000000);
                            terms_txt.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);
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

    public void setLanguage(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale= locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
