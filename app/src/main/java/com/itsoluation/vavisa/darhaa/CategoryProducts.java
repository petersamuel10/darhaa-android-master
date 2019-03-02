package com.itsoluation.vavisa.darhaa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.itsoluation.vavisa.darhaa.adapter.CategoryProductAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentCategoryDetails;
import com.itsoluation.vavisa.darhaa.view_setting.Filter;
import com.itsoluation.vavisa.darhaa.view_setting.Sort;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryProducts extends AppCompatActivity {

    CategoryProductAdapter adapter;
    ArrayList<String> product_ids, thumbs, names, prices, specials, minimums;
    ArrayList<Boolean>wishLists,stocks;
    RecyclerView recyclerView;

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.sl)
    SwipeRefreshLayout sl;

    public static String filter_type, filter_value, category_price_min_value, category_price_max_value, sort_type;

    public final static int REQUEST_SORT = 1;
    public final static int REQUEST_FILTER = 2;

    @OnClick(R.id.sortBtn)
    public void setSort(){
        Intent i = new Intent(this, Sort.class);
        startActivityForResult(i, REQUEST_SORT);
    }
    @OnClick(R.id.filterBtn)
    public void setFilter(){
        Intent i = new Intent(this, Filter.class);
        startActivityForResult(i, REQUEST_FILTER);
    }
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

