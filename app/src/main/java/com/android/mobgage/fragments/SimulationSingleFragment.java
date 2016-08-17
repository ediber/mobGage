package com.android.mobgage.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mobgage.R;


public class SimulationSingleFragment extends Fragment {

    public SimulationSingleFragment() {
        // Required empty public constructor
    }

    public static SimulationSingleFragment newInstance() {
        SimulationSingleFragment fragment = new SimulationSingleFragment();


        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simulation_single, container, false);

        return view;
    }


}
