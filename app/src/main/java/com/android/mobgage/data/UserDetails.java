package com.android.mobgage.data;

public class UserDetails 
{
	public String userID;
	public String email;
	public float mortgageAmount;
	public float monthRepayment;
	public float assetValue;
	public String assetLocation;
	public float leveragePercent;
	public float freeIncoming;
	
	public UserDetails(String userID, String email, float mortgageAmount,
			float monthRepayment, float assetValue, String assetLocation,
			float leveragePercent, float freeIncoming)
	{
		this.userID = userID;
		this.email = email;
		this.mortgageAmount = mortgageAmount;
		this.monthRepayment = monthRepayment;
		this.assetValue = assetValue;
		this.assetLocation = assetLocation;
		this.leveragePercent = leveragePercent;
		this.freeIncoming = freeIncoming;
	}
}
