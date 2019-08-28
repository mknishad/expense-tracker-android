package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.monir.expensetracker.R;
import com.monir.expensetracker.database.CreditDataSource;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.ui.barchart.MyValueFormatter;
import com.monir.expensetracker.ui.barchart.ValueFormatter;
import com.monir.expensetracker.ui.barchart.XYMarkerView;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditStatisticsFragment extends Fragment {

    private final String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    private Context context;
    private CreditDataSource dataSource;
    private Calendar calendar;
    private TextView yearTextView;
    private ImageView previousImageView;
    private ImageView nextImageView;
    private BarChart barChart;

    public CreditStatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_credit_statistics, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        context = getActivity();
        dataSource = new CreditDataSource(context);
        calendar = Calendar.getInstance();
        initViews(view);
        setData(calendar.get(Calendar.YEAR));
    }

    private void initViews(View view) {
        barChart = view.findViewById(R.id.barChart);
        yearTextView = view.findViewById(R.id.yearTextView);
        previousImageView = view.findViewById(R.id.previousImageView);
        nextImageView = view.findViewById(R.id.nextImageView);

        yearTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        yearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(context,
                        new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                yearTextView.setText(String.valueOf(selectedYear));
                                setData(selectedYear);
                                calendar.set(Calendar.YEAR, selectedYear);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                builder.setTitle("Select Year")
                        .setActivatedYear(calendar.get(Calendar.YEAR))
                        .showYearOnly()
                        .build()
                        .show();
            }
        });
        previousImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                calendar.set(Calendar.YEAR, year - 1);
                yearTextView.setText(String.valueOf(year - 1));
                setData(year - 1);
            }
        });
        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                    return;
                }
                calendar.set(Calendar.YEAR, year + 1);
                yearTextView.setText(String.valueOf(year + 1));
                setData(year + 1);
            }
        });

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public int getDecimalDigits() {
                return 0;
            }

            @Override
            public String getFormattedValue(float value) {
                return months[(int) (value - 1) % months.length];
            }
        };

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("৳");

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(context, xAxisFormatter);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the chart
    }

    private void setData(int year) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, (float) dataSource.getTotalCreditAmountByMonth(1, year)));
        entries.add(new BarEntry(2, (float) dataSource.getTotalCreditAmountByMonth(2, year)));
        entries.add(new BarEntry(3, (float) dataSource.getTotalCreditAmountByMonth(3, year)));
        entries.add(new BarEntry(4, (float) dataSource.getTotalCreditAmountByMonth(4, year)));
        entries.add(new BarEntry(5, (float) dataSource.getTotalCreditAmountByMonth(5, year)));
        entries.add(new BarEntry(6, (float) dataSource.getTotalCreditAmountByMonth(6, year)));
        entries.add(new BarEntry(7, (float) dataSource.getTotalCreditAmountByMonth(7, year)));
        entries.add(new BarEntry(8, (float) dataSource.getTotalCreditAmountByMonth(8, year)));
        entries.add(new BarEntry(9, (float) dataSource.getTotalCreditAmountByMonth(9, year)));
        entries.add(new BarEntry(10, (float) dataSource.getTotalCreditAmountByMonth(10, year)));
        entries.add(new BarEntry(11, (float) dataSource.getTotalCreditAmountByMonth(11, year)));
        entries.add(new BarEntry(12, (float) dataSource.getTotalCreditAmountByMonth(12, year)));

        BarDataSet barDataSet = new BarDataSet(entries, getResources().getString(R.string.year_credits, year));
        barDataSet.setColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
        ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
        iBarDataSets.add(barDataSet);
        BarData barData = new BarData(iBarDataSets);
        barChart.setData(barData);
        barChart.animateY(5000, Easing.EasingOption.EaseOutElastic);
    }
}
