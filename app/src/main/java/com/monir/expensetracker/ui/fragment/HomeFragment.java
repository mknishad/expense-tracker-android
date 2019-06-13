package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.monir.expensetracker.R;
import com.monir.expensetracker.database.ExpenseDataSource;
import com.monir.expensetracker.ui.activity.CreditEditorActivity;
import com.monir.expensetracker.ui.activity.DebitEditorActivity;
import com.monir.expensetracker.util.Constant;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Context context;
    private View view;

    private float[] yData;
    private PieChart pieChart;
    private TextView balanceTextView;
    private TextView creditTextView;
    private TextView debitTextView;

    private ExpenseDataSource expenseDataSource;

    private double creditAmount;
    private double debitAmount;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        initViews(view);
        showData();

        return view;
    }

    private void init() {
        context = getActivity();
        getActivity().setTitle("Home");
        expenseDataSource = new ExpenseDataSource(context);
    }

    private void initViews(View view) {
        pieChart = view.findViewById(R.id.pieChart);
        balanceTextView = view.findViewById(R.id.balanceTextView);
        creditTextView = view.findViewById(R.id.creditTextView);
        debitTextView = view.findViewById(R.id.debitTextView);
        view.findViewById(R.id.addCreditImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreditEditorActivity.class);
                intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.addDebitImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DebitEditorActivity.class);
                intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
                startActivity(intent);
            }
        });
    }

    private void showData() {
        creditAmount = expenseDataSource.getTotalCreditAmount();
        debitAmount = expenseDataSource.getTotalDebitAmount();
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
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        yEntrys.add(new PieEntry((float) creditAmount, getString(R.string.credit)));
        yEntrys.add(new PieEntry((float) (debitAmount + 200000.0), getString(R.string.debit)));

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");

        //add colors to data set
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ResourcesCompat.getColor(getResources(), android.R.color.holo_green_light, null));
        colors.add(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, null));

        pieDataSet.setColors(colors);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
