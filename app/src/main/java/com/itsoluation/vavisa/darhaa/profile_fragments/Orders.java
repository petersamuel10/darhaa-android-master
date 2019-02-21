package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Orders extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.item_order)
    LinearLayout orderLN;

   /* @OnClick(R.id.item_order)
    public void setd(){
        startActivity(new Intent(Orders.this,OrderDetails.class));
    }
*/
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_orders);
        ButterKnife.bind(this);

        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

        orderLN.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v== orderLN){
            startActivity(new Intent(this, OrderDetails.class));}
    }
}
