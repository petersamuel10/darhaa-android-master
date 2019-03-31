package com.itsoluation.vavisa.darhaa.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.Login;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.User;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressDetails;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.payment.BillingAddress;
import com.itsoluation.vavisa.darhaa.payment.Checkout;
import com.itsoluation.vavisa.darhaa.web_service.ApiInterface;
import com.itsoluation.vavisa.darhaa.web_service.Controller;
import com.itsoluation.vavisa.darhaa.web_service.Controller2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Common {

    public static boolean isArabic = false;
    public static User current_user;
    public static AddressDetails currentAddress;
    public static boolean isEditAddress = false;
    public static String address_id = null;
    public static boolean showAddrDetails = false;
   // public static String userAccessToken ="";



    public static Boolean isConnectToTheInternet (Context context) {

        boolean connected = false;
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()
                && cm.getActiveNetworkInfo().isAvailable()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url
                        .openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    connected = true;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (cm != null) {
           /* final NetworkInfo[] netInfoAll = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfoAll) {
                System.out.println("get network type :::" + ni.getTypeName());
                if ((ni.getTypeName().equalsIgnoreCase("WIFI") || ni
                        .getTypeName().equalsIgnoreCase("MOBILE"))
                        && ni.isConnected() && ni.isAvailable()) {
                    connected = true;
                    if (connected) {
                        break;
                    }
                }
            }*/
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable()) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    connected = true;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    connected = true;
                }
            } else {
                // not connected to the internet
                connected = false;
            }
        }
        return connected;

    }

    public static ApiInterface getAPI ()
    {
        return new Controller().getAPI();
    }

  //  public static ApiInterface getAPI2(){ return new  Controller2(userAccessToken).getAPI();}

    public static void errorConnectionMess(Context context){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button

        TextView title_ =  dialog.findViewById(R.id.alert_title);
        TextView message_ = dialog.findViewById(R.id.alert_message);
        TextView cancel =  dialog.findViewById(R.id.cancel_alert);

        title_.setText(context.getText(R.string.error));
        message_.setText(context.getText(R.string.error_connection));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public static void showAlert(Context context, int title, int msg) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button

        TextView title_ =  dialog.findViewById(R.id.alert_title);
        TextView message_ = dialog.findViewById(R.id.alert_message);
        TextView cancel =  dialog.findViewById(R.id.cancel_alert);

        title_.setText(context.getText(title));
        message_.setText(context.getText(msg));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showAlert2(Context context, String title, String msg) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button

        TextView title_ =  dialog.findViewById(R.id.alert_title);
        TextView message_ = dialog.findViewById(R.id.alert_message);
        TextView cancel =  dialog.findViewById(R.id.cancel_alert);

        title_.setText(title);
        message_.setText(msg);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
