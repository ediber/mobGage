package com.android.mobgage.dialogs;

import com.android.mobgage.R;
import com.android.mobgage.interfaces.IPickerCallback;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PickerDialog extends DialogFragment implements OnItemClickListener
{
	private IPickerCallback callback;
	private String[] data;
	private String title;
	private boolean wrapCpntentHeight;
	
	public PickerDialog(String title, String[] data , IPickerCallback callback, boolean wrapCpntentHeight)
	{
		this.data = data;
		this.callback = callback;
		this.title = title;
		this.wrapCpntentHeight = wrapCpntentHeight;
	}	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Display display =  getActivity().getWindowManager().getDefaultDisplay();
 		Point size = new Point();
 		display.getSize(size);
 		final int zoomDialogWidth = (int)(size.x*0.80);
 		final int zoomDialogHeight = (int)(size.y*0.70);
 		
    	dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(getActivity().getLayoutInflater().inflate(R.layout.dialog_picker, null));
		TextView titleTV = (TextView)dialog.findViewById(R.id.title);
		titleTV.setText(title);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_picker_row, R.id.text, data);
		ListView listView = (ListView) dialog.findViewById(R.id.list);
		listView.setFadingEdgeLength(0);
        listView.setSelector(R.drawable.picker_selector); 
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
// 		dialog.setOnShowListener(new OnShowListener()
// 		{
//			@Override
//			public void onShow(DialogInterface dialogI)
//			{
//				if(wrapCpntentHeight)
//				{
//					int currentHeight = dialog.getWindow().getAttributes().height;
//					dialog.getWindow().setLayout(zoomDialogWidth, currentHeight);
//				}
//				else
//				{
//					dialog.getWindow().setLayout(zoomDialogWidth, zoomDialogHeight);
//				}
//			}
//		});
		if(wrapCpntentHeight)
				{
					int currentHeight = dialog.getWindow().getAttributes().height;
					dialog.getWindow().setLayout(zoomDialogWidth, currentHeight);
				}
				else
				{
					dialog.getWindow().setLayout(zoomDialogWidth, zoomDialogHeight);
				}

		return dialog;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		callback.onUserSelection(data[position], position);
		dismiss();
	}
	@Override
	public void onDismiss(DialogInterface dialog) 
	{
		super.onDismiss(dialog);
		dismiss();
	}
}
