package com.itsoluations.vavisa.darhaa.fargments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluations.vavisa.darhaa.Interface.RecyclerItemTouchHelperListner;
import com.itsoluations.vavisa.darhaa.Login;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.adapter.FavoritesAdapter;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.Status;
import com.itsoluations.vavisa.darhaa.model.favorite.Products;
import com.itsoluations.vavisa.darhaa.recyclerItemTouchHelper.RecyclerItemTouchHelperFavorite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favourite extends Fragment implements RecyclerItemTouchHelperListner {

    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.fav_rec)
    RecyclerView fav_rec;
    @BindView(R.id.notFound)
    TextView notFound;
    @BindView(R.id.rootLayout)
    FrameLayout rootLayout;
    @BindView(R.id.loginLN)
    LinearLayout loginLN;

    @OnClick(R.id.loginText)
    public void login() {
        startActivity(new Intent(getContext(), Login.class));
    }


    private CompositeDisposable compositeDisposable;
    FavoritesAdapter adapter;
    ProgressDialog progressDialog;
    int id = -1;
    ArrayList<Products> favList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Paper.book("DarHaa").contains("currentUser")) {
            id = Common.current_user.getCustomerInfo().getCustomer_id();
            setUpSwipeRefreshLayout();
            setupRecyclerView();
            requestData();
            sl.setVisibility(View.VISIBLE);
            loginLN.setVisibility(View.GONE);
        } else
            loginLN.setVisibility(View.VISIBLE);


    }


    private void requestData() {

        compositeDisposable = new CompositeDisposable();
        if (Common.isConnectToTheInternet(getActivity())) {
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

                                    if (result.contains("error")) {
                                        if (result.contains(getString(R.string.no_data_found)))
                                            notFound.setVisibility(View.VISIBLE);
                                        else {
                                            JSONObject object = new JSONObject(result);
                                            Common.showAlert2(getContext(), object.getString("status"), object.getString("message"));
                                        }
                                    } else {
                                        JSONArray jArray = new JSONArray(result);
                                        favList = new ArrayList<>();
                                        if (jArray.length() == 0)
                                            notFound.setVisibility(View.VISIBLE);
                                        else {
                                            for (int i = 0; i < jArray.length(); i++) {
                                                JSONObject object1 = jArray.getJSONObject(i);
                                                Products products = new Products();
                                                products.setProduct_id(object1.getString("product_id"));
                                                products.setThumb(object1.getString("thumb"));
                                                products.setName(object1.getString("name"));
                                                products.setPrice(object1.getString("price"));
                                                products.setSpecial(object1.getString("special"));
                                                favList.add(products);
                                            }
                                            notFound.setVisibility(View.GONE);
                                            adapter.addFavList(favList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }));

                } catch (Exception e) {
                    Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
                    Log.d("rrrrrr", e.getMessage());
                }

            }
        } else
            Common.errorConnectionMess(getContext());
    }

    private void setupRecyclerView() {

        fav_rec.setHasFixedSize(true);
        fav_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoritesAdapter();
        fav_rec.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelperFavorite(0, ItemTouchHelper.START, this);

        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(fav_rec);
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

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        final String product_id = String.valueOf(favList.get(viewHolder.getAdapterPosition()).getProduct_id());
        String user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());

        if (Common.isConnectToTheInternet(getContext())) {

            try {
                if (viewHolder instanceof FavoritesAdapter.ViewHolder) {
                    progressDialog.show();
                    Call<Status> delete = Common.getAPI().removeFavorte_(product_id, user_id);
                    delete.enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {

                            if (response.body().getStatus().equals("error"))
                                Common.showAlert2(getContext(), response.body().getStatus(), response.body().getMessage());
                            else {
                                progressDialog.dismiss();

                                favList.remove(position);
                                adapter.addFavList(favList);
                                if (adapter.getItemCount() == 0) {
                                    notFound.setVisibility(View.VISIBLE);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable t) {

                        }
                    });
                }
            } catch (Exception e) {
                Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
            }
        } else
            Common.errorConnectionMess(getContext());
    }
}
