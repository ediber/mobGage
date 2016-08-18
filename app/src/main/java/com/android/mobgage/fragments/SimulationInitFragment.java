package com.android.mobgage.fragments;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;


public class SimulationInitFragment extends Fragment {


    private View primeBtn;
    private View changingBtn;
    private View IndexBtn;
    private EditText primeEdt;
    private EditText changingEdt;
    private EditText IndexEdt;
    private TextView primeTxt;
    private TextView changingTxt;
    private TextView IndexTxt;
    private View calculate;

    public SimulationInitFragment() {
        // Required empty public constructor
    }


    public static SimulationInitFragment newInstance() {
        SimulationInitFragment fragment = new SimulationInitFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simulation_init, container, false);

        primeBtn =  view.findViewById(R.id.simulation_init_prime_btn);
        changingBtn =  view.findViewById(R.id.simulation_init_changing_btn);
        IndexBtn =  view.findViewById(R.id.simulation_init_index_btn);
        calculate =  view.findViewById(R.id.simulation_init_calculate);


        primeTxt = (TextView)view.findViewById(R.id.simulation_init_prime_txt);
        changingTxt = (TextView)view.findViewById(R.id.simulation_init_changing_txt);
        IndexTxt = (TextView)view.findViewById(R.id.simulation_init_index_txt);

        primeEdt = (EditText)view.findViewById(R.id.simulation_init_prime_edit);
        changingEdt = (EditText)view.findViewById(R.id.simulation_init_changing_edit);
        IndexEdt = (EditText)view.findViewById(R.id.simulation_init_index_edit);

        primeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                primeEdt.setText(primeTxt.getText());
            }
        });

        changingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingEdt.setText(changingTxt.getText());
            }
        });

        IndexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndexEdt.setText(IndexTxt.getText());
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MobgageMainActivity) (getActivity())).showScreen(MobgageMainActivity.SCREEN_USER_SIMULATION_COMPARE, true, null);
            }
        });

        return view;
    }


}
