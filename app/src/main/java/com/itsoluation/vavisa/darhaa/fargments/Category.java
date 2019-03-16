package com.itsoluation.vavisa.darhaa.fargments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.CategoryAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.common.CurrentCategoryDetails;

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
import io.reactivex.disposables.CompositeDisposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class Category extends Fragment {

    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.recyclerView)
    RecyclerView category_rec;
//    @BindView(R.id.item_category)
//    LinearLayout category;
//    @OnClick(R.id.item_category)
//    public void test(){
//        getActivity().startActivity(new Intent(getActivity(), CategoryProducts.class));
//    }

    // private CategoryAdapter homeAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;
    ArrayList<String> category_ids, category_names, category_images, isSubCats, isProds;
    RecyclerView recyclerView;
    CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        progressDialog = new ProgressDialog(getActivity());

        ButterKnife.bind(this,view);

        setUpSwipeRefreshLayout();

        recyclerView = view.findViewById(R.id.recyclerView);

        //setup recycler
        setupRecyclerView();

        if(Common.isConnectToTheInternet(getActivity()))
            new GetCategoryItemsBackgroundTask(getActivity()).execute();
        else
            Common.errorConnectionMess(getContext());

        return view;
    }

    private void setupRecyclerView() {

        category_rec.setHasFixedSize(false);
        category_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        /*
        home_recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.HORIZONTAL));
        home_recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
                */

      //  homeAdapter = new CategoryAdapter();
      //  home_recyclerView.setAdapter(homeAdapter);
    }

    private void setUpSwipeRefreshLayout() {
        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectToTheInternet(getActivity()))
                    new GetCategoryItemsBackgroundTask(getActivity()).execute();
                else
                    Common.errorConnectionMess(getContext());

                sl.setRefreshing(false);
            }
        });
    }


    /** Get Home Items **/
    private class GetCategoryItemsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        GetCategoryItemsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);

            category_ids = new ArrayList<>();
            category_names = new ArrayList<>();
            category_images = new ArrayList<>();
            isSubCats = new ArrayList<>();
            isProds = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String get_category_url = getString(R.string.category_items_api);
            try {
                URL url = new URL(get_category_url);
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

            JSONArray firstJsonArray = null;
            try {
                firstJsonArray = new JSONArray(result);
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

                    // set up the RecyclerView
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new CategoryAdapter(category_ids_array, category_images_array, category_names_array, category_isSub_array, category_isProds_array);
//                    adapter.setClickListener(this); // to allow on click events
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
