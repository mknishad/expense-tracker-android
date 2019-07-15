package com.monir.expensetracker.ui.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.CreditDataSource;
import com.monir.expensetracker.database.ExpenseDataSource;
import com.monir.expensetracker.model.Credit;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpandableCreditListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private CreditDataSource creditDataSource;
    private List<String> mListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Credit>> mListDataChild;

    public ExpandableCreditListAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<Credit>> listDataChild) {
        this.mContext = context;
        this.creditDataSource = new CreditDataSource(mContext);
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String descriptionText = ((Credit) getChild(groupPosition, childPosition))
                .getCreditDescription();
        final String dateText = ((Credit) getChild(groupPosition, childPosition)).getCreditDate();
        final Double amount = ((Credit) getChild(groupPosition, childPosition))
                .getCreditAmount();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView txtDescription = convertView.findViewById(R.id.descriptionTextView);
        txtDescription.setText(descriptionText);
        TextView txtDate = convertView.findViewById(R.id.dateTextView);
        txtDate.setText(dateText);
        TextView txtAmount = convertView.findViewById(R.id.amountTextView);
        txtAmount.setText(String.format(Locale.getDefault(), "৳%.2f", amount));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        double headerAmount = creditDataSource.getTotalCreditAmountByCategory(headerTitle);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        ConstraintLayout groupContainer = convertView.findViewById(R.id.groupContainer);
        groupContainer.setBackgroundResource(android.R.color.holo_green_light);
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
        categoryTextView.setText(headerTitle);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);
        amountTextView.setText(String.format(Locale.getDefault(), "৳%.2f", headerAmount));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
