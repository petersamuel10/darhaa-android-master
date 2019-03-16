package com.itsoluation.vavisa.darhaa.payment.paymentResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.MainActivity;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class PaymentResult extends AppCompatActivity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_result);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        payment_id_txt.setText(getIntent().getStringExtra("paymentId"));
        date_txt.setText(getIntent().getStringExtra("date"));
        result_txt.setText(getIntent().getStringExtra("result"));
        total_txt.setText(getIntent().getStringExtra("total"));
    }

    @OnClick(R.id.doneBtn)
    public void done(){

        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onBackPressed() { }

}
