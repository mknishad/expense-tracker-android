package com.monir.expensetracker.ui.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.monir.expensetracker.R;
import com.monir.expensetracker.ui.fragment.CreditStatisticsFragment;
import com.monir.expensetracker.ui.fragment.DebitStatisticsFragment;

/**
 * Created by Nishad on 5/8/2017.
 */

public class StatisticsAdapter extends FragmentPagerAdapter {

  /**
   * Context of the app
   */
  private Context mContext;

  public StatisticsAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return new CreditStatisticsFragment();
    } else {
      return new DebitStatisticsFragment();
    }
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    if (position == 0) {
      return mContext.getString(R.string.credit);
    } else {
      return mContext.getString(R.string.debit);
    }
  }
}
