package com.itsoluation.vavisa.darhaa.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressAdd;
import com.itsoluation.vavisa.darhaa.model.address.address.Countries;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.Area;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.AreaAndCities;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.City;
import com.itsoluation.vavisa.darhaa.model.paymentData.UserSendData;

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

public class ShippingAddress extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.user_name)
    EditText user_name_ed;
    @BindView(R.id.company_name)
    EditText company_name_ed;
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
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @OnClick(R.id.back_arrow)
    public void setBack() { onBackPressed(); }

    ArrayList<Countries> countryArrayList;
    ArrayList<Area> areasArrayList;
    ArrayList<City> cityArrayList;
    List<String> country_name_list;
    List<String> area_name_list;
    List<String> cities_name_list;

    CompositeDisposable compositeDisposable ;
    String user_name,postcode,company_name,address_details,city_name,
            country_id,zone_id,device_id,country_,zone;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_shipping_address);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(ShippingAddress.this);
        progressDialog.setCancelable(false);

        if (Common.isArabic) { back_arrow.setRotation(180); }

        country_spinner.setOnItemSelectedListener(this);
        area_spinner.setOnItemSelectedListener(this);
        city_spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        compositeDisposable = new CompositeDisposable();

        // to show adddress details only
        if (Common.isConnectToTheInternet(this)) {
            getCountries();
        } else
            Common.errorConnectionMess(ShippingAddress.this);
    }

    private void getCountries() {
        progressDialog.show();
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
               Common.showAlert2(ShippingAddress.this,getResources().getString(R.string.error),e.getMessage());
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
            }
        });
    } catch (Exception e) {
        Common.showAlert2(this, getString(R.string.warning), e.getMessage());
    }

}

    private void getArea(String country_id, String country_code) {
     //   progressDialog.show();
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
                       //     progressDialog.dismiss();
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


                        }
                    }));
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(Color.BLACK);
        switch (parent.getId()) {
            case R.id.country:
                Countries country = countryArrayList.get(position);
                country_id = country.getCountry_id();
                country_ = country.getName();
                if (Common.isConnectToTheInternet(this)) {
                    getArea(country.getCountry_id(), country.getCountry_code());
                } else
                    Common.errorConnectionMess(ShippingAddress.this);
                break;
            case R.id.area:
                Area area = areasArrayList.get(position);
                zone_id = area.getZone_id();
                zone = area.getName();
                break;
            case R.id.city:
                city_name = cities_name_list.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @OnClick(R.id.save_shipping_bn)
    public void saveAddress(){

        user_name = user_name_ed.getText().toString();
        company_name = company_name_ed.getText().toString();
        postcode = postcode_ed.getText().toString();
        address_details = address_desc_ed.getText().toString();

        UserSendData shippingData;
        UserSendData billingData;
        AddressAdd shippingAddress;

        //get billing address
        billingData = getIntent().getExtras().getParcelable("billing_address");
        Intent intent;

        if(validation(user_name,company_name,postcode,address_details)){
            shippingAddress = new AddressAdd(user_name,address_details,city_name,country_id,postcode,zone_id,company_name,zone,country_,"");
            shippingData = new UserSendData(device_id,"0",shippingAddress);

                intent = new Intent(ShippingAddress.this, PaymentMethod.class);
                intent.putExtra("billing_address",billingData);
                intent.putExtra("shipping_address",shippingData);
                intent.putExtra("total",getIntent().getStringExtra("total"));
                if(getIntent().hasExtra("couponCode"))
                    intent.putExtra("couponCode",getIntent().getStringExtra("couponCode"));
                startActivity(intent);
        }

    }

    private boolean validation(String user_name,String company_name_,String postcode_, String address_desc) {

        if (TextUtils.isEmpty(user_name)) {
            Snackbar snackbar = Snackbar.make(rootLayout, R.string.enter_user_name, Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        if (TextUtils.isEmpty(company_name_)) {
            company_name = "";
        }

        if (TextUtils.isEmpty(address_desc)) {
            Snackbar snackbar = Snackbar.make(rootLayout, R.string.enter_address_desc, Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        if (TextUtils.isEmpty(postcode_)) {
            postcode = "";
        }

        return true;
    }
}
