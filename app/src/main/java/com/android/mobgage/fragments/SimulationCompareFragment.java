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
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.managers.DataManager;

import java.util.List;


public class SimulationCompareFragment extends Fragment {


    private RecyclerView recyclerView;
    private SimulationCompareAdapter adapter;

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
        View view = inflater.inflate(R.layout.fragment_simulation_compare, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        adapter = new SimulationCompareAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void moveNext(String proposalID){
        ((MobgageMainActivity)(getActivity())).showScreen(MobgageMainActivity.SCREEN_USER_SIMULATION_SINGLE, true, proposalID);
    }


    private class SimulationCompareAdapter extends RecyclerView.Adapter<SimulationCompareAdapter.CustomViewHolder> {

        private List<Proposal> proposals;
        private Context context;

        public SimulationCompareAdapter(Context context) {
            this.context = context;
            this.proposals = DataManager.getInstance().getProposalsListByOrder();
        }


        @Override
        public SimulationCompareAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulation_compare_row, null);


            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimulationCompareAdapter.CustomViewHolder holder, int position) {
            final Proposal proposal = proposals.get(position);


            String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;
            String rowTitle = bankName + " - " + context.getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(proposal.proposalID));
            holder.name.setText(rowTitle);
            holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    moveNext(proposal.getProposalID());

                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return proposals.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            private View parent;
            private TextView overall;
            private  TextView monthlyReturn;
            private  TextView name;


            public CustomViewHolder(View view) {
                super(view);
                this.parent = view;
                this.overall = (TextView) view.findViewById(R.id.compare_row_overall);
                this.monthlyReturn = (TextView) view.findViewById(R.id.compare_row_monthly_return);
                this.name = (TextView) view.findViewById(R.id.compare_row_name);

            }
        }
    }


}
