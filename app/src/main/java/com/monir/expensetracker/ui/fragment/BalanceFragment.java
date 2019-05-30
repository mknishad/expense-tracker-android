package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.monir.expensetracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {


    private PieChart pie_chart_balance;

    private RadioGroup rg_balance_chart;

    private RadioGroup rg_balance_chart_monthly;

    private float[] yData; //= {2500.00f, 5000.00f, 10000.00f, 1923.00f, 2309.00f, 2300.00f, 4350.0f,4000.00f,2323.00f,1000.00f,1000.00f,1000.00f};
    private String[] xData; //= {"January", "February" , "March" , "April", "May", "June", "July","August","September","October","November","December"};

    private List<Double> yDataList;
    private List<Double> xDataList;

    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private Context context;

    private String[] descriptoins = {"Yearly Cost(In ৳)", "Monthly Cost(In ৳)"};

    private List<Integer> colorList = new ArrayList<>(Arrays.asList(Color.GRAY, Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.YELLOW,
            Color.MAGENTA, Color.DKGRAY, Color.LTGRAY, Color.CYAN, Color.RED, Color.GREEN));

//    colors.add(Color.GRAY);
//            colors.add(Color.BLUE);
//            colors.add(Color.RED);
//            colors.add(Color.GREEN);
//            colors.add(Color.CYAN);
//            colors.add(Color.YELLOW);
//            colors.add(Color.MAGENTA);
//            colors.add(Color.DKGRAY);
//            colors.add(Color.WHITE);
//            colors.add(Color.LTGRAY);
//            colors.add(Color.CYAN);
//            colors.add(Color.MAGENTA);


    public BalanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        this.context = getActivity().getApplicationContext();

        getActivity().setTitle("Balance");

        inits(view);

        return view;
    }

    private void inits(View view) {

        pie_chart_balance = (PieChart) view.findViewById(R.id.pie_chart_balance);

        rg_balance_chart = (RadioGroup) view.findViewById(R.id.rg_balance_chart);

        rg_balance_chart_monthly = (RadioGroup) view.findViewById(R.id.rg_balance_chart_monthly);

        xDataList = new ArrayList<>();
        yDataList = new ArrayList<>();


        //By Default selected yearly
        setPieChart(R.id.rb_yearly);

        //Change the pie chart on which radio button is click
        rg_balance_chart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                setPieChart(checkedId);
            }
        });

        //trace which radio button is clicked
        rg_balance_chart_monthly.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                setPieChart(R.id.rb_monthly);
            }
        });

        //if monthly radiobutton is selected then change the debit and credit
        pie_chart_balance.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                String hlight = h.toString();   //Highlight, x: 11.0, y: 1000.0, dataSetIndex: 0, stackIndex (only stacked barentry): -1

                String[] sCommas = hlight.split(","); // x: 11.0  and y: 1000.0

                String[] sX = sCommas[1].trim().split(" ");

                int index = (int) Float.parseFloat(sX[1].trim());

                Toast.makeText(context, xData[index] + "\n" + "Cost: ৳" + yData[index], Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setPieChart(int id) {

        if (id == R.id.rb_yearly) {

            pie_chart_balance.setDescription(descriptoins[0]);
            pie_chart_balance.setRotationEnabled(true);
            pie_chart_balance.setHoleRadius(25f);
            pie_chart_balance.setTransparentCircleAlpha(0);
            pie_chart_balance.setCenterText("Balance");
            pie_chart_balance.setCenterTextSize(10);
            rg_balance_chart_monthly.setVisibility(View.GONE);

        } else if (id == R.id.rb_monthly) {
            pie_chart_balance.setDescription(descriptoins[1]);
            pie_chart_balance.setRotationEnabled(true);
            pie_chart_balance.setHoleRadius(25f);
            pie_chart_balance.setTransparentCircleAlpha(0);
            pie_chart_balance.setCenterText("Balance");
            pie_chart_balance.setCenterTextSize(10);
            rg_balance_chart_monthly.setVisibility(View.VISIBLE);
        }
        setyData(id);
        setxData(id);

        addDataSet(id);
    }

    private void setxData(int id) {

        if (id == R.id.rb_yearly) {

            xData = new String[2];

            xData[0] = "Debit";
            xData[1] = "Credit";

        } else if (id == R.id.rb_monthly) {

            xData = new String[12];

            for (int i = 0; i < 12; i++) {

                xData[i] = months[i];

            }
        }

    }

    private void setyData(int id) {

        if (id == R.id.rb_yearly) {

            yData = new float[2];

            yData[0] = getDebitBalance();
            yData[1] = getCreditBalance();

        } else if (id == R.id.rb_monthly) {

            int mid = rg_balance_chart_monthly.getCheckedRadioButtonId();
            setyDataMonthlyDebitOrCredit(mid);

        }


    }

    private float getDebitBalance() {
        float debitBal = 300.00f;

        return debitBal;
    }

    private float getCreditBalance() {

        float creditBal = 200.00f;

        return creditBal;
    }


    private void setyDataMonthlyDebitOrCredit(int id) {

        yData = new float[12];
        for (int i = 0; i < 12; i++) {

            if (i <= 4)
                yData[i] = getMonthlyDebitOrCredit(id, i);
            else
                yData[i] = 0.0f;
        }
    }

    private float getMonthlyDebitOrCredit(int id, int month) {

        if (id == R.id.rb_debit)
            return 230.00f;
        else if (id == R.id.rb_credit) {
            return 120.0f;
        }
        return 0.0f;
    }

    private List<Integer> getColors(int id) {

        //add colors to dataset
        List<Integer> colors = new ArrayList<>();

        colors.clear();

        colors = colorList.subList(0, yData.length);

//        if(id == R.id.rb_yearly){
//
//            colors.add(Color.GRAY);
//            colors.add(Color.BLUE);
//        }
//
//        else if(id == R.id.rb_monthly) {
//            colors.add(Color.GRAY);
//            colors.add(Color.BLUE);
//            colors.add(Color.RED);
//            colors.add(Color.GREEN);
//            colors.add(Color.CYAN);
//            colors.add(Color.YELLOW);
//            colors.add(Color.MAGENTA);
//            colors.add(Color.DKGRAY);
//            colors.add(Color.WHITE);
//            colors.add(Color.LTGRAY);
//            colors.add(Color.CYAN);
//            colors.add(Color.MAGENTA);
//        }

        return colors;
    }


    private void addDataSet(int id) {

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length && i < xData.length; i++) {
            if (yData[i] > 0.0) {
                yEntrys.add(new PieEntry(yData[i], i));
                xEntrys.add(xData[i]);
            }
        }

//        for(int i = 0; i < xData.length; i++){
//            xEntrys.add(xData[i]);
//        }


        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Balance");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);


        pieDataSet.setColors(getColors(id));

        //add legend to chart
        Legend legend = pie_chart_balance.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pie_chart_balance.setData(pieData);
        pie_chart_balance.invalidate();
    }

