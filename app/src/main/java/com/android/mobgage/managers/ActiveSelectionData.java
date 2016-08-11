package com.android.mobgage.managers;

import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.Route;
import com.android.mobgage.data.UserDetails;


public class ActiveSelectionData 
{
	private static ActiveSelectionData instance;
	
	private UserDetails currentUser;
	private Proposal currentProposal;
	private Route currentRoute;
	private boolean isEditExisingProposal;

	private ActiveSelectionData() {}

	public static ActiveSelectionData getInstance()
	{
		if(instance == null)
		{
			instance = new ActiveSelectionData();
		}
		return instance;
	}

	public UserDetails getCurrentUser() 
	{
		return currentUser;
	}

	public void setCurrentUser(UserDetails currentUser) {
		
		this.currentUser = currentUser;
	}

	public Proposal getCurrentProposal() 
	{
		return currentProposal;
	}

	public void setCurrentProposal(Proposal currentProposal) 
	{
		this.currentProposal = currentProposal;
	}

	public Route getCurrentRoute() 
	{
		return currentRoute;
	}

	public void setCurrentRoute(Route currentRoute) 
	{
		this.currentRoute = currentRoute;
	}

	public static void setInstance(ActiveSelectionData instance) 
	{
		ActiveSelectionData.instance = instance;
	}
	
	public void clearProposal()
	{
		currentProposal = null;
		currentRoute = null;
	}
	
	public void clearRoute()
	{
		currentRoute = null;
	}
	

	public boolean isEditExisingProposal() 
	{
		return isEditExisingProposal;
	}

	public void setEditExisingProposal(boolean isEditExisingProposal) 
	{
		this.isEditExisingProposal = isEditExisingProposal;
	}
}
