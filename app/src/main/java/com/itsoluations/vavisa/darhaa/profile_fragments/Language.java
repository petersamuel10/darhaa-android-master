package com.itsoluations.vavisa.darhaa.profile_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class Language extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.english)
    ConstraintLayout english;
    @BindView(R.id.true_english)
    ImageView true_english;
    @BindView(R.id.arabic)
    ConstraintLayout arabic;
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
            setLan();
        }else if (v == arabic ){

            true_arabic.setVisibility(View.VISIBLE);
            true_english.setVisibility(View.GONE);
            setLan();
        }

    }

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
