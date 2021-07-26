package com.monir.expensetracker.ui.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.model.Debit;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpandableDebitListAdapter extends BaseExpandableListAdapter {

  private Context mContext;
  private DebitDataSource debitDataSource;
  private List<String> mListDataHeader; // header titles
  // child data in format of header title, child title
  private HashMap<String, List<Debit>> mListDataChild;
  private Calendar calendar;

  public ExpandableDebitListAdapter(Context context, List<String> listDataHeader,
                                    HashMap<String, List<Debit>> listDataChild,
                                    Calendar calendar) {
    this.mContext = context;
    this.debitDataSource = new DebitDataSource(mContext);
    this.mListDataHeader = listDataHeader;
    this.mListDataChild = listDataChild;
    this.calendar = calendar;
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
        .get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public View getChildView(int groupPosition, final int childPosition,
                           boolean isLastChild, View convertView, ViewGroup parent) {

    final String descriptionText = ((Debit) getChild(groupPosition, childPosition)).getDebitDescription();
    final String dateText = ((Debit) getChild(groupPosition, childPosition)).getDebitDate();
    final double amount = ((Debit) getChild(groupPosition, childPosition)).getDebitAmount();

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
  public View getGroupView(int groupPosition, boolean isExpanded,
                           View convertView, ViewGroup parent) {
    String headerTitle = (String) getGroup(groupPosition);
    double headerAmount = debitDataSource.getTotalDebitAmountByCategoryAndMonth(headerTitle,
        calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) this.mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.list_group, null);
    }

    ConstraintLayout groupContainer = convertView.findViewById(R.id.groupContainer);
    groupContainer.setBackgroundResource(android.R.color.holo_red_light);
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
