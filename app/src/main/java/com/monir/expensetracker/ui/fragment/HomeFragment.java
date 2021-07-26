package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.monir.expensetracker.R;
import com.monir.expensetracker.database.CreditDataSource;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.ui.activity.CreditCategoryDetailsActivity;
import com.monir.expensetracker.ui.activity.CreditEditorActivity;
import com.monir.expensetracker.ui.activity.DebitCategoryDetailsActivity;
import com.monir.expensetracker.ui.activity.DebitEditorActivity;
import com.monir.expensetracker.util.Constant;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

  private static final String TAG = "HomeFragment";

  private static final int RC_ACTIVITY_CREDIT = 1111;
  private static final int RC_ACTIVITY_DEBIT = 1112;
  private static final int RC_ACTIVITY_CATEGORY = 1113;

  private Context context;
  private View view;

  private PieChart pieChart;
  private TextView balanceTextView;
  private TextView creditTextView;
  private TextView debitTextView;
  private RelativeLayout creditLayout;
  private RelativeLayout debitLayout;
  private ImageView addCreditImageView;
  private ImageView addDebitImageView;
  private TextView monthTextView;
  private ImageView previousImageView;
  private ImageView nextImageView;

  private Calendar calendar;
  private DebitDataSource debitDataSource;
  private CreditDataSource creditDataSource;

  private double creditAmount;
  private double debitAmount;

  public HomeFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    getActivity().setTitle(R.string.app_name);
    view = inflater.inflate(R.layout.fragment_home, container, false);

    init();
    initViews(view);
    showData(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

    return view;
  }

  private void init() {
    context = getActivity();
    //getActivity().setTitle("Home");
    debitDataSource = new DebitDataSource(context);
    creditDataSource = new CreditDataSource(context);
    calendar = Calendar.getInstance();
  }

  private void initViews(View view) {
    pieChart = view.findViewById(R.id.pieChart);
    balanceTextView = view.findViewById(R.id.balanceTextView);
    creditTextView = view.findViewById(R.id.creditTextView);
    debitTextView = view.findViewById(R.id.debitTextView);
    creditLayout = view.findViewById(R.id.creditLayout);
    debitLayout = view.findViewById(R.id.debitLayout);
    addCreditImageView = view.findViewById(R.id.addCreditImageView);
    addDebitImageView = view.findViewById(R.id.addDebitImageView);
    monthTextView = view.findViewById(R.id.monthTextView);
    previousImageView = view.findViewById(R.id.previousImageView);
    nextImageView = view.findViewById(R.id.nextImageView);

    creditLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent creditCategoryIntent = new Intent(getActivity(), CreditCategoryDetailsActivity.class);
        startActivityForResult(creditCategoryIntent, RC_ACTIVITY_CATEGORY);
      }
    });
    debitLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent debitCategoryIntent = new Intent(getActivity(), DebitCategoryDetailsActivity.class);
        startActivityForResult(debitCategoryIntent, RC_ACTIVITY_CATEGORY);
      }
    });
    addCreditImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, CreditEditorActivity.class);
        intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
        startActivityForResult(intent, RC_ACTIVITY_CREDIT);
      }
    });
    addDebitImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, DebitEditorActivity.class);
        intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
        startActivityForResult(intent, RC_ACTIVITY_DEBIT);
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
                showData(selectedMonth, selectedYear);
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
        showData(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
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
        showData(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
      }
    });

    monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
        getMonthString(calendar.get(Calendar.MONTH)), calendar.get(Calendar.YEAR)));
  }

  private void showData(int month, int year) {
    creditAmount = creditDataSource.getTotalCreditAmountByMonth(month + 1, year);
    debitAmount = debitDataSource.getTotalDebitAmountByMonth(month + 1, year);
    balanceTextView.setText(String.format(Locale.getDefault(), "৳%.2f",
        (creditAmount - debitAmount)));
    creditTextView.setText(String.format(Locale.getDefault(), "৳%.2f", creditAmount));
    debitTextView.setText(String.format(Locale.getDefault(), "৳%.2f", debitAmount));

    showPieChart();
  }

  private void showPieChart() {
    pieChart.getDescription().setEnabled(false);
    pieChart.setTransparentCircleColor(Color.WHITE);
    pieChart.setTransparentCircleAlpha(110);
    pieChart.setTransparentCircleRadius(30f);
    pieChart.setHoleRadius(25f);
    pieChart.setDrawEntryLabels(false);

    addDataSet();

    pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
      @Override
      public void onValueSelected(Entry e, Highlight h) {
        PieEntry pe = (PieEntry) e;

        if (pe.getLabel().equalsIgnoreCase("debit")) {
          Intent debitCategoryDetailsIntent = new Intent(getActivity(), DebitCategoryDetailsActivity.class);
          startActivityForResult(debitCategoryDetailsIntent, RC_ACTIVITY_CATEGORY);
        } else {
          Intent creditCategoryIntent = new Intent(getActivity(), CreditCategoryDetailsActivity.class);
          startActivityForResult(creditCategoryIntent, RC_ACTIVITY_CATEGORY);
        }
      }

      @Override
      public void onNothingSelected() {

      }
    });
  }

  private void addDataSet() {
    Log.d(TAG, "addDataSet started");
    ArrayList<PieEntry> entries = new ArrayList<>();
    entries.add(new PieEntry((float) creditAmount, getString(R.string.credit)));
    entries.add(new PieEntry((float) debitAmount, getString(R.string.debit)));

    //create the data set
    PieDataSet pieDataSet = new PieDataSet(entries, "");

    //add colors to data set
    ArrayList<Integer> colors = new ArrayList<>();
    colors.add(ResourcesCompat.getColor(getResources(), android.R.color.holo_green_light, null));
    colors.add(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, null));

    pieDataSet.setColors(colors);

    Legend l = pieChart.getLegend();
    l.setForm(Legend.LegendForm.CIRCLE);

    //create pie data object
    PieData pieData = new PieData(pieDataSet);
    pieChart.setData(pieData);
    pieChart.invalidate();
    pieChart.animateY(2000);
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      showData(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }
  }
}
