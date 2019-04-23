package com.itsoluations.vavisa.darhaa.fargments;

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

import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.adapter.HomeAdapter;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.home.CategoryData;
import com.itsoluations.vavisa.darhaa.model.home.HomeData;

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

    public ArrayList<CategoryData> categories;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

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
            try {
            compositeDisposable.add(Common.getAPI().home()
                               .subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(new Consumer<HomeData>() {
                                   @Override
                                   public void accept(HomeData home) throws Exception {
                                       if(home.getStatus() !=null ){
                                           progressDialog.dismiss();
                                           Common.showAlert2(getContext(),home.getStatus(), home.getMessage());
                                       }else {
                                           categories = new ArrayList<>();
                                           categories.addAll(home.getCategories());
                                           //fist HomeData item
                                           categories.add(0, home.getRecent_category());
                                           homeAdapter.addHomeList(categories);
                                           homeAdapter.notifyDataSetChanged();
                                           progressDialog.dismiss();
                                       }
                                   }
                               }));

        } catch (Exception e) {
            Common.showAlert2(getContext(), getString(R.string.warning), e.getMessage());
        }
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
}
