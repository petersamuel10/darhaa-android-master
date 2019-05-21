package com.itsoluations.vavisa.darhaa;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.itsoluations.vavisa.darhaa.common.Common;
import com.itsoluations.vavisa.darhaa.fargments.Category;
import com.itsoluations.vavisa.darhaa.fargments.ContactUS;
import com.itsoluations.vavisa.darhaa.fargments.Favourite;
import com.itsoluations.vavisa.darhaa.fargments.Home;
import com.itsoluations.vavisa.darhaa.fargments.Profile;

import java.util.Locale;

import butterknife.ButterKnife;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    //@BindView(R.id.action_search)
    // ImageView ic_search;


    Fragment fragment = null;
    TextView titleTxt;
    MenuItem mSearch, mCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocal();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        titleTxt = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        //for get context to show message when error in connection
        Common.mActivity = this;

        //init paper
        Paper.init(this);

        chooseFragment(new Home());
        titleTxt.setText(getResources().getText(R.string.home));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.action_search);
    }


    public void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Paper.book("DarHaa").write("language", lang);

    }

    public void loadLocal() {
        Paper.init(this);
        String lan = Paper.book("DarHaa").read("language");
        if (!TextUtils.isEmpty(lan)) {
            setLanguage(lan);
            if (lan.contentEquals("ar"))
                Common.isArabic = true;

            checkAuthontication();
        } else {
            startActivity(new Intent(MainActivity.this, ChooseLanguage.class));
            finish();
        }
    }

    private void checkAuthontication() {
        if (Paper.book("DarHaa").contains("currentUser")) {
            Common.current_user = Paper.book("DarHaa").read("currentUser");
            // startActivity(new Intent(this,MainActivity.class));
        } else {
            if (Paper.book("DarHaa").contains("isSkip")) {
                // stay home
            } else {
                startActivity(new Intent(MainActivity.this, RegisterLogin.class));
                finish();
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            this.finish();
            System.exit(0);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearch = menu.findItem(R.id.action_search);

        mCart = menu.findItem(R.id.action_cart);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        //mSearchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeData/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            startActivity(new Intent(this, Cart.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new Home();
            //  mSearch.setVisible(false);
            titleTxt.setPaddingRelative(0, 0, 24, 0);
        } else if (id == R.id.nav_category) {
            fragment = new Category();
            // mSearch.setVisible(true);
            titleTxt.setPaddingRelative(8, 0, 0, 0);
        } else if (id == R.id.nav_favourite) {
            fragment = new Favourite();
            //  mSearch.setVisible(false);
            titleTxt.setPaddingRelative(0, 0, 24, 0);
        } else if (id == R.id.nav_profile) {
            fragment = new Profile();
            //mSearch.setVisible(false);
            titleTxt.setPaddingRelative(0, 0, 24, 0);
        } else if (id == R.id.nav_support) {
            fragment = new ContactUS();
            // mSearch.setVisible(false);
            titleTxt.setPaddingRelative(0, 0, 24, 0);
        }

        // item.setChecked(true);

        titleTxt.setText(item.getTitle());

        chooseFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void chooseFragment(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

}
