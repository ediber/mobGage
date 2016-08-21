package com.android.mobgage.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.mobgage.R;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.managers.DataManager;


public class SimulationSingleFragment extends Fragment {

    private static final String PROPOSAL_ID = "proposalId";

    private String proposalId;
    private RecyclerView recyclerView;
    private SimulationSingleAdapter adapter;
    private TextView offer;

    public SimulationSingleFragment() {
        // Required empty public constructor
    }

    public static SimulationSingleFragment newInstance(String proposalId) {
        SimulationSingleFragment fragment = new SimulationSingleFragment();

        Bundle args = new Bundle();
        args.putString(PROPOSAL_ID, proposalId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            proposalId = getArguments().getString(PROPOSAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simulation_single, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.simulation_single_recyclerView);
        offer = (TextView)view.findViewById(R.id.simulation_single_offer);

        adapter =  new SimulationSingleAdapter(proposalId);
        recyclerView.setAdapter(adapter);

        Proposal proposal = DataManager.getInstance().getProposalByProposalID(proposalId);
        String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;
        String rowTitle = bankName + " - " + getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(proposal.proposalID));
        offer.setText(rowTitle);

        return view;
    }

    //    getProposalPositionByID
    private class SimulationSingleAdapter extends RecyclerView.Adapter<SimulationSingleAdapter.CustomViewHolder>{

        private Proposal proposal;
        private String proposalId;

        public SimulationSingleAdapter(String proposalId) {
            this.proposalId = proposalId;
            this.proposal = DataManager.getInstance().getProposalByProposalID(proposalId);
        }

        @Override
        public SimulationSingleAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulation_single_row, null);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimulationSingleAdapter.CustomViewHolder holder, int position) {
            holder.date.setText(position + "");
        }

        @Override
        public int getItemCount() {
            return proposal.getMaxYears() * 12; // months
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            private TextView interest;
            private TextView left;
            private TextView month_return;
            private TextView date;
            private View parent;



            public CustomViewHolder(View view) {
                super(view);
                this.parent = view;
                this.interest = (TextView) view.findViewById(R.id.single_row_interest);
                this.left = (TextView) view.findViewById(R.id.single_row_left);
                this.month_return = (TextView) view.findViewById(R.id.single_row_month_return);
                this.date = (TextView) view.findViewById(R.id.single_row_date);
///
            }
        }
    }


}
