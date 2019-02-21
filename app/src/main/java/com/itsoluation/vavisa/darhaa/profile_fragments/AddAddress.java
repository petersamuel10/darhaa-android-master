package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class AddAddress extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    @BindView(R.id.address_name)
    EditText address_name;
    @BindView(R.id.address_phone)
    EditText address_phone;
    @BindView(R.id.govenorate)
    Spinner governorate;
    @BindView(R.id.city)
    Spinner city;
    @BindView(R.id.address_block)
    EditText address_block;
    @BindView(R.id.address_street)
    EditText address_street;
    @BindView(R.id.address_building)
    EditText address_building;
    @BindView(R.id.address_floor)
    EditText address_floor;
    @BindView(R.id.address_apartment)
    EditText address_apartment;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.save_address)
    Button save_address;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}


    List<String> governorateList = new ArrayList<>() ;
    List<String> citiesList;
  //  List<City> allCitiesList;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String name,phone,block,street,building,floor,apartment;

    int userId,cityId;
   // Address address,editAddress;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_add_address);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(getBaseContext());
        progressDialog.setCancelable(false);

        if(Common.isArabic){ back_arrow.setRotation(180); }
        if(Common.isConnectToTheInternet(this))
        {
            getGovernorates();
            getAllCities();
        }
        else
            Common.errorConnectionMess(AddAddress.this);

        governorate.setOnItemSelectedListener(this);
        save_address.setOnClickListener(this);

    }



    private void getGovernorates() {

    }

    private void getAllCities() {

    }



    @OnClick(R.id.save_address)
    public void saveNewAddress(){

      //  userId = Common.currentUser.getId();
        name = address_name.getText().toString();
        phone = address_phone.getText().toString();
        block = address_block.getText().toString();
        street = address_street.getText().toString();
        building = address_building.getText().toString();
        floor = address_floor.getText().toString();
        apartment = address_apartment.getText().toString();

        //getCityId();

        if (validation(name, phone, block, street, building)) {

        }
    }

    private boolean validation(String name, String phone, String block, String street, String building) {

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, R.string.enterName, Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, R.string.enterPhone, Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(block))
        {
            Toast.makeText(this, R.string.enterBlock, Toast.LENGTH_SHORT).show();
            return false;
        } else if(TextUtils.isEmpty(street))
        {
            Toast.makeText(this, R.string.enterStreet, Toast.LENGTH_SHORT).show();
            return false;
        } else if(TextUtils.isEmpty(building))
        {
            Toast.makeText(this, R.string.enterBuilding, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

    }
}
