package com.android.mobgage.provider.tablesColumns;

import android.provider.BaseColumns;

public class ProposalColumns implements BaseColumns
{
	public static final String COLUMN_USER_ID = "UserId";
	public static final String COLUMN_PROPOSAL_ID = "ProposalID";
	public static final String COLUMN_PROPOSAL_NUM = "PoposalNum";
	public static final String COLUMN_BANK = "Bank";
	public static final String COLUMN_MORTGAGE_AMOUNT = "MortgageAmount";
	public static final String COLUMN_YEARS = "Years";
	public static final String COLUMN_TOTAL_REPAYMENT = "TotalRepayment";
	public static final String COLUMN_MONTH_REPAYMENT = "MonthRepayment";
	public static final String COLUMN_MY_MORTGAGE = "MyMortgage";		//	1 - mine,  0 - not mine
}
