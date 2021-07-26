package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.model.Debit;
import com.monir.expensetracker.ui.activity.DebitEditorActivity;
import com.monir.expensetracker.ui.adapter.CreditListAdapter;
import com.monir.expensetracker.ui.adapter.DebitListAdapter;
import com.monir.expensetracker.util.Constant;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class DebitFragment extends Fragment {

  private static final String TAG = DebitFragment.class.getSimpleName();

  private static final int OPEN_DEBIT_EDITOR_ACTIVITY = 203;

  private Context context;
  private List<Debit> debitList;
  private DebitDataSource debitDataSource;
  private DebitListAdapter debitListAdapter;
  private ListView debitListView;
  private TextView debitEmptyView;
  private View view;
  private TextView tvFooter;
  private TextView tvFooterDebitAmount;
  private TextView monthTextView;
  private ImageView previousImageView;
  private ImageView nextImageView;

  private Calendar calendar;

  public DebitFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView");
    getActivity().setTitle("Debit");
    context = getActivity();
    return view = inflater.inflate(R.layout.fragment_debit, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (null != view) {
      debitListView = view.findViewById(R.id.lv_debits);
      debitEmptyView = view.findViewById(R.id.empty_view_debit);
      //Log.e(TAG, "footer amount text view initialized");
      tvFooter = view.findViewById(R.id.tv_footer);
      tvFooterDebitAmount = view.findViewById(R.id.text_view_amount_debit);
      monthTextView = view.findViewById(R.id.monthTextView);
      previousImageView = view.findViewById(R.id.previousImageView);
      nextImageView = view.findViewById(R.id.nextImageView);

      debitListView.setEmptyView(debitEmptyView);
      debitListView.setTextFilterEnabled(false);

      debitDataSource = new DebitDataSource(getContext());
      calendar = Calendar.getInstance();

      FloatingActionButton fabDebit = view.findViewById(R.id.fab_debit);
      fabDebit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(getContext(), DebitEditorActivity.class);
          intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
          startActivityForResult(intent, OPEN_DEBIT_EDITOR_ACTIVITY);
        }
      });

      debitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          Intent intent = new Intent(getContext(), DebitEditorActivity.class);
          intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_EDIT);
          intent.putExtra(Constant.DEBIT_ITEM_ID, debitList.get(position).getDebitId());
          Log.e(TAG, "Clicked item id: " + id);
          startActivityForResult(intent, OPEN_DEBIT_EDITOR_ACTIVITY);
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
                  loadDebits();
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
          loadDebits();
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
          loadDebits();
        }
      });

      monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
          getMonthString(calendar.get(Calendar.MONTH)), calendar.get(Calendar.YEAR)));

      loadDebits();
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == OPEN_DEBIT_EDITOR_ACTIVITY && resultCode == RESULT_OK) {
      loadDebits();
    }
  }

  private void loadDebits() {
    debitListView.setAdapter(new CreditListAdapter(context, new ArrayList<Credit>()));

    try {
      debitList = debitDataSource.getDebitsByMonth(calendar.get(Calendar.MONTH) + 1,
          calendar.get(Calendar.YEAR));
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (debitList.size() == 0) {
      debitEmptyView.setVisibility(View.VISIBLE);
      tvFooter.setVisibility(View.GONE);
      tvFooterDebitAmount.setVisibility(View.GONE);
    } else {
      Log.e(TAG, "debitList size: " + debitList.size());
      debitListAdapter = new DebitListAdapter(context, debitList);
      debitListView.setAdapter(debitListAdapter);
      tvFooter.setVisibility(View.VISIBLE);
      tvFooterDebitAmount.setVisibility(View.VISIBLE);
      tvFooterDebitAmount.setText(String.valueOf(debitDataSource.getTotalDebitAmountByMonth(
          calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))));
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    getActivity().getMenuInflater().inflate(R.menu.menu_list, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
      @Override
      public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      @Override
      public boolean onMenuItemActionCollapse(MenuItem item) {
        loadDebits();
        return true;
      }
    });
    SearchView searchView = (SearchView) searchItem.getActionView();

    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String s) {
            if (TextUtils.isEmpty(s)) {
              debitListView.clearTextFilter();
            } else {
              debitListView.setFilterText(s);
            }
            return false;
          }

          @Override
          public boolean onQueryTextChange(String s) {
            Filter filter = debitListAdapter.getFilter();
            filter.filter(s);
            return true;
          }
        }
    );
  }
}
