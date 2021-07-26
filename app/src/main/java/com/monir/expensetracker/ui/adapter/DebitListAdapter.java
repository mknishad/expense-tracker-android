package com.monir.expensetracker.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Locale;


public class DebitListAdapter extends BaseAdapter implements Filterable {
  private List<Debit> mOriginalDebits;
  private List<Debit> mDisplayedDebits;
  private LayoutInflater inflater;

  public DebitListAdapter(@NonNull Context context, @NonNull List<Debit> debits) {
    super();
    mDisplayedDebits = debits;
    inflater = LayoutInflater.from(context);
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
    holder.tvAmount.setText(String.format(Locale.getDefault(), "৳%.2f",
        mDisplayedDebits.get(position).getDebitAmount()));

        /*holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        List<Debit> filteredDebits = new ArrayList<>();

        if (mOriginalDebits == null) {
          mOriginalDebits = mDisplayedDebits;
        }

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

  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
  }

  private class ViewHolder {
    RelativeLayout rlContainer;
    TextView tvCategory, tvDate, tvAmount;
  }
}
