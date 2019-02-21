package com.itsoluation.vavisa.darhaa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Register extends AppCompatActivity {

    @BindView(R.id.registerBtn)
    Button register_btn;
    @BindView(R.id.edName)
    EditText full_name_field;
    @BindView(R.id.edEmail)
    EditText email_field;
    @BindView(R.id.edPhone)
    EditText phone_field;
    @BindView(R.id.edPassword)
    EditText password_field;
    @BindView(R.id.edConfirmPass)
    EditText confirm_field;

    String full_name_str, email_str, phone_str, password_str, confirm_str;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @OnClick(R.id.have_account)
    public void loginActivity(){
        startActivity(new Intent(Register.this,Login.class));
        finish();
    }
    @OnClick(R.id.registerBtn)
    public void register(){
        makeRegisteration();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        Paper.init(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void makeRegisteration() {

        //hide keyboard when click button
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(register_btn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        full_name_str = String.valueOf(full_name_field.getText());
        email_str = String.valueOf(email_field.getText());
        phone_str = String.valueOf(phone_field.getText());
        password_str = String.valueOf(password_field.getText());
        confirm_str = String.valueOf(confirm_field.getText());

        /** check if fields are empty **/
        if (full_name_str.equals("") || email_str.equals("") || phone_str.equals("") || password_str.equals("") || confirm_str.equals("")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
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
            /** validate email **/
            if (!email_str.contains("@") || !email_str.contains(".com")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
                builder1.setMessage(R.string.validate_email);
                builder1.setCancelable(false);
                builder1.setPositiveButton(
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                /** validate phone number **/
                if (phone_str.length() < 8) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
                    builder1.setMessage(R.string.validate_phone_number);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    /** check if password is not less than 6 characters **/
                    if (password_str.length() >= 6 || confirm_str.length() >= 6) {
                        /** check if passwords match **/
                        if (password_str.equals(confirm_str)) {
                            if(Common.isConnectToTheInternet(this))
                                callRegisterApi(full_name_str,email_str,phone_str,password_str,confirm_str);
                            else
                                Common.errorConnectionMess(this);
                            // new RegisterBackgroundTask(User.this).execute();
                        } else {
                            /** passwords do not match **/
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
                            builder1.setMessage(R.string.passwords_do_not_match);
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
                        }
                    } else {
                        /** password is less than 6 characters **/
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
                        builder1.setMessage("Password must at least contain 6 characters.");
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
                    }
                }
            }
        }
    }

    private void callRegisterApi(String name, String email, String phone, String password, String confirm) {

        this.progressDialog.setMessage(getString(R.string.loading));
        this.progressDialog.show();


        compositeDisposable.add(Common.getAPI().register(name,email,phone,password,confirm)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<User>() {
                               @Override
                               public void accept(User user) throws Exception {
                                   progressDialog.dismiss();
                                   if (user.getStatus().equals("error")) {
                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(Register.this);
                                       builder1.setMessage(R.string.email_already_registered);
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
                                       Common.current_user = user;
                                       Paper.book("DarHaa").write("currentUser",Common.current_user);
                                       startActivity(new Intent(Register.this,MainActivity.class));
                                       finish();
                                   }
                               }
                           }));

    }



/*

    // *** User ***
    private class RegisterBackgroundTask extends AsyncTask<String, Void, String> {
        public ProgressDialog dialog;
        Boolean is_arabic = Common.isArabic;

        RegisterBackgroundTask(Activity activity) {
            this.dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signup_url = getString(R.string.register_api);
            try {
                URL url = new URL(signup_url);
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

                String str =  "{\"firstname\": \"" + full_name_str + "\", \"email\": \"" + email_str + "\", \"password\": \"" + password_str + "\", \"confirm\": \"" + confirm_str + "\", \"telephone\": \"" + phone_str + "\"}";
                byte[] outputInBytes = str.getBytes("UTF-8");

                Log.d("ststst", str);

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                OS.write(outputInBytes);
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

            SharedPreferences.Editor editor = getSharedPreferences("is_logged_in", MODE_PRIVATE).edit();

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("error")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(User.this);
                    builder1.setMessage(R.string.email_already_registered);
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

                    Intent intent = new Intent(User.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
  */
}
