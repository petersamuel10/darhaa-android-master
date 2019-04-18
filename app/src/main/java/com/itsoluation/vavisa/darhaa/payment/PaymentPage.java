package com.itsoluation.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.paymentData.CheckoutPageParameters;
import com.itsoluation.vavisa.darhaa.model.paymentData.CheckoutProductPage;
import com.itsoluation.vavisa.darhaa.payment.paymentResult.PaymentResult;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PaymentPage extends AppCompatActivity {

    @BindView(R.id.wb_payment)
    WebView wb_payment;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;
    String url,paymentId,refId,result,total,date,is_capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* if(Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_knet_page);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        CheckoutPageParameters checkoutPageParameters = getIntent().getExtras().getParcelable("checkout");
        if(Common.isConnectToTheInternet(this))
            callAPI(checkoutPageParameters);
        else
            Common.errorConnectionMess(this);
    }

    private void callAPI(final CheckoutPageParameters checkoutPageParameters) {

        compositeDisposable = new CompositeDisposable();

        progressDialog.show();
        try {
        compositeDisposable.add(Common.getAPI().checkoutProductPage(checkoutPageParameters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CheckoutProductPage>() {
                    @Override
                    public void accept(CheckoutProductPage checkoutProductPage) throws Exception {

                        try{
                            url = checkoutProductPage.getPayment();

                            WebSettings webSettings = wb_payment.getSettings();
                            webSettings.setJavaScriptEnabled(true);


                            wb_payment.loadUrl(url);
                            progressDialog.dismiss();

                            wb_payment.setWebViewClient(new WebViewClient(){
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);

                                    wb_payment.evaluateJavascript("document.getElementById('PaymentID').value", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {

                                            Log.i("xx1",value);
                                            paymentId = value.substring(1,value.length()-1);
                                        }
                                    });

                                    wb_payment.evaluateJavascript("document.getElementById('refID').value", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {

                                            Log.i("xx2",value);
                                            refId = value.substring(1,value.length()-1);
                                        }
                                    });

                                    wb_payment.evaluateJavascript("document.getElementById('Result').value", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.i("xx3",value);

                                            result = value.substring(1,value.length()-1);
                                        }
                                    });

                                    wb_payment.evaluateJavascript("document.getElementById('total').value", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {

                                            Log.i("xx4",value);

                                            total= value.substring(1,value.length()-1);
                                        }
                                    });

                                    wb_payment.evaluateJavascript("document.getElementById('date').value", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.i("xx5",value);

                                            date = value.substring(1,value.length()-1);
                                        }
                                    });



                                    wb_payment.evaluateJavascript("document.getElementById('is_captured').value", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {

                                            Log.i("xx6",value);

                                            is_capture = value.substring(1,value.length()-1);

                                            if("\"1\"".equals(value)){
                                                Intent intent = new Intent(PaymentPage.this, PaymentResult.class);
                                                intent.putExtra("paymentId",paymentId);
                                                intent.putExtra("date",date);
                                                intent.putExtra("result",result);
                                                intent.putExtra("total",total);
                                                intent.putExtra("status",value);
                                                startActivity(intent);
                                                finish();

                                            }else if ("\"0\"".equals(value))
                                            {Intent intent = new Intent(PaymentPage.this, PaymentResult.class);
                                                intent.putExtra("paymentId",paymentId);
                                                intent.putExtra("date",date);
                                                intent.putExtra("result",result);
                                                intent.putExtra("total",total);
                                                intent.putExtra("status",value);
                                                startActivity(intent);
                                                finish();

                                            }
                                        }
                                    });
                                }
                            });



                        }catch (Exception e){
                            Log.i("errrr",e.getMessage());
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

        if(Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");
    }
}
