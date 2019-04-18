package com.itsoluation.vavisa.darhaa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.User;
import com.itsoluation.vavisa.darhaa.web_service.Controller;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Register extends AppCompatActivity {

    @BindView(R.id.registerBtn)
    Button register_btn;
    @BindView(R.id.edName)
    EditText full_name_field;
    @BindView(R.id.edEmail)
    EditText email_field;
    @BindView(R.id.edPhone)
    EditText phone_field;
    @BindView(R.id.edPassword)
    EditText password_field;
    @BindView(R.id.edConfirmPass)
    EditText confirm_field;

    String full_name_str, email_str, phone_str, password_str, confirm_str, device_id;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @OnClick(R.id.have_account)
    public void loginActivity() {
        startActivity(new Intent(Register.this, Login.class));
        finish();
    }

    @OnClick(R.id.registerBtn)
    public void register() {
        makeRegisteration();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        Paper.init(this);

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void makeRegisteration() {

        //hide keyboard when click button
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(register_btn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        full_name_str = String.valueOf(full_name_field.getText());
        email_str = String.valueOf(email_field.getText());
        phone_str = String.valueOf(phone_field.getText());
        password_str = String.valueOf(password_field.getText());
        confirm_str = String.valueOf(confirm_field.getText());

        /** check if fields are empty **/
        if (full_name_str.equals("") || email_str.equals("") || phone_str.equals("") || password_str.equals("") || confirm_str.equals("")) {
            Common.showAlert(this, R.string.error, R.string.fill_fields_to_continue);
        } else {
            /** validate email **/
            if (!email_str.contains("@")) {
                Common.showAlert(this, R.string.error, R.string.validate_email);
            } else {
                /** validate phone number **/
                if (phone_str.equals("")) {
                    phone_str = "";
                } else {
                    /** check if password is not less than 6 characters **/
                    if (password_str.length() >= 6 || confirm_str.length() >= 6) {
                        /** check if passwords match **/
                        if (password_str.equals(confirm_str)) {
                            if (Common.isConnectToTheInternet(this))
                                callRegisterApi(full_name_str, email_str, phone_str, password_str, confirm_str);
                            else
                                Common.errorConnectionMess(this);
                            // new RegisterBackgroundTask(User.this).execute();
                        } else {
                            /** passwords do not match **/
                            Common.showAlert(this, R.string.error, R.string.passwords_do_not_match);
                        }
                    } else {
                        /** password is less than 6 characters **/
                        Common.showAlert(this, R.string.error, R.string.password_less);
                    }
                }
            }
        }
    }

    private void callRegisterApi(String name, String email, String phone, String password, String confirm) {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();


        compositeDisposable.add(new Controller().getAPI().register(name, email, phone, password, confirm, device_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        progressDialog.dismiss();
                        if (user.getStatus().equals("error")) {
                            Common.showAlert(Register.this, R.string.error, R.string.email_already_registered);
                        } else {
                            Common.current_user = user;
                            Paper.book("DarHaa").write("currentUser", Common.current_user);
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                    }
                }));

    }

}
