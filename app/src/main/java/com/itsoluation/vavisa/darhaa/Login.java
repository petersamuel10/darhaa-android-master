package com.itsoluation.vavisa.darhaa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Login extends AppCompatActivity {

    @BindView(R.id.edEmail)
    EditText email_field;
    @BindView(R.id.edPassword)
    EditText password_field;
    @BindView(R.id.loginBtn)
    Button login_btn;

    String email_str, password_str;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @OnClick(R.id.forget_password)
    public void forgetPassword(){startActivity(new Intent(Login.this,ForgetPassword.class));}
    @OnClick(R.id.dont_have_account)
    public void registerActivity(){
        startActivity(new Intent(Login.this,Register.class));
        finish();
    }
    @OnClick(R.id.loginBtn)
    public void login(){
        loginMethod();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        Paper.init(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void loginMethod() {

        //hide keyboard when click button
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login_btn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        email_str = String.valueOf(email_field.getText());
        password_str = String.valueOf(password_field.getText());

        /** check if fields are empty **/
        if (email_str.equals("") || password_str.equals("")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Login.this);
            builder1.setMessage(R.string.fill_fields_to_continue);
            builder1.setTitle(R.string.error);
            builder1.setCancelable(false);
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
            if(Common.isConnectToTheInternet(this))
                callLoginApi(email_str,password_str);
            else
                Common.errorConnectionMess(this);
        }

    }

    private void callLoginApi(String email, String password) {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();

        compositeDisposable.add(Common.getAPI().login(email,password)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<User>() {
                               @Override
                               public void accept(User user) throws Exception {
                                   progressDialog.dismiss();
                                   if (user.getStatus().equals("error")) {
                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(Login.this);
                                       builder1.setMessage(R.string.login_no_match);
                                       builder1.setTitle(R.string.warning);
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
                                   }else {
                                       if(Common.isBooking) {
                                           Common.isBooking =false;
                                           onBackPressed();
                                           finish();
                                       }
                                       else {
                                           Common.current_user = user;
                                           Paper.book("DarHaa").write("currentUser", Common.current_user);
                                           startActivity(new Intent(Login.this, MainActivity.class));
                                           progressDialog.dismiss();
                                           finish();
                                       }
                                   }
                               }
                           }));
    }


    /*
    // *** Login ***
    private class LoginBackgroundTask extends AsyncTask<String, Void, String> {

        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;
        public JSONObject jsonObject=null;

        LoginBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.login_api);;
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("api_Token", getString(R.string.api_token));
                httpURLConnection.setRequestProperty("Content-Type", getString(R.string.content_type));
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                if (is_arabic) {
                    httpURLConnection.setRequestProperty("language", "ar");
                } else {
                    httpURLConnection.setRequestProperty("language", "en");
                }

                String str =  "{\"email\": \"" + email_str + "\", \"password\": \"" + password_str + "\"}";
                byte[] outputInBytes = str.getBytes("UTF-8");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                OS.write( outputInBytes );
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            Log.d("ststst", "result login: " + result);
            SharedPreferences.Editor editor = getSharedPreferences("is_logged_in", MODE_PRIVATE).edit();

           // Toast.makeText(Login.this, "//////////////", Toast.LENGTH_SHORT).show();
            try {
                jsonObject = new JSONObject(new String(result));
                String status = jsonObject.getString("status");

                if (status.equals("error")) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Login.this);
                    builder1.setMessage(R.string.login_no_match);
                    builder1.setTitle(R.string.warning);
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

                    Toast.makeText(Login.this, "1111111111111", Toast.LENGTH_SHORT).show();
                    JSONObject customerInfo = jsonObject.getJSONObject("customerInfo");
                    String customer_id = customerInfo.getString("customer_id");
                    String firstname = customerInfo.getString("firstname");
                    String email = customerInfo.getString("email");
                    String telephone = customerInfo.getString("telephone");

                    editor.putString("customer_id", customer_id);
                    editor.putString("firstname", firstname);
                    editor.putString("email", email);
                    editor.putString("telephone", telephone);
                    editor.putBoolean("logged_in", true);
                    editor.apply();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {

                Toast.makeText(Login.this, "RRRRRRRRRRRR", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.d("lololo",e.getMessage());
            }

        }
    }

    */
}
