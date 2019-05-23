package com.monir.expensetracker.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import com.monir.expensetracker.R;
import com.monir.expensetracker.constant.CalendarCollection;
import com.monir.expensetracker.fragments.AboutFragment;
import com.monir.expensetracker.fragments.BalanceFragment;
import com.monir.expensetracker.fragments.CreditFragment;
import com.monir.expensetracker.fragments.DebitFragment;
import com.monir.expensetracker.fragments.HistoryFragment;
import com.monir.expensetracker.fragments.HelpFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Add the calendar list

        CalendarCollection.date_collection_arr=new ArrayList<CalendarCollection>();
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2015-04-01","John Birthday"));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2015-04-04","Client Meeting at 5 p.m."));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2015-04-06","A Small Party at my office"));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2015-05-02","Marriage Anniversary"));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2015-04-11","Live Event and Concert of sonu"));


        // set debit as home fragment
        setFragment(R.id.nav_debit);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(int id){
        if (id == R.id.nav_debit) {
            // Handle the camera action

            DebitFragment debitFragment = new DebitFragment();
//            debitFragment.setContext(context);

            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rlContent,debitFragment,debitFragment.getTag()).commit();

        } else if (id == R.id.nav_credit) {

            CreditFragment creditFragment = new CreditFragment();
//            debitFragment.setContext(context);

            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rlContent,creditFragment,creditFragment.getTag()).commit();

        } else if (id == R.id.nav_balance) {

            BalanceFragment balanceFragment = new BalanceFragment();
//            debitFragment.setContext(context);

            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rlContent,balanceFragment,balanceFragment.getTag()).commit();

        } else if (id == R.id.nav_history) {

            HistoryFragment historyFragment = new HistoryFragment();
//            debitFragment.setContext(context);

            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rlContent, historyFragment, historyFragment.getTag()).commit();

            //} else if (id == R.id.nav_settings) {

//            DebitFragment debitFragment = new DebitFragment();
////            debitFragment.setContext(context);
//
//            manager = getSupportFragmentManager();
//            manager.beginTransaction().replace(R.id.rlContent,debitFragment,debitFragment.getTag()).commit();

        } else if (id == R.id.nav_about) {

            AboutFragment aboutFragment = new AboutFragment();
//            debitFragment.setContext(context);

            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rlContent,aboutFragment,aboutFragment.getTag()).commit();

        } else if (id == R.id.nav_help) {

            HelpFragment helpFragment = new HelpFragment();
//            debitFragment.setContext(context);

            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rlContent,helpFragment,helpFragment.getTag()).commit();

        }

    }

}
