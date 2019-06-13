package com.itsoluations.vavisa.darhaa.fargments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.itsoluations.vavisa.darhaa.MainActivity;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.model.favorite.Products;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUS extends Fragment {

    LinearLayout call, send_email;
    String telephone,email;

    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_support, container, false);

        call = view.findViewById(R.id.call);
        send_email = view.findViewById(R.id.send_email);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        callApi();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage(getResources().getString(R.string.call_to)+" "+telephone);
                builder1.setPositiveButton(
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                boolean call = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
                                if(call) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone));
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            }
                        });

                builder1.setNegativeButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                              dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                try {
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                }

            }
        });

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

    private void callApi() {

        compositeDisposable = new CompositeDisposable();
            if (Common.isConnectToTheInternet(getActivity())) {
                    this.progressDialog.show();
                    compositeDisposable.add(Common.getAPI().getSettings()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<JsonElement>() {
                                @Override
                                public void accept(JsonElement jsonElement) throws Exception {
                                    progressDialog.dismiss();
                                    String result = jsonElement.toString();
                                    JSONObject object = new JSONObject(result);

                                    telephone = object.getString("telephone");
                                    email = object.getString("email");

                                }
                            }));

            }else
                Common.errorConnectionMess(getContext());
        }
}