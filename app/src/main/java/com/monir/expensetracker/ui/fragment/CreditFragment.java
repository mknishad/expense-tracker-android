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
import com.monir.expensetracker.database.CreditDataSource;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.ui.activity.CreditEditorActivity;
import com.monir.expensetracker.ui.adapter.CreditListAdapter;
import com.monir.expensetracker.util.Constant;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditFragment extends Fragment {

  private static final String TAG = CreditFragment.class.getSimpleName();

  //private static final int PERMISSION_CALLBACK_CONSTANT = 101;
  private static final int REQUEST_PERMISSION_SETTING = 102;
  private static final int OPEN_CREDIT_EDITOR_ACTIVITY = 103;

  private Context context;
  private List<Credit> creditList;
  private CreditDataSource creditDataSource;
  //private ProgressBar loadingCreditProgressBar;
  private CreditListAdapter creditListAdapter;
  private ListView creditListView;
  private TextView creditEmptyView;
  private View view;
  private TextView tvFooter;
  private TextView tvFooterCreditAmount;
  private TextView monthTextView;
  private ImageView previousImageView;
  private ImageView nextImageView;

  private Calendar today;

  private boolean sentToCreditEditor = false;

    /*private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private List<String> categoriesString = new ArrayList<>();*/

  // stopped here
  // http://www.androidhive.info/2016/11/android-working-marshmallow-m-runtime-permissions/

  public CreditFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Log.e(TAG, "onCreateView");
    getActivity().setTitle("Credit");
    context = getActivity();
    return view = inflater.inflate(R.layout.fragment_credit, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    //permissionStatus = getActivity().getSharedPreferences("permissionStatus", getActivity().MODE_PRIVATE);

    if (null != view) {
      creditListView = view.findViewById(R.id.lv_credits);
      creditEmptyView = view.findViewById(R.id.empty_view_credit);
      //loadingCreditProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading_credits);
      tvFooter = view.findViewById(R.id.tv_footer);
      tvFooterCreditAmount = view.findViewById(R.id.text_view_amount_credit);
      monthTextView = view.findViewById(R.id.monthTextView);
      previousImageView = view.findViewById(R.id.previousImageView);
      nextImageView = view.findViewById(R.id.nextImageView);

      creditListView.setEmptyView(creditEmptyView);
      creditListView.setTextFilterEnabled(false);

      creditDataSource = new CreditDataSource(getContext());
      today = Calendar.getInstance();

      FloatingActionButton fabCredit = view.findViewById(R.id.fab_credit);
      fabCredit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          sentToCreditEditor = true;
          Intent intent = new Intent(getContext(), CreditEditorActivity.class);
          intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
          startActivityForResult(intent, OPEN_CREDIT_EDITOR_ACTIVITY);
        }
      });

      creditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          //showCreditDetailInDialog(position);
          Log.e("CrditFragment", "----------------------position: " + position + " id: " + id);

          sentToCreditEditor = true;
          Intent intent = new Intent(getContext(), CreditEditorActivity.class);
          intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_EDIT);
          intent.putExtra(Constant.CREDIT_ITEM_ID, creditList.get(position).getCreditId());
          Log.e(TAG, "Clicked item id: " + id);
          startActivityForResult(intent, OPEN_CREDIT_EDITOR_ACTIVITY);
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
                  today.set(Calendar.MONTH, selectedMonth);
                  today.set(Calendar.YEAR, selectedYear);
                  loadCredits();
                }
              }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

          builder.setActivatedMonth(today.get(Calendar.MONTH))
              .setActivatedYear(today.get(Calendar.YEAR))
              .setTitle("Select Month")
              .build()
              .show();
        }
      });
      previousImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int month = today.get(Calendar.MONTH);
          int year = today.get(Calendar.YEAR);
          if (month == Calendar.JANUARY) {
            today.set(Calendar.MONTH, Calendar.DECEMBER);
            today.set(Calendar.YEAR, year - 1);
          } else {
            today.set(Calendar.MONTH, month - 1);
          }
          monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
              getMonthString(today.get(Calendar.MONTH)), today.get(Calendar.YEAR)));
          loadCredits();
        }
      });
      nextImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int month = today.get(Calendar.MONTH);
          int year = today.get(Calendar.YEAR);
          if (month == Calendar.getInstance().get(Calendar.MONTH)
              && year == Calendar.getInstance().get(Calendar.YEAR)) {
            return;
          }
          if (month == Calendar.DECEMBER) {
            today.set(Calendar.MONTH, Calendar.JANUARY);
            today.set(Calendar.YEAR, year + 1);
          } else {
            today.set(Calendar.MONTH, month + 1);
          }
          monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
              getMonthString(today.get(Calendar.MONTH)), today.get(Calendar.YEAR)));
          loadCredits();
        }
      });

      monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
          getMonthString(today.get(Calendar.MONTH)), today.get(Calendar.YEAR)));

      loadCredits();
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
    if (requestCode == OPEN_CREDIT_EDITOR_ACTIVITY) {
      loadCredits();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.e(TAG, "onResume");

    if (sentToCreditEditor) {
      sentToCreditEditor = false;
      loadCredits();
    }
  }

  private void loadCredits() {
    creditListView.setAdapter(new CreditListAdapter(context, new ArrayList<Credit>()));

    try {
      creditList = creditDataSource.getCreditsByMonth(today.get(Calendar.MONTH) + 1,
          today.get(Calendar.YEAR));
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (creditList.size() == 0) {
      creditEmptyView.setVisibility(View.VISIBLE);
      tvFooter.setVisibility(View.GONE);
      tvFooterCreditAmount.setVisibility(View.GONE);
    } else {
      Log.e(TAG, "creditList size: " + creditList.size());
      creditListAdapter = new CreditListAdapter(context, creditList);
      creditListView.setAdapter(creditListAdapter);
      tvFooter.setVisibility(View.VISIBLE);
      tvFooterCreditAmount.setVisibility(View.VISIBLE);
      tvFooterCreditAmount.setText(String.valueOf(creditDataSource.getTotalCreditAmountByMonth(
          today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR))));
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
        loadCredits();
        return true;
      }
    });
    SearchView searchView = (SearchView) searchItem.getActionView();

    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String s) {
            if (TextUtils.isEmpty(s)) {
              creditListView.clearTextFilter();
            } else {
              creditListView.setFilterText(s);
            }
            return false;
          }

          @Override
          public boolean onQueryTextChange(String s) {
            Filter filter = creditListAdapter.getFilter();
            filter.filter(s);
            return true;
          }
        }
    );
  }
}
