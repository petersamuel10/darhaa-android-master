package com.itsoluations.vavisa.darhaa.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import com.itsoluations.vavisa.darhaa.MainActivity;
import com.itsoluations.vavisa.darhaa.R;
import com.itsoluations.vavisa.darhaa.model.User;
import com.itsoluations.vavisa.darhaa.web_service.ApiInterface;
import com.itsoluations.vavisa.darhaa.web_service.Controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Common {

    public static boolean isArabic = false;
    public static User current_user;
    public static boolean isEditAddress = false;
    public static boolean showAddrDetails = false;
    public static Activity mActivity;
    public static String App_version = "Android-1.0.4";
    public static String cart_count = "0";


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

        TextView message_ = dialog.findViewById(R.id.alert_message);
        TextView cancel =  dialog.findViewById(R.id.cancel_alert);

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

        TextView message_ = dialog.findViewById(R.id.alert_message);
        TextView cancel =  dialog.findViewById(R.id.cancel_alert);

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

        TextView message_ = dialog.findViewById(R.id.alert_message);
        TextView cancel =  dialog.findViewById(R.id.cancel_alert);

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
