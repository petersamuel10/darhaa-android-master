package com.itsoluation.vavisa.darhaa.view_setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    public void setBack() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_filter);

        ButterKnife.bind(this);
        if (Common.isArabic) {
            back_arrow.setRotation(180);
            rangeSeekBar.setRotationY(180);
        }

        filter_apply = findViewById(R.id.filterApply);
        if (!CategoryProducts.category_price_max_value.equals("0.000")) {

            min_price.setText(getResources().getString(R.string.from) + " " + CategoryProducts.category_price_min_value + " " + getResources().getString(R.string.kd));
            max_price.setText(getResources().getString(R.string.to) + " " + CategoryProducts.category_price_max_value + " " + getResources().getString(R.string.kd));

            if (CategoryProducts.category_price_max_value.equals(CategoryProducts.category_price_min_value)) {
                rangeSeekBar.setEnabled(false);
                filter_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });

            }else {

                rangeSeekBar.setMaxValue(Float.parseFloat(CategoryProducts.category_price_max_value));
                rangeSeekBar.setMinValue(Float.parseFloat(CategoryProducts.category_price_min_value));

                if(Float.parseFloat(CategoryProducts.category_price_max_value)<10.000) {
                    rangeSeekBar.setSteps(0.25f);
                }else
                    rangeSeekBar.setSteps(1.0f);
                // set listener
                rangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        min_price.setText(getResources().getString(R.string.from) + " " + String.format("%.3f", minValue.floatValue()) + " " + getResources().getString(R.string.kd));
                        max_price.setText(getResources().getString(R.string.to) + " " + String.format("%.3f", maxValue.floatValue()) + " " + getResources().getString(R.string.kd));

                        min_value = String.format("%.3f", minValue.floatValue());
                        max_value = String.format("%.3f", maxValue.floatValue());
                    }
                });


                filter_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CategoryProducts.sort_type = "p.price";
                        CategoryProducts.filter_value_price = min_value + "-" + max_value;
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }

        } else {
            rangeSeekBar.setEnabled(false);
            min_price.setText(getResources().getString(R.string.from) + " " + CategoryProducts.category_price_min_value + " " + getResources().getString(R.string.kd));
            max_price.setText(getResources().getString(R.string.to) + " " + CategoryProducts.category_price_max_value + " " + getResources().getString(R.string.kd));

            filter_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}