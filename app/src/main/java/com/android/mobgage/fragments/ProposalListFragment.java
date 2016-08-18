package com.android.mobgage.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.UserDetails;
import com.android.mobgage.interfaces.ISortButtonCallback;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.NumberUtils;

import static com.android.mobgage.utils.Utils.createProposal;

public class ProposalListFragment extends Fragment implements OnClickListener, ISortButtonCallback
{
	private ProposalsAdapter adapter;
	private ListView routesList;
	private ArrayList<Proposal> data;

	ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_proposal_list, null);
		
		view.findViewById(R.id.list_add_proposal_btn).setOnClickListener(this);
		view.findViewById(R.id.list_recommendation_btn).setOnClickListener(this);
		
		routesList = (ListView) view.findViewById(R.id.list_list);
		data = DataManager.getInstance().getProposalsListByOrder();
		adapter = new ProposalsAdapter(getActivity(), R.layout.proposal_row, data);
		routesList.setAdapter(adapter);
		routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
				try {
					Proposal proposal = data.get(position);
					ActiveSelectionData.getInstance().setCurrentProposal(Proposal.copyProposal(proposal));
					ProposalListFragment.this.moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_DETAILS, true);
				} catch (Exception e) {
				}
			}
		});
		clearSelections();		//	clear current proposal if it is a proposal that already saved in DB
		
		return view;
	}
	
	private void clearSelections()
	{
		Proposal current = ActiveSelectionData.getInstance().getCurrentProposal();
		if(current != null)
		{
			boolean isInDB = (DataManager.getInstance().getProposalByProposalID(current.proposalID) != null);
			if(isInDB)
			{
				ActiveSelectionData.getInstance().clearProposal();
			}
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		final int id = v.getId();
		switch (id) 
		{
		case R.id.list_recommendation_btn: getRecommendationClick(v); break;
		case R.id.list_add_proposal_btn: addProposalClick(v); break;
		default:break;
		}
	}



	private void getRecommendationClick(View v)
	{
		UserDetails userDetails = ActiveSelectionData.getInstance().getCurrentUser();
		if(userDetails == null){
			return;
		}
		final Proposal proposal = createProposal(userDetails);
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.recommendation_dialog));
		progressDialog.show();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog.cancel();
					ActiveSelectionData.getInstance().setCurrentProposal(proposal);
					moveToScreen(MobgageMainActivity.SCREEN_FRIEND_RECOMMENDATION, true);
				}
			}
		}, 3500);
	}



	private void addProposalClick(View v)
	{
		moveToScreen(MobgageMainActivity.SCREEN_CHOOSE_BANK, true);
	}
	
	private void moveToScreen(int screen, boolean forward)
	{
		((MobgageMainActivity)(getActivity())).showScreen(screen, forward, null);
	}
	
	
	public class ProposalsAdapter  extends ArrayAdapter<Proposal>
	{
		Context context; 
	    int layoutResourceId;
		ArrayList<Proposal> proposalData = new ArrayList<Proposal>();
		private int itemsCount;

		public ProposalsAdapter(Context context, int layoutResourceId, ArrayList<Proposal> data)
	    {
	        super(context, layoutResourceId);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
			setData(data);
	    }

		public void setData(ArrayList<Proposal> data) {
			proposalData = data;
			if (proposalData != null) {
				itemsCount = proposalData.size();
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return itemsCount;
		}

		@Override
		public Proposal getItem(int position) {
			Proposal item = null;
			if (proposalData != null && proposalData.size() > position) {
				item = proposalData.get(position);
			}
			return item;
		}

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {
	        View row = convertView;
	        ViewHolder holder = null;


	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new ViewHolder();
				holder.row = row;
	            holder.proposalTitleTV = (TextView)row.findViewById(R.id.list_row_title);
	            holder.motgageAmountTV = (TextView)row.findViewById(R.id.list_motgage_amount);
	            holder.yearsTV = (TextView)row.findViewById(R.id.list_years);
	            holder.totalRepaymentTV = (TextView)row.findViewById(R.id.list_total_repayment);
	            holder.monthRepaymentTV = (TextView)row.findViewById(R.id.list_month_repayment);
	            holder.editBtn = (FrameLayout)row.findViewById(R.id.list_edit_button);
	            
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (ViewHolder)row.getTag();
	        }
	        
	        final Proposal item = proposalData.get(position);
	        
	        String bankName = DataManager.getInstance().getBankByID(item.bank).bankName;
//	        String rowTitle = bankName + " - " + getResources().getString(R.string.list_proposal_num) + " " + (position+1);
	        String rowTitle = bankName + " - " + getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(item.proposalID));
	        holder.proposalTitleTV.setText(rowTitle);
	        
	        holder.monthRepaymentTV.setText(NumberUtils.formatedRound(item.monthRepayment + ""));
	        holder.yearsTV.setText(item.years + "");
	        holder.motgageAmountTV.setText(NumberUtils.formatedRound(item.mortgageAmount + ""));
	        holder.totalRepaymentTV.setText(NumberUtils.formatedRound(item.totalRepayment + ""));
	        holder.editBtn.setOnClickListener(new View.OnClickListener()
            {
				@Override
				public void onClick(View v)
				{
					editProposal(v, item);
				}
			});

			final ViewHolder finalHolder = holder;
			/*holder.row.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					finalHolder.row.setBackgroundColor(Color.YELLOW);
					notifyDataSetChanged();
					editProposal(v, item);
					return true;
				}
			});*/
	        
	        return row;
	    }
	    
	    private class ViewHolder
	    {
			View row;
	    	TextView proposalTitleTV;
	        TextView motgageAmountTV;
	        TextView yearsTV;
	        TextView totalRepaymentTV;
	        TextView monthRepaymentTV;
	        FrameLayout editBtn;
	    }
	}
	
	private void editProposal(View v, final Proposal proposal)
	{
		PopupMenu popup = new PopupMenu(getActivity(), v);         
        popup.getMenuInflater().inflate(R.menu.menu_edit_proposal, popup.getMenu());    
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() 
        {  
        	public boolean onMenuItemClick(MenuItem item) 
        	{  
        		switch (item.getItemId()) 
        		{
        		case R.id.edit_proposal:
				{
					ActiveSelectionData.getInstance().setCurrentProposal(Proposal.copyProposal(proposal));
					moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_DETAILS, true);
					break;
				}
        		case R.id.delete_proposal:
				{
					DataManager.getInstance().deleteProposal(proposal.proposalID, false);
					data = DataManager.getInstance().getProposalsListByOrder();
					if(data.isEmpty())
					{
						adapter = new ProposalsAdapter(getActivity(), R.layout.proposal_row, data);
						routesList.setAdapter(adapter);
					}
					else
					{
						adapter.setData(data);
					}
					break;
				}
        		case R.id.set_my_motgage:
				{
					DataManager.getInstance().setMyMortgage(proposal);
					break;
				}
				default: break;
				}
        		return true; 
        	}
        }); 
        popup.show();
	}

	@Override
	public void sort(int filter)
	{
		switch (filter) 
		{
		case MobgageMainActivity.FILTER_BANK:
		{
			Collections.sort(data, new Comparator<Proposal>() {
	            @Override
	            public int compare(Proposal lhs, Proposal rhs) 
	            {
	                return ((Integer)lhs.bank).compareTo(rhs.bank);
	            }
	        });
			break;
		}
		case MobgageMainActivity.FILTER_MONTH_REPAYMENT:
		{
			Collections.sort(data, new Comparator<Proposal>() {
	            @Override
	            public int compare(Proposal lhs, Proposal rhs) 
	            {
	                return ((Float)lhs.monthRepayment).compareTo(rhs.monthRepayment);
	            }
	        });
			break;
		}
		case MobgageMainActivity.FILTER_MOTGAGE_AMOUNT:
		{
			Collections.sort(data, new Comparator<Proposal>() {
	            @Override
	            public int compare(Proposal lhs, Proposal rhs) 
	            {
	                return ((Float)lhs.mortgageAmount).compareTo(rhs.mortgageAmount);
	            }
	        });
			break;
		}
		case MobgageMainActivity.FILTER_TOTAL_REPAYMENT:
		{
			Collections.sort(data, new Comparator<Proposal>() {
	            @Override
	            public int compare(Proposal lhs, Proposal rhs) 
	            {
	                return ((Float)lhs.totalRepayment).compareTo(rhs.totalRepayment);
	            }
	        });
			break;
		}
		case MobgageMainActivity.FILTER_YEARS:
		{
			Collections.sort(data, new Comparator<Proposal>() {
	            @Override
	            public int compare(Proposal lhs, Proposal rhs) 
	            {
	                return ((Integer)lhs.years).compareTo(rhs.years);
	            }
	        });
			break;
		}
		case MobgageMainActivity.FILTER_PROPOSAL_NUM:
		{
			Collections.sort(data, new Comparator<Proposal>() {
	            @Override
	            public int compare(Proposal lhs, Proposal rhs) 
	            {
	                return ((Integer)lhs.proposalNum).compareTo(rhs.proposalNum);
	            }
	        });
			break;
		}
		default: break;
		}
		adapter.notifyDataSetChanged();
	}
}