//    @OnClick(R.id.item)
//    public void ff(){
//        startActivity(new Intent(this, Product.class));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        ButterKnife.bind(this);
        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

        product_ids = new ArrayList<>();
        thumbs = new ArrayList<>();
        names = new ArrayList<>();
        prices = new ArrayList<>();
        specials = new ArrayList<>();
        minimums = new ArrayList<>();
        wishLists = new ArrayList<>();
        stocks = new ArrayList<>();

        recyclerView = findViewById(R.id.rvNumbers);
        setUpSwipeRefreshLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Common.isConnectToTheInternet(CategoryProducts.this))
            new GetCategoryProductsBackgroundTask(CategoryProducts.this).execute();
        else
            Common.errorConnectionMess(CategoryProducts.this);
    }

    private void setUpSwipeRefreshLayout() {
        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (adapter != null) {
                    if(Common.isConnectToTheInternet(CategoryProducts.this))
                        new GetCategoryProductsBackgroundTask(CategoryProducts.this).execute();
                    else
                        Common.errorConnectionMess(CategoryProducts.this);
                }

                sl.setRefreshing(false);
            }
        });
    }

    /** Get Category Items **/
    private class GetCategoryProductsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        GetCategoryProductsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String get_products_url;
            Integer user_id = null;
            if(Common.current_user !=null) {
                user_id = Common.current_user.getCustomerInfo().getCustomer_id();
                get_products_url = getString(R.string.category_products_api)
                        + "&category_id=" + CurrentCategoryDetails.category_id + "&user_id=" + user_id;
            } else
                    get_products_url = getString(R.string.category_products_api)
                            + "&category_id=" + CurrentCategoryDetails.category_id;

            try {
                URL url = new URL(get_products_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("api_Token", getString(R.string.api_token));
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
            Log.i("vvvv",result.toString());

            JSONObject firstJsonObject = null;
            try {
                firstJsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            product_ids.clear();
            thumbs.clear();
            names.clear();
            prices.clear();
            specials.clear();
            minimums.clear();
            wishLists.clear();
            stocks.clear();

            try {
                JSONArray products = firstJsonObject.getJSONArray("products");
                // added this inside try catch because there might not be any products in a category
                try {
                    for (int j = 0; j < products.length(); j++) {
                        JSONObject object = products.getJSONObject(j);
                        String product_id = object.getString("product_id");
                        String thumb = object.getString("thumb");
                        String name = object.getString("name");
                        String price = object.getString("price");
                        String special = object.getString("special");
                        String minimum = object.getString("minimum");
                        Boolean wishList = object.getBoolean("wishList");
                        Boolean stock = object.getBoolean("stock");

                        product_ids.add(product_id);
                        thumbs.add(thumb);
                        names.add(name);
                        prices.add(price);
                        specials.add(special);
                        minimums.add(minimum);
                        wishLists.add(wishList); // TODO: add these to adapter, too
                        stocks.add(stock);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String total = firstJsonObject.getString("totalProducts");
                String page = firstJsonObject.getString("totalPages");
                String product_per_page = firstJsonObject.getString("productPerPage");
                String limit = firstJsonObject.getString("limit");
                String min_price = firstJsonObject.getString("MinPrice");
                String max_price = firstJsonObject.getString("MaxPrice");

                category_price_min_value = min_price;
                category_price_max_value = max_price;

                String[] product_ids_array = product_ids.toArray(new String[product_ids.size()]);
                String[] thumbs_array = thumbs.toArray(new String[thumbs.size()]);
                String[] names_array = names.toArray(new String[names.size()]);
                String[] prices_array = prices.toArray(new String[prices.size()]);
                String[] specials_array = specials.toArray(new String[specials.size()]);
                String[] minimums_array = minimums.toArray(new String[minimums.size()]);
             //   String[] stocks_array =  minimums.toArray(new String[stocks.size()]);
/*
                for(int i=0;i<wishLists.size();i++){
                    wishList_array[i] = wishLists.get(i).toString();
                }
*/

                // set up the RecyclerView
                int numberOfColumns = 2;
                recyclerView.setLayoutManager(new GridLayoutManager(CategoryProducts.this, numberOfColumns));
                recyclerView.scheduleLayoutAnimation();
                adapter = new CategoryProductAdapter(CategoryProducts.this, product_ids_array, thumbs_array, names_array, prices_array, specials_array,
                        minimums_array, wishLists, stocks,false);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** Filter Category Items **/
    private class FilterCategoryProductsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        FilterCategoryProductsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String get_products_url = getString(R.string.category_products_api) + "&category_id=" + CurrentCategoryDetails.category_id + "&sort=" + sort_type + "&" + filter_type + "=" + filter_value;
            try {
                URL url = new URL(get_products_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("api_Token", getString(R.string.api_token));
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

            product_ids.clear();
            thumbs.clear();
            names.clear();
            prices.clear();
            specials.clear();
            minimums.clear();

            try {
                JSONArray products = firstJsonObject.getJSONArray("products");
                // added this inside try catch because there might not be any products in a category
                try {
                    for (int j = 0; j < products.length(); j++) {
                        JSONObject object = products.getJSONObject(j);
                        String product_id = object.getString("product_id");
                        String thumb = object.getString("thumb");
                        String name = object.getString("name");
                        String price = object.getString("price");
                        String special = object.getString("special");
                        String minimum = object.getString("minimum");
                        Boolean wishList = object.getBoolean("wishList");
                        Boolean stock = object.getBoolean("stock");

                        product_ids.add(product_id);
                        thumbs.add(thumb);
                        names.add(name);
                        prices.add(price);
                        specials.add(special);
                        minimums.add(minimum);
                        wishLists.add(wishList);
                        stocks.add(stock);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String total = firstJsonObject.getString("totalProducts");
                String total_pages = firstJsonObject.getString("totalPages");
                String product_per_page = firstJsonObject.getString("productPerPage");
                String page = firstJsonObject.getString("page");
                String limit = firstJsonObject.getString("limit");

                String[] product_ids_array = product_ids.toArray(new String[product_ids.size()]);
                String[] thumbs_array = thumbs.toArray(new String[thumbs.size()]);
                String[] names_array = names.toArray(new String[names.size()]);
                String[] prices_array = prices.toArray(new String[prices.size()]);
                String[] specials_array = specials.toArray(new String[specials.size()]);
                String[] minimums_array = minimums.toArray(new String[minimums.size()]);
               // String[] wishList_array = minimums.toArray(new String[wishLists.size()]);
               // String[] stocks_array = minimums.toArray(new String[stocks.size()]);

                // set up the RecyclerView
                int numberOfColumns = 2;
                recyclerView.setLayoutManager(new GridLayoutManager(CategoryProducts.this, numberOfColumns));
                adapter = new CategoryProductAdapter(CategoryProducts.this, product_ids_array, thumbs_array, names_array, prices_array, specials_array,
                        minimums_array, wishLists, stocks, false);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // This is used when the current activity is waiting until the next activity is finished to do something
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_SORT:
                new FilterCategoryProductsBackgroundTask(this).execute();
                break;
            case REQUEST_FILTER:
                new FilterCategoryProductsBackgroundTask(this).execute();
                break;
        }
    }
}
