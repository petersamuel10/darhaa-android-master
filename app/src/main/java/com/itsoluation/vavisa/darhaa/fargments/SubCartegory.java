package com.itsoluation.vavisa.darhaa.fargments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.CategoryAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentCategoryDetails;
import com.itsoluation.vavisa.darhaa.model.home.CategoryData;

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
    ArrayList<CategoryData> categoryList;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sub_cartegory);
        progressDialog = new ProgressDialog(this);
        ButterKnife.bind(this);

        if(Common.isArabic){back_arrow.setRotation(180); }


        title.setText(Html.fromHtml(CurrentCategoryDetails.category_name).toString());

        categoryList = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        //setup recycler
        setupRecyclerView();
        requestData();
        setUpSwipeRefreshLayout();
    }


    private void requestData() {
        if(Common.isConnectToTheInternet(this)) {
            try {
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            compositeDisposable.add(Common.getAPI().getSubCat(CurrentCategoryDetails.category_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonElement>() {
                        @Override
                        public void accept(JsonElement jsonElement) throws Exception {

                            categoryList.clear();
                            String result = jsonElement.toString();
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

                                    CategoryData categoryData = new CategoryData(categories.getString("category_id"),categories.getString("image"),
                                            categories.getString("name"),categories.getString("isSubCat"),categories.getString("isProducts"));
                                    categoryList.add(categoryData);
                                    adapter = new CategoryAdapter(categoryList);
                                    subCat_rec.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }));

        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }
        }else {
            Common.errorConnectionMess(this);
            progressDialog.dismiss();
        }
    }

    private void setupRecyclerView() {
        subCat_rec.setHasFixedSize(false);
        subCat_rec.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setUpSwipeRefreshLayout() {
        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    requestData();
                }
                sl.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
