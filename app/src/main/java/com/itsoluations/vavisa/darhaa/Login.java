package com.itsoluations.vavisa.darhaa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.User;
import com.itsoluations.vavisa.darhaa.web_service.Controller;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Login extends AppCompatActivity {

    @BindView(R.id.edEmail)
    EditText email_field;
    @BindView(R.id.edPassword)
    EditText password_field;
    @BindView(R.id.loginBtn)
    Button login_btn;

    String email_str, password_str, device_id;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @OnClick(R.id.forget_password)
    public void forgetPassword() {
        startActivity(new Intent(Login.this, ForgetPassword.class));
    }

    @OnClick(R.id.dont_have_account)
    public void registerActivity() {
        startActivity(new Intent(Login.this, Register.class));
        finish();
    }

    @OnClick(R.id.loginBtn)
    public void login() {
        loginMethod();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        Paper.init(this);

        device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void loginMethod() {

        //hide keyboard when click button
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login_btn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        email_str = String.valueOf(email_field.getText());
        password_str = String.valueOf(password_field.getText());

        /** check if fields are empty **/
        if (email_str.equals("") || password_str.equals("")) {
            Common.showAlert(this, R.string.error, R.string.fill_fields_to_continue);
        } else {
            if (Common.isConnectToTheInternet(this))
                callLoginApi(email_str, password_str);
            else
                Common.errorConnectionMess(this);
        }

    }

    private void callLoginApi(String email, String password) {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();
        try {
            compositeDisposable.add(new Controller().getAPI().login(email, password, device_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<User>() {
                        @Override
                        public void accept(User user) throws Exception {
                            progressDialog.dismiss();
                            if (user.getStatus().equals("error")) {
                                Common.showAlert(Login.this, R.string.error, R.string.login_no_match);
                            } else {
                                Common.current_user = user;
                                // Common.userAccessToken = user.getUserAccessToken();
                                Paper.book("DarHaa").write("currentUser", Common.current_user);

                                if (getIntent().hasExtra("first_time")) {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    progressDialog.dismiss();
                                    finish();
                                } else {
                                    onBackPressed();
                                    finish();
                                }
                            }
                        }
                    }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }

    }

}
