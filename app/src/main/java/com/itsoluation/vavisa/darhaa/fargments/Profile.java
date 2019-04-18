package com.itsoluation.vavisa.darhaa.fargments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.itsoluation.vavisa.darhaa.Login;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.ProfileData;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.profile_fragments.Addresses;
import com.itsoluation.vavisa.darhaa.profile_fragments.ChangePassword;
import com.itsoluation.vavisa.darhaa.profile_fragments.EditProfile;
import com.itsoluation.vavisa.darhaa.profile_fragments.Language;
import com.itsoluation.vavisa.darhaa.profile_fragments.Orders;
import com.itsoluation.vavisa.darhaa.profile_fragments.Terms;
import com.itsoluation.vavisa.darhaa.web_service.Controller2;

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

    @BindView(R.id.view)
    LinearLayout contentView;
    @BindView(R.id.scrollData)
    ScrollView scrollView;
    @BindView(R.id.headerLN)
    LinearLayout headerLN;
    @BindView(R.id.addressesLN)
    LinearLayout addressLN;
    @BindView(R.id.orderLN)
    LinearLayout orderLN;
    @BindView(R.id.languageLN)
    LinearLayout languageLN;
    @BindView(R.id.changePassLN)
    LinearLayout changePasswordLN;
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.are_you_logout);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton(R.string.no,null);

        builder.show();
    }
    @OnClick(R.id.editBtn)
    public void editProfile(){getContext().startActivity(new Intent(getContext(), EditProfile.class));}
    @OnClick(R.id.loginLN)
    public void login(){
        Intent i = new Intent(getContext(),Login.class);
        i.putExtra("first_time",true);
        startActivity(i);
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;
    String first_name,last_name,email,phone;


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
        if(Paper.book("DarHaa").contains("currentUser")) {
            showView();
            contentView.setVisibility(View.GONE);
            requestData();
        } else
            hideView();

        addressLN.setOnClickListener(this);
        orderLN.setOnClickListener(this);
        languageLN.setOnClickListener(this);
        changePasswordLN.setOnClickListener(this);
        termsLN.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Paper.book("DarHaa").contains("currentUser")) {
            name_txt.setText(Common.current_user.getCustomerInfo().getFirstname());
            email_txt.setText(Common.current_user.getCustomerInfo().getEmail());
            phone_txt.setText(Common.current_user.getCustomerInfo().getTelephone());
            setCharacters();
        }
    }

    private void requestData() {

        if (Common.isConnectToTheInternet(getActivity())) {

            try {
            this.progressDialog.setMessage(getString(R.string.loading));
            this.progressDialog.show();
            compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().getProfile(Common.current_user.getCustomerInfo().getCustomer_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ProfileData>() {
                        @Override
                        public void accept(ProfileData profileData) throws Exception {
                            progressDialog.dismiss();
                            contentView.setVisibility(View.VISIBLE);
                            if (profileData.getStatus() != null) {
                                if(profileData.getMessage().contains(getString(R.string.access_token))) {
                                    Common.showAlert2(getContext(),getResources().getString(R.string.warning),getResources().getString(R.string.login_in_another_device));
                                    logout();
                                    showView();
                                }else
                                    Common.showAlert2(getContext(),profileData.getStatus(),profileData.getMessage());

                            } else {
                                first_name = profileData.getFirstname();
                                email = profileData.getEmail();
                                phone = profileData.getTelephone();

                                name_txt.setText(first_name);
                                email_txt.setText(email);
                                phone_txt.setText(phone);


                               setCharacters();
                            }

                        }
                    }));

        } catch (Exception e) {
            Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
        }

        } else
            Common.errorConnectionMess(getContext());
    }

    private void  setCharacters(){

        String initials = "";
        for (String s : name_txt.getText().toString().split(" ")) {
            initials+=s.charAt(0);
        }

        if(initials.length()>2)
            initials = initials.substring(0,2);

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().textColor(Color.BLACK).toUpperCase()
                .withBorder(8).bold().fontSize(160).endConfig()
                .buildRoundRect(initials, Color.WHITE,300);

        //   Toast.makeText(getContext(), "//"+profile_image_txt.toUpperCase(), Toast.LENGTH_SHORT).show();

        profile_image.setImageDrawable(drawable);
    }
    @Override
    public void onClick(View v) {

        if (v == addressLN) {
            getActivity().startActivity(new Intent(getContext(), Addresses.class));
        } else if (v == orderLN) {
            getActivity().startActivity(new Intent(getContext(), Orders.class));
        } else if (v == languageLN) {
            getActivity().startActivity(new Intent(getContext(), Language.class));
        } else if(v == changePasswordLN){
            getContext().startActivity(new Intent(getContext(), ChangePassword.class));
        } else if (v == termsLN) {
            getActivity().startActivity(new Intent(getContext(), Terms.class));
        }
    }

    private void logout() {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();

        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if(Common.isConnectToTheInternet(getContext())) {
            try {

            compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().logout(Common.current_user.getCustomerInfo().getCustomer_id(), android_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            progressDialog.dismiss();
                            if (status.getStatus().equals("error")) {
                                Common.showAlert2(getContext(), status.getStatus(), status.getMessage());
                            } else {

                                Snackbar snackbar = Snackbar.make(contentView, getResources().getString(R.string.logout_successfully), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                Paper.book("DarHaa").delete("currentUser");
                                Common.current_user = null;
                                hideView();
                            }
                        }
                    }));

            } catch (Exception e) {
                Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
            }
        }else
            Common.errorConnectionMess(getContext());

    }

    private void hideView() {

        headerLN.setVisibility(View.GONE);
        addressLN.setVisibility(View.GONE);
        orderLN.setVisibility(View.GONE);
        changePasswordLN.setVisibility(View.GONE);
        loginLN.setVisibility(View.VISIBLE);
    }

    private void showView() {

        headerLN.setVisibility(View.VISIBLE);
        addressLN.setVisibility(View.VISIBLE);
        orderLN.setVisibility(View.VISIBLE);
        changePasswordLN.setVisibility(View.VISIBLE);
        loginLN.setVisibility(View.GONE);
    }
}
