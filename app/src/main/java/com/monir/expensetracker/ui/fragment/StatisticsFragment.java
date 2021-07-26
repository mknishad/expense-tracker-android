package com.monir.expensetracker.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monir.expensetracker.R;
import com.monir.expensetracker.ui.adapter.StatisticsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {


  public StatisticsFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    getActivity().setTitle("Statistics");

    View view = inflater.inflate(R.layout.fragment_statistics, container, false);

    init(view);

    return view;
  }

  private void init(View view) {
    ViewPager viewPager = view.findViewById(R.id.viewPager);
    TabLayout tabLayout = view.findViewById(R.id.tabLayout);
    tabLayout.setupWithViewPager(viewPager);

    StatisticsAdapter adapter = new StatisticsAdapter(getActivity(), getChildFragmentManager());
    viewPager.setAdapter(adapter);
  }
}
