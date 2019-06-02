package com.itsoluations.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.Status;
import com.itsoluations.vavisa.darhaa.model.address.address.AddressDetails;
import com.itsoluations.vavisa.darhaa.model.address.address.Countries;
import com.itsoluations.vavisa.darhaa.model.address.address.areaAndCity.Area;
import com.itsoluations.vavisa.darhaa.model.address.address.areaAndCity.AreaAndCities;
import com.itsoluations.vavisa.darhaa.model.address.address.areaAndCity.City;
import com.itsoluations.vavisa.darhaa.web_service.Controller2;

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

    //for showing address
    @BindView(R.id.country_ln)
    LinearLayout country_ln;
    @BindView(R.id.country_ed)
    EditText country_ed;
    @BindView(R.id.area_ln)
    LinearLayout area_ln;
    @BindView(R.id.area_ed)
    EditText area_ed;
    @BindView(R.id.city_ln)
    LinearLayout city_ln;
    @BindView(R.id.city_ed)
    EditText city_ed;

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
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
    boolean set_area_for_edit = false;

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
        if(Common.showAddrDetails){
            toolBarTitle.setText("");
            title_ed.setEnabled(false);
            country_ed.setVisibility(View.VISIBLE);
            area_ed.setVisibility(View.VISIBLE);
            city_ed.setVisibility(View.VISIBLE);
            country_ln.setVisibility(View.GONE);
            area_ln.setVisibility(View.GONE);
            city_ln.setVisibility(View.GONE);
            postcode_ed.setEnabled(false);
            address_desc_ed.setEnabled(false);
            default_address_ck.setEnabled(false);
            Common.showAddrDetails = false;

            save.setVisibility(View.GONE);

            showAddressDetails();

        }else {
            if (Common.isConnectToTheInternet(this)) {
                getCountries();
            } else
                Common.errorConnectionMess(AddAddress.this);

            country_spinner.setOnItemSelectedListener(this);
            area_spinner.setOnItemSelectedListener(this);
            city_spinner.setOnItemSelectedListener(this);
        }
    }

    private void showAddressDetails(){

        try {
        compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI()
                .getAddress(String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()),getIntent().getStringExtra("address_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AddressDetails>() {
                    @Override
                    public void accept(AddressDetails addressDetails) throws Exception {
                        if (addressDetails.getStatus() != null) {
                            Common.showAlert2(AddAddress.this, addressDetails.getStatus(), addressDetails.getMessage());
                        } else {
                            title_ed.setText(addressDetails.getTitle());
                            postcode_ed.setText(addressDetails.getPostcode());
                            address_desc_ed.setText(addressDetails.getAddress_1());
                            default_address_ck.setChecked(addressDetails.getDefault_());
                            country_ed.setText(addressDetails.getCountry());
                            area_ed.setText(addressDetails.getZone());
                            city_ed.setText(addressDetails.getCity());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(AddAddress.this, getString(R.string.error_occur), Toast.LENGTH_SHORT).show();
                    }
                }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }


    }

    private void getCountries() {
        countryArrayList = new ArrayList<>();
        country_name_list = new ArrayList<>();

try {
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
                List<String> countries_name_list = new ArrayList<>();
                for (Countries country : countryArrayList) {
                    countries_name_list.add(country.getName());
                }
                ArrayAdapter<String> country_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, countries_name_list);
                country_adapter.setDropDownViewResource(R.layout.spinner_item);
                country_adapter.notifyDataSetChanged();
                country_spinner.setAdapter(country_adapter);
            }
        });
    } catch (Exception e) {
        Common.showAlert2(this, getString(R.string.warning), e.getMessage());
    }

}

    private void getArea(String country_id, String country_code) {
        progressDialog.show();
        area_name_list = new ArrayList<>();
        cities_name_list = new ArrayList<>();
        areasArrayList = new ArrayList<>();
        cityArrayList = new ArrayList<>();
        try {
            if (Common.isConnectToTheInternet(this)) {
                compositeDisposable.add(Common.getAPI().getAreaAndCities(country_id, country_code)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<AreaAndCities>() {
                            @Override
                            public void accept(AreaAndCities areaAndCities) throws Exception {
                                if (!Common.isEditAddress)
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

                                if (Common.isEditAddress) {
                                    Common.isEditAddress = false;
                                    setAddressData();
                                }
                                if (set_area_for_edit) {
                                    set_area_for_edit = false;
                                    city_spinner.setSelection(cities_name_list.indexOf(currentAddress.getCity()));
                                    area_spinner.setSelection(area_name_list.indexOf(currentAddress.getZone()));
                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(AddAddress.this, getString(R.string.error_occur), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }else
                Common.errorConnectionMess(this);
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }

    }

    private void setAddressData() {

        try {
        compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI()
                .getAddress(String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()), getIntent().getStringExtra("address_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AddressDetails>() {
                    @Override
                    public void accept(AddressDetails addressDetails) throws Exception {
                        if (addressDetails.getStatus() != null) {
                            Common.showAlert2(AddAddress.this, addressDetails.getStatus(), addressDetails.getMessage());
                        } else {
                            currentAddress = addressDetails;
                            title_ed.setText(currentAddress.getTitle());
                            postcode_ed.setText(currentAddress.getPostcode());
                            address_desc_ed.setText(currentAddress.getAddress_1());
                            default_address_ck.setChecked(currentAddress.getDefault_());
                            country_id = currentAddress.getCountry_id();
                            country_spinner.setSelection(country_name_list.indexOf(currentAddress.getCountry()));
                            zone_id = currentAddress.getZone_id();
                            city_name = currentAddress.getCity();
                            set_area_for_edit = true;
                            save.setText(R.string.update);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(AddAddress.this, getString(R.string.error_occur), Toast.LENGTH_SHORT).show();
                    }
                }));
    } catch (Exception e) {
        Common.showAlert2(this, getString(R.string.warning), e.getMessage());
    }

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
            try {
            if(save.getText().equals(getResources().getString(R.string.save))) {
                if(Common.isConnectToTheInternet(this)) {
                    compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().addAddress(userId, first_name, address_desc, city_name, country_id, postcode, zone_id, title, default_)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Status>() {
                                @Override
                                public void accept(Status status) throws Exception {
                                    if (status.getStatus().equals("error"))
                                        Common.showAlert2(AddAddress.this, status.getStatus(), status.getMessage());
                                    else
                                        onBackPressed();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(AddAddress.this, getString(R.string.error_occur), Toast.LENGTH_SHORT).show();
                                }
                            }));
                }else
                    Common.errorConnectionMess(this);

            }else {
                if (Common.isConnectToTheInternet(this)) {
                    compositeDisposable.add(new Controller2(Common.current_user.getUserAccessToken()).getAPI().editAddress(userId,getIntent().getStringExtra("address_id"), first_name, address_desc, city_name, country_id, postcode, zone_id, title, default_)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Status>() {
                                @Override
                                public void accept(Status status) throws Exception {
                                    if (status.getStatus() == "error")
                                        Common.showAlert2(AddAddress.this, status.getStatus(), status.getMessage());
                                    else
                                        finish();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(AddAddress.this, getString(R.string.error_occur), Toast.LENGTH_SHORT).show();
                                }
                            }));

            }else
                    Common.errorConnectionMess(this);
            }
            } catch (Exception e) {
                Common.showAlert2(this, getString(R.string.warning), e.getMessage());
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(Color.BLACK);
        switch (parent.getId()) {
            case R.id.country:
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
            postcode = "";
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
