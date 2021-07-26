package com.monir.expensetracker.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monir.expensetracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

  private Context context;

  public AboutFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    context = getActivity().getApplicationContext();

    getActivity().setTitle("About");

    View view = inflater.inflate(R.layout.fragment_about, container, false);

    TextView privacyPolicyTextView = view.findViewById(R.id.privacyPolicyTextView);
    privacyPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance());

    return view;
  }
}
