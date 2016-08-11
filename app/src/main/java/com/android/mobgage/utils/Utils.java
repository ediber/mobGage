package com.android.mobgage.utils;

import java.util.regex.PatternSyntaxException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.Route;
import com.android.mobgage.data.Types.RouteKinds;
import com.android.mobgage.data.UserDetails;
import com.android.mobgage.managers.DataManager;

public class Utils 
{
	public static final String REGEX_EMAIL_VALIDATION = "(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}\\b";
	
	public static boolean isStringMatchesRegex(String string, String regex)
	{
		try
		{
			if(string.matches(regex))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(PatternSyntaxException ex)
		{
			return false;
		}
	}
	
	public static boolean isNumeric(String str)
    {
        try
        {
           Double.valueOf(str);
        }
        catch(Exception nfe)
        {
            return false;
        }
        return true;
    }
	
	public static boolean isRoundedNumeric(String str)
    {
        try
        {
            Integer.valueOf(str);
        }
        catch(Exception nfe)
        {
            return false;
        }
        return true;
    }
	
	
	public static void showAlert(String title, String msg, String buttonText, Context ctx, DialogInterface.OnClickListener listener)
	{
		AlertDialog alert = new AlertDialog.Builder(ctx).create();
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setCanceledOnTouchOutside(true);
		alert.setCancelable(true);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, buttonText, listener);
		alert.show();
	}
	
	public static AlertDialog getAlert(String title, String msg, Context ctx)
	{
		AlertDialog alert = new AlertDialog.Builder(ctx).create();
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setCanceledOnTouchOutside(true);
		alert.setCancelable(true);
		return alert;
	}

	public static Proposal createProposal(UserDetails userDetails) {
		final Proposal proposal = new Proposal(userDetails.userID, DataManager.getInstance().generateUniqueID(), 0, 2, userDetails.mortgageAmount, 15, 0, userDetails.monthRepayment, 0);
		Route route1 = new Route(userDetails.userID, proposal.proposalID, 1, userDetails.mortgageAmount / 3, 5, (float) -0.9, 0, 1, RouteKinds.KIND_PRIME, 0);
		Route route2 = new Route(userDetails.userID, proposal.proposalID, 2, userDetails.mortgageAmount / 3, 20, (float) 2, 0, 1, RouteKinds.KIND_KAVUA_TZAMUD, 0);
		Route route3 = new Route(userDetails.userID, proposal.proposalID, 3, userDetails.mortgageAmount / 3, 15, (float) 1.6, 0, 1, RouteKinds.KIND_KAVUA_LO_TZAMUD, 0);
		DataManager.getInstance().calculate(route1);
		DataManager.getInstance().calculate(route2);
		DataManager.getInstance().calculate(route3);
		proposal.addOrUpdateRoute(route1);
		proposal.addOrUpdateRoute(route2);
		proposal.addOrUpdateRoute(route3);
		return proposal;
	}
}
