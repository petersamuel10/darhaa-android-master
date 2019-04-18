package com.itsoluation.vavisa.darhaa;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.adapter.CategoryProductAdapter;
import com.itsoluation.vavisa.darhaa.adapter.MainSliderAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentProductDetails;
import com.itsoluation.vavisa.darhaa.common.OptionsSelection;
import com.itsoluation.vavisa.darhaa.expandableAdapter.ExpandableListAdapter;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.addToCart.AddToCardData;
import com.itsoluation.vavisa.darhaa.model.addToCart.Options;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;
import com.itsoluation.vavisa.darhaa.singleton.InterfaceProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class Product extends AppCompatActivity implements View.OnClickListener, RecyclerViewItemClickListener {

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.scrollData)
    ScrollView scrollView;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.ic_fav)
    ImageView ic_fav;
    @BindView(R.id.ic_share)
    ImageView ic_share;
    @BindView(R.id.ic_add)
    ImageView ic_add;
    @BindView(R.id.ic_remove)
    ImageView ic_remove;
    @BindView(R.id.item_amount)
    TextView item_amount;
    @BindView(R.id.item_manf)
    TextView item_manf;
    @BindView(R.id.item_price_special)
    TextView special_price;
    @BindView(R.id.item_stock)
    TextView item_stock;
    @BindView(R.id.item_sku)
    TextView item_sku;
    @BindView(R.id.seller_details)
    ConstraintLayout seller_details_layout;
    @BindView(R.id.seller_name)
    TextView seller_name_txt;
    @BindView(R.id.related_rec)
    RecyclerView related_rec;
    @BindView(R.id.product_options)
    LinearLayout product_options;
    @BindView(R.id.attributes_layout)
    LinearLayout attributes_layout;
    @BindView(R.id.discount_layout)
    LinearLayout discount_layout;
    @BindView(R.id.addCardBtn)
    Button addCart;

    TextView item_price, item_name;
    WebView item_desc_details;
    Slider slider;

    CategoryProductAdapter adapter;
    ArrayList<Products> relativeProductsList;
    RecyclerViewItemClickListener recyclerListener;

    static String current_product_id, seller_name, seller_email, seller_phone;
    private int amount = 1;

    com.itsoluation.vavisa.darhaa.expandableAdapter.ExpandableListAdapter expandable_adapter;

    List<EditText> editTexts = new ArrayList<>();
    List<ExpandableListView> expandableListViews = new ArrayList<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    String product_id, minimum;
    String user_id;
    Boolean wishList;
    Double price, totalPrice;
    AddToCardData addCard;
    Products current_product;

    ArrayList<Options> cartOptions;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    @OnClick(R.id.seller_phone)
    public void call() {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder1.setMessage(getResources().getString(R.string.call_to) + " " + seller_phone);
        builder1.setPositiveButton(
                getString(R.string.call),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        boolean call = getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
                        if (call) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + seller_phone));
                            startActivity(intent);
                            dialog.cancel();
                        }
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @OnClick(R.id.seller_email)
    public void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{seller_email});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, "body of email");
        try {
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    @OnClick(R.id.addCardBtn)
    public void addOrder() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
        if (Common.isConnectToTheInternet(this))
            addToCart();
        else
            Common.errorConnectionMess(this);
    }

    private void addToCart() {

        cartOptions = new ArrayList<>();
        addCard = new AddToCardData();
        if (getCartOPtions()) {
            addCard.setOptions(cartOptions);

            if (user_id == null)
                user_id = String.valueOf(0);
            addCard.setUser_id(String.valueOf(user_id));
            addCard.setQuantity(item_amount.getText().toString());
            addCard.setProduct_id(product_id);
            addCard.setDevice_id(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

            progressDialog.show();
            try {
                compositeDisposable.add(Common.getAPI().addToCart(addCard).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Status>() {
                            @Override
                            public void accept(Status status) throws Exception {
                                progressDialog.dismiss();
                                if (status.getStatus().equals("error"))
                                    Common.showAlert2(Product.this, status.getStatus(), status.getMessage());
                                else {
                                    Snackbar snackbar = Snackbar.make(rootLayout, status.getMessage(), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }
                        }));
            } catch (Exception e) {
                Common.showAlert2(this, getString(R.string.warning), e.getMessage());
            }

        }

    }

    private boolean getCartOPtions() {

        for (EditText ed : editTexts) {
            if (ed.getText().toString().equals("")) {
                if (Boolean.parseBoolean(ed.getTag(R.string.required).toString())) {
                    Toast.makeText(this, getResources().getString(R.string.warning) + " : " + getResources().getString(R.string.please_enter) + " " + ed.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Options option = new Options();
                option.setId(ed.getTag(R.string.option_id).toString());
                option.setValue(ed.getText().toString());
                cartOptions.add(option);
            }
        }

        for (Map.Entry<String, List<String>> entry : OptionsSelection.optionsSelection.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value.get(0).equals("true")) {
                if (value.size() > 2) {

                    Options option = new Options();
                    option.setId(key);
                    option.setValue(value.get(2));
                    cartOptions.add(option);

                } else {
                    Toast.makeText(this, getResources().getString(R.string.warning) + " : " +
                                    getResources().getString(R.string.please_enter) + " " + value.get(1),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        OptionsSelection.optionsSelection.clear();
        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : null;

        product_id = CurrentProductDetails.product_id;
        current_product = getIntent().getParcelableExtra("product_");
        ic_add.setOnClickListener(this);
        ic_remove.setOnClickListener(this);
        ic_fav.setOnClickListener(this);
        ic_share.setOnClickListener(this);


        recyclerListener = this;
        item_price = findViewById(R.id.item_price);
        item_name = findViewById(R.id.item_name);
        item_desc_details = findViewById(R.id.item_desc_details);
        slider = findViewById(R.id.banner_slider1);

    }

    @Override
    protected void onStart() {
        super.onStart();
        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : null;

        editTexts = new ArrayList<>();
        expandableListViews = new ArrayList<>();

        product_options.removeAllViews();
        attributes_layout.removeAllViews();
        discount_layout.removeAllViews();

        OptionsSelection.optionsSelection.clear();
        if (Common.isArabic)
            setLanguage("ar");
        else
            setLanguage("en");

        if (Common.isConnectToTheInternet(this))
            new GetItemDetailsBackgroundTask(this).execute();
        else
            Common.errorConnectionMess(this);
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Dar Haa");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_share:
                share();
                break;
            case R.id.ic_add:
                amount++;
                totalPrice += price;
                if (special_price.getVisibility() == View.VISIBLE) {
                    special_price.setText(String.format(Locale.US, "%.3f", totalPrice));
                    item_amount.setText(String.valueOf(amount));
                } else {
                    item_price.setText(String.format(Locale.US, "%.3f", totalPrice));
                    item_amount.setText(String.valueOf(amount));
                }
                break;
            case R.id.ic_remove:
                if (amount == Integer.parseInt(minimum)) {
                    Common.showAlert(this, R.string.warning, R.string.minimum_number);
                } else {
                    amount--;
                    totalPrice -= price;
                    if (special_price.getVisibility() == View.VISIBLE) {
                        special_price.setText(String.format(Locale.US, "%.3f", totalPrice));
                        item_amount.setText(String.valueOf(amount));
                    } else {
                        item_price.setText(String.format(Locale.US, "%.3f", totalPrice));
                        item_amount.setText(String.valueOf(amount));
                    }
                }
                break;
            case R.id.ic_fav:
                if (user_id != null)
                    setFavorite();
                else
                    showAlert();
                break;
        }

    }

    private void setFavorite() {
        if (wishList) {
            removeFav(product_id, ic_fav);
            wishList = false;
        } else {
            addFav(product_id, ic_fav);
            wishList = true;
        }
    }

    private void addFav(String product_id, ImageView ic_fav) {
        if (Common.isConnectToTheInternet(this)) {
            ic_fav.setImageResource(R.drawable.ic_fav);

            // current product come from category only not from related
            if (current_product != null) {
                current_product.setWishList(true);
                InterfaceProduct.getInstance().setPosition(getIntent().getExtras().getInt("position"));
                InterfaceProduct.getInstance().setProduct(current_product);
            }
            try {
                compositeDisposable.add(Common.getAPI().addFavorte(product_id, user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Status>() {
                            @Override
                            public void accept(Status status) throws Exception {
                                if (status.getStatus().equals("error")) {
                                    Common.showAlert2(Product.this, status.getStatus(), status.getMessage());
                                }
                            }
                        }));
            } catch (Exception e) {
                Common.showAlert2(this, getString(R.string.warning), e.getMessage());
            }

        } else
            Common.errorConnectionMess(this);
    }

    private void removeFav(String product_id, ImageView ic_fav) {
        if (Common.isConnectToTheInternet(this)) {
            ic_fav.setImageResource(R.drawable.ic_fav_border);
            // current product come from category only not from related
            if (current_product != null) {
                current_product.setWishList(false);
                InterfaceProduct.getInstance().setPosition(getIntent().getExtras().getInt("position"));
                InterfaceProduct.getInstance().setProduct(current_product);
            }
            try {
                compositeDisposable.add(Common.getAPI().removeFavorte(product_id, user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Status>() {
                            @Override
                            public void accept(Status status) throws Exception {
                                if (status.getStatus().equals("error")) {
                                    Common.showAlert2(Product.this, status.getStatus(), status.getMessage());
                                }
                            }
                        }));
            } catch (Exception e) {
                Common.showAlert2(this, getString(R.string.warning), e.getMessage());
            }

        } else
            Common.errorConnectionMess(this);

    }

    @Override
    public void onClick(View view, int position, String product_id, String product_name, int flag) {
        if (flag == 0) {
            CurrentProductDetails.product_id = product_id;
            CurrentProductDetails.product_name = product_name;
            startActivity(new Intent(this, Product.class));
        } else if (flag == 1) {
            if (user_id != null)
                setFavoriteRelate(position, view);
            else
                showAlert();

        }
    }


    private void setFavoriteRelate(int position, View view) {
        if (relativeProductsList.get(position).getWishList()) {
            removeFav(relativeProductsList.get(position).getProduct_id(), ((ImageView) view));
            relativeProductsList.get(position).setWishList(false);
        } else {
            addFav(relativeProductsList.get(position).getProduct_id(), ((ImageView) view));
            relativeProductsList.get(position).setWishList(true);
        }

    }

    private void showAlert() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alert_login_fav);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button

        TextView login = dialog.findViewById(R.id.login_alert_btn);
        TextView cancel = dialog.findViewById(R.id.cancel_alert_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Product.this, Login.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Get Item Details Items
     **/
    private class GetItemDetailsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        RelativeLayout.LayoutParams linear;

        GetItemDetailsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
            linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            linear.setMargins(0, 0, 0, 12);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String get_product_details_url;
            Integer user_id = null;
            if (Common.current_user != null) {
                user_id = Common.current_user.getCustomerInfo().getCustomer_id();
                get_product_details_url = getString(R.string.product_details_api) + "&product_id=" + product_id + "&user_id=" + user_id;

            } else
                get_product_details_url = getString(R.string.product_details_api) + "&product_id=" + product_id;

            try {
                URL url = new URL(get_product_details_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("api-token", getString(R.string.api_token));
                httpURLConnection.setRequestProperty("Content-Type", getString(R.string.content_type));
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                if (is_arabic) {
                    httpURLConnection.setRequestProperty("language", "ar");
                } else {
                    httpURLConnection.setRequestProperty("language", "en");
                }

                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();


            Log.d("ststst", "result: " + result);

            JSONObject firstJsonObject = null;
            try {
                firstJsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(result)) {
                Common.showAlert2(Product.this, getString(R.string.error), getString(R.string.missing_data));
            } else {

                try {

                    scrollView.setVisibility(View.VISIBLE);

                    String product_id = firstJsonObject.getString("product_id");
                    String manufactur = firstJsonObject.getString("manufacturer");
                    String description = firstJsonObject.getString("description");
                    item_desc_details.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);

                    String name = firstJsonObject.getString("name");
                    String special = firstJsonObject.getString("special");
                    Boolean stock = firstJsonObject.getBoolean("stock");
                    String mainImage = firstJsonObject.getString("mainImage");
                    JSONArray images = firstJsonObject.getJSONArray("images");
                    minimum = firstJsonObject.getString("minimum");
                    wishList = firstJsonObject.getBoolean("wishList");
                    price = Double.parseDouble(firstJsonObject.getString("price"));
                    //  String sku = firstJsonObject.getString("sku");

                    if (manufactur.equals("null")) {
                        item_manf.setVisibility(View.GONE);
                    } else
                        item_manf.setText(manufactur);
                    if (wishList)
                        ic_fav.setImageResource(R.drawable.ic_fav);
                    item_name.setText(name);
                    item_price.setText(String.format(Locale.US, "%.3f", price));

                    if (!special.equals("false")) {
                        item_price.setPaintFlags(item_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        special_price.setText(special + " " + getResources().getString(R.string.kd));
                        special_price.setVisibility(View.VISIBLE);
                        price = Double.parseDouble(special);
                    } else {
                        special_price.setVisibility(View.GONE);
                        price = Double.parseDouble(firstJsonObject.getString("price"));
                    }

                    totalPrice = price;
                    if (!stock) {
                        addCart.setVisibility(View.GONE);
                        product_options.setVisibility(View.GONE);
                        item_stock.setVisibility(View.VISIBLE);

                    } else
                        item_stock.setVisibility(View.GONE);

                /*if(!sku.equals("")){
                    item_sku.setVisibility(View.VISIBLE);
                    item_sku.setText(getResources().getString(R.string.sku) +": "+sku);
                }*/

                    item_amount.setText(minimum);
                    amount = Integer.parseInt(minimum);
           /*     String mime = "text/html";
                String encoding = "utf-8";
                item_desc_details.getSettings().setJavaScriptEnabled(true);
                item_desc_details.loadDataWithBaseURL(null, description, mime, encoding, null);
                item_desc_details.setBackgroundColor(0x00000000);
               // item_desc_details.loadData(description , "text/html; charset=UTF-8", null);
                //item_desc_details.setText(description);*/
                    current_product_id = product_id;


                    ArrayList<String> side_images = new ArrayList<>();
                    side_images.add(mainImage);
                    try {
                        for (int i = 0; i < images.length(); i++) {
                            JSONObject jsonObject = images.getJSONObject(i);
                            String image = jsonObject.getString("image");
                            side_images.add(image);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    slider.setAdapter(new MainSliderAdapter(Product.this, side_images));

                    Slider.init(new PicassoImageLoadingService(Product.this));

                    try {

                        JSONObject seller_details = firstJsonObject.getJSONObject("seller_detail");
                        if (seller_details.length() == 0)
                            seller_details_layout.setVisibility(View.GONE);
                        else {
                            seller_details_layout.setVisibility(View.VISIBLE);
                            seller_name = seller_details.getString("seller_name");
                            seller_email = seller_details.getString("seller_email");
                            seller_phone = seller_details.getString("seller_phone");

                            seller_name_txt.setText(getString(R.string.by) + " " + seller_name);
                        }

                    } catch (Exception e) {
                    }

                    JSONArray discounts = firstJsonObject.getJSONArray("discounts");

                    try {
                        for (int i = 0; i < discounts.length(); i++) {

                            discount_layout.setVisibility(View.VISIBLE);

                            JSONObject discount_item = discounts.getJSONObject(i);
                            String discount_quantity = discount_item.getString("quantity");
                            String discount_price = discount_item.getString("price");

                            String text = getString(R.string.take) + " " + discount_quantity + " " + getString(R.string.or_more_for) + " " +
                                    discount_price + " " + getString(R.string.each);

                            createTextTitle(discount_layout, text, true, true);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray options = firstJsonObject.getJSONArray("options");
                    try {
                        // TODO: ask what can options array contain so that you can know what to add for strings
                        for (int i = 0; i < options.length(); i++) {

                            JSONObject option_product = options.getJSONObject(i);
                            String option_type = option_product.getString("type");
                            String option_name = option_product.getString("name");
                            String option_id = option_product.getString("product_option_id");
                            Boolean option_required = (option_product.getString("required").equals("1")) ? true : false;
                            JSONArray option_options = option_product.getJSONArray("product_option_value");

                            if (option_type.equals("text"))
                                createEditText(option_name, option_required, option_id);

                            if (option_type.equals("select"))
                                createCheck(option_name, option_options, false, option_required, option_id);

                            if (option_type.equals("radio"))
                                createCheck(option_name, option_options, false, option_required, option_id);

                            if (option_type.equals("checkbox"))
                                createCheck(option_name, option_options, true, option_required, option_id);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONArray attribute_groups = firstJsonObject.getJSONArray("attribute_groups");

                    try {
                        for (int i = 0; i < attribute_groups.length(); i++) {

                            JSONObject attribut_item = attribute_groups.getJSONObject(i);

                            attributes_layout.setVisibility(View.VISIBLE);
                            //title
                            createTextTitle(attributes_layout, attribut_item.getString("name"), true, false);
                            JSONArray attributes = attribut_item.getJSONArray("attribute");
                            for (int x = 0; x < attributes.length(); x++) {

                                JSONObject attribut2 = attributes.getJSONObject(x);
                                //attribute name
                                createTextTitle(attributes_layout, attribut2.getString("name") + ": " + attribut2.getString("text"), false, false);
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // related products
                    JSONArray relatedProducts = firstJsonObject.getJSONArray("relatedProducts");
                    setRelatedProduct(relatedProducts);


                } catch (JSONException e) {
                    // This means no product details are found
                    try {
                        Log.i("mmmm", e.getMessage());
                        String status = firstJsonObject.getString("status");
                        String message = firstJsonObject.getString("message");

                        if (status.equals("error")) {
                            Common.showAlert2(Product.this, status, message);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Common.showAlert2(Product.this, "error", e1.getMessage());
                    }
                }
            }
        }

        private void createCheck(String option_name, final JSONArray option_options, boolean isCK, Boolean isRequired, final String option_id) {

            final ExpandableListView expandableListView = new ExpandableListView(Product.this);
            expandableListView.setLayoutParams(linear);

            expandableListView.setBackground(getResources().getDrawable(R.drawable.border_blue));

            expandableListView.setSelected(true);

            final List<String> expandableListTitle = new ArrayList<>();
            List<String> childList = new ArrayList<>();
            final List<String> childListId = new ArrayList<>();

            final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

            expandableListTitle.add(option_name);

            try {
                for (int x = 0; x < option_options.length(); x++) {
                    JSONObject option_child = option_options.getJSONObject(x);
                    String name = option_child.getString("name");
                    String price = option_child.getString("price");
                    String price_prefix = option_child.getString("price_prefix");
                    String title = name + " (" + price_prefix + price + ")";
                    String id = option_child.getString("product_option_value_id");
                    childList.add(title);
                    childListId.add(id);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            expandableListDetail.put(expandableListTitle.get(0), childList);

            expandable_adapter = new ExpandableListAdapter(Product.this, expandableListTitle, expandableListDetail, isCK, isRequired, option_id, childListId);

            expandableListView.setAdapter(expandable_adapter);

            product_options.addView(expandableListView);
            expandableListViews.add(expandableListView);

            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    setListViewHeight(parent, groupPosition);
                    return false;
                }
            });

            /*
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    /*
                    Options option = new Options();
                    option.setId(option_id);
                    option.setValue(childListId.get(childPosition));
                    Log.i("nnn1",option.getId());
                    Log.i("nnn2222",option.getValue());

                    Toast.makeText(
                            getApplicationContext(),
                            expandableListTitle.get(groupPosition)
                                    + " -> "
                                    + expandableListDetail.get(
                                    expandableListTitle.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT
                    ).show();
                    return false;
                }
            });
            */
        }

        private void setRelatedProduct(JSONArray relatedProducts_) {

            try {
                relativeProductsList = new ArrayList<>();

                for (int i = 0; i < relatedProducts_.length(); i++) {
                    JSONObject object = relatedProducts_.getJSONObject(i);

                    Products products1 = new Products();

                    products1.setProduct_id(object.getString("product_id"));
                    products1.setName(object.getString("name"));
                    products1.setThumb(object.getString("image"));
                    products1.setPrice(object.getString("price"));
                    products1.setSpecial(object.getString("special"));
                    products1.setStock(object.getBoolean("stock"));
                    products1.setMinimum(object.getString("minimum"));
                    products1.setWishList(object.getBoolean("wishList"));

                    relativeProductsList.add(products1);

                }

                related_rec.setHasFixedSize(false);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Product.this, LinearLayoutManager.HORIZONTAL, false);
                related_rec.setLayoutManager(linearLayoutManager);

                adapter = new CategoryProductAdapter(relativeProductsList, true);
                adapter.setItemClickListener(recyclerListener);

                related_rec.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void createEditText(String option_name, Boolean required, String option_id) {

            RelativeLayout.LayoutParams linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            linear.setMargins(0, 0, 0, 12);
            EditText note = new EditText(Product.this);
            note.setBackground(getResources().getDrawable(R.drawable.border_blue));
            note.setGravity(Gravity.CENTER_HORIZONTAL);
            note.setPadding(4, 30, 4, 30);
            // note.setLayoutParams(linear);
            note.setHint(option_name);
            note.setTextSize(20);
            note.setLayoutParams(linear);
            note.setHintTextColor(Product.this.getResources().getColor(R.color.grey));
            note.setTextColor(Product.this.getResources().getColor(R.color.black));
            note.setLines(1);


            if (required) {
                note.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                note.setCompoundDrawablePadding(24);
            }


            product_options.addView(note);
            editTexts.add(note);

            note.setTag(R.string.required, required);
            note.setTag(R.string.option_id, option_id);
        }

        private void createTextTitle(LinearLayout layout, String attribute_name, Boolean isTitle, Boolean isDiscount) {

            RelativeLayout.LayoutParams linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            linear.setMargins(4, 4, 4, 4);

            TextView attribute = new TextView(Product.this);
            attribute.setGravity(Gravity.START);
            attribute.setLines(1);
            attribute.setLayoutParams(linear);

            attribute.setText(attribute_name);


            if (isTitle) {
                attribute.setTextSize(22);
                attribute.setPadding(4, 30, 4, 4);
                if (isDiscount) {
                    attribute.setTextColor(Product.this.getResources().getColor(R.color.colorAccent));
                    attribute.setPadding(4, 4, 4, 4);
                } else
                    attribute.setTextColor(Product.this.getResources().getColor(R.color.blue));
            } else if (!isTitle) {
                attribute.setTextSize(16);
                attribute.setPadding(16, 2, 16, 4);
                attribute.setTextColor(Product.this.getResources().getColor(R.color.black));
            }


            layout.addView(attribute);

        }

        private void setListViewHeight(ExpandableListView listView,
                                       int group) {
            ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                    View.MeasureSpec.EXACTLY);
            for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                View groupItem = listAdapter.getGroupView(i, false, null, listView);
                groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                totalHeight += groupItem.getMeasuredHeight();

                if (((listView.isGroupExpanded(i)) && (i != group))
                        || ((!listView.isGroupExpanded(i)) && (i == group))) {
                    for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                        View listItem = listAdapter.getChildView(i, j, false, null,
                                listView);
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                        totalHeight += listItem.getMeasuredHeight();

                    }
                }
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
            if (height < 10)
                height = 200;
            params.height = height;
            listView.setLayoutParams(params);
            listView.requestLayout();

        }

    }

    public void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}
