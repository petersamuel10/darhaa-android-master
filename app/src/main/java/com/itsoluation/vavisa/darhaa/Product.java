package com.itsoluation.vavisa.darhaa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itsoluation.vavisa.darhaa.adapter.CategoryProductAdapter;
import com.itsoluation.vavisa.darhaa.adapter.MainSliderAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentProductDetails;
import com.itsoluation.vavisa.darhaa.common.OptionsSelection;
import com.itsoluation.vavisa.darhaa.expandableAdapter.ExpandableListAdapter;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.addToCart.AddToCardData;
import com.itsoluation.vavisa.darhaa.model.addToCart.Options;

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
import io.reactivex.Scheduler;
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
    @BindView(R.id.item_sku)
    TextView item_sku;
    @BindView(R.id.related_rec)
    RecyclerView related_rec;
    @BindView(R.id.product_options)
    LinearLayout product_options;

    TextView item_price, item_name, item_desc_details;
    Slider slider;

    CategoryProductAdapter adapter;

    static String current_product_id;
    private int amount = 1;

    com.itsoluation.vavisa.darhaa.expandableAdapter.ExpandableListAdapter expandable_adapter;

    ArrayList<String> related_product_ids, related_images, related_names, related_prices,
            related_specials, related_minimums;

    ArrayList<Boolean>related_wishLists, related_stocks;
    List<EditText> editTexts = new ArrayList<>();
    List<ExpandableListView> expandableListViews = new ArrayList<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    String product_id, minimum,maximum;
    Integer user_id;
    Boolean wishList;
    Double price ,totalPrice;
    AddToCardData addCard;

    ArrayList<Options> cartOptions;
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    @OnClick(R.id.addCardBtn)
    public void addOrder(){
        addToCart();
    }

    private void addToCart() {

        cartOptions = new ArrayList<>();
        addCard = new AddToCardData();
        if(getCartOPtions()) {
            addCard.setOptions(cartOptions);

            addCard.setUser_id(String.valueOf(user_id));
            addCard.setQuantity(item_amount.getText().toString());
            addCard.setProduct_id(product_id);
            addCard.setDevice_id(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

            compositeDisposable.add(Common.getAPI().addToCart(addCard).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            Common.showAlert2(Product.this, status.getStatus(), status.getMessage());
                        }
                    }));
        }

    }

    private boolean getCartOPtions() {

        for (EditText ed: editTexts) {
            if(ed.getText().toString().equals("")){
                if(Boolean.parseBoolean(ed.getTag(R.string.required).toString())){
                    Toast.makeText(this, getResources().getString(R.string.warning)+" : "+getResources().getString(R.string.please_enter) +" "+ ed.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Options option = new Options();
                option.setId(ed.getTag(R.string.option_id).toString());
                option.setValue(ed.getText().toString());
                cartOptions.add(option);
            }
        }

        for (Map.Entry<String, List<String>> entry : OptionsSelection.optionsSelection.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if(value.get(0).equals("true")){
                if(value.size()>2){

                    Options option = new Options();
                    option.setId(key);
                    option.setValue(value.get(2));
                    cartOptions.add(option);

                }else
                {
                    Toast.makeText(this, getResources().getString(R.string.warning)+" : "+
                            getResources().getString(R.string.please_enter) +" "+ value.get(1),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        OptionsSelection.optionsSelection.clear();
        if(Common.isArabic){
            back_arrow.setRotation(180);
        }

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
        ic_share.setOnClickListener(this);

//        product_image = findViewById(R.id.product_image);
        item_price = findViewById(R.id.item_price);
        item_name = findViewById(R.id.item_name);
        item_desc_details = findViewById(R.id.item_desc_details);
        slider = findViewById(R.id.banner_slider1);

        new GetItemDetailsBackgroundTask(this).execute();
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
               totalPrice +=price;
               item_price.setText(String.format(Locale.US, "%.3f", totalPrice));
               item_amount.setText(String.valueOf(amount));
               break;
           case R.id.ic_remove:
               if(amount == Integer.parseInt(minimum)) {
                   Common.showAlert(this, R.string.warning, R.string.minimum_number);
               }else {
                   amount--;
                   totalPrice -=price;
                   item_price.setText(String.format(Locale.US, "%.3f", totalPrice));
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

        RelativeLayout.LayoutParams linear;

        GetItemDetailsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
            linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            linear.setMargins(0,0,0,12);
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
                price = Double.parseDouble(firstJsonObject.getString("price"));
                String sku = firstJsonObject.getString("sku");

                if(manufacturer.equals("null")) {
                    item_manf.setVisibility(View.GONE);
                }
                else
                    item_manf.setText(manufacturer);
                if(wishList)
                    ic_fav.setImageResource(R.drawable.ic_fav);
                item_name.setText(name);
                item_price.setText(String.format(Locale.US, "%.3f", price));
                totalPrice = price;

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
                    item_sku.setText(getResources().getString(R.string.sku) +": "+sku);
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

                        JSONObject option_product = options.getJSONObject(i);
                        String option_type = option_product.getString("type");
                        String option_name = option_product.getString("name");
                        String option_id = option_product.getString("product_option_id");
                        Boolean option_required = (option_product.getString("required").equals("1"))? true : false ;
                        JSONArray option_options = option_product.getJSONArray("product_option_value");

                        if(option_type.equals("text"))
                            createEditText(option_name,option_required,option_id);

                        if (option_type.equals("select"))
                            createCheck(option_name,option_options,false,option_required,option_id);

                        if (option_type.equals("checkbox"))
                            createCheck(option_name,option_options,true,option_required,option_id);

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

                // related products
                JSONArray relatedProducts = firstJsonObject.getJSONArray("relatedProducts");
                setRelatedProduct(relatedProducts);


            } catch (JSONException e) {
                // This means no product details are found
                try {
                    Log.i("mmmm",e.getMessage());
                    String status = firstJsonObject.getString("status");
                    String message = firstJsonObject.getString("message");

                    if (status.equals("error")) {
                        Common.showAlert2(Product.this,status,message);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Common.showAlert2(Product.this,"error",e1.getMessage());
                }
            }
        }

        private void createCheck(String option_name, final JSONArray option_options, boolean isCK, Boolean isRequired, final String option_id) {

            final ExpandableListView expandableListView  = new ExpandableListView(Product.this);
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
                    String id = option_child.getString("product_option_value_id");
                    childList.add(name);
                    childListId.add(id);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            expandableListDetail.put(expandableListTitle.get(0),childList);

            expandable_adapter = new ExpandableListAdapter(Product.this,expandableListTitle,expandableListDetail,isCK ,isRequired,option_id,childListId);

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

            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {

                    Toast.makeText(getApplicationContext(),
                            expandableListTitle.get(groupPosition) + " List Expanded.",
                            Toast.LENGTH_SHORT).show();

                }
            });

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
                    Toast.makeText(getApplicationContext(),
                            expandableListTitle.get(groupPosition) + " List Collapsed.",
                            Toast.LENGTH_SHORT).show();

                }
            });


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
                    */
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

        }

        private void setRelatedProduct(JSONArray relatedProducts) {

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
        }

        private void createEditText(String option_name, Boolean required, String option_id) {

            RelativeLayout.LayoutParams linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            linear.setMargins(0,0,0,12);
            EditText note = new EditText(Product.this);
            note.setBackground(getResources().getDrawable(R.drawable.border_blue));
            note.setGravity(Gravity.CENTER_HORIZONTAL);
            note.setPadding(4,30,4,30);
           // note.setLayoutParams(linear);
            note.setHint(option_name);
            note.setTextSize(20);
            note.setLayoutParams(linear);
            note.setHintTextColor(Product.this.getResources().getColor(R.color.grey));
            note.setTextColor(Product.this.getResources().getColor(R.color.black));
            note.setLines(1);


            if (required){
                note.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,0,0,0);
                note.setCompoundDrawablePadding(24);
            }


            product_options.addView(note);
            editTexts.add(note);

            note.setTag(R.string.required,required);
            note.setTag(R.string.option_id,option_id);
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
}
