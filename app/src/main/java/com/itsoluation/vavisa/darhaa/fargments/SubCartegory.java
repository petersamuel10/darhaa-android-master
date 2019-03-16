package com.itsoluation.vavisa.darhaa.fargments;

import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.CategoryAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentCategoryDetails;
import com.itsoluation.vavisa.darhaa.model.home.Catecory;
import com.itsoluation.vavisa.darhaa.model.home.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubCartegory extends AppCompatActivity {

    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.subCat)
    RecyclerView subCat_rec;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.toolBarTitle)
    TextView title;
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    CategoryAdapter adapter;

    public ArrayList<Catecory> categories;
    ArrayList<String> category_ids, category_names, category_images, isSubCats, isProds;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sub_cartegory);
        progressDialog = new ProgressDialog(this);
        ButterKnife.bind(this);

        if(Common.isArabic){back_arrow.setRotation(180); }

        title.setText(Html.fromHtml(CurrentCategoryDetails.category_name).toString());

        category_ids = new ArrayList<>();
        category_names = new ArrayList<>();
        category_images = new ArrayList<>();
        isSubCats = new ArrayList<>();
        isProds = new ArrayList<>();

        //setup recycler
        setupRecyclerView();

        requestData();

    }

    private void requestData() {
        if(Common.isConnectToTheInternet(this)) {
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            compositeDisposable.add(Common.getAPI().getSubCat("62")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonElement>() {
                        @Override
                        public void accept(JsonElement jsonElement) throws Exception {

                            String result = jsonElement.toString();
                            Log.i("rrrr",result);
                            JSONArray firstJsonArray = null;
                            try {
                                firstJsonArray = new JSONArray(result);
                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < firstJsonArray.length(); i++) {
                                try {
                                    JSONObject categories = firstJsonArray.getJSONObject(i);

                                    String category_id = categories.getString("category_id");
                                    String image = categories.getString("image");
                                    String name = categories.getString("name");
                                    String isSubCat = categories.getString("isSubCat");
                                    String isProducts = categories.getString("isProducts");

                                    category_ids.add(category_id);
                                    category_images.add(image);
                                    category_names.add(name);
                                    isSubCats.add(isSubCat);
                                    isProds.add(isProducts);

                                    String[] category_ids_array = category_ids.toArray(new String[category_ids.size()]);
                                    String[] category_images_array = category_images.toArray(new String[category_images.size()]);
                                    String[] category_names_array = category_names.toArray(new String[category_names.size()]);
                                    String[] category_isSub_array = isSubCats.toArray(new String[isSubCats.size()]);
                                    String[] category_isProds_array = isProds.toArray(new String[isProds.size()]);

                                    Log.i("nnnn", String.valueOf(category_names_array.length));

                                    // set up the RecyclerView
                                     adapter = new CategoryAdapter(category_ids_array, category_images_array,
                                            category_names_array, category_isSub_array, category_isProds_array);
                                    subCat_rec.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }));
        }else {
            Common.errorConnectionMess(this);
            progressDialog.dismiss();
        }
    }

    private void setupRecyclerView() {
        subCat_rec.setHasFixedSize(false);
        subCat_rec.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
