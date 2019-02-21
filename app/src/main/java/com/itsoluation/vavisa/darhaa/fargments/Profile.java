package com.itsoluation.vavisa.darhaa.fargments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.itsoluation.vavisa.darhaa.Login;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.ProfileData;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.profile_fragments.Addresses;
import com.itsoluation.vavisa.darhaa.profile_fragments.Language;
import com.itsoluation.vavisa.darhaa.profile_fragments.Orders;
import com.itsoluation.vavisa.darhaa.profile_fragments.Terms;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment implements View.OnClickListener {

    @BindView(R.id.headerLN)
    LinearLayout headerLN;
    @BindView(R.id.addressesLN)
    LinearLayout addressLN;
    @BindView(R.id.orderLN)
    LinearLayout orderLN;
    @BindView(R.id.languageLN)
    LinearLayout languageLN;
    @BindView(R.id.termsLN)
    LinearLayout termsLN;
    @BindView(R.id.loginLN)
    LinearLayout loginLN;
    @BindView(R.id.logoutBtn)
    ImageView logout;
    @BindView(R.id.ic_phone)
    ImageView ic_phone;

    @BindView(R.id.profile_image)
    ImageView profile_image;
    @BindView(R.id.txtName)
    TextView name_txt;
    @BindView(R.id.txtEmail)
    TextView email_txt;
    @BindView(R.id.txtPhone)
    TextView phone_txt;

    @OnClick(R.id.logoutBtn)
    public void logout_() {
        logout();
    }
    @OnClick(R.id.loginLN)
    public void login(){startActivity(new Intent(getContext(),Login.class));}

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;
    String device_id,first_name,last_name,email,phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        progressDialog = new ProgressDialog(getActivity());
        ButterKnife.bind(this, view);
        if (Common.isArabic) {
            logout.setRotationY(180);
            ic_phone.setRotationY(180);
        }

        addressLN.setOnClickListener(this);
        orderLN.setOnClickListener(this);
        languageLN.setOnClickListener(this);
        termsLN.setOnClickListener(this);
        if(Paper.book("DarHaa").contains("currentUser"))
            requestData();
        else
            newView();
        return view;
    }

    private void requestData() {
        if (Common.isConnectToTheInternet(getActivity())) {
            this.progressDialog.setMessage(getString(R.string.loading));
            this.progressDialog.show();
            compositeDisposable.add(Common.getAPI2().getProfile(Common.current_user.getCustomerInfo().getCustomer_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ProfileData>() {
                        @Override
                        public void accept(ProfileData profileData) throws Exception {
                            progressDialog.dismiss();
                            if (profileData.getStatus() != null) {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                builder1.setMessage(profileData.getMessage());
                                builder1.setTitle(profileData.getStatus());
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

                                first_name = profileData.getFirstname();
                                last_name = profileData.getLastname();
                                email = profileData.getEmail();
                                phone = profileData.getTelephone();

                                name_txt.setText(first_name+" "+last_name);
                                email_txt.setText(email);
                                phone_txt.setText(phone);

                                String profile_image_txt = String.valueOf(first_name.charAt(0))+String.valueOf(last_name.charAt(0));

                                TextDrawable drawable = TextDrawable.builder()
                                        .beginConfig().textColor(Color.BLACK).toUpperCase()
                                        .withBorder(8).bold().fontSize(160).endConfig()
                                        .buildRoundRect(profile_image_txt, Color.WHITE,300);

                             //   Toast.makeText(getContext(), "//"+profile_image_txt.toUpperCase(), Toast.LENGTH_SHORT).show();

                                profile_image.setImageDrawable(drawable);
                            }

                        }
                    }));

        } else
            Common.errorConnectionMess(getContext());
    }

    @Override
    public void onClick(View v) {

        if (v == addressLN) {
            getActivity().startActivity(new Intent(getContext(), Addresses.class));
        } else if (v == orderLN) {
            getActivity().startActivity(new Intent(getContext(), Orders.class));
        } else if (v == languageLN) {
            getActivity().startActivity(new Intent(getContext(), Language.class));
        } else if (v == termsLN) {
            getActivity().startActivity(new Intent(getContext(), Terms.class));
        }
    }


    private void logout() {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();

        device_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        compositeDisposable.add(Common.getAPI().logout(Common.current_user.getCustomerInfo().getCustomer_id(), device_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        progressDialog.dismiss();
                        if (status.getStatus().equals("error")) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage(status.getMessage());
                            builder1.setTitle(status.getStatus());
                            builder1.setCancelable(true);
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage(status.getMessage());
                            builder1.setTitle(status.getStatus());
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            Paper.book("DarHaa").delete("currentUser");
                            newView();
                        }

                    }
                }));
    }

    private void newView() {

        headerLN.setVisibility(View.GONE);
        addressLN.setVisibility(View.GONE);
        orderLN.setVisibility(View.GONE);
        loginLN.setVisibility(View.VISIBLE);
    }

}