//    private void addDataSet() {
//
//        ArrayList<PieEntry> yEntrys = new ArrayList<>();
//        ArrayList<String> xEntrys = new ArrayList<>();
//
//        for(int i = 0; i < yData.length; i++){
//            yEntrys.add(new PieEntry(yData[i] , i));
//        }
//
//        for(int i = 0; i < xData.length; i++){
//            xEntrys.add(xData[i]);
//        }
//
//        //create the data set
//        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Monthly Cost");
//        pieDataSet.setSliceSpace(2);
//        pieDataSet.setValueTextSize(12);
//
//        //add colors to dataset
//        ArrayList<Integer> colors = new ArrayList<>();
//        colors.add(Color.GRAY);
//        colors.add(Color.BLUE);
//        colors.add(Color.RED);
//        colors.add(Color.GREEN);
//        colors.add(Color.CYAN);
//        colors.add(Color.YELLOW);
//        colors.add(Color.MAGENTA);
//        colors.add(Color.DKGRAY);
//        colors.add(Color.WHITE);
//        colors.add(Color.LTGRAY);
//        colors.add(Color.CYAN);
//        colors.add(Color.MAGENTA);
//
//        pieDataSet.setColors(colors);
//
//        //add legend to chart
//        Legend legend = pie_chart_balance.getLegend();
//        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
//
//        //create pie data object
//        PieData pieData = new PieData(pieDataSet);
//        pie_chart_balance.setData(pieData);
//        pie_chart_balance.invalidate();
//    }

}
