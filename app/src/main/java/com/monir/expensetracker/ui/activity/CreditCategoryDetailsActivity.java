package com.monir.expensetracker.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.ExpenseDataSource;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.ui.adapter.ExpandableCreditListAdapter;
import com.monir.expensetracker.util.Constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreditCategoryDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CreditCategoryDetailsAc";
    private static final int OPEN_CREDIT_EDITOR_ACTIVITY = 205;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ExpandableListView expListView;
    private ExpandableCreditListAdapter listAdapter;
    private ExpenseDataSource expenseDataSource;
    private List<String> listDataHeader;
    private HashMap<String, List<Credit>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        init();
    }

    private void init() {
        expenseDataSource = new ExpenseDataSource(this);

        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        expListView = findViewById(R.id.expandableListView);
        initToolbar();
        populateListView();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Credit credit = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                //Toast.makeText(DebitCategoryDetailsActivity.this, debit.getDebitCategory() + " " + debit.getDebitDescription(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreditCategoryDetailsActivity.this, CreditEditorActivity.class);
                intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_EDIT);
                intent.putExtra(Constant.CREDIT_ITEM_ID, credit.getCreditId());
                Log.e(TAG, "Clicked item id: " + id);
                startActivityForResult(intent, OPEN_CREDIT_EDITOR_ACTIVITY);

                return false;
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        toolbar.setTitle(R.string.debit_categories);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void populateListView() {
        progressBar.setVisibility(View.VISIBLE);
        prepareListData();
        listAdapter = new ExpandableCreditListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        progressBar.setVisibility(View.GONE);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        String[] categories = getResources().getStringArray(R.array.credit_categories);
        listDataHeader = new LinkedList<>(Arrays.asList(categories));
        listDataHeader.remove(0);

        listDataChild = new HashMap<>();
        for (String category : listDataHeader) {
            List<Credit> credits = expenseDataSource.getCreditsByCategory(category);
            listDataChild.put(category, credits);
        }

        for (Map.Entry<String, List<Credit>> entry : listDataChild.entrySet()) {
            if (entry.getValue().size() == 0) {
                listDataHeader.remove(entry.getKey());
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CREDIT_EDITOR_ACTIVITY && resultCode == RESULT_OK) {
            populateListView();
        }
    }
}
