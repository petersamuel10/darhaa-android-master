package com.itsoluation.vavisa.darhaa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ForgetPassword extends AppCompatActivity {

    @BindView(R.id.edEmail)
    EditText email_field;
    @BindView(R.id.forgetBtn)
    Button forget_btn;

    @OnClick(R.id.forgetBtn)
    public void forget_(){forgetPassword();}

    String email_str;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void forgetPassword() {

        //hide keyboard when click button
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(forget_btn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        email_str = String.valueOf(email_field.getText());

        /** check if fields are empty **/
        if (email_str.equals("")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgetPassword.this);
            builder1.setMessage(R.string.please_enter_email);
            builder1.setTitle(R.string.error);
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
            if(Common.isConnectToTheInternet(this))
                callLoginApi(email_str);
            else
                Common.errorConnectionMess(this);
        }
    }

    private void callLoginApi(String email) {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();
        try {

        compositeDisposable.add(Common.getAPI().forgotten(email)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<User>() {
                               @Override
                               public void accept(User user) throws Exception {
                                   progressDialog.dismiss();
                                   if (user.getStatus().equals("error")) {
                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgetPassword.this);
                                       builder1.setMessage(user.getMessage());
                                       builder1.setTitle(R.string.warning);
                                       builder1.setCancelable(true);
                                       builder1.setPositiveButton(
                                               R.string.ok,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick(DialogInterface dialog, int id) {
                                                       dialog.cancel();
                                                       email_field.setText("");
                                                   }
                                               });

                                       AlertDialog alert11 = builder1.create();
                                       alert11.show();
                                   }else {
                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgetPassword.this);
                                       builder1.setMessage(user.getMessage());
                                       builder1.setTitle(user.getStatus());
                                       builder1.setCancelable(true);
                                       builder1.setPositiveButton(
                                               R.string.ok,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick(DialogInterface dialog, int id) {
                                                       dialog.cancel();
                                                       email_field.setText("");
                                                   }
                                               });

                                       AlertDialog alert11 = builder1.create();
                                       alert11.show();
                                   }
                               }
                           }));

    } catch (Exception e) {
        Common.showAlert2(this, getString(R.string.warning), e.getMessage());
    }

}

}
