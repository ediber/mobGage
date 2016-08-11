package com.android.mobgage.data;

public class Route 
{
	public String userID;
	public String proposalID;
	public int routeNum;
	public float loanAmount;
	public int years;
	public float interest;
	public float monthRepayment;
	public int returnMethod;
	
	public int routeKind;
	public float totalRepayment;
	
	public Route(String userID, String proposalID, int routeNum, float loanAmount,
			int years, float interest, float monthRepayment, int returnMethod,
			int routeKind, float totalRepayment) 
	{
		this.userID = userID;
		this.proposalID = proposalID;
		this.routeNum = routeNum;
		this.loanAmount = loanAmount;
		this.years = years;
		this.interest = interest;
		this.monthRepayment = monthRepayment;
		this.returnMethod = returnMethod;
		this.routeKind = routeKind;
		this.totalRepayment = totalRepayment;
	}
	
	public Route copyRoute()
	{
		return new Route(userID, proposalID, routeNum, loanAmount, years,
				interest, monthRepayment, returnMethod, routeKind, totalRepayment);
		
	}
}
