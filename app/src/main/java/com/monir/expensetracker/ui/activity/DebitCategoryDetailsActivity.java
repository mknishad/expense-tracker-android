package com.monir.expensetracker.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.model.Debit;
import com.monir.expensetracker.ui.adapter.ExpandableDebitListAdapter;
import com.monir.expensetracker.util.Constant;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DebitCategoryDetailsActivity extends AppCompatActivity {

  private static final String TAG = "DebitCategoryDetailsAct";
  private static final int OPEN_DEBIT_EDITOR_ACTIVITY = 204;

  private Context context;
  private Toolbar toolbar;
  private ProgressBar progressBar;
  private ExpandableListView expListView;
  private ExpandableDebitListAdapter listAdapter;
  private DebitDataSource debitDataSource;
  private List<String> listDataHeader;
  private HashMap<String, List<Debit>> listDataChild;
  private TextView monthTextView;
  private ImageView previousImageView;
  private ImageView nextImageView;
  private TextView tvFooter;

  private Calendar calendar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_category_details);

    init();
  }

  private void init() {
    context = DebitCategoryDetailsActivity.this;
    debitDataSource = new DebitDataSource(context);
    calendar = Calendar.getInstance();

    initViews();
  }

  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    progressBar = findViewById(R.id.progressBar);
    expListView = findViewById(R.id.expandableListView);
    monthTextView = findViewById(R.id.monthTextView);
    previousImageView = findViewById(R.id.previousImageView);
    nextImageView = findViewById(R.id.nextImageView);
    tvFooter = findViewById(R.id.tv_footer);
    initToolbar();

    expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
      @Override
      public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Debit debit = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
        //Toast.makeText(DebitCategoryDetailsActivity.this, debit.getDebitCategory() + " " + debit.getDebitDescription(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(DebitCategoryDetailsActivity.this, DebitEditorActivity.class);
        intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_EDIT);
        intent.putExtra(Constant.DEBIT_ITEM_ID, debit.getDebitId());
        Log.e(TAG, "Clicked item id: " + id);
        startActivityForResult(intent, OPEN_DEBIT_EDITOR_ACTIVITY);

        return false;
      }
    });
    monthTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(context,
            new MonthPickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(int selectedMonth, int selectedYear) {
                /* on date set */
                Log.d(TAG, "selectedMonth = " + selectedMonth +
                    " selectedYear = " + selectedYear);
                monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
                    getMonthString(selectedMonth), selectedYear));
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.YEAR, selectedYear);
                loadData();
              }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

        builder.setActivatedMonth(calendar.get(Calendar.MONTH))
            .setActivatedYear(calendar.get(Calendar.YEAR))
            .setTitle("Select Month")
            .build()
            .show();
      }
    });
    previousImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (month == Calendar.JANUARY) {
          calendar.set(Calendar.MONTH, Calendar.DECEMBER);
          calendar.set(Calendar.YEAR, year - 1);
        } else {
          calendar.set(Calendar.MONTH, month - 1);
        }
        monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
            getMonthString(calendar.get(Calendar.MONTH)), calendar.get(Calendar.YEAR)));
        loadData();
      }
    });
    nextImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (month == Calendar.getInstance().get(Calendar.MONTH)
            && year == Calendar.getInstance().get(Calendar.YEAR)) {
          return;
        }
        if (month == Calendar.DECEMBER) {
          calendar.set(Calendar.MONTH, Calendar.JANUARY);
          calendar.set(Calendar.YEAR, year + 1);
        } else {
          calendar.set(Calendar.MONTH, month + 1);
        }
        monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
            getMonthString(calendar.get(Calendar.MONTH)), calendar.get(Calendar.YEAR)));
        loadData();
      }
    });

    monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
        getMonthString(calendar.get(Calendar.MONTH)), calendar.get(Calendar.YEAR)));

    loadData();
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

  private void loadData() {
    progressBar.setVisibility(View.VISIBLE);
    prepareListData();
    listAdapter = new ExpandableDebitListAdapter(this, listDataHeader, listDataChild, calendar);
    expListView.setAdapter(listAdapter);
    progressBar.setVisibility(View.GONE);
  }

  /*
   * Preparing the list data
   */
  private void prepareListData() {
    String[] categories = getResources().getStringArray(R.array.debit_categories);
    listDataHeader = new LinkedList<>(Arrays.asList(categories));
    listDataHeader.remove(0);

    listDataChild = new HashMap<>();
    for (String category : listDataHeader) {
      List<Debit> debits = debitDataSource.getDebitsByCategoryAndMonth(category,
          calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
      listDataChild.put(category, debits);
    }

    double totalDebit = 0;
    for (Map.Entry<String, List<Debit>> entry : listDataChild.entrySet()) {
      if (entry.getValue().size() == 0) {
        listDataHeader.remove(entry.getKey());
      }
      for (Debit d : entry.getValue()) {
        totalDebit += d.getDebitAmount();
      }
    }

    if (totalDebit > 0) {
      tvFooter.setVisibility(View.VISIBLE);
      tvFooter.setText(String.format(Locale.getDefault(), "Total: %.2f", totalDebit));
    } else {
      tvFooter.setVisibility(View.GONE);
    }
  }

  private String getMonthString(int i) {
    String month;
    switch (i) {
      case 0:
        month = "Jan";
        break;
      case 1:
        month = "Feb";
        break;
      case 2:
        month = "Mar";
        break;
      case 3:
        month = "Apr";
        break;
      case 4:
        month = "May";
        break;
      case 5:
        month = "Jun";
        break;
      case 6:
        month = "Jul";
        break;
      case 7:
        month = "Aug";
        break;
      case 8:
        month = "Sep";
        break;
      case 9:
        month = "Oct";
        break;
      case 10:
        month = "Nov";
        break;
      case 11:
        month = "Dec";
        break;
      default:
        month = "";
    }
    return month;
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_OK);
    finish();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == OPEN_DEBIT_EDITOR_ACTIVITY && resultCode == RESULT_OK) {
      loadData();
    }
  }
}
