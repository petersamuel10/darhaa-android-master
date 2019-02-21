package com.itsoluation.vavisa.darhaa.common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.itsoluation.vavisa.darhaa.Login;
import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.model.CustomerInfo;
import com.itsoluation.vavisa.darhaa.model.User;
import com.itsoluation.vavisa.darhaa.web_service.ApiInterface;
import com.itsoluation.vavisa.darhaa.web_service.Controller;
import com.itsoluation.vavisa.darhaa.web_service.Controller2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Common {

    public static boolean isArabic = false;
    public static boolean isSkip = false;
    public static User current_user;


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

    public static ApiInterface getAPI2(){ return new  Controller2().getAPI();}

    public static void errorConnectionMess(Context context){

        AlertDialog.Builder error = new AlertDialog.Builder(context);
        error.setMessage(R.string.error_connection);
        AlertDialog dialog = error.create();
        dialog.show();

    }

}
