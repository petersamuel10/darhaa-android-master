package com.itsoluations.vavisa.darhaa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class ChooseLanguage extends AppCompatActivity implements OSSubscriptionObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        ButterKnife.bind(this);
        Paper.init(this);

        OneSignal.addSubscriptionObserver(this);

    }

    @OnClick(R.id.lan_arabic)
    public void setArabic()
    {
        Paper.book("DarHaa").write("language","ar");
        nextActivity();
    }

    @OnClick(R.id.lan_english)
    public void setEnglish()
    {
        Paper.book("DarHaa").write("language","en");
        nextActivity();
    }

    private void nextActivity() {

        startActivity(new Intent(this,MainActivity.class));
      //  finish();
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {

        if (!stateChanges.getFrom().getSubscribed() && stateChanges.getTo().getSubscribed()) {
            // get player ID
            Paper.book("DarHaa").write("oneSignalPlayerId",stateChanges.getTo().getUserId());
        }
    }
}
