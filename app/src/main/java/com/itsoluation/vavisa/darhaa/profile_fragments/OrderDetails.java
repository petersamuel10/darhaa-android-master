package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.OrdersAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.orders.OrdersData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderDetails extends AppCompatActivity {

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
    @BindView(R.id.reference_id)
    TextView reference_id_txt;
    @BindView(R.id.payment_id)
    TextView payment_id_txt;
    @BindView(R.id.transfer_id)
    TextView transfer_txt;
    @BindView(R.id.result)
    TextView result_txt;
    @BindView(R.id.price)
    TextView price_txt;

    //products info
    @BindView(R.id.product_info)
    LinearLayout product_Ln;

    //history
    @BindView(R.id.history_info)
    LinearLayout history_Ln;

    //totals
    @BindView(R.id.total_info)
    LinearLayout total_Ln;

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @OnClick(R.id.back_arrow)
    public void setBack() {onBackPressed(); }


    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    String user_id ,order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_order_details_new);

        progressDialog = new ProgressDialog(OrderDetails.this);
        progressDialog.setCancelable(false);

        ButterKnife.bind(this);

        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());
        order_id = getIntent().getStringExtra("order_id");

        Log.i("rrrrr2",user_id);
        Log.i("rrrrr",order_id);

        if (Common.isArabic) { back_arrow.setRotation(180);}

        callAPI();
    }

    private void callAPI() {

        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI2().getOrderDetails(user_id,order_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement jsonElement) throws Exception {
                        progressDialog.dismiss();
                        String result = jsonElement.toString();
                        Log.i("rrrrr",result);

                        if (result.contains("error")) {
                            JSONObject object = new JSONObject(result);
                            Common.showAlert2(OrderDetails.this,object.getString("status"),object.getString("message"));
                        } else {
                            JSONObject object = new JSONObject(result);
                            bindData(object);
                        }
                        }
                }));

    }

    private void bindData(JSONObject object) {

        try {

            //order info
            order_id_txt.setText(object.getString("order_id"));
            added_date_txt.setText(object.getString("date_added"));
            payment_address_txt.setText(Html.fromHtml(object.getString("payment_address")).toString());
            payment_method_txt.setText(object.getString("payment_method"));
            shipping_address_txt.setText(Html.fromHtml(object.getString("shipping_address")).toString());
            shipping_method_txt.setText(object.getString("shipping_method"));

            // payment info
            JSONObject payment_info = object.getJSONObject("payment_info");
            reference_id_txt.setText(payment_info.getString("refID"));
            payment_id_txt.setText(payment_info.getString("payment_id"));
            result_txt.setText(payment_info.getString("result"));
            price_txt.setText(payment_info.getString("amount"));

            // products info
            JSONArray products_info = object.getJSONArray("products");
            for (int i = 0; i < products_info.length(); i++) {
                JSONObject product_info = products_info.getJSONObject(i);
                TextView name = createText(product_info.getString("name"));
                TextView model = createText(product_info.getString("model"));
                TextView quantity = createText(product_info.getString("quantity")+" "+getResources().getString(R.string.items));
                TextView sku = createText(product_info.getString("sku"));
               // TextView options = createText(product_info.getString("option"));
                TextView price = createText(product_info.getString("price"));

                product_Ln.addView(name);
                product_Ln.addView(model);
                product_Ln.addView(quantity);
                if(!sku.getText().equals(""))
                    product_Ln.addView(sku);
               // product_Ln.addView(options);
                product_Ln.addView(price);

            }

            // history info
            JSONArray history_info = object.getJSONArray("histories");
            for (int i = 0; i < history_info.length(); i++) {
                JSONObject history = history_info.getJSONObject(i);

                TextView date = createText(history.getString("date_added")+" : "+history.getString("status"));
                TextView comment = createText(history.getString("comment"));

                history_Ln.addView(date);
                history_Ln.addView(comment);
            }

            // total info
            JSONArray total_info = object.getJSONArray("totals");
            for (int i = 0; i < total_info.length(); i++) {
                JSONObject history = total_info.getJSONObject(i);

                TextView title = createText(history.getString("title")+" : "+history.getString("text"));
                total_Ln.addView(title);
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

        return textView;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
