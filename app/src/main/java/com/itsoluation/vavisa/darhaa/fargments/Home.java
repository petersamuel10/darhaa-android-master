package com.itsoluation.vavisa.darhaa.fargments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.HomeAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.home.Catecory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Home extends Fragment {

    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.home_rec)
    RecyclerView home_rec;

    HomeAdapter homeAdapter;

    public ArrayList<Catecory> categories;


    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity());

        ButterKnife.bind(this,view);

        setUpSwipeRefreshLayout();
        //setup recycler
        setupRecyclerView();
        //get Data
        requestData();

        return view;
    }

    private void requestData() {
        if(Common.isConnectToTheInternet(getActivity())) {
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
           // this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            compositeDisposable.add(Common.getAPI().home()
                               .subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(new Consumer<com.itsoluation.vavisa.darhaa.model.home.Home>() {
                                   @Override
                                   public void accept(com.itsoluation.vavisa.darhaa.model.home.Home home) throws Exception {
                                       categories = new ArrayList<>();
                                       categories.addAll(home.getCategories());
                                       categories.add(0,home.getRecent_category());
                                       homeAdapter.addCategory(categories);
                                       homeAdapter.notifyDataSetChanged();
                                   //    Toast.makeText(getContext(), "/RRR/"+categories.size(), Toast.LENGTH_SHORT).show();
                                       progressDialog.dismiss();

                                   }
                               }));
        }else {
            Common.errorConnectionMess(getContext());
            progressDialog.dismiss();
        }
    }

    private void setupRecyclerView() {

        home_rec.setHasFixedSize(true);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(homeAdapter.getItemViewType(position)){
                    case HomeAdapter.TYPE_FIRST_ITEM:
                        return 2;
                    case HomeAdapter.TYPE_ITEM:
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        home_rec.setLayoutManager(mLayoutManager);
        homeAdapter = new HomeAdapter();
        home_rec.setAdapter(homeAdapter);
    }

    private void setUpSwipeRefreshLayout() {
        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (homeAdapter != null) {
                    setupRecyclerView();
                    requestData();
                }

                sl.setRefreshing(false);
            }
        });
    }

/*
    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
    }
*/


/*
    // Get Home Items
    private class GetHomeItemsBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        GetHomeItemsBackgroundTask(Activity activity) {
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
            String get_home_url = getString(R.string.home_items_api);
            try {
                URL url = new URL(get_home_url);
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

            JSONObject firstJsonObject = null;
            try {
                firstJsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < firstJsonObject.length(); i++) {
                try {
                    JSONArray categories = firstJsonObject.getJSONArray("categories");

                    for (int j = 0; j < categories.length(); j++) {
                        JSONObject object = categories.getJSONObject(j);
                        String category_id = object.getString("category_id");
                        String image = object.getString("image");
                        String name = object.getString("name");
                        String isSubCat = object.getString("isSubCat");

                        category_ids.add(category_id);
                        category_images.add(image);
                        category_names.add(name);
                        isSubCats.add(isSubCat);
                    }

                    String[] category_ids_array = category_ids.toArray(new String[category_ids.size()]);
                    String[] category_images_array = category_images.toArray(new String[category_images.size()]);
                    String[] category_names_array = category_names.toArray(new String[category_names.size()]);

                    // set up the RecyclerView
                    int numberOfColumns = 2;
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
                    adapter = new HomeAdapter(getContext(), category_ids_array, category_images_array, category_names_array);
//                    adapter.setClickListener(this); // to allow on click events
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/
}
