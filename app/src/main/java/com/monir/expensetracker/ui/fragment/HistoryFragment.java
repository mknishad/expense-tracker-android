package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.model.Debit;
import com.monir.expensetracker.ui.adapter.CalendarAdapter;
import com.monir.expensetracker.ui.adapter.DebitListAdapter;
import com.monir.expensetracker.util.CalendarCollection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

  public GregorianCalendar cal_month, cal_month_copy;
  private CalendarAdapter cal_adapter;
  private TextView tv_month, tv_selected_date, tv_selected_date_amount;

  private TextView empty_view_his_debit;
  private ListView lv_daily_debit;
  private DebitDataSource debitDataSource;
  private Context context;


  public HistoryFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    getActivity().setTitle("History");

    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_detail, container, false);

    context = getActivity();
    debitDataSource = new DebitDataSource(context);
    cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
    cal_month_copy = (GregorianCalendar) cal_month.clone();
    cal_adapter = new CalendarAdapter(this.context, cal_month, CalendarCollection.calendarCollections);

    inits(view);

    return view;
  }

  private void inits(View view) {
    tv_month = view.findViewById(R.id.tv_month);
    tv_month.setText(DateFormat.format("MMMM yyyy", cal_month));
    tv_selected_date = view.findViewById(R.id.tv_selected_date);
    tv_selected_date_amount = view.findViewById(R.id.tv_selected_date_amount);
    empty_view_his_debit = view.findViewById(R.id.empty_view_his_debit);
    lv_daily_debit = view.findViewById(R.id.lv_daily_debit);
    String curentDateString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    tv_selected_date.setText(curentDateString);
    tv_selected_date_amount.setText(getAmount(curentDateString));
    lv_daily_debit.setEmptyView(empty_view_his_debit);
    setDailyDebitList(tv_selected_date.getText().toString());
    ImageButton previous = view.findViewById(R.id.ib_prev);
    previous.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setPreviousMonth();
        refreshCalendar();
      }
    });
    ImageButton next = view.findViewById(R.id.Ib_next);
    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNextMonth();
        refreshCalendar();
      }
    });
    GridView gridview = view.findViewById(R.id.gv_calendar);
    gridview.setAdapter(cal_adapter);
    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v,
                              int position, long id) {
        ((CalendarAdapter) parent.getAdapter()).setSelected(v, position);
        String selectedGridDate = CalendarAdapter.day_string
            .get(position);
        tv_selected_date.setText(selectedGridDate);
        tv_selected_date_amount.setText(getAmount(selectedGridDate));
        setDailyDebitList(tv_selected_date.getText().toString());
        String[] separatedTime = selectedGridDate.split("-");
        String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
        int gridvalue = Integer.parseInt(gridvalueString);
        if ((gridvalue > 10) && (position < 8)) {
          setPreviousMonth();
          refreshCalendar();
        } else if ((gridvalue < 7) && (position > 28)) {
          setNextMonth();
          refreshCalendar();
        }
        ((CalendarAdapter) parent.getAdapter()).setSelected(v, position);
        ((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, getActivity());
      }

    });
  }

  private void setDailyDebitList(String date) {
    List<Debit> debits = debitDataSource.getDebitsInThisDate(date);
//        if(date.equals("20-06-2017")){
//
//
//            String des = "Mongo 50\nBanana 90\nIftar 50";
//
//            Debit debit = new Debit(date,"Food",des,190.00);
//            Debit debit1 = new Debit(date,"Education",des,290.00);
//            Debit debit2 = new Debit(date,"Health",des,900.00);
//
//            debits.add(debit);
//            debits.add(debit1);
//            debits.add(debit2);
//        } else if(date.equals("21-06-2017")){
//
//            String des = "Mongo 50\nBanana 90\nIftar 50";
//
//            Debit debit = new Debit(date,"Food",des,150.00);
//            Debit debit1 = new Debit(date,"Education",des,160.00);
//            Debit debit2 = new Debit(date,"Health",des,100.00);
//
//            debits.add(debit);
//            debits.add(debit1);
//            debits.add(debit2);
//
//        }
    DebitListAdapter adapter = null;

    // Log.d("HistoryFragment", "setDailyDebitList: "+debits.size());

    if (debits != null && debits.size() > 0) {

      adapter = new DebitListAdapter(context, debits);

      lv_daily_debit.setAdapter(adapter);
      adapter.notifyDataSetChanged();

    } else {
      lv_daily_debit.setAdapter(adapter);
      //lv_daily_debit.setEmptyView(empty_view_his_debit);
    }


  }

  private String getAmount(String date) {
    String amount = "0";
    // amount fetch from db
    return amount;
  }


  protected void setNextMonth() {
    if (cal_month.get(GregorianCalendar.MONTH) == cal_month
        .getActualMaximum(GregorianCalendar.MONTH)) {
      cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1),
          cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
    } else {
      cal_month.set(GregorianCalendar.MONTH,
          cal_month.get(GregorianCalendar.MONTH) + 1);
    }
  }

  protected void setPreviousMonth() {
    if (cal_month.get(GregorianCalendar.MONTH) == cal_month
        .getActualMinimum(GregorianCalendar.MONTH)) {
      cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1),
          cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
    } else {
      cal_month.set(GregorianCalendar.MONTH,
          cal_month.get(GregorianCalendar.MONTH) - 1);
    }
  }

  public void refreshCalendar() {
    cal_adapter.refreshDays();
    cal_adapter.notifyDataSetChanged();
    tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
  }
}
