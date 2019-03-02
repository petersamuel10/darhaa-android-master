package com.itsoluation.vavisa.darhaa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.adapter.CategoryProductAdapter;
import com.itsoluation.vavisa.darhaa.adapter.MainSliderAdapter;
import com.itsoluation.vavisa.darhaa.adapter.ViewPagerAdapter;
import com.itsoluation.vavisa.darhaa.adapter.ViewPagerModel;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentProductDetails;
import com.itsoluation.vavisa.darhaa.model.Expandable;
import com.itsoluation.vavisa.darhaa.model.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class Product extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.ic_fav)
    ImageView ic_fav;
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
    @BindView(R.id.item_sku)
    TextView item_sku;
    @BindView(R.id.related_rec)
    RecyclerView related_rec;
    @BindView(R.id.expand_size)
    ExpandableLayout expand_size;
    @BindView(R.id.expand_color)
    ExpandableLayout expand_color;
    @BindView(R.id.print_color)
    ExpandableLayout expand_print_color;

    private ViewPagerAdapter mAdapter;

    TextView item_price, item_name, item_desc_details;
    Slider slider;
    ViewPager mViewpager;
    CategoryProductAdapter adapter;

    private ArrayList<ViewPagerModel> mContents;

    static String current_product_id;
    private int amount = 1;

    ArrayList<String> related_product_ids, related_images, related_names, related_prices,
            related_specials, related_minimums;

    ArrayList<Boolean>related_wishLists, related_stocks;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    String product_id, minimum;
    Integer user_id;
    Boolean wishList;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    @OnClick(R.id.addCardBtn)
    public void addOrder(){
        startActivity(new Intent(this,Cart.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        if(Common.isArabic){
            back_arrow.setRotation(180);
        }
        
        setSize();
        setColor();
        setPrintColor();

        user_id = (Common.current_user != null) ? Common.current_user.getCustomerInfo().getCustomer_id() : null;

        related_product_ids = new ArrayList<>();
        related_images = new ArrayList<>();
        related_names = new ArrayList<>();
        related_prices = new ArrayList<>();
        related_specials = new ArrayList<>();
        related_minimums = new ArrayList<>();
        related_wishLists = new ArrayList<>();
        related_stocks = new ArrayList<>();

        product_id = CurrentProductDetails.product_id;
        ic_add.setOnClickListener(this);
        ic_remove.setOnClickListener(this);
        ic_fav.setOnClickListener(this);

//        product_image = findViewById(R.id.product_image);
        item_price = findViewById(R.id.item_price);
        item_name = findViewById(R.id.item_name);
        item_desc_details = findViewById(R.id.item_desc_details);
        slider = findViewById(R.id.banner_slider1);

        mViewpager = findViewById(R.id.viewpager);
        mContents = new ArrayList<>();
        mAdapter = new ViewPagerAdapter(mContents, this);

        new GetItemDetailsBackgroundTask(this).execute();
    }

    private void setPrintColor() {

      expand_print_color.setRenderer(new ExpandableLayout.Renderer<Expandable,Expandable>() {
            @Override
            public void renderParent(View view, Expandable expandable, boolean b, int i) {

                ((TextView)view.findViewById(R.id.listTitle)).setText(expandable.name);
            }

            @Override
            public void renderChild(View view, Expandable expandable, int i, int i1) {

                ((TextView)view.findViewById(R.id.expandedListItem)).setText(expandable.name);
            }
        });
        expand_print_color.addSection(setSectionPrintColor());

    }
    private Section<Expandable,Expandable> setSectionPrintColor() {

        Section<Expandable,Expandable> section = new Section<>();
        Expandable parent = new Expandable("Color");
        List<Expandable> list = new ArrayList<>();
        list.add(new Expandable("Red"));
        list.add(new Expandable("Blue"));
        list.add(new Expandable("Black"));
        section.parent = parent;
        section.children.addAll(list);
        return section;
    }

    private void setColor() {

        expand_color.setRenderer(new ExpandableLayout.Renderer<Expandable,Expandable>() {
            @Override
            public void renderParent(View view, Expandable expandable, boolean b, int i) {

                ((TextView)view.findViewById(R.id.listTitle)).setText(expandable.name);
            }

            @Override
            public void renderChild(View view, Expandable expandable, int i, int i1) {

                ((TextView)view.findViewById(R.id.expandedListItem)).setText(expandable.name);
            }
        });
        expand_color.addSection(setSectionColor());


        /*
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Color");

        List<String> color = new ArrayList<String>();

        color.add("Red");
        color.add("Blue");
        color.add("Yellow");
        color.add("Black");

        listDataChild.put(listDataHeader.get(0), color);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expand_color.setAdapter(listAdapter);
        */
    }

    private Section<Expandable,Expandable> setSectionColor() {

        Section<Expandable,Expandable> section = new Section<>();
        Expandable parent = new Expandable("Color");
        List<Expandable> list = new ArrayList<>();
        list.add(new Expandable("Red"));
        list.add(new Expandable("Black"));
        list.add(new Expandable("White"));
        section.parent = parent;
        section.children.addAll(list);
        return section;
    }

    private void setSize() {

        expand_size.setRenderer(new ExpandableLayout.Renderer<Expandable,Expandable>() {
            @Override
            public void renderParent(View view, Expandable expandable, boolean b, int i) {

                ((TextView)view.findViewById(R.id.listTitle)).setText(expandable.name);
            }

            @Override
            public void renderChild(View view, Expandable expandable, int i, int i1) {

                ((TextView)view.findViewById(R.id.expandedListItem)).setText(expandable.name);
            }
        });
        expand_size.addSection(setSectionSize());

    }
    private Section<Expandable,Expandable> setSectionSize() {

        Section<Expandable,Expandable> section = new Section<>();
        Expandable parent = new Expandable("Size");
        List<Expandable> list = new ArrayList<>();
        list.add(new Expandable("Small"));
        list.add(new Expandable("Medium"));
        list.add(new Expandable("Large"));
        section.parent = parent;
        section.children.addAll(list);
        return section;
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
       switch (v.getId()){
           case R.id.ic_share:
               share();
               break;
           case R.id.ic_add:
               amount++;
               item_amount.setText(String.valueOf(amount));
               break;
           case R.id.ic_remove:
               if(amount == Integer.parseInt(minimum)) {
                   Log.i("minnnn", minimum);
                   Common.showAlert(this, R.string.warning, R.string.minimum_number);
               }else {
                   amount--;
                   item_amount.setText(String.valueOf(amount));
               }
               break;
           case R.id.ic_fav:
               if(user_id !=null)
                   setFavorite();
               else
                   startActivity(new Intent(this,Login.class));
               break;
       }

    }

    private void setFavorite() {
        if(wishList)
            removeFav();
        else
            addFav();
    }

    private void addFav() {

        compositeDisposable.add(Common.getAPI().addFavorte(product_id,user_id)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<Status>() {
                               @Override
                               public void accept(Status status) throws Exception {
                                   if(status.getStatus().equals("error")){
                                       Common.showAlert2(Product.this,status.getStatus(),status.getMessage());
                                   }else {

                                       ic_fav.setImageResource(R.drawable.ic_fav);
                                       wishList = true;
                                   }
                               }
                           }));

    }

    private void removeFav() {
        compositeDisposable.add(Common.getAPI().removeFavorte(product_id,user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        if(status.getStatus().equals("error")){
                            Common.showAlert2(Product.this,status.getStatus(),status.getMessage());
                        }else {

                            ic_fav.setImageResource(R.drawable.ic_fav_border);
                            wishList = false;
                        }
                    }
                }));

    }

    /** Get Item Details Items **/
    private class GetItemDetailsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        GetItemDetailsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
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
            if(Common.current_user !=null) {
                user_id = Common.current_user.getCustomerInfo().getCustomer_id();
                get_product_details_url = getString(R.string.product_details_api)+ "&product_id=" + product_id+ "&user_id=" + user_id;

            }else
                get_product_details_url = getString(R.string.product_details_api)+ "&product_id=" + product_id;

            try {
                URL url = new URL(get_product_details_url);
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

            try {
                String product_id = firstJsonObject.getString("product_id");
                String manufacturer = firstJsonObject.getString("manufacturer");
                String description = firstJsonObject.getString("description");
                String name = firstJsonObject.getString("name");
                String special = firstJsonObject.getString("special");
                Boolean stock = firstJsonObject.getBoolean("stock");
                String mainImage = firstJsonObject.getString("mainImage");
                JSONArray images = firstJsonObject.getJSONArray("images");
                minimum = firstJsonObject.getString("minimum");
                wishList = firstJsonObject.getBoolean("wishList");
                String price = firstJsonObject.getString("price");
                String sku = firstJsonObject.getString("sku");

                if(manufacturer.equals("null")) {
                    item_manf.setVisibility(View.GONE);
                }
                else
                    item_manf.setText(manufacturer);
                if(wishList)
                    ic_fav.setImageResource(R.drawable.ic_fav);
                item_name.setText(name);
                item_price.setText(price+" "+getResources().getString(R.string.kd));

                if(!special.equals("false")){
                    item_price.setPaintFlags(item_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    special_price.setText(special+" "+getResources().getString(R.string.kd));
                    special_price.setVisibility(View.VISIBLE);
                }

                if(!stock){
                    special_price.setText(getResources().getString(R.string.out_of_stock));
                    special_price.setVisibility(View.VISIBLE);
                }

                if(!sku.equals("")){
                    item_sku.setVisibility(View.VISIBLE);
                    item_sku.setText(sku);
                }

                item_amount.setText(minimum);
                amount = Integer.parseInt(minimum);
                item_desc_details.setText(description);
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


                JSONArray discounts = firstJsonObject.getJSONArray("discounts");

//                try { // TODO: ask what can discounts array contain so that you can know what to add for strings
//                    for (int i = 0; i < discounts.length(); i++) {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                JSONArray options = firstJsonObject.getJSONArray("options");
                try {
                    // TODO: ask what can options array contain so that you can know what to add for strings
                    for (int i = 0; i < options.length(); i++) {


                    }
               } catch (Exception e) {
                    e.printStackTrace();
                }



                JSONArray attribute_groups = firstJsonObject.getJSONArray("attribute_groups");

//                try { // TODO: ask what can attribute_groups array contain so that you can know what to add for strings
//                    for (int i = 0; i < attribute_groups.length(); i++) {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                JSONArray relatedProducts = firstJsonObject.getJSONArray("relatedProducts");
                related_product_ids.clear();
                related_images.clear();
                related_names.clear();
                related_prices.clear();
                related_specials.clear();
                related_minimums.clear();
                related_wishLists.clear();
                related_stocks.clear();
                try {
                    for (int i = 0; i < relatedProducts.length(); i++) {
                        JSONObject related_product = relatedProducts.getJSONObject(i);
                        String related_product_id = related_product.getString("product_id");
                        String related_image = related_product.getString("image");
                        String related_name = related_product.getString("name");
                        String related_price = related_product.getString("price");
                        String related_special = related_product.getString("special");
                        String related_minimum = related_product.getString("minimum");
                        Boolean related_wishList = related_product.getBoolean("wishList");
                        Boolean related_stock = related_product.getBoolean("stock");

                        related_product_ids.add(related_product_id);
                        related_images.add(related_image);
                        related_names.add(related_name);
                        related_prices.add(related_price);
                        related_specials.add(related_special);
                        related_minimums.add(related_minimum);
                        related_wishLists.add(related_wishList);
                        related_stocks.add(related_stock);
                    }
                    String[] related_product_ids_array, related_images_array, related_names_array, related_prices_array,
                            related_specials_array, related_minimums_array, related_wishLists_array ,related_stocks_array;

                    related_product_ids_array = related_product_ids.toArray(new String[related_product_ids.size()]);
                    related_images_array = related_images.toArray(new String[related_images.size()]);
                    related_names_array = related_names.toArray(new String[related_names.size()]);
                    related_prices_array = related_prices.toArray(new String[related_prices.size()]);
                    related_specials_array = related_specials.toArray(new String[related_specials.size()]);
                    related_minimums_array = related_minimums.toArray(new String[related_minimums.size()]);
                   // related_wishLists_array = related_wishLists.toArray(new String[related_wishLists.size()]);
                  //  related_stocks_array = related_stocks.toArray(new String[related_stocks.size()]);

                    related_rec.setHasFixedSize(false);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Product.this,LinearLayoutManager.HORIZONTAL,false);
                    related_rec.setLayoutManager(linearLayoutManager);

                    adapter = new CategoryProductAdapter(Product.this, related_product_ids_array, related_images_array, related_names_array,
                            related_prices_array, related_specials_array, related_minimums_array, related_wishLists, related_stocks,true);

                    related_rec.setAdapter(adapter);

                    /*
                    if (related_product_ids != null) {
                        Log.d("ststst", "1");
                        for (int m = 0; m < related_product_ids.size(); m++) {
                            ViewPagerModel viewPagerModel = new ViewPagerModel();

                            viewPagerModel.images = related_images.get(m);
                            viewPagerModel.names = related_names.get(m);
                            viewPagerModel.prices = related_prices.get(m);
                            viewPagerModel.ids = related_product_ids.get(m);

                            mViewpager.setOffscreenPageLimit(0); // Lazy loading (does not show until it is scrolled into view)
                            mViewpager.setAdapter(mAdapter);

                            if (!is_arabic) {
                                mViewpager.setPadding(0, 0, 290, 0);
                            } else {
                                mViewpager.setPadding(290, 0, 0, 0);
                            }

                            mViewpager.setPageMargin(54);
                            mContents.add(viewPagerModel);

                            if (mViewpager.getParent() != null)
                                ((ViewGroup) mViewpager.getParent()).removeView(mViewpager);
                        }
                        mViewpager.setAdapter(new ViewPagerAdapter(mContents, Product.this));
                    }
                    */
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                // This means no product details are found
                try {
                    Log.i("mmmm",e.getMessage());
                    String status = firstJsonObject.getString("status");
                    if (status.equals("error")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Product.this);
                        builder1.setMessage(R.string.no_product_details_found);
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
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
