package com.android.mobgage.enums;

import com.android.mobgage.R;
import com.android.mobgage.managers.DataManager;

public enum EReturnMethod 
{
	EQUAL_FOUNDATION(0), SHPITZER(1), BOLIT(2);
	
	private int intValue;
	private EReturnMethod(int value)
	{
		intValue = value;
	}
	public static EReturnMethod toEReturnMethod(int value)
	{
		switch (value) 
		{
		case 0:
		{
			return EQUAL_FOUNDATION;
		}
		case 1:
		{
			return SHPITZER;
		}
		case 2:
		{
			return BOLIT;
		}
		default:
			return null;
		}
	}
	public String stringValue()
	{
		switch (intValue) 
		{
		case 0:
		{
			return DataManager.getInstance().getStringResource(R.string.equal_foundation);
		}
		case 1:
		{
			return DataManager.getInstance().getStringResource(R.string.shpitzer);
		}
		case 2:
		{
			return DataManager.getInstance().getStringResource(R.string.bolit);
		}
		default:
			return null;
		}
	}
	
	public int getIntValue()
	{
		return intValue;
	}
}
