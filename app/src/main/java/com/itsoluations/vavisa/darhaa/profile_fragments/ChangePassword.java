package com.itsoluations.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.Status;
import com.itsoluations.vavisa.darhaa.web_service.Controller2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChangePassword extends AppCompatActivity {

    @BindView(R.id.saveBtn_)
    Button save;
    @BindView(R.id.oldPassword)
    EditText oldPass;
    @BindView(R.id.newPassword)
    EditText newPass;
    @BindView(R.id.confirmPassword)
    EditText conPass;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.saveBtn_)
    public void changePass() {
        changePassword();
    }

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    String oldPass_st, newPass_st, conPass_st;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_change_password);

        ButterKnife.bind(this);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

    }

    private void changePassword() {
        oldPass_st = oldPass.getText().toString();
        newPass_st = newPass.getText().toString();
        conPass_st = conPass.getText().toString();

        if (validation(oldPass_st, newPass_st, conPass_st)) {
            if (Common.isConnectToTheInternet(this))
                callRegisterApi(newPass_st, conPass_st, Common.current_user.getCustomerInfo().getCustomer_id(), oldPass_st);
            else
                Common.errorConnectionMess(this);
        }
    }

    private void callRegisterApi(String newPass_st, String conPass_st, Integer customer_id, String oldPass_st) {

        progressDialog.show();
        try {
            compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().changePassword(newPass_st, conPass_st, customer_id, oldPass_st)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            progressDialog.dismiss();

                            if (status.getStatus().contains("error")) {
                                Common.showAlert2(ChangePassword.this, status.getStatus(), status.getMessage());
                                oldPass.setText("");
                                newPass.setText("");
                                conPass.setText("");
                            } else {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ChangePassword.this);
                                builder1.setMessage(status.getMessage());
                                builder1.setTitle(status.getStatus());
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                finish();
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

    public boolean validation(String old_st, String new_st, String con_st) {

        /** check if fields are empty **/
        if (old_st.equals("") || new_st.equals("") || con_st.equals("")) {
            Common.showAlert(ChangePassword.this, R.string.error, R.string.fill_fields_to_continue);
            return false;
        } else {
            /** check if password is not less than 6 characters **/
            if (new_st.length() >= 6) {
                /** check if passwords match **/
                if (new_st.equals(con_st)) {
                    return true;
                } else {
                    Common.showAlert(ChangePassword.this, R.string.error, R.string.passwords_do_not_match);
                    return false;
                }
            } else {
                Common.showAlert(ChangePassword.this, R.string.error, R.string.password_less);

                return false;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
