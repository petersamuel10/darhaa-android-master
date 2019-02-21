package com.itsoluation.vavisa.darhaa.view_setting;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.itsoluation.vavisa.darhaa.CategoryProducts;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Filter extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @BindView(R.id.rangeSeekbar)
    CrystalRangeSeekbar rangeSeekBar;

    @BindView(R.id.minPrice)
    TextView min_price;
    @BindView(R.id.maxPrice)
    TextView max_price;

    Button filter_apply;
    String min_value, max_value;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_filter);

        ButterKnife.bind(this);
        if(Common.isArabic) {
            back_arrow.setRotation(180);
            rangeSeekBar.setRotationY(180);
        }

        filter_apply = findViewById(R.id.filterApply);

        rangeSeekBar.setMaxValue(Float.parseFloat(CategoryProducts.category_price_max_value));
        rangeSeekBar.setMinValue(Float.parseFloat(CategoryProducts.category_price_min_value));

        // set listener
        rangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                min_price.setText(getResources().getString(R.string.from)+" "+String.valueOf(minValue)+" "+getResources().getString(R.string.kd));
                max_price.setText(getResources().getString(R.string.to)+" "+String.valueOf(maxValue)+" "+getResources().getString(R.string.kd));

                min_value = String.valueOf(minValue);
                max_value = String.valueOf(maxValue);
            }
        });

        filter_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryProducts.filter_type = "price_range";
                CategoryProducts.filter_value = min_value + "-" + max_value;
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}