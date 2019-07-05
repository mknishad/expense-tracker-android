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
import com.monir.expensetracker.model.Credit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CreditListAdapter extends BaseAdapter implements Filterable {
    private List<Credit> mOriginalCredits;
    private List<Credit> mDisplayedCredits;
    private LayoutInflater inflater;

    public CreditListAdapter(@NonNull Context context, @NonNull List<Credit> credits) {
        super();
        mDisplayedCredits = credits;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.credit_list_item, parent, false);
            holder.rlContainer = convertView.findViewById(R.id.mainContainer);
            holder.tvCategory = convertView.findViewById(R.id.tv_credit_category);
            holder.tvDate = convertView.findViewById(R.id.tv_credit_date);
            holder.tvAmount = convertView.findViewById(R.id.tv_credit_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCategory.setText(mDisplayedCredits.get(position).getCreditCategory());
        holder.tvDate.setText(mDisplayedCredits.get(position).getCreditDate());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "৳%.2f",
                mDisplayedCredits.get(position).getCreditAmount()));

        /*holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Credit> filteredCredits = new ArrayList<>();

                if (mOriginalCredits == null) {
                    mOriginalCredits = mDisplayedCredits;
                }

                if (constraint != null) {
                    if (mOriginalCredits != null && mOriginalCredits.size() > 0) {
                        for (Credit c : mOriginalCredits) {
                            if (c.getCreditCategory().toLowerCase().contains(constraint.toString())) {
                                filteredCredits.add(c);
                            }
                        }
                    }
                    results.values = filteredCredits;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDisplayedCredits = (ArrayList<Credit>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return mDisplayedCredits.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayedCredits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
