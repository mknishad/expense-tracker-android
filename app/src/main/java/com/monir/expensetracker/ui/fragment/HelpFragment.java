package com.monir.expensetracker.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monir.expensetracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {


  public HelpFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    getActivity().setTitle("Help");

    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_help, container, false);
  }

}
