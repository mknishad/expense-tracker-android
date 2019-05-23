package com.monir.expensetracker.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.monir.expensetracker.R;
import com.monir.expensetracker.activities.DebitEditorActivity;
import com.monir.expensetracker.adapter.CreditListAdapter;
import com.monir.expensetracker.adapter.DebitListAdapter;
import com.monir.expensetracker.constant.Constant;
import com.monir.expensetracker.database.ExpenseDataSource;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.model.Debit;


/**
 * A simple {@link Fragment} subclass.
 */
public class DebitFragment extends Fragment {

    private static final String TAG = DebitFragment.class.getSimpleName();

    private static final int OPEN_DEBIT_EDITOR_ACTIVITY = 203;

    private List<Debit> debitList;
    private ExpenseDataSource expenseDataSource;
    private DebitListAdapter debitListAdapter;
    private ListView debitListView;
    private TextView debitEmptyView;
    private View view;
    private TextView tvFooterDebitAmount;

    private boolean sentToDebitEditor = false;
    private List<String> categoriesString = new ArrayList<>();

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
        Log.e(TAG, "onCreateView");
        getActivity().setTitle("Debit");
        return view = inflater.inflate(R.layout.fragment_debit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (null != view) {
            debitListView = (ListView) view.findViewById(R.id.lv_debits);
            debitEmptyView = (TextView) view.findViewById(R.id.empty_view_debit);
            //Log.e(TAG, "footer amount text view initialized");
            tvFooterDebitAmount = (TextView) view.findViewById(R.id.text_view_amount_debit);

            debitListView.setEmptyView(debitEmptyView);

            expenseDataSource = new ExpenseDataSource(getContext());

            FloatingActionButton fabDebit = (FloatingActionButton) view.findViewById(R.id.fab_debit);
            fabDebit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentToDebitEditor = true;
                    Intent intent = new Intent(getContext(), DebitEditorActivity.class);
                    intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);
                    startActivityForResult(intent, OPEN_DEBIT_EDITOR_ACTIVITY);
                }
            });

            debitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //showCreditDetailInDialog(position);
                    Log.e("CrditFragment", "----------------------position: " + position + " id: " + id);

                    sentToDebitEditor = true;
                    Intent intent = new Intent(getContext(), DebitEditorActivity.class);
                    intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_EDIT);
                    intent.putExtra(Constant.DEBIT_ITEM_ID, debitList.get(position).getDebitId());
                    Log.e(TAG, "Clicked item id: " + id);
                    startActivityForResult(intent, OPEN_DEBIT_EDITOR_ACTIVITY);
                }
            });

            loadDebits();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_DEBIT_EDITOR_ACTIVITY) {
            loadDebits();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sentToDebitEditor) {
            sentToDebitEditor = false;
            loadDebits();
        }
    }

    private void loadDebits() {
        debitListView.setAdapter(new CreditListAdapter(getContext(), new ArrayList<Credit>()));

        try {
            debitList = expenseDataSource.getAllDebits();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (debitList.size() == 0) {
            debitEmptyView.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "debitList size: " + debitList.size());
            debitListAdapter = new DebitListAdapter(getContext(), debitList);
            debitListView.setAdapter(debitListAdapter);
            tvFooterDebitAmount.setText("" + expenseDataSource.getTotalDebitAmount());
        }
    }
}
