package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class Language extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.english)
    RelativeLayout english;
    @BindView(R.id.true_english)
    ImageView true_english;
    @BindView(R.id.arabic)
    RelativeLayout arabic;
    @BindView(R.id.true_arabic)
    ImageView true_arabic;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_language);
        ButterKnife.bind(this);

        Paper.init(this);

        if(Common.isArabic) {
            true_arabic.setVisibility(View.VISIBLE);
            back_arrow.setRotation(180);
        }
        else
            true_english.setVisibility(View.VISIBLE);

        english.setOnClickListener(this);
        arabic.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(v==english)
        {
            true_english.setVisibility(View.VISIBLE);
            true_arabic.setVisibility(View.GONE);
        }else if (v == arabic ){

            true_arabic.setVisibility(View.VISIBLE);
            true_english.setVisibility(View.GONE);
        }


    }

    @OnClick(R.id.lanApply)
    public void setLan()
    {
        if(true_arabic.getVisibility() == View.VISIBLE){
            Paper.book("DarHaa").write("language","ar");
            Common.isArabic = true;
        }else if (true_english.getVisibility() == View.VISIBLE){
            Paper.book("DarHaa").write("language","en");
            Common.isArabic =false;
        }

        Intent i = this.getPackageManager()
                .getLaunchIntentForPackage( this.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
