package com.android.mobgage.dialogs;

import com.android.mobgage.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class TipsDialog extends DialogFragment 
{
	private int activeScreen;
	
	public TipsDialog(int activeScreen)
	{
		this.activeScreen = activeScreen;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.setContentView(getActivity().getLayoutInflater().inflate(R.layout.dialog_tips, null));
		TextView msgText = (TextView) dialog.findViewById(R.id.tips_msg);
		String[] tips = getResources().getStringArray(R.array.tips);
		String msg = "";
		if(activeScreen < tips.length)
		{
			msg = (tips)[activeScreen];
		}
		msgText.setText(msg);
		dialog.findViewById(R.id.tips_close).setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				dismiss();
			}
		});
		
		return dialog;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) 
	{
		super.onDismiss(dialog);
		dismiss();
	}
	
	@Override
	public void onActivityCreated(Bundle bundle) 
	{
		super.onActivityCreated(bundle);
		if(getDialog() != null)
		{
			getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		}
	}
}
