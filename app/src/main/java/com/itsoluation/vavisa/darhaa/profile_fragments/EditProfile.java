package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.web_service.Controller2;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EditProfile extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.edName)
    MaterialEditText name_ed;
    @BindView(R.id.edEmail)
    MaterialEditText email_ed;
    @BindView(R.id.edPhone)
    MaterialEditText phone_ed;

    String name_str, email_str, phone_str;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_profile);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        Paper.init(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");
        name_ed.setText(Common.current_user.getCustomerInfo().getFirstname());
        email_ed.setText(Common.current_user.getCustomerInfo().getEmail());
        phone_ed.setText(Common.current_user.getCustomerInfo().getTelephone());
    }

    @OnClick(R.id.update)
    public void update() {
        name_str = name_ed.getText().toString();
        email_str = email_ed.getText().toString();
        phone_str = phone_ed.getText().toString();

        if (validate(name_str, email_str, phone_str)) {
            if (Common.isConnectToTheInternet(this))
                callUpdateApi(name_str, email_str, phone_str, Common.current_user.getCustomerInfo().getCustomer_id());
            else
                Common.errorConnectionMess(this);

        }

    }

    private void callUpdateApi(final String name, final String email, final String phone, Integer user_id) {
        progressDialog.show();
        try {
            compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().editProfile(name, email, phone, String.valueOf(user_id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            progressDialog.dismiss();
                            if (status.getStatus().equals("error")) {
                                Common.showAlert2(EditProfile.this, status.getStatus(), status.getMessage());
                            } else {

                                Common.current_user.getCustomerInfo().setFirstname(name);
                                Common.current_user.getCustomerInfo().setEmail(email);
                                Common.current_user.getCustomerInfo().setTelephone(phone);

                                Paper.book("DarHaa").write("currentUser", Common.current_user);
                                finish();

                            }

                        }
                    }));

        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }
    }

    private boolean validate(String name, String email, String phone) {

        if (name.equals("") || email.equals("") || phone.equals("")) {
            Common.showAlert(EditProfile.this, R.string.error, R.string.missing_required_data);
            return false;
        } else if (!email.contains("@")) {
            Common.showAlert(EditProfile.this, R.string.error, R.string.validate_email);
            return false;
        } else
            return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}
