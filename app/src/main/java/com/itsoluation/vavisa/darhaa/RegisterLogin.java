package com.itsoluation.vavisa.darhaa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterLogin extends AppCompatActivity {

    @BindView(R.id.footer)
    ImageView footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginBtn)
    public void loginActivity(){
        startActivity(new Intent(RegisterLogin.this,Login.class));
        finish();
    }

    @OnClick(R.id.registerBtn)
    public void RegisterActivity(){
        startActivity(new Intent(RegisterLogin.this,Register.class));
        finish();
    }

    @OnClick(R.id.skip)
    public void skipActivity(){
        Common.isSkip = true;
        startActivity(new Intent(RegisterLogin.this,MainActivity.class));
        finish();
    }
}