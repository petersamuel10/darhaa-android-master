package com.itsoluation.vavisa.darhaa.payment.paymentResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.MainActivity;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class PaymentResult extends AppCompatActivity {

    @BindView(R.id.status)
    TextView status_txt;
    @BindView(R.id.message)
    TextView message_txt;
    @BindView(R.id.paymentId)
    TextView payment_id_txt;
    @BindView(R.id.date)
    TextView date_txt;
    @BindView(R.id.result)
    TextView result_txt;
    @BindView(R.id.total)
    TextView total_txt;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_result);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        if(getIntent().getStringExtra("status").equals("\"1\"")){
            status_txt.setText(R.string.congratulation);
            status_txt.setTextColor(getResources().getColor(R.color.blue));
            message_txt.setText(R.string.your_order_completed_successfuly);
        }else {
            status_txt.setText(R.string.order_payment_failed);
            status_txt.setTextColor(Color.RED);
            message_txt.setText(R.string.order_payment_failed);
        }
        payment_id_txt.setText(getIntent().getStringExtra("paymentId"));
        date_txt.setText(getIntent().getStringExtra("date"));
        result_txt.setText(getIntent().getStringExtra("result"));
        total_txt.setText(getIntent().getStringExtra("total"));
    }

    public void setLanguage(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale= locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    @OnClick(R.id.doneBtn)
    public void done(){

        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
