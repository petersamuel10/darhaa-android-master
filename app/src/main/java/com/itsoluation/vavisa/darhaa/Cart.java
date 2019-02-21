package com.itsoluation.vavisa.darhaa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Cart extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
