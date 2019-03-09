package com.itsoluation.vavisa.darhaa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.Interface.CartInterface;
import com.itsoluation.vavisa.darhaa.Interface.EditDeleteAddrInterface;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerItemTouchHelperListner;
import com.itsoluation.vavisa.darhaa.adapter.AddressAdapter;
import com.itsoluation.vavisa.darhaa.adapter.CartAdapter;
import com.itsoluation.vavisa.darhaa.adapter.FavoritesAdapter;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;
import com.itsoluation.vavisa.darhaa.profile_fragments.Addresses;
import com.itsoluation.vavisa.darhaa.recyclerItemTouchHelper.RecyclerItemTouchHelper;
import com.itsoluation.vavisa.darhaa.recyclerItemTouchHelper.RecyclerViewItemTouchHelperCart;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Cart extends AppCompatActivity implements CartInterface, RecyclerItemTouchHelperListner {

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.cart_rec)
    RecyclerView cart_rec;

    @OnClick(R.id.back_arrow)
    public void setBack() {
        onBackPressed();
    }

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    CartAdapter adapter;
    CartData cartData;

    String user_id,device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        setupRecyclerView();

        user_id = String.valueOf(Common.current_user.getCustomerInfo().getCustomer_id());

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(Common.isConnectToTheInternet(this)){
            callAPI();
        } else
            Common.errorConnectionMess(this);
    }

    private void callAPI() {

        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().viewCart(user_id,device_id)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<CartData>() {
                               @Override
                               public void accept(CartData cartList) throws Exception {
                                   progressDialog.dismiss();
                                   if(cartList.getStatus()!=null){
                                       Common.showAlert2(Cart.this,cartList.getStatus(),cartList.getMessage());
                                   }else {
                                       cartData = cartList;
                                       adapter.addAddress(cartList.getProducts());
                                       adapter.notifyDataSetChanged();
                                   }
                               }
                           }));
    }

    private void setupRecyclerView() {

        cart_rec.setHasFixedSize(true);
        cart_rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter();
        adapter.setListener(this);
        cart_rec.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerViewItemTouchHelperCart(0, ItemTouchHelper.START, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(cart_rec);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClick(int position, int flag, View item_amount_txt, View item_price_txt) {
        int amount = Integer.parseInt(((TextView)item_amount_txt).getText().toString());
        double price = Double.parseDouble(((TextView)item_price_txt).getText().toString());
        int minimum  = Integer.parseInt(cartData.getProducts().get(position).getMinimum());
        int max = Integer.parseInt(cartData.getProducts().get(position).getTotal_quantity());

        if(flag == 0)
        {
            // delete address
              callDeleteAddressAPI(cartData.getProducts().get(position).getCart_id(),position);

        }else if(flag == 1) {
            // edit address
            // add
        }else if (flag == 2) {
            if(amount == max) {
                Common.showAlert(Cart.this,R.string.warning,R.string.max_number);
            }else {
                amount++;
                ((TextView) item_amount_txt).setText(String.valueOf(amount));
                price += Double.parseDouble(cartData.getProducts().get(position).getPrice());
                ((TextView) item_price_txt).setText(String.format(Locale.US, "%.3f", price));
            }
            //minus
        }else if (flag == 3) {
            if (amount == minimum) {
                Common.showAlert(Cart.this,R.string.warning,R.string.minimum_number);
            }else {
                amount--;
                ((TextView) item_amount_txt).setText(String.valueOf(amount));
                price -= Double.parseDouble(cartData.getProducts().get(position).getPrice());
                ((TextView) item_price_txt).setText(String.format(Locale.US, "%.3f", price));
            }
        }
    }

    private void callDeleteAddressAPI(String cart_id, final int position_) {
        progressDialog.show();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Common.getAPI().deleteCart(cart_id,user_id,device_id)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<Status>() {
                               @Override
                               public void accept(Status status) throws Exception {
                                   progressDialog.dismiss();
                                   if (status.getStatus().equals("error")) {
                                       Common.showAlert2(Cart.this, status.getStatus(), status.getMessage());
                                   } else {
                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(Cart.this);
                                       builder1.setMessage(status.getMessage());
                                       builder1.setTitle(status.getStatus());
                                       builder1.setCancelable(false);
                                       builder1.setPositiveButton(
                                               R.string.ok,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick(DialogInterface dialog, int id) {
                                                       dialog.cancel();
                                                       adapter.removeCart(position_);
                                                       adapter.notifyItemRemoved(position_);
                                                   }
                                               });

                                       AlertDialog alert11 = builder1.create();
                                       alert11.show();
                                   }
                               }
                           }));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        compositeDisposable = new CompositeDisposable();

        String cart_id = String.valueOf(cartData.getProducts().get(viewHolder.getAdapterPosition()).getCart_id());

        if (viewHolder instanceof FavoritesAdapter.ViewHolder) {

            compositeDisposable.add(Common.getAPI().deleteCart(cart_id, user_id,device_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Status>() {
                        @Override
                        public void accept(Status status) throws Exception {
                            if (status.getStatus().equals("error"))
                                Common.showAlert2(Cart.this, status.getStatus(), status.getMessage());
                            else{
                                Snackbar snackbar = Snackbar.make(rootLayout, status.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }));

            adapter.removeCart(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    }
}
