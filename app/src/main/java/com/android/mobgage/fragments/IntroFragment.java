package com.android.mobgage.fragments;

import android.app.Activity;
import android.view.*;
import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.interfaces.IBackCallback;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

public class IntroFragment extends DialogFragment
{
	private boolean firstTime;
	private IBackCallback callback;



	public IntroFragment(boolean isFirstTime, IBackCallback callback)
	{
		this.firstTime = isFirstTime;
		this.callback = callback;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		if(getDialog() == null)
		{
			View view = inflater.inflate(R.layout.fragment_intro, null);
			view.findViewById(R.id.intro_continue_button).setOnClickListener(new View.OnClickListener() 
			{	
				@Override
				public void onClick(View v) 
				{
					((MobgageMainActivity)getActivity()).showScreen(MobgageMainActivity.SCREEN_MAIN, true, null);
				}
			});
			return view;
		}
		else
		{
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(getActivity().getLayoutInflater().inflate(R.layout.fragment_intro, null));
		if(!firstTime)
		{
			dialog.findViewById(R.id.intro_continue_button_image).setVisibility(View.GONE);
			TextView btnText = (TextView) dialog.findViewById(R.id.intro_continue_button_text);
			btnText.setText(getResources().getString(R.string.back));
		}
		dialog.findViewById(R.id.intro_continue_button).setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				if(callback!=null) { callback.back(); }
				dismiss();
			}
		});
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
					//return firstTime ? true : false;

                    return false;
				}
				return false;
			}
		});
		
		return dialog;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) 
	{
		super.onDismiss(dialog);
        if(firstTime)
            getActivity().onBackPressed();
        else
		    dismiss();
	}
	
	@Override
	public void onActivityCreated(Bundle bundle) 
	{
		super.onActivityCreated(bundle);
		if(getDialog() != null)
		{
			if(!firstTime)
			{
				getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
			}
			else
			{
				getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
			}
		}
	}
}
