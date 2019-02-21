package com.itsoluation.vavisa.darhaa.profile_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.Common;
import com.itsoluation.vavisa.darhaa.view_setting.Filter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class Terms extends AppCompatActivity {

    @BindView(R.id.wb_terms)
    WebView webView_terms;

    @BindView(R.id.back_arrow)
    ImageView back_arrow;

    @OnClick(R.id.back_arrow)
    public void setBack(){onBackPressed();}

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_terms);
        progressDialog = new ProgressDialog(getBaseContext());

        ButterKnife.bind(this);

        if(Common.isArabic) {
            back_arrow.setRotation(180);
        }

    getData();
}

    private void getData() {
        if(Common.isConnectToTheInternet(this)) {
//            progressDialog.show();
            ///
        }else
            errorConnectionMess();
    }
/*
    private void bindData(PageData pageData) {
        if(Common.isArabic){
            String head = "<head><style type='text/css'>@font-face {font-family: 'arial';src: url('file:///android_asset/AvenirLTStd-Book.otf');}body {font-family: 'verdana';text-align: justify;background-color:#00000000;}</style></head>";
            String myHtmlString = "<html>" + head +
                    "<body style=\"font-family: arial\">" + pageData.getContentAR() + "</body></html>";
            webView_terms.loadDataWithBaseURL("", myHtmlString, "text/html", "utf-8", "");
        }else {
            String head = "<head><style type='text/css'>@font-face {font-family: 'arial';src: url('file:///android_asset/AvenirLTStd-Book.otf');}body {font-family: 'verdana';text-align: justify;background-color:#00000000;}</style></head>";
            String myHtmlString = "<html>" + head +
                    "<body style=\"font-family: arial\">" + pageData.getContentEN() + "</body></html>";
            webView_terms.loadDataWithBaseURL("", myHtmlString, "text/html", "utf-8", "");
        }
    }
*/
    public void errorConnectionMess(){

        AlertDialog.Builder error = new AlertDialog.Builder(this);
        error.setMessage(R.string.error_connection);
        AlertDialog dialog = error.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
