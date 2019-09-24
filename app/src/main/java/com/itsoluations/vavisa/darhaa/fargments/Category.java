package com.itsoluations.vavisa.darhaa.fargments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.itsoluations.vavisa.darhaa.MainActivity;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.adapter.CategoryAdapter;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.home.CategoryData;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class Category extends Fragment {

    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.recyclerView)
    RecyclerView category_rec;
    MenuItem mSearch;
    SearchView searchView;

    ProgressDialog progressDialog;

    ArrayList<CategoryData> categoryList = new ArrayList<>();
    RecyclerView recyclerView;
    CategoryAdapter adapter = new CategoryAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        progressDialog = new ProgressDialog(getActivity());
        ButterKnife.bind(this, view);

        setUpSwipeRefreshLayout();
        recyclerView = view.findViewById(R.id.recyclerView);

        //setup recycler
        setupRecyclerView();

        searchView = (SearchView) (((MainActivity) getActivity()).mSearch).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase().trim();
                ArrayList<CategoryData> newList = new ArrayList<>();

                if (!newText.equals("")) {
                    for (CategoryData category : categoryList) {

                        String category_name = category.getName().toLowerCase();

                        if (category_name.contains(newText)) {
                            newList.add(category);
                        }
                    }
                    adapter.setFilter(newList);
                } else
                    adapter.setFilter(categoryList);

                return false;
            }
        });


        if (Common.isConnectToTheInternet(getActivity()))
            new GetCategoryItemsBackgroundTask(getActivity()).execute();
        else
            Common.errorConnectionMess(getContext());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (Integer.valueOf(Common.cart_count) > 0) {
                ((MainActivity) getActivity()).cart_count.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).cart_count.setText(Common.cart_count);
            } else
                ((MainActivity) getActivity()).cart_count.setVisibility(View.GONE);

        } catch (Exception e) {
        }
    }


    private void setupRecyclerView() {

        category_rec.setHasFixedSize(false);
        category_rec.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpSwipeRefreshLayout() {


        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectToTheInternet(getActivity()))
                    new GetCategoryItemsBackgroundTask(getActivity()).execute();
                else
                    Common.errorConnectionMess(getContext());

                sl.setRefreshing(false);
            }
        });
    }

    /*** Get HomeData Items**/
    private class GetCategoryItemsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        GetCategoryItemsBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
            categoryList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String get_category_url = Common.URL+"index.php?route=restapi/category";
            try {
                URL url = new URL(get_category_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("api-token", getString(R.string.api_token));
                httpURLConnection.setRequestProperty("Content-Type", getString(R.string.content_type));
                httpURLConnection.setRequestProperty("App-version", Common.App_version);
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
                Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
                return "";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();


            if (result.contains("error")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("message");
                    Common.showAlert2(getContext(), getString(R.string.error), message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                JSONArray firstJsonArray = null;
                try {
                    firstJsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    for (int i = 0; i < firstJsonArray.length(); i++) {

                        JSONObject categories = firstJsonArray.getJSONObject(i);

                        CategoryData categoryData = new CategoryData(categories.getString("category_id"), categories.getString("image"),
                                categories.getString("name"), categories.getString("isSubCat"), categories.getString("isProducts"));

                        categoryList.add(categoryData);

                        // set up the RecyclerView
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new CategoryAdapter(categoryList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    }

}
