package com.android.mobgage.fragments;

import java.util.List;
import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Bank;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.Utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/* In this module a bank list is shown (represented by SCREEN_CHOOSE_BANK in MobgageMainActivity)

 */
public class ChooseBankFragment extends Fragment implements OnItemClickListener 
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_banks, null);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(getResources().getString(R.string.choose_bank_title));
		ListView bankList = (ListView)view.findViewById(R.id.list);
		bankList.setAdapter(new DataAdapter(DataManager.getInstance().getAllBanks()));
		bankList.setOnItemClickListener(this);

		return view;
	}
	
	private class DataAdapter extends ArrayAdapter<Bank> 
	{      
		public DataAdapter(List<Bank> banks) {
			super(getActivity(), R.layout.bank_row,
					R.id.text, banks);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View v = super.getView(position, convertView, parent);
			
			TextView text = (TextView) v.findViewById(R.id.text);
			ImageView image = (ImageView) v.findViewById(R.id.image);
			Bank bank = getItem(position); // could be also Bank bank = data.get(position)
			image.setImageDrawable(DataManager.getInstance().getBankImage(bank.bankID, getContext()));
			text.setVisibility(View.GONE);
			image.setVisibility(View.VISIBLE);
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)  
	{
		Bank bank = (Bank) parent.getItemAtPosition(position);
		String userID = ActiveSelectionData.getInstance().getCurrentUser().userID;
		
		Proposal proposal = ActiveSelectionData.getInstance().getCurrentProposal();
		if(proposal != null)
		{	
			if((proposal.bank == bank.bankID) && (!(proposal.getRoutes().isEmpty())))
			{
				showAlertForHasProposalForThisBank(bank, userID);
			}
			else
			{
				ActiveSelectionData.getInstance().setCurrentProposal(DataManager.getInstance().getNewProposal(bank.bankID, userID));
				moveToScreen(MobgageMainActivity.SCREEN_CHOOSE_ROUTE, true); //move to ChooseRouteFragmant
			}
		}
		else
		{
			ActiveSelectionData.getInstance().setCurrentProposal(DataManager.getInstance().getNewProposal(bank.bankID, userID));
			moveToScreen(MobgageMainActivity.SCREEN_CHOOSE_ROUTE, true); //move to ChooseRouteFragmant
		}
	}
	
	private void moveToScreen(int screen, boolean forward)
	{
		((MobgageMainActivity)(getActivity())).showScreen(screen, forward, null);
	}
	
	
	private void showAlertForHasProposalForThisBank(final Bank bank, final String userID)
	{
		Resources res = getResources();
		AlertDialog alert = Utils.getAlert(res.getString(R.string.alert2_title), res.getString(R.string.alert2_msg), getActivity());
		alert.setButton(AlertDialog.BUTTON_POSITIVE, res.getString(R.string.alert2_btn1), new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface dialog, int which) 		//	edit existing proposal
			{
				moveToScreen(MobgageMainActivity.SCREEN_CHOOSE_ROUTE, true);
			}
		});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, res.getString(R.string.alert2_btn2), new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface dialog, int which) 	//	restart
			{
				ActiveSelectionData.getInstance().setCurrentProposal(DataManager.getInstance().getNewProposal(bank.bankID, userID));
				moveToScreen(MobgageMainActivity.SCREEN_CHOOSE_ROUTE, true);
			}
		});
		alert.show();
	}
}
