package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressAdd;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressDetails;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.model.address.address.Countries;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.Area;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.AreaAndCities;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.City;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AddAddress extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.address_name)
    EditText title_ed;
    @BindView(R.id.country)
    Spinner country_spinner;
    @BindView(R.id.area)
    Spinner area_spinner;
    @BindView(R.id.city)
    Spinner city_spinner;
    @BindView(R.id.postcode)
    EditText postcode_ed;
    @BindView(R.id.address_description)
    EditText address_desc_ed;
    @BindView(R.id.default_address)
    CheckBox default_address_ck;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    ArrayList<Countries> countryArrayList;
    ArrayList<Area> areasArrayList;
    ArrayList<City> cityArrayList;
    List<String> country_name_list;
    List<String> area_name_list;
    List<String> cities_name_list;
    AddressDetails currentAddress;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String first_name,title,postcode,address_desc,city_name,country_id,zone_id,default_;;
    Integer userId;

    ProgressDialog progressDialog;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_add_address);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(AddAddress.this);
        progressDialog.setCancelable(false);
        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        save = findViewById(R.id.save_ad_bn);


        // to show adddress details only
            if (Common.isConnectToTheInternet(this)) {
                getCountries();
            } else
                Common.errorConnectionMess(AddAddress.this);

            country_spinner.setOnItemSelectedListener(this);
            area_spinner.setOnItemSelectedListener(this);
            city_spinner.setOnItemSelectedListener(this);
    }

    private void getCountries() {
        progressDialog.show();
        countryArrayList = new ArrayList<>();
        country_name_list = new ArrayList<>();

        Observable<ArrayList<Countries>> apiCountry = Common.getAPI().getCountries().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        apiCountry.subscribe(new Observer<ArrayList<Countries>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<Countries> countries) {
                countryArrayList.addAll(countries);
                for (Countries country : countries) {
                    country_name_list.add(country.getName());
                }
            }

            @Override
            public void onError(Throwable e) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddAddress.this);
                builder1.setMessage(e.getMessage());
                builder1.setTitle(R.string.error);
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
            }

            @Override
            public void onComplete() {
                progressDialog.dismiss();
                List<String> countries_name_list = new ArrayList<>();
                for (Countries country : countryArrayList) {
                    countries_name_list.add(country.getName());
                }
                ArrayAdapter<String> country_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, countries_name_list);
                country_adapter.setDropDownViewResource(R.layout.spinner_item);
                country_adapter.notifyDataSetChanged();
                country_spinner.setAdapter(country_adapter);

                if (Common.isEditAddress) {
                    currentAddress = new AddressDetails ();
                    currentAddress = Common.currentAddress;
                    // Common.isEditAddress = false;
                    setAddressData();
                }
            }
        });
    }

    private void getArea(String country_id, String country_code) {
        progressDialog.show();
        area_name_list = new ArrayList<>();
        cities_name_list = new ArrayList<>();
        areasArrayList = new ArrayList<>();
        cityArrayList = new ArrayList<>();
        try {
            compositeDisposable.add(Common.getAPI().getAreaAndCities(country_id, country_code)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AreaAndCities>() {
                        @Override
                        public void accept(AreaAndCities areaAndCities) throws Exception {
                            progressDialog.dismiss();
                            areasArrayList.addAll(areaAndCities.getAreas());
                            cityArrayList.addAll(areaAndCities.getCities());
                            for (Area area : areasArrayList) {
                                area_name_list.add(area.getName());
                            }
                            for (City city : cityArrayList) {
                                cities_name_list.add(city.getCityName());
                            }
                            ArrayAdapter<String> area_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, area_name_list);
                            area_adapter.setDropDownViewResource(R.layout.spinner_item);
                            area_adapter.notifyDataSetChanged();
                            area_spinner.setAdapter(area_adapter);

                            ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, cities_name_list);
                            city_adapter.setDropDownViewResource(R.layout.spinner_item);
                            city_adapter.notifyDataSetChanged();
                            city_spinner.setAdapter(city_adapter);

                            if(Common.isEditAddress){
                                city_spinner.setSelection(cities_name_list.indexOf(currentAddress.getCity()));
                                area_spinner.setSelection(area_name_list.indexOf(currentAddress.getZone()));
                                Common.isEditAddress = false;
                                save.setText(R.string.update);
                            }

                        }
                    }));
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }

    }

    private void setAddressData() {
        if(Common.showAddrDetails){
            Common.showAddrDetails = false;
            Common.isEditAddress = false;
            title_ed.setEnabled(false);
            country_spinner.setEnabled(false);
            area_spinner.setEnabled(false);
            city_spinner.setEnabled(false);
            postcode_ed.setEnabled(false);
            address_desc_ed.setEnabled(false);
            default_address_ck.setEnabled(false);
            save.setVisibility(View.GONE);
        }
        title_ed.setText(currentAddress.getTitle());
        postcode_ed.setText(currentAddress.getPostcode());
        address_desc_ed.setText(currentAddress.getAddress_1());
        default_address_ck.setChecked(currentAddress.getDefault_());
        country_spinner.setSelection(country_name_list.indexOf(currentAddress.getCountry()));
        country_id = currentAddress.getCountry_id();
        zone_id = currentAddress.getZone_id();
        city_name = currentAddress.getCity();
    }

    @OnClick(R.id.save_ad_bn)
    public void saveNewAddress() {

        userId = Common.current_user.getCustomerInfo().getCustomer_id();
        first_name = Common.current_user.getCustomerInfo().getFirstname();
        title = title_ed.getText().toString();
        postcode = postcode_ed.getText().toString();
        address_desc = address_desc_ed.getText().toString();

        if (validation(title, postcode, address_desc)) {
            if (default_address_ck.isChecked())
                default_ = "1";
            else
                default_ = null;

            if(save.getText().equals(R.string.save)) {
                compositeDisposable.add(Common.getAPI2().addAddress(userId, first_name, address_desc, city_name, country_id, postcode, zone_id, title, default_)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Status>() {
                            @Override
                            public void accept(Status status) throws Exception {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddAddress.this);
                                builder1.setMessage(status.getMessage());
                                builder1.setTitle(status.getStatus());
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
                            }
                        }));


                title_ed.setText("");
                postcode_ed.setText("");
                address_desc_ed.setText("");
                default_address_ck.setChecked(false);
                country_spinner.setSelection(0);
            }else
            {
                Log.i("aaaasss","eeeeeee");
                compositeDisposable.add(Common.getAPI2().editAddress(userId,Common.address_id, first_name, address_desc, city_name, country_id, postcode, zone_id, title, default_)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Status>() {
                            @Override
                            public void accept(Status status) throws Exception {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddAddress.this);
                                builder1.setMessage(status.getMessage());
                                builder1.setTitle(status.getStatus());
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
                            }
                        }));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(Color.BLACK);
        switch (parent.getId()) {
            case R.id.country:
                Log.i("tttrr","nnn,mnnnndf");
                Countries country = countryArrayList.get(position);
                country_id = country.getCountry_id();
                if (Common.isConnectToTheInternet(this)) {
                    getArea(country.getCountry_id(), country.getCountry_code());
                } else
                    Common.errorConnectionMess(AddAddress.this);
                break;
            case R.id.area:
                Area area = areasArrayList.get(position);
                zone_id = area.getZone_id();
                break;
            case R.id.city:
                city_name = cities_name_list.get(position);
        }
    }

    private boolean validation(String title, String postcode_, String address_desc) {

        if (TextUtils.isEmpty(title)) {
            Common.showAlert(AddAddress.this, R.string.error, R.string.enter_title);
            return false;
        } else if (TextUtils.isEmpty(address_desc)) {
            Common.showAlert(AddAddress.this, R.string.error, R.string.enter_address_desc);
            return false;
        } else if (TextUtils.isEmpty(postcode_)) {
            postcode = null;
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
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
