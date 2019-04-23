package com.itsoluations.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.cartData.Options;
import com.itsoluations.vavisa.darhaa.web_service.Controller2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OrderDetails extends AppCompatActivity {

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    //order information
    @BindView(R.id.order_id)
    TextView order_id_txt;
    @BindView(R.id.added_date)
    TextView added_date_txt;
    @BindView(R.id.payment_address)
    TextView payment_address_txt;
    @BindView(R.id.payment_method)
    TextView payment_method_txt;
    @BindView(R.id.shipping_address)
    TextView shipping_address_txt;
    @BindView(R.id.shipping_method)
    TextView shipping_method_txt;

    //payment info
    @BindView(R.id.p1)
    TextView reference_id_title;
    @BindView(R.id.reference_id)
    TextView reference_id_txt;
    @BindView(R.id.p2)
    TextView payment_id_title;
    @BindView(R.id.payment_id)
    TextView payment_id_txt;
    @BindView(R.id.p3)
    TextView transfer_id_title;
    @BindView(R.id.transfer_id)
    TextView transfer_txt;
    @BindView(R.id.p4)
    TextView result_title;
    @BindView(R.id.result)
    TextView result_txt;
    @BindView(R.id.price)
    TextView price_txt;

    //products info
    @BindView(R.id.product_info_title)
    LinearLayout product_info_title;
    @BindView(R.id.product_info)
    LinearLayout product_Ln;

    //gift message
    @BindView(R.id.message_layout)
    LinearLayout message_layout;
    @BindView(R.id.gift_message_txt)
    TextView  message_txt;

    //history
    @BindView(R.id.history_info_title)
    LinearLayout history_Ln_title;
    @BindView(R.id.history_info_ln)
    LinearLayout history_Ln;

    //totals
    @BindView(R.id.totals_info_title)
    LinearLayout total_Ln_titles;
    @BindView(R.id.totals_info_ln)
    LinearLayout total_Ln;

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @OnClick(R.id.back_arrow)
    public void setBack() {onBackPressed(); }

    ProgressDialog progressDialog;

    String user_id ,order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_order_details_new);

        progressDialog = new ProgressDialog(OrderDetails.this);
        progressDialog.setCancelable(false);

        ButterKnife.bind(this);

        Log.i("access",Common.current_user.getUserAccessToken());

        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());
        order_id = getIntent().getStringExtra("order_id");


        if (Common.isArabic) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            back_arrow.setRotation(180);}
        else{
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        if(Common.isConnectToTheInternet(this))
            callAPI();
        else
            Common.errorConnectionMess(this);
    }

    private void callAPI() {

        progressDialog.show();

        try {

            Observable<JsonElement> orderDetails = new Controller2(Common.current_user.getUserAccessToken())
                    .getAPI().getOrderDetails(user_id, order_id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            orderDetails.subscribe(new Observer<JsonElement>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(JsonElement jsonElement) {
                    progressDialog.dismiss();
                    String result = jsonElement.toString();
                    scrollView.setVisibility(View.VISIBLE);

                    try {

                        if (result.contains("error")) {
                            JSONObject object = null;

                            object = new JSONObject(result);

                            Common.showAlert2(OrderDetails.this, object.getString("status"), object.getString("message"));
                        } else {
                            JSONObject object = new JSONObject(result);
                            bindData(object);
                        }
                    }catch (Exception e) {
                        Common.showAlert2(OrderDetails.this, getString(R.string.error), getString(R.string.missing_data));
                    }

                }

                @Override
                public void onError(Throwable e) {
                    progressDialog.dismiss();
                    Common.showAlert2(OrderDetails.this, getString(R.string.error), getString(R.string.missing_data));
                }

                @Override
                public void onComplete() {

                }
            });

        }catch (Exception e){
            Common.showAlert2(this,getString(R.string.error),getString(R.string.missing_data));
        }

    }

    private void bindData(JSONObject object) {

        try {

            scrollView.setVisibility(View.VISIBLE);
            //order info
            order_id_txt.setText(object.getString("order_id"));
            added_date_txt.setText(object.getString("date_added"));
            payment_address_txt.setText(Html.fromHtml(object.getString("payment_address")).toString());
            payment_method_txt.setText(object.getString("payment_method"));
            shipping_address_txt.setText(Html.fromHtml(object.getString("shipping_address")).toString());
            shipping_method_txt.setText(object.getString("shipping_method"));

            // payment info
            JSONObject payment_info = object.getJSONObject("payment_info");

            String refId = payment_info.getString("refID");
            String payment_id = payment_info.getString("payment_id");
            String transID = payment_info.getString("transID");
            String result = payment_info.getString("result");

            if(!refId.equals("null"))
                reference_id_txt.setText(refId);
            else {
                reference_id_txt.setVisibility(View.GONE);
                reference_id_title.setVisibility(View.GONE);
            }

            if(!payment_id.equals("null"))
                payment_id_txt.setText(payment_id);
            else {
                payment_id_txt.setVisibility(View.GONE);
                payment_id_title.setVisibility(View.GONE);
            }

            if(!transID.equals("null"))
                transfer_txt.setText(transID);
            else {
                transfer_txt.setVisibility(View.GONE);
                transfer_id_title.setVisibility(View.GONE);
            }

            if(!result.equals("null"))
                result_txt.setText(result);
            else {
                result_txt.setVisibility(View.GONE);
                result_title.setVisibility(View.GONE);
            }

            price_txt.setText(payment_info.getString("amount")+" "+getResources().getString(R.string.kd));


            // products info
            JSONArray products_info = object.getJSONArray("products");
            for (int i = 0; i < products_info.length(); i++) {

                JSONObject product_info = products_info.getJSONObject(i);

                TextView name = createText(product_info.getString("name"));
                name.setTypeface(name.getTypeface(), Typeface.BOLD);
                name.setSingleLine(true);
                name.setEllipsize(TextUtils.TruncateAt.END);

                TextView name_title = createText(getResources().getString(R.string.product_name));
                name_title.setTextColor(getResources().getColor(R.color.brownLight));
                name_title.setTypeface(name.getTypeface(), Typeface.BOLD);
                product_info_title.addView(name_title);

                TextView model = createText(product_info.getString("model"));
                TextView model_title = createText(getResources().getString(R.string.model));
                model_title.setTextColor(getResources().getColor(R.color.brownLight));
                model_title.setTypeface(name.getTypeface(), Typeface.BOLD);
                product_info_title.addView(model_title);

                TextView quantity = createText(product_info.getString("quantity")+" "+getResources().getString(R.string.items));
                TextView quantity_title = createText(getResources().getString(R.string.quantity));
                quantity_title.setTextColor(getResources().getColor(R.color.brownLight));
                quantity_title.setTypeface(quantity_title.getTypeface(), Typeface.BOLD);
                product_info_title.addView(quantity_title);

                TextView sku = createText(product_info.getString("sku"));
                TextView sku_title = createText(getResources().getString(R.string.sku));
                sku_title.setTextColor(getResources().getColor(R.color.brownLight));
                sku_title.setTypeface(sku_title.getTypeface(), Typeface.BOLD);

                TextView price = createText(product_info.getString("price")+" "+getResources().getString(R.string.kd));
                TextView price_title = createText(getResources().getString(R.string.total));
                price_title.setTextColor(getResources().getColor(R.color.brownLight));
                price_title.setTypeface(price_title.getTypeface(), Typeface.BOLD);
                product_info_title.addView(price_title);

                ArrayList<Options> options = new Gson().fromJson((product_info.getJSONArray("option")).toString(), new TypeToken<List<Options>>(){}.getType());

                product_Ln.addView(name);
                product_Ln.addView(model);

                //to show item options
                if(options.size()>0){
                    TextView options_txt = createText("");
                    options_txt.setText(getResources().getString(R.string.options));

                    String options_str;
                    for (Options option:options) {
                        options_str =  options_txt.getText().toString();
                        options_txt.setText(options_str+"\n \u25CF"+option.getName()+": "+option.getValue());
                    }
                    product_Ln.addView(options_txt);
                    product_info_title.addView(options_txt);
                }

                product_Ln.addView(quantity);
                product_Ln.addView(price);

                if(sku.getText().toString().length()>5) {
                    product_Ln.addView(sku);
                    product_info_title.addView(sku_title);
                }

                if(i<products_info.length()-1) {
                    product_Ln.addView(createView());
                    product_info_title.addView(createView());
                }
            }


            // gift message
            String gift_message = object.getString("gift_message");
            if(gift_message.equals("")){
                message_layout.setVisibility(View.GONE);
            }else {
                message_txt.setText(gift_message);
            }

            // history info
            JSONArray history_info = object.getJSONArray("histories");
            for (int i = 0; i < history_info.length(); i++) {
                JSONObject history = history_info.getJSONObject(i);

                TextView date = createText(history.getString("date_added")+" : ");
                date.setTextColor(getResources().getColor(R.color.brownLight));
                date.setTypeface(date.getTypeface(), Typeface.BOLD);
                TextView status = createText(history.getString("status"));
                TextView comment = createText(history.getString("comment"));
                comment.setSingleLine(true);
                comment.setEllipsize(TextUtils.TruncateAt.END);
                TextView comment_title = createText(getResources().getString(R.string.comment));
                comment_title.setTextColor(getResources().getColor(R.color.brownLight));
                comment_title.setTypeface(comment_title.getTypeface(), Typeface.BOLD);

                history_Ln_title.addView(date);
                history_Ln.addView(status);
                if(comment.length()>3) {
                    history_Ln.addView(comment);
                    history_Ln_title.addView(comment_title);
                }

                if(i<history_info.length()-1) {
                    history_Ln.addView(createView());
                    history_Ln_title.addView(createView());
                }
            }

            // total info
            JSONArray total_info = object.getJSONArray("totals");
            for (int i = 0; i < total_info.length(); i++) {
                JSONObject total = total_info.getJSONObject(i);

                TextView title = createText(total.getString("title")+" : ");
                title.setTextColor(getResources().getColor(R.color.brownLight));
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                TextView value = createText(total.getString("text")+" "+getResources().getString(R.string.kd));

                total_Ln_titles.addView(title);
                total_Ln.addView(value);

                if(i<total_info.length()-1) {
                    total_Ln.addView(createView());
                    total_Ln_titles.addView(createView());
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public TextView createText (String text){

        TextView textView = new TextView(this);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.START;

        textView.setLayoutParams(params);

        return textView;
    }

    public View createView (){
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1));
        v.setBackgroundColor(getResources().getColor(R.color.grey));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
        params.setMargins(0, 2, 0, 4);
        v.setLayoutParams(params);
        return v;
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
