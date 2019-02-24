package com.itsoluation.vavisa.darhaa.fargments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itsoluation.vavisa.darhaa.Login;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.FavoritesAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.favorite.FavoritesData;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favourite extends Fragment {

    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.fav_rec)
    RecyclerView fav_rec;
    @BindView(R.id.notFound)
    TextView notFound;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    FavoritesAdapter adapter;
    ProgressDialog progressDialog;
    int id = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        ButterKnife.bind(this,view);
        progressDialog = new ProgressDialog(getActivity());

        if (Paper.book("DarHaa").contains("currentUser"))
            id = Common.current_user.getCustomerInfo().getCustomer_id();
            setUpSwipeRefreshLayout();
            setupRecyclerView();
            requestData();

        return view;
    }

    private void requestData() {
        if(Common.isConnectToTheInternet(getActivity())) {
            if (Common.isConnectToTheInternet(getActivity())) {

                try {
                    this.progressDialog.setMessage(getString(R.string.loading));
                    this.progressDialog.show();
                    compositeDisposable.add(Common.getAPI().getWishList(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<JsonElement>() {
                                @Override
                                public void accept(JsonElement jsonElement) throws Exception {
                                    progressDialog.dismiss();
                                    String result = jsonElement.toString();

                                    if(result.contains("error")){
                                        JSONObject object = new JSONObject(result);
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                        builder1.setMessage(object.getString("message"));
                                        builder1.setTitle(object.getString("status"));
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
                                    else
                                    {
                                        JSONArray jArray = new JSONArray(result);
                                        ArrayList<Products> favList = new ArrayList<>();

                                            for (int i=0;i<jArray.length();i++){
                                                JSONObject object1 = jArray.getJSONObject(i);
                                                Products products = new Products();
                                                products.setProduct_id(object1.getInt("product_id"));
                                                products.setThumb(object1.getString("thumb"));
                                                products.setName(object1.getString("name"));
                                                products.setPrice(object1.getString("price"));
                                                products.setSpecial(object1.getString("special"));
                                                favList.add(products);
                                            }
                                            adapter.addFavList(favList);
                                            adapter.notifyDataSetChanged();
                                    }
                                }
                            }));

                }catch (Exception e) {
                    Log.d("rrrrrr", e.getMessage());
                }

            }
        }else
           Common.errorConnectionMess(getContext());
    }
    private void setupRecyclerView() {

        fav_rec.setHasFixedSize(true);
        fav_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(fav_rec.getContext(),R.anim.layout_fall_down);
        fav_rec.setLayoutAnimation(controller);
        adapter = new FavoritesAdapter();
        fav_rec.setAdapter(adapter);
    }

    private void setUpSwipeRefreshLayout() {
        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (adapter != null) {
                    setupRecyclerView();
                    requestData();
                }

                sl.setRefreshing(false);
            }
        });
    }
}
