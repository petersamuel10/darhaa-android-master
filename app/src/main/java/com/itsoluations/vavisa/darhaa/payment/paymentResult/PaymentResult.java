package com.itsoluations.vavisa.darhaa.payment.paymentResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itsoluations.vavisa.darhaa.MainActivity;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;

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
    @BindView(R.id.knet_titles)
    LinearLayout knet_titles;
    @BindView(R.id.knet_info)
    LinearLayout knet_info;
    @BindView(R.id.order_title)
    TextView order_title;
    @BindView(R.id.order_id)
    TextView order_id_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_result);

        ButterKnife.bind(this);

        if (getIntent().getExtras().containsKey("status")) {
            if (getIntent().getStringExtra("status").equals("\"1\"")) {
                order_title.setVisibility(View.GONE);
                order_id_txt.setVisibility(View.GONE);
                status_txt.setText(R.string.congratulation);
                status_txt.setTextColor(getResources().getColor(R.color.blue));
                message_txt.setText(R.string.your_order_completed_successfuly);
                payment_id_txt.setText(getIntent().getStringExtra("paymentId"));
                date_txt.setText(getIntent().getStringExtra("date"));
                result_txt.setText(getIntent().getStringExtra("result"));
            } else if (getIntent().getStringExtra("status").equals("\"0\"")) {
                order_title.setVisibility(View.GONE);
                order_id_txt.setVisibility(View.GONE);
                status_txt.setText(R.string.order_payment_failed);
                status_txt.setTextColor(Color.RED);
                message_txt.setText(R.string.order_payment_failed);
                payment_id_txt.setText(getIntent().getStringExtra("paymentId"));
                date_txt.setText(getIntent().getStringExtra("date"));
                result_txt.setText(getIntent().getStringExtra("result"));
            }
        } else if (getIntent().getExtras().containsKey("order_id")) {
            knet_info.setVisibility(View.GONE);
            knet_titles.setVisibility(View.GONE);
            order_title.setVisibility(View.VISIBLE);
            order_id_txt.setVisibility(View.VISIBLE);

            order_id_txt.setText(getIntent().getStringExtra("order_id"));

        }

        total_txt.setText(getIntent().getStringExtra("total"));
    }

    public void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @OnClick(R.id.doneBtn)
    public void done() {

        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onBackPressed() {
    }
}
