package com.itsoluation.vavisa.darhaa.fargments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerItemTouchHelperListner;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.FavoritesAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;
import com.itsoluation.vavisa.darhaa.recyclerItemTouchHelper.RecyclerItemTouchHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

        if (Paper.book("DarHaa").contains("currentUser"))
            id = Common.current_user.getCustomerInfo().getCustomer_id();
        setUpSwipeRefreshLayout();
        setupRecyclerView();
        requestData();

        return view;
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

                                    } else {
                                        JSONArray jArray = new JSONArray(result);
                                        favList = new ArrayList<>();

                                        for (int i = 0; i < jArray.length(); i++) {
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

                } catch (Exception e) {
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

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0, ItemTouchHelper.START, this);

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
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {

        compositeDisposable = new CompositeDisposable();
        String product_id = String.valueOf(favList.get(viewHolder.getAdapterPosition()).getProduct_id());
        int user_id = Common.current_user.getCustomerInfo().getCustomer_id();

        if (viewHolder instanceof FavoritesAdapter.ViewHolder) {

            compositeDisposable.add(Common.getAPI().removeFavorte(product_id, user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            if (status.getStatus().equals("error"))
                                Common.showAlert2(getContext(), status.getStatus(), status.getMessage());
                            else{
                                Snackbar snackbar = Snackbar.make(rootLayout,status.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }));

            adapter.deleteItem(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    }
}
