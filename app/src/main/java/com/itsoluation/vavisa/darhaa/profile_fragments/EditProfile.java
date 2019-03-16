package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    String name_str,email_str,phone_str;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void setBack() {onBackPressed(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_profile);

        ButterKnife.bind(this);

        if (Common.isArabic) {back_arrow.setRotation(180);}

        name_ed.setText(Common.current_user.getCustomerInfo().getFirstname());
        email_ed.setText(Common.current_user.getCustomerInfo().getEmail());
        phone_ed.setText(Common.current_user.getCustomerInfo().getTelephone());
    }

    @OnClick(R.id.update)
    public void update() {
        name_str = name_ed.getText().toString();
        email_str = email_ed.getText().toString();
        phone_str =  phone_ed.getText().toString();

        if (validate(name_str,email_str,phone_str)){
                if (Common.isConnectToTheInternet(this))
                    callUpdateApi(name_str, email_str, phone_str ,Common.current_user.getCustomerInfo().getCustomer_id());
                else
                    Common.errorConnectionMess(this);

        }

    }

    private void callUpdateApi(final String name, final String email, final String phone, Integer user_id) {

        compositeDisposable.add(Common.getAPI2().editProfile(name,email,phone, String.valueOf(user_id))
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<Status>() {
                               @Override
                               public void accept(Status status) throws Exception {
                                   if(status.getStatus().equals("error")){
                                       Common.showAlert2(EditProfile.this,status.getStatus(),status.getMessage());
                                   }else {
                                       Common.current_user.getCustomerInfo().setFirstname(name);
                                       Common.current_user.getCustomerInfo().setEmail(email);
                                       Common.current_user.getCustomerInfo().setTelephone(phone);

                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProfile.this);
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

    }

    private boolean validate(String name, String email, String phone) {

        if (name.equals("") || email.equals("") || phone.equals("")) {
            Common.showAlert(EditProfile.this,R.string.error,R.string.missing_required_data);
            return false;
        }else if (!email.contains("@") || !email.contains(".com")) {
            Common.showAlert(EditProfile.this,R.string.error,R.string.validate_email);
            return false;
        } else if (phone_str.length() < 8) {
                Common.showAlert(EditProfile.this,R.string.error,R.string.validate_phone_number);
                return false;
            }else
                return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
