package com.monir.expensetracker.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.model.Debit;

import java.util.List;


public class DebitListAdapter extends ArrayAdapter<Debit> {

    public DebitListAdapter(@NonNull Context context, @NonNull List<Debit> debits) {
        super(context, 0, debits);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.debit_list_item, parent, false);
        }

        Debit currentDebit = getItem(position);

        TextView debitCategoryTextView = (TextView) listItemView.findViewById(R.id.tv_debit_category);

        debitCategoryTextView.setText(currentDebit.getDebitCategory());

        TextView debitDateTextView = (TextView) listItemView.findViewById(R.id.tv_debit_date);

        debitDateTextView.setText(currentDebit.getDebitDate());

        TextView debitAmountTextView = (TextView) listItemView.findViewById(R.id.tv_debit_amount);

        debitAmountTextView.setText("" + currentDebit.getDebitAmount());

        return listItemView;
    }
}
