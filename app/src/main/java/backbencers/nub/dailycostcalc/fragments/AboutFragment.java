package backbencers.nub.dailycostcalc.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import backbencers.nub.dailycostcalc.R;

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

        return view;


    }

}
