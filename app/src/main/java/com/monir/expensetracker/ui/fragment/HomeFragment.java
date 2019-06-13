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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.monir.expensetracker.R;
import com.monir.expensetracker.ui.activity.CreditEditorActivity;
import com.monir.expensetracker.ui.activity.DebitEditorActivity;
import com.monir.expensetracker.util.Constant;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Context context;
    private View view;

    private float[] yData = {2000f, 1000f};
    PieChart pieChart;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        getActivity().setTitle("Home");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        pieChart = view.findViewById(R.id.pieChart);

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

        //Description description = new Description();
        //description.setText("Sales by employee (In Thousands $) ");
        //pieChart.setDescription(description);
        //pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        //pieChart.setTransparentCircleAlpha(0);
        //pieChart.setCenterText("Super Cool Chart");
        //pieChart.setCenterTextSize(10);
        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(20);
        //More options just check out the documentation!

        pieChart.getDescription().setEnabled(false);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(25f);

        addDataSet();

        /*pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("(sum): ");
                String sales = e.toString().substring(pos1 + 7);

                for(int i = 0; i < yData.length; i++){
                    if(yData[i] == Float.parseFloat(sales)){
                        pos1 = i;
                        break;
                    }
                }
                String employee = xData[pos1 + 1];
                Toast.makeText(getActivity(), "Employee " + employee + "\n" + "Sales: $" + sales + "K", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });*/

        return view;
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        //ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] , i));
        }

        /*for(int i = 1; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }*/

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        //pieDataSet.setSliceSpace(2);
        //pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ResourcesCompat.getColor(getResources(), android.R.color.holo_green_light, null));
        colors.add(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, null));

        pieDataSet.setColors(colors);

        //add legend to chart
        //Legend legend = pieChart.getLegend();
        //legend.setForm(Legend.LegendForm.CIRCLE);
        //legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
