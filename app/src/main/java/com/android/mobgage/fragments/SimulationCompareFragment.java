package com.android.mobgage.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mobgage.R;


public class SimulationCompareFragment extends Fragment {




    public SimulationCompareFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SimulationCompareFragment newInstance() {
        SimulationCompareFragment fragment = new SimulationCompareFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simulation_compare, container, false);

        return view;
    }




}
