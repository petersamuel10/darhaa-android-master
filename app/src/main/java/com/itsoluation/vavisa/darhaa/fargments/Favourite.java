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

import com.itsoluation.vavisa.darhaa.Login;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.adapter.FavoritesAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.favorite.FavoritesData;

import java.util.ArrayList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        ButterKnife.bind(this,view);
        progressDialog = new ProgressDialog(getActivity());

        if (Paper.book("DarHaa").contains("currentUser")) {
            setUpSwipeRefreshLayout();
            setupRecyclerView();
            requestData();
        }
        else
            notFound.setVisibility(View.VISIBLE);

        return view;
    }

    private void requestData() {
        if(Common.isConnectToTheInternet(getActivity())) {
            if (Common.isConnectToTheInternet(getActivity())) {

                try {
                    this.progressDialog.setMessage(getString(R.string.loading));
                    this.progressDialog.show();
                    compositeDisposable.add(Common.getAPI().getWishList(Common.current_user.getCustomerInfo().getCustomer_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FavoritesData>() {
                                @Override
                                public void accept(FavoritesData favoritesData) throws Exception {
                                    try {
                                        Toast.makeText(getContext(), "///" + favoritesData.getProducts().size(), Toast.LENGTH_SHORT).show();

                                        if (favoritesData.getStatus() != null) {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                            builder1.setMessage(favoritesData.getMessage());
                                            builder1.setTitle(favoritesData.getStatus());
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
                                            adapter.addFavList(favoritesData.getProducts());
                                            progressDialog.dismiss();
                                        }

                                    } catch (Exception e) {
                                        Log.d("rrrrrr", e.getMessage());
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

                /*
                if (adapter != null) {
                    setupRecyclerView();
                    requestData();
                } */

                sl.setRefreshing(false);
            }
        });
    }
}
