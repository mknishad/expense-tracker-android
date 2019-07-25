package com.monir.expensetracker.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.monir.expensetracker.R;
import com.monir.expensetracker.ui.fragment.AboutFragment;
import com.monir.expensetracker.ui.fragment.CreditFragment;
import com.monir.expensetracker.ui.fragment.CreditStatisticsFragment;
import com.monir.expensetracker.ui.fragment.DebitFragment;
import com.monir.expensetracker.ui.fragment.HelpFragment;
import com.monir.expensetracker.ui.fragment.HomeFragment;
import com.monir.expensetracker.ui.fragment.DebitStatisticsFragment;
import com.monir.expensetracker.util.CalendarCollection;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FragmentManager fragmentManager;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        //Add the calendar list
        CalendarCollection.calendarCollections = new ArrayList<>();
        CalendarCollection.calendarCollections.add(new CalendarCollection("2019-05-01", "John Birthday"));
        CalendarCollection.calendarCollections.add(new CalendarCollection("2015-04-04", "Client Meeting at 5 p.m."));
        CalendarCollection.calendarCollections.add(new CalendarCollection("2015-04-06", "A Small Party at my office"));
        CalendarCollection.calendarCollections.add(new CalendarCollection("2015-05-02", "Marriage Anniversary"));
        CalendarCollection.calendarCollections.add(new CalendarCollection("2015-04-11", "Live Event and Concert of sonu"));


        // set debit as home fragment
        setFragment(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setFragment(id);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(int id) {
        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, homeFragment,
                    homeFragment.getTag()).commit();
        } else if (id == R.id.nav_debit) {
            DebitFragment debitFragment = new DebitFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, debitFragment,
                    debitFragment.getTag()).commit();
        } else if (id == R.id.nav_credit) {
            CreditFragment creditFragment = new CreditFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, creditFragment,
                    creditFragment.getTag()).commit();
        } else if (id == R.id.nav_statistics) {
            CreditStatisticsFragment debitStatisticsFragment = new CreditStatisticsFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, debitStatisticsFragment,
                    debitStatisticsFragment.getTag()).commit();
        }/* else if (id == R.id.nav_history) {
            HistoryFragment historyFragment = new HistoryFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, historyFragment,
            historyFragment.getTag()).commit();
        }*/ else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, aboutFragment,
                    aboutFragment.getTag()).commit();
        } else if (id == R.id.nav_help) {
            HelpFragment helpFragment = new HelpFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, helpFragment,
                    helpFragment.getTag()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (checkNavigationMenuItem() != 0) {
            navigationView.setCheckedItem(R.id.nav_home);
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.rlContent, homeFragment, homeFragment.getTag()).commit();
        } else
            super.onBackPressed();
    }

    private int checkNavigationMenuItem() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isChecked())
                return i;
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
