package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.itsoluation.vavisa.darhaa.Interface.RecyclerViewItemClickListener;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

import static java.security.AccessController.getContext;

public class Addresses extends AppCompatActivity implements RecyclerViewItemClickListener {

    @BindView(R.id.addresses_rec)
    RecyclerView address_rec;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.sw)
    SwipeRevealLayout sw;

    @OnClick(R.id.ic_add_address)
    public void addAddress(){
        startActivity(new Intent(Addresses.this,AddAddress.class)); }
    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    int pos = 0;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
   // AddressAdapter adapter;
    ProgressDialog progressDialog;
   // List<Address> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_addresses);
        progressDialog = new ProgressDialog(this);
        ButterKnife.bind(this);
        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

        sw.setDragEdge(2);

    }

    @Override
    public void onStart() {
        super.onStart();

       // list.clear();

        setupRecyclerView();
        if(Common.isConnectToTheInternet(this)){
            requestData();
        } else
        {
            AlertDialog.Builder error = new AlertDialog.Builder(this);
            error.setMessage(R.string.error_connection);
            AlertDialog dialog = error.create();
            dialog.show();
        }

        //  adapter.notifyDataSetChanged();

        // adapter = new AddressAdapter();
        // requestData();
    }

    private void requestData() {
//        progressDialog.show();


    }

    private void setupRecyclerView() {

        address_rec.setHasFixedSize(true);
        address_rec.setLayoutManager(new LinearLayoutManager(this));
      //  adapter = new AddressAdapter();
     //   adapter.setListener(this);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(address_rec.getContext(),R.anim.layout_fall_down);
        address_rec.setLayoutAnimation(controller);
    //    address_rec.setAdapter(adapter);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view, int position) {

    }
}
