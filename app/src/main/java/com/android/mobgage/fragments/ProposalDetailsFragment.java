package com.android.mobgage.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.widget.*;

import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.Route;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.NumberUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import static com.android.mobgage.utils.NumberUtils.doubleToMoney;

public class ProposalDetailsFragment extends Fragment implements OnClickListener {
    private Proposal proposal;
    private TextView totalAmountsTV, totalMonthRepaymentTV, totalRepaymentTV, proposalSaveBtnTV;
    private boolean readOnly = false;
    private RoutesAdapter adapter;
    private ArrayList<Route> data;

    private double totalAmounts;
    private double totalMonthRepayment;
    private double totalRepayment;
    private String titleText;

    private View proposalSaveBtn;
    private View proposalAddRouteBtn;
    private View proposalShareBtn;
    private View proposalChangeBtn;

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposal_details, null);
        proposal = ActiveSelectionData.getInstance().getCurrentProposal();

        TextView title = (TextView) view.findViewById(R.id.proposal_title);
        String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;

        proposalSaveBtn = view.findViewById(R.id.proposal_save_btn);
        proposalAddRouteBtn = view.findViewById(R.id.proposal_add_route_btn);
        proposalShareBtn = view.findViewById(R.id.proposal_share_btn);
        proposalChangeBtn = view.findViewById(R.id.proposal_change_btn);

        int proposalNum = DataManager.getInstance().getProposalPositionByID(proposal.proposalID);
        String proposalNumString = getResources().getString(R.string.proposal_title);
        String myMotgageString = getResources().getString(R.string.my_motgage);
        String recommendedMortgageString = getResources().getString(R.string.recommendation_motgage);
        titleText = "";
        if (readOnly) {
            titleText = recommendedMortgageString + " - " + bankName;
        } else if (proposal.myMortgage == 1) {
            titleText = myMotgageString + " - " + bankName;
        } else {
            titleText = bankName + " - " + (proposalNumString) + " " + (proposalNum);
        }

        title.setText(titleText);

        totalAmountsTV = (TextView) view.findViewById(R.id.proposal_total_amounts);
        totalMonthRepaymentTV = (TextView) view.findViewById(R.id.proposal_total_month_repayment);
        totalRepaymentTV = (TextView) view.findViewById(R.id.proposal_total_repayment);
        proposalSaveBtnTV = (TextView) view.findViewById(R.id.proposal_save_btn_text);

        updateSums();

        if (readOnly) {
            view.findViewById(R.id.proposal_buttons_frame).setVisibility(View.GONE);
        } else {
            if (DataManager.getInstance().isInMortgageFlow()) { // my mortgage
                proposalSaveBtnTV.setText(getString(R.string.proposal_update_my_mortgage));

                proposalSaveBtn.setVisibility(View.GONE);
                proposalAddRouteBtn.setVisibility(View.GONE);
                proposalChangeBtn.setOnClickListener(this);
            } else {                                            // regular offer
                proposalSaveBtn.setOnClickListener(this);
                proposalAddRouteBtn.setOnClickListener(this);
                proposalShareBtn.setOnClickListener(this);
                proposalChangeBtn.setVisibility(View.GONE);
            }
        }

        ListView routesList = (ListView) view.findViewById(R.id.proposal_list);
        data = proposal.getRoutes();
        adapter = new RoutesAdapter(getActivity(), R.layout.route_row);
        routesList.setAdapter(adapter);
        routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Route route = data.get(position);
                    ActiveSelectionData.getInstance().setCurrentRoute(route);
                    moveToScreen(MobgageMainActivity.SCREEN_ROUTE_DETAILS, true);
                } catch (Exception e) {
                }
            }
        });
        return view;
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.proposal_save_btn:
                saveProposalClick(v);
                break;
            case R.id.proposal_add_route_btn:
                addRouteClick(v);
                break;
            case R.id.proposal_share_btn:
                shareClick(v);
                break;
            case R.id.proposal_change_btn:
                changeClick(v);
                break;
            default:
                break;
        }
    }




    private void updateSums() {
        totalAmounts = proposal.getAllRoutesAmountSum();
        totalMonthRepayment = proposal.getAllRoutesMonthRepaymentSum();
        totalRepayment = proposal.getTotalRepaymentSum();
        int years = proposal.getMaxYears();

        totalAmountsTV.setText(NumberUtils.formatedRound(((int) totalAmounts) + ""));
        totalMonthRepaymentTV.setText(NumberUtils.formatedRound(((int) totalMonthRepayment) + ""));
        totalRepaymentTV.setText(NumberUtils.formatedRound((float) totalRepayment + ""));

        proposal.monthRepayment = (float) totalMonthRepayment;
        proposal.mortgageAmount = (float) totalAmounts;
        proposal.totalRepayment = (float) totalRepayment;
        proposal.years = years;
    }


    private void saveProposalClick(View v) {
        DataManager.getInstance().saveOrUpdateProposal(ActiveSelectionData.getInstance().getCurrentProposal()
                , DataManager.getInstance().isInMortgageFlow());
        ActiveSelectionData.getInstance().clearProposal();
        if (DataManager.getInstance().isInMortgageFlow()) {
            moveToScreen(MobgageMainActivity.SCREEN_MAIN, false);
        } else {
            moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, false);
        }
    }

    private void addRouteClick(View v) {
        moveToScreen(MobgageMainActivity.SCREEN_CHOOSE_ROUTE, true);
    }

    private void shareClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, titleText);

        String body = getResources().getString(R.string.proposal_share_total_amount2, doubleToMoney(totalAmounts)) + '\n';
        body += getResources().getString(R.string.proposal_share_month_repayment, doubleToMoney(totalMonthRepayment)) + '\n';
        body += getResources().getString(R.string.proposal_share_total_repayment, doubleToMoney(totalRepayment)) + '\n' + '\n';

        for (int i=0; i < data.size(); i++){
            body += adapter.getString(i);
        }

        intent.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void changeClick(View v) {
        moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, true);
    }



    private void moveToScreen(int screen, boolean forward) {
        ((MobgageMainActivity) (getActivity())).showScreen(screen, forward, null);
    }


    public class RoutesAdapter extends ArrayAdapter<Route> {
        Context context;
        int layoutResourceId;

        public RoutesAdapter(Context context, int layoutResourceId) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ViewHolder();
                holder.routeTitleTV = (TextView) row.findViewById(R.id.route_row_title);
//	            holder.loamAmountLabelTV = (TextView)row.findViewById(R.id.loan_amount_label);
                holder.loanAmountTV = (TextView) row.findViewById(R.id.loan_amount);
                holder.yearsTV = (TextView) row.findViewById(R.id.years);
                holder.interestTV = (TextView) row.findViewById(R.id.interest);
                holder.monthRepaymentTV = (TextView) row.findViewById(R.id.month_repayment);
                holder.editBtn = (FrameLayout) row.findViewById(R.id.route_edit_button);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            final Route item = data.get(position);

            String routKindName = DataManager.getInstance().getRouteKindByID(item.routeKind).routeKindName;
            String maslul = getResources().getString(R.string.route);
            int routePosition = position + 1;
            holder.routeTitleTV.setText(maslul + " " + (routePosition) + " - " + routKindName);

            holder.loanAmountTV.setText(NumberUtils.formatedRound(item.loanAmount + ""));
            holder.monthRepaymentTV.setText(NumberUtils.formatedRound(item.monthRepayment + ""));
            holder.yearsTV.setText(item.years + "");
            holder.interestTV.setText(NumberUtils.formatedRoundPrecision1(item.interest + ""));
            if (readOnly) {
                holder.editBtn.setVisibility(View.GONE);
            } else {
                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editRoute(v, item);
                    }
                });
            }

            return row;
        }

        public String getString(int position){
            String ans;

            final Route item = data.get(position);
            String routKindName = DataManager.getInstance().getRouteKindByID(item.routeKind).routeKindName;
            String maslul = getResources().getString(R.string.route);
            int routePosition = position + 1;
            ans = maslul + " " + (routePosition) + " - " + routKindName +'\n' ;

//            ans = "<b>" + maslul + " " + (routePosition) + " - " + routKindName + "</b>" +'\n' ;
            ans += getResources().getString(R.string.proposal_share_loan_amount, doubleToMoney(item.loanAmount)) + '\n';
            ans += getResources().getString(R.string.proposal_share_years, item.years) + '\n';
            if(item.interest > 0){
                ans += getResources().getString(R.string.proposal_share_interest, item.interest) + '\n';
            } else { // prime
                String interestStr = item.interest + "";
                interestStr = interestStr.substring(1, interestStr.length()) + "-";
                ans += getResources().getString(R.string.proposal_share_interest_prime, interestStr) + '\n';
            }
            ans += getResources().getString(R.string.proposal_share_row_month_repayment, doubleToMoney(item.monthRepayment)) + '\n' + '\n';

            return ans;
        }

        private class ViewHolder {
            TextView routeTitleTV;
            //	        TextView loamAmountLabelTV;
            TextView loanAmountTV;
            TextView yearsTV;
            TextView interestTV;
            TextView monthRepaymentTV;
            FrameLayout editBtn;
        }
    }

    private void editRoute(View v, final Route route) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.menu_edit_route, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_route: {
                        ActiveSelectionData.getInstance().setCurrentRoute(route);
                        moveToScreen(MobgageMainActivity.SCREEN_ROUTE_DETAILS, true);
                        break;
                    }
                    case R.id.delete_route: {
                        proposal.getRoutes().remove(route);
                        data = proposal.getRoutes();
                        adapter.notifyDataSetChanged();
                        updateSums();

                        if (data.isEmpty()) {
                            DataManager.getInstance().deleteProposal(proposal.proposalID, DataManager.getInstance().isInMortgageFlow() ? true : false);
                            ActiveSelectionData.getInstance().clearProposal();
                            moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, true);
                        }

                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}	
