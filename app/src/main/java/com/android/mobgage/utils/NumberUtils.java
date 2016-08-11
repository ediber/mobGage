package com.android.mobgage.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtils 
{
//	private static DecimalFormat df = new DecimalFormat("#,##0.00");
	private static DecimalFormat d = new DecimalFormat("#,##0");
	private static DecimalFormat dfnn = new DecimalFormat("#,##0.#");
	
	//	#,##0.00
	public static String formatStr(String numStr) 
	{
		if(numStr == null || numStr.isEmpty()) return "";
        Float numFloat = Float.parseFloat(numStr);
        return d.format(numFloat);
    }
	
	//	#,###%
    public static String formatedRoundWithPercent(String rounded) 
    {
        return  formatedRound(rounded) + "%";
    }
    
    //	#,###
    public static String formatedRound(String rounded) 
    {
    	int theNum;
    	
    	try
    	{
    		theNum = Integer.parseInt(rounded);
    	}
    	catch (NumberFormatException e)
    	{
    		double d = Double.parseDouble(rounded);
    		theNum = (int) (Math.round(d));
    	}
        String retVal = d.format(theNum);
        return retVal;
    }
    
    //	#,##0.#%
    public static String formatedRoundPrecision1(String unRounded) {
        String retVal = " ";
        try {
            float theNum = roundPrecision1(unRounded);
            retVal = dfnn.format(new Float(theNum));
            int pointPos = retVal.indexOf('.');
            if (retVal.length() - pointPos == 1) {
                retVal += "0";
            }
            return retVal + "%";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retVal;
    }
    
    public static String formatedRoundPrecision1NoPercent(String unRounded) 
    {
        String retVal = " ";
        try {
            float theNum = roundPrecision1(unRounded);
            retVal = dfnn.format(new Float(theNum));
            int pointPos = retVal.indexOf('.');
            if (retVal.length() - pointPos == 1) {
                retVal += "0";
            }
            return retVal;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retVal;
    }
    
    public static float roundPrecision1(String unRounded) 
    {
        BigDecimal a = new BigDecimal(unRounded);
        a = a.setScale(1, BigDecimal.ROUND_HALF_UP);
        return a.floatValue();
    }

    public static String doubleToMoney(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String amountStr = formatter.format(amount);
        return amountStr;
    }
}
