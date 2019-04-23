package com.itsoluations.vavisa.darhaa.view_setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.itsoluations.vavisa.darhaa.CategoryProducts;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Sort extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.lanGroup)
    RadioGroup radioGroup;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    Button sort_apply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_sort);

        ButterKnife.bind(this);
        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

        sort_apply = findViewById(R.id.sortApply);

        radioGroup.clearCheck();

        sort_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioButtonGroup = findViewById(R.id.lanGroup);

                int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                View radioButton = radioButtonGroup.findViewById(radioButtonID);
                int idx = radioButtonGroup.indexOfChild(radioButton);

                if (idx == 0) { // A - Z
                    CategoryProducts.filter_value = "ASC";
                    CategoryProducts.sort_type = "pd.name";
                } else if (idx == 1 ){ // Z - A
                    CategoryProducts.filter_value = "DESC";
                    CategoryProducts.sort_type = "pd.name";
                } else if (idx == 2) { // price high -low
                    CategoryProducts.filter_value = "DESC";
                    CategoryProducts.sort_type = "p.price";
                } else { // price low - high
                    CategoryProducts.filter_value = "ASC";
                    CategoryProducts.sort_type = "p.price";
                }

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