package com.monir.expensetracker.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.model.Debit;

import java.util.ArrayList;
import java.util.List;


public class DebitListAdapter extends BaseAdapter implements Filterable {

    private List<Debit> mOriginalDebits;
    private List<Debit> mDisplayedDebits;
    private LayoutInflater inflater;

    public DebitListAdapter(@NonNull Context context, @NonNull List<Debit> debits) {
        //super(context, 0, debits);
        super();
        //mOriginalDebits = debits;
        mDisplayedDebits = debits;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDisplayedDebits.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayedDebits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.debit_list_item, parent, false);

            holder.rlContainer = convertView.findViewById(R.id.mainContainer);
            holder.tvCategory = convertView.findViewById(R.id.tv_debit_category);
            holder.tvDate = convertView.findViewById(R.id.tv_debit_date);
            holder.tvAmount = convertView.findViewById(R.id.tv_debit_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCategory.setText(mDisplayedDebits.get(position).getDebitCategory());
        holder.tvDate.setText(mDisplayedDebits.get(position).getDebitDate());
        holder.tvAmount.setText(String.valueOf(mDisplayedDebits.get(position).getDebitAmount()));

        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;

        /*View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.debit_list_item, parent, false);
        }

        Debit currentDebit = getItem(position);
        TextView debitCategoryTextView = (TextView) listItemView.findViewById(R.id.tv_debit_category);
        debitCategoryTextView.setText(currentDebit.getDebitCategory());
        TextView debitDateTextView = (TextView) listItemView.findViewById(R.id.tv_debit_date);
        debitDateTextView.setText(currentDebit.getDebitDate());
        TextView debitAmountTextView = (TextView) listItemView.findViewById(R.id.tv_debit_amount);
        debitAmountTextView.setText(String.valueOf(currentDebit.getDebitAmount()));

        return listItemView;*/
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Debit> filteredDebits = new ArrayList<>();

                if (mOriginalDebits == null) {
                    mOriginalDebits = mDisplayedDebits;
                }

                /*if (constraint == null || constraint.length() == 0) {
                    results.count = mOriginalDebits.size();
                    results.values = mOriginalDebits;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalDebits.size(); i++) {
                        String data = mOriginalDebits.get(i).getDebitCategory();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            filteredDebits.add(new Debit(
                                    mOriginalDebits.get(i).getDebitId(),
                                    mOriginalDebits.get(i).getDebitDate(),
                                    mOriginalDebits.get(i).getDebitCategory(),
                                    mOriginalDebits.get(i).getDebitDescription(),
                                    mOriginalDebits.get(i).getDebitAmount()
                            ));
                        }
                    }

                    results.count = filteredDebits.size();
                    results.values = filteredDebits;
                }*/

                if (constraint != null) {
                    if (mOriginalDebits != null && mOriginalDebits.size() > 0) {
                        for (Debit d : mOriginalDebits) {
                            if (d.getDebitCategory().toLowerCase().contains(constraint.toString())) {
                                filteredDebits.add(d);
                            }
                        }
                    }
                    results.values = filteredDebits;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDisplayedDebits = (ArrayList<Debit>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        RelativeLayout rlContainer;
        TextView tvCategory, tvDate, tvAmount;
    }
}
