package com.monir.expensetracker.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.monir.expensetracker.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {


    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        initViews(view);
    }

    private void initViews(View view) {
        BarChart barChart = view.findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(8f, 0));
        entries.add(new BarEntry(2f, 1));
        entries.add(new BarEntry(5f, 2));
        entries.add(new BarEntry(20f, 3));
        entries.add(new BarEntry(15f, 4));
        entries.add(new BarEntry(19f, 5));
        entries.add(new BarEntry(8f, 6));
        entries.add(new BarEntry(2f, 7));
        entries.add(new BarEntry(5f, 8));
        entries.add(new BarEntry(20f, 9));
        entries.add(new BarEntry(15f, 10));
        entries.add(new BarEntry(19f, 11));

        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Dec");
        labels.add("Nov");
        labels.add("Oct");
        labels.add("Sep");
        labels.add("Aug");
        labels.add("Jul");
        labels.add("Jun");
        labels.add("May");
        labels.add("Apr");
        labels.add("Mar");
        labels.add("Feb");
        labels.add("Jan");

        BarDataSet barDataSet = new BarDataSet(entries, "Expenditure for the year 2018");
        ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
        iBarDataSets.add(barDataSet);
        BarData barData = new BarData(iBarDataSets);
        barChart.setData(barData);
        barChart.animateY(5000);

        /*BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of lables into chart

        barChart.setDescription("Expenditure for the year 2018");  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.animateY(5000);*/
    }
}
