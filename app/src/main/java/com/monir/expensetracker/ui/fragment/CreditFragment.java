package com.monir.expensetracker.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
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
import com.monir.expensetracker.database.ExpenseDataSource;
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
    private ExpenseDataSource expenseDataSource;
    //private ProgressBar loadingCreditProgressBar;
    private CreditListAdapter creditListAdapter;
    private ListView creditListView;
    private TextView creditEmptyView;
    private View view;
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
            creditListView = (ListView) view.findViewById(R.id.lv_credits);
            creditEmptyView = (TextView) view.findViewById(R.id.empty_view_credit);
            //loadingCreditProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading_credits);
            tvFooterCreditAmount = (TextView) view.findViewById(R.id.text_view_amount_credit);
            monthTextView = view.findViewById(R.id.monthTextView);
            previousImageView = view.findViewById(R.id.previousImageView);
            nextImageView = view.findViewById(R.id.nextImageView);

            creditListView.setEmptyView(creditEmptyView);
            creditListView.setTextFilterEnabled(false);

            expenseDataSource = new ExpenseDataSource(getContext());
            today = Calendar.getInstance();

            FloatingActionButton fabCredit = (FloatingActionButton) view.findViewById(R.id.fab_credit);
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
                                    loadCredits(selectedMonth, selectedYear);
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
                    loadCredits(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
                }
            });
            nextImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int month = today.get(Calendar.MONTH);
                    int year = today.get(Calendar.YEAR);
                    if (month == Calendar.DECEMBER) {
                        today.set(Calendar.MONTH, Calendar.JANUARY);
                        today.set(Calendar.YEAR, year + 1);
                    } else {
                        today.set(Calendar.MONTH, month + 1);
                    }
                    monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
                            getMonthString(today.get(Calendar.MONTH)), today.get(Calendar.YEAR)));
                    loadCredits(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
                }
            });

            monthTextView.setText(String.format(Locale.getDefault(), "%s, %d",
                    getMonthString(today.get(Calendar.MONTH)), today.get(Calendar.YEAR)));

            loadCredits(today.get(Calendar.MONTH), today.get(Calendar.YEAR));

            /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_SMS)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Need Permission");
                    builder.setMessage("This app needs SMS permission.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSION_CALLBACK_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.READ_SMS, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Need Permission");
                    builder.setMessage("This app needs SMS permission.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getActivity(), "Go to Permissions to Grant SMS", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSION_CALLBACK_CONSTANT);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.READ_PHONE_STATE, true);
                editor.putBoolean(Manifest.permission.READ_SMS, true);
                editor.commit();
            } else {
                //You already have the permission, just go ahead.
                loadCredits();
            }*/
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

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }


            if (allgranted) {
                loadCredits();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_SMS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Need SMS Permission");
                builder.setMessage("This app needs SMS permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getActivity(), "Unable to get Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                loadCredits();
            }
        } else */
        if (requestCode == OPEN_CREDIT_EDITOR_ACTIVITY) {
            loadCredits(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        /*if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                loadCredits();
            }
        }*/

        if (sentToCreditEditor) {
            sentToCreditEditor = false;
            loadCredits(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
    }

    private void loadCredits(int month, int year) {
        creditListView.setAdapter(new CreditListAdapter(getContext(), new ArrayList<Credit>()));

        try {
            creditList = expenseDataSource.getCreditsByMonth(month + 1, year);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (creditList.size() == 0) {
            creditEmptyView.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "creditList size: " + creditList.size());
            creditListAdapter = new CreditListAdapter(getContext(), creditList);
            creditListView.setAdapter(creditListAdapter);
            tvFooterCreditAmount.setText(String.valueOf(expenseDataSource.getTotalCreditAmountByMonth(month, year)));
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
                loadCredits(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
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

    /*// Get all categories from database
    private void getCategoriesFromDatabase() {
        ArrayList<Category> categories = expenseDataSource.getAllCategories();

        for (int i = 0; i < categories.size(); i++) {
            String c = categories.get(i).getCategoryName();
            categoriesString.add(c);
        }
    }

    // Is the category existed on database
    private boolean isCategoryExisted(String category) {
        for (String s : categoriesString) {
            if (s.equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    private void showCreditDetailInDialog(int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        Credit credit = (Credit) creditListView.getItemAtPosition(position);
        builder.setTitle(credit.getCreditDate())
                .setMessage(credit.getCreditDescription())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();
    }*/

    /*private void loadCredits() {
        loadingCreditProgressBar.setVisibility(View.VISIBLE);
        creditListView.setAdapter(new CreditListAdapter(getContext(), new ArrayList<Credit>()));
        //expenseDataSource.deleteAllCredits();

        Timestamp timestamp;
        Date date;
        int intTimestamp;

        Credit credit = new Credit();
        credit.setCreditCategory("Bank");

        Cursor creditCursor = getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (creditCursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (int idx = 0; idx < creditCursor.getColumnCount(); idx++) {
                    //msgData += " " + cursor.getColumnName(idx) + " : " + cursor.getString(idx) + "\n";

                    //getCategoriesFromDatabase();

                    if (creditCursor.getColumnName(idx).equals("date")) {
                        timestamp = new Timestamp(creditCursor.getLong(idx));
                        intTimestamp = (int) (timestamp.getTime() % 100000000);
                        Log.e(TAG, "message longTimestamp: " + intTimestamp);
                        date = new Date(timestamp.getTime());

                        String dateString = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);

                        *//*String fullDateString = date.toString();
                        String monthDateString = fullDateString.substring(4, 10);
                        String yearString = fullDateString.substring(30, 34);
                        String dateString = monthDateString + ", " + yearString;*//*

                        credit.setCreditDate(dateString);
                        credit.setCreditTimestamp(intTimestamp);
                        //Log.i(CreditFragment.class.getSimpleName(), "Date: " + dateString);
                    }

                    if (creditCursor.getColumnName(idx).equals("body")) {
                        // TODO: add logic to add only the credit messages
                        String messageBodyNormal = creditCursor.getString(idx);
                        String messageBodyLowerCase = messageBodyNormal.toLowerCase();

                        if (messageBodyLowerCase.contains("credited") || messageBodyLowerCase.contains("cash in") || messageBodyLowerCase.contains("received")) {
                            Log.i(TAG, creditCursor.getString(idx));

                            credit.setCreditDescription(messageBodyNormal);
                            credit.setCreditAmount(findCreditAmountFromMessageBody(messageBodyLowerCase));

                            *//*if (!isCategoryExisted(credit.getCreditCategory())) {
                                expenseDataSource.insertCategory(new Category("Bank"));
                            }*//*

                            if (!isCreditExisted(credit.getCreditTimestamp())) {
                                expenseDataSource.insertCredit(credit);
                            }
                        }
                    }

                }
                //Log.i(CreditFragment.class.getSimpleName(), credit.getCreditDate() + "\n\n");
                //pbLoading.setVisibility(View.INVISIBLE);
                // use msgData
//                if (msgData.toLowerCase().contains("credited")) {
//                    tvMessages.append(msgData + "\n\n\n");
//                }
            } while (creditCursor.moveToNext());
        } else {
            // empty box, no SMS
            //pbLoading.setVisibility(View.INVISIBLE);
            //tvMessages.append("\n\nNo messages found!\n\n");
        }

        try {
            if (creditCursor != null) {
                creditCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            creditList = expenseDataSource.getAllCredits();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadingCreditProgressBar.setVisibility(View.INVISIBLE);

        if (creditList.size() == 0) {
            creditEmptyView.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "creditList size: " + creditList.size());
            adapter = new CreditListAdapter(getContext(), creditList);
            creditListView.setAdapter(adapter);
            tvFooterCreditAmount.setText("" + expenseDataSource.getTotalCreditAmount());
        }
    }*/

    /*private boolean isCreditExisted(int creditTimestamp) {
        ArrayList<Credit> credits = expenseDataSource.getAllCredits();
        ArrayList<Credit> deletedCredits = expenseDataSource.getAllDeletedCredits();

        for (Credit c : credits) {
            if (c.getCreditTimestamp() == creditTimestamp) {
                return true;
            }
        }

        for (Credit c : deletedCredits) {
            if (c.getCreditTimestamp() == creditTimestamp) {
                return true;
            }
        }

        return false;
    }*/

    /*private Double findCreditAmountFromMessageBody(String messageBody) {
        int indexOfTaka = -1;
        if (messageBody.contains("bdt")) {
            indexOfTaka = messageBody.indexOf("bdt");
        } else if (messageBody.contains("tk")) {
            indexOfTaka = messageBody.indexOf("tk");
        }

        double creditAmount = 0;
        if (indexOfTaka != -1) {
            for (int i = indexOfTaka; i < messageBody.length(); i++) {
                char c = messageBody.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = (int) c - 48;
                    creditAmount = (creditAmount * 10) + digit;
                } else if (c == '.') {
                    break;
                }
            }
        }

        return creditAmount;
    }*/
