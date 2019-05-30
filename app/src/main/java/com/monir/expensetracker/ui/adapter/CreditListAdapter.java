package com.monir.expensetracker.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.model.Credit;

import java.util.List;


public class CreditListAdapter extends ArrayAdapter<Credit> {

    public CreditListAdapter(@NonNull Context context, @NonNull List<Credit> credits) {
        super(context, 0, credits);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.credit_list_item, parent, false);
        }

        Credit currentCredit = getItem(position);

        TextView creditCategoryTextView = (TextView) listItemView.findViewById(R.id.tv_credit_category);
        creditCategoryTextView.setText(currentCredit.getCreditCategory());

        TextView creditDateTextView = (TextView) listItemView.findViewById(R.id.tv_credit_date);
        creditDateTextView.setText(currentCredit.getCreditDate());

        Log.e(CreditListAdapter.class.getSimpleName(), "Date: " + currentCredit.getCreditDate());
        //creditDateTextView.setText("Kaj hoy na kere!");

        TextView creditAmountTextView = (TextView) listItemView.findViewById(R.id.tv_credit_amount);
        creditAmountTextView.setText("" + currentCredit.getCreditAmount());

        return listItemView;
    }
}
