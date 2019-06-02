package com.itsoluations.vavisa.darhaa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itsoluations.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluations.vavisa.darhaa.adapter.CategoryProductAdapter;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.common.CurrentCategoryDetails;
import com.itsoluations.vavisa.darhaa.common.CurrentProductDetails;
import com.itsoluations.vavisa.darhaa.model.Status;
import com.itsoluations.vavisa.darhaa.model.category_products.CategoryProductData;
import com.itsoluations.vavisa.darhaa.model.favorite.Products;
import com.itsoluations.vavisa.darhaa.singleton.InterfaceProduct;
import com.itsoluations.vavisa.darhaa.view_setting.Filter;
import com.itsoluations.vavisa.darhaa.view_setting.Sort;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CategoryProducts extends AppCompatActivity implements RecyclerViewItemClickListener, InterfaceProduct.OnProductWishChanges {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.sl)
    SwipeRefreshLayout sl;
    @BindView(R.id.title_)
    TextView title;
    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @OnClick(R.id.sortBtn)
    public void setSort() {
        Intent i = new Intent(this, Sort.class);
        startActivityForResult(i, REQUEST_SORT);
    }

    @OnClick(R.id.filterBtn)
    public void setFilter() {
        Intent i = new Intent(this, Filter.class);
        startActivityForResult(i, REQUEST_FILTER);
    }

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    public static String user_id;
    public static String filter_value = "ASC";
    public static String filter_value_price = "";
    public static String sort_type = "p.sort_order";
    public static String category_price_min_value = "0.000";
    public static String category_price_max_value = "0.000";
    public ArrayList<Products> productsArrayList;
    public static ArrayList<Products> productsListFirstPage;

    RecyclerViewItemClickListener itemClickListener;
    CategoryProductAdapter adapter;
    CategoryProductData categoryProductData;
    RecyclerView recyclerView;
    int page;
    int totalPages;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;
    public final static int REQUEST_SORT = 1;
    public final static int REQUEST_FILTER = 2;
    String search_str = "";
    MenuItem mSearch;
    boolean isFirstCall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);
        ButterKnife.bind(this);
        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //that is for favourite in product and listen in category product when on back pressed
        InterfaceProduct.getInstance().setmListener(this);

        // that's for empty search call bind first page
        // put here to prevent call in start after go back from sort and filter
        isFirstCall = true;

        setSupportActionBar(toolbar);
        itemClickListener = this;
        recyclerView = findViewById(R.id.rvNumbers);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : "0";
        title.setText(CurrentCategoryDetails.category_name);

        productsArrayList = new ArrayList<>();
        categoryProductData = new CategoryProductData();
        productsListFirstPage = new ArrayList<>();
        adapter = new CategoryProductAdapter();


        setup_for_call_api();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (llm.findLastCompletelyVisibleItemPosition() == productsArrayList.size() - 1) {
                    if (totalPages > page) {
                        page++;
                        progressBar.setVisibility(View.VISIBLE);
                        callAPI(false);
                    }
                }
            }
        });

        setUpSwipeRefreshLayout();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // get user_id after come from login activity
        user_id = (Common.current_user != null) ? String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id()) : "0";
    }

    public void setup_for_call_api() {
        page = 1;
        totalPages = 1;
        //disable progress when write search text
        if (search_str.equals(""))
            progressDialog.show();

        callAPI(true);
    }

    private void setUpSwipeRefreshLayout() {

        sl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    MenuItemCompat.collapseActionView(mSearch);
                    filter_value = "ASC";
                    filter_value_price = "";
                    sort_type = "p.sort_order";
                    category_price_min_value = "0";
                    category_price_max_value = "0";
                    search_str = "";
                    setup_for_call_api();
                }
                sl.setRefreshing(false);
            }
        });
    }

    private void callAPI(final boolean load) {
        if (Common.isConnectToTheInternet(this)) {

            compositeDisposable = new CompositeDisposable();
            try {
                compositeDisposable.add(Common.getAPI().get_product_pagination(CurrentCategoryDetails.category_id, sort_type,
                        CategoryProducts.filter_value, CategoryProducts.filter_value_price, "10", String.valueOf(page), search_str, user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<CategoryProductData>() {
                            @Override
                            public void accept(CategoryProductData categoryProducts) throws Exception {

                                if (categoryProducts.getStatus() != null) {
                                    if (progressDialog.isShowing() && progressDialog != null)
                                        progressDialog.dismiss();
                                    Common.showAlert2(CategoryProducts.this, categoryProducts.getStatus(), categoryProducts.getMessage());
                                } else {
                                    // that's to save first page list to display when empty search
                                    if (isFirstCall) {
                                        productsListFirstPage.addAll(categoryProducts.getProducts());
                                        category_price_min_value = categoryProducts.getMinPrice();
                                        category_price_max_value = categoryProducts.getMaxPrice();
                                        isFirstCall = false;
                                    }

                                    if (categoryProducts.getProducts().size() == 0) {
                                        productsArrayList.clear();
                                        adapter.clearList();
                                        if (progressDialog.isShowing() && progressDialog != null)
                                            progressDialog.dismiss();
                                    } else {

                                        if (load) {
                                            productsArrayList.clear();
                                            adapter.clearList();
                                        }

                                        productsArrayList.addAll(categoryProducts.getProducts());
                                        adapter = new CategoryProductAdapter(productsArrayList, false);
                                        categoryProductData.setProducts(productsArrayList);
                                        categoryProductData.setProductPerPage(categoryProducts.getProductPerPage());
                                        categoryProductData.setPage(categoryProducts.getPage());
                                        categoryProductData.setLimit(10);
                                        categoryProductData.setMinPrice(categoryProducts.getMinPrice());
                                        categoryProductData.setMaxPrice(categoryProducts.getMaxPrice());


                                        adapter.setItemClickListener(itemClickListener);

                                        if (page == 1) {
                                            recyclerView.setAdapter(adapter);
                                            totalPages = categoryProducts.getTotalPages();
                                            categoryProductData.setTotalProducts(categoryProducts.getTotalProducts());
                                            categoryProductData.setTotalPages(categoryProducts.getTotalPages());
                                        } else
                                            adapter.notifyDataSetChanged();

                                        progressBar.setVisibility(View.GONE);
                                        if (progressDialog.isShowing() && progressDialog != null)
                                            progressDialog.dismiss();
                                    }
                                }
                            }
                        }));
            } catch (Exception e) {
                if (progressDialog.isShowing() && progressDialog != null)
                    progressDialog.dismiss();
                Common.showAlert2(this, getString(R.string.warning), e.getMessage());
            }

        } else {
            if (progressDialog.isShowing() && progressDialog != null)
                progressDialog.dismiss();
            Common.errorConnectionMess(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu4products, menu);
        mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                search_str = s.toLowerCase().trim();
                filter_value = "ASC";
                filter_value_price = "";
                sort_type = "p.sort_order";
                if (search_str.length() > 0)
                    setup_for_call_api();
                else
                    adapter.setFilter(productsListFirstPage);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view, int position, String product_id, String product_name, int flag) {

        if (flag == 0) {
            CurrentProductDetails.product_id = product_id;
            CurrentProductDetails.product_name = product_name;
            Intent i = new Intent(this, Product.class);
            i.putExtra("product_", productsArrayList.get(position));
            i.putExtra("position", position);
            startActivity(i);
        } else if (flag == 1) {
            if (!user_id.equals("0"))
                setFavorite(position, view);
            else
                showAlert();
        }
    }

    private void setFavorite(int position, View view) {
        if (Common.isConnectToTheInternet(this)) {
            if (productsArrayList.get(position).getWishList())
                removeFav(position, view);
            else
                addFav(position, view);
        } else
            Common.errorConnectionMess(this);

    }

    private void addFav(final int position, final View view) {
        ((ImageView) view).setImageResource(R.drawable.ic_fav);
        productsArrayList.get(position).setWishList(true);
        try {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Common.getAPI().addFavorte(productsArrayList.get(position).getProduct_id(), user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            if (status.getStatus().equals("error")) {
                                Common.showAlert2(CategoryProducts.this, status.getStatus(), status.getMessage());
                            }/*else {
                            ((ImageView)view).setImageResource(R.drawable.ic_fav);
                            productsArrayList.get(position).setWishList(true);
                        }*/
                        }
                    }));

        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }


    }

    private void removeFav(final int position, final View view) {
        ((ImageView) view).setImageResource(R.drawable.ic_fav_border);
        productsArrayList.get(position).setWishList(false);
        try {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Common.getAPI().removeFavorte(productsArrayList.get(position).getProduct_id(), user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            if (status.getStatus().equals("error")) {
                                Common.showAlert2(CategoryProducts.this, status.getStatus(), status.getMessage());
                            } else {
                           /* ((ImageView)view).setImageResource(R.drawable.ic_fav_border);
                            productsArrayList.get(position).setWishList(false);*/
                            }
                        }
                    }));
        } catch (Exception e) {
            Common.showAlert2(this, getString(R.string.warning), e.getMessage());
        }

    }

    private void showAlert() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alert_login_fav);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView login = dialog.findViewById(R.id.login_alert_btn);
        TextView cancel = dialog.findViewById(R.id.cancel_alert_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(CategoryProducts.this, Login.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cart) {
            startActivity(new Intent(this, Cart.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // This is used when the current activity is waiting until the next activity is finished to do something
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SORT:
                search_str = "";
                filter_value_price = "";
                MenuItemCompat.collapseActionView(mSearch);
                setup_for_call_api();
                break;
            case REQUEST_FILTER:
                search_str = "";
                filter_value = "ASC";
                MenuItemCompat.collapseActionView(mSearch);
                setup_for_call_api();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        filter_value = "ASC";
        filter_value_price = "";
        sort_type = "p.sort_order";
        super.onBackPressed();

    }

    //update ui when you wish in product details and come back again
    @Override
    public void wishChanged() {

        Products products = InterfaceProduct.getInstance().getProduct();
        int position = InterfaceProduct.getInstance().getPosition();

        productsArrayList.set(position, products);
        adapter.notifyItemChanged(position);

    }
}
