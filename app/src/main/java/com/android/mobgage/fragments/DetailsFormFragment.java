package com.android.mobgage.fragments;

import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.UserDetails;
import com.android.mobgage.dialogs.PickerDialog;
import com.android.mobgage.interfaces.IPickerCallback;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.NumberUtils;
import com.android.mobgage.utils.Utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import static com.android.mobgage.utils.Utils.createProposal;

public class DetailsFormFragment extends Fragment implements OnClickListener, TextWatcher, OnFocusChangeListener
{
	private static final String CAME_FROM = "cameFrom";
	public static final int MAIN = 0;
	public static final int RECOMMENDATION = 1;

	EditText ETmortgageAmount, ETmonthRepayment, ETassetValue, ETEmail, ETsalary;
	TextView TVassetLocation, TVleveragePercent;
	EditText currentFocusEditText = null;
    double percent;
    double leverageThtreshold = 75;
    boolean isShowLeverageAlert = true;
	private int cameFrom;

	public static DetailsFormFragment newInstance(int cameFrom){
		DetailsFormFragment fragment = new DetailsFormFragment();

		Bundle args = new Bundle();
		args.putInt(CAME_FROM, cameFrom);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			cameFrom = getArguments().getInt(CAME_FROM);
		}
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{

        View view = inflater.inflate(R.layout.fragment_details_form, null);
		view.findViewById(R.id.form_continue_button).setOnClickListener(this);
		view.findViewById(R.id.form_asset_location).setOnClickListener(this);
		
		ETmortgageAmount = (EditText) view.findViewById(R.id.form_mortgage_amount);
		ETmonthRepayment = (EditText) view.findViewById(R.id.form_month_repayment);
		ETassetValue = (EditText) view.findViewById(R.id.form_assetAmount);
		ETEmail = (EditText) view.findViewById(R.id.form_email);
        ETsalary = (EditText) view.findViewById(R.id.form_salary);
		TVassetLocation = (TextView) view.findViewById(R.id.form_asset_location);
		TVleveragePercent = (TextView) view.findViewById(R.id.form_motgage_percent);
		
		if(ActiveSelectionData.getInstance().getCurrentUser() != null)
		{
			initFields();
		}
		
		ETmortgageAmount.addTextChangedListener(this);
		ETmonthRepayment.addTextChangedListener(this);
		ETassetValue.addTextChangedListener(this);
        ETsalary.addTextChangedListener(this);
		
		ETmortgageAmount.setOnFocusChangeListener(this);
		ETmonthRepayment.setOnFocusChangeListener(this);
		ETassetValue.setOnFocusChangeListener(this);
        ETsalary.setOnFocusChangeListener(this);


        return view;
	}
	
	private void initFields()
	{
		UserDetails user = ActiveSelectionData.getInstance().getCurrentUser();
		
		ETmortgageAmount.setText(NumberUtils.formatedRound((int)user.mortgageAmount + ""));
		ETmonthRepayment.setText(NumberUtils.formatedRound((int)user.monthRepayment + ""));
		ETassetValue.setText(NumberUtils.formatedRound((int)user.assetValue + ""));
		ETEmail.setText(user.email);
		ETsalary.setText(NumberUtils.formatedRound((int)user.freeIncoming + ""));
		TVassetLocation.setText(user.assetLocation);
		updateTVleveragePercent();
	}

	@Override
	public void onClick(View v) 
	{
		final int id = v.getId();
		switch (id) 
		{
		case R.id.form_continue_button: continueClick(); break;
		case R.id.form_asset_location: assetLocationClick(); break;
		default:break;
		}
	}
	
	public void continueClick()
	{	
		if(!(isInputValid()))
		{
			Utils.showAlert(getResources().getString(R.string.form_alert_title), 
					getResources().getString(R.string.form_alert_msg), getResources().getString(R.string.back) ,getActivity(), null);
		}
		else if(!(isEmailValid()))
		{
			Utils.showAlert(getResources().getString(R.string.form_alert_title2), 
					getResources().getString(R.string.form_alert_msg2), getResources().getString(R.string.back) ,getActivity(), null);
		}
		else
		{
			String email = ETEmail.getText().toString();
			String userID = email;

			String mortgageAmountText = ETmortgageAmount.getText().toString().replace(",", "");
			float mortgageAmount = Float.parseFloat(mortgageAmountText);

			String monthRepaymentText = ETmonthRepayment.getText().toString().replace(",", "");
			float monthRepayment = Float.parseFloat(monthRepaymentText);

			String assetValueText = ETassetValue.getText().toString().replace(",", "");
			float assetValue = Float.parseFloat(assetValueText);

			String assetLocation = TVassetLocation.getText().toString();

			String leveragePercentText = TVleveragePercent.getText().toString().replace("%", "");
			float leveragePercent = Float.parseFloat(leveragePercentText);

			String freeIncomingText = ETsalary.getText().toString().replace(",", "");
			float freeIncoming = Float.parseFloat(freeIncomingText);


			UserDetails user = new UserDetails(userID, email, mortgageAmount, monthRepayment,
					assetValue, assetLocation, leveragePercent, freeIncoming);
			
			if(ActiveSelectionData.getInstance().getCurrentUser() == null)
			{
				DataManager.getInstance().insertUser(user);
				DataManager.getInstance().addUser(user);
				ActiveSelectionData.getInstance().setCurrentUser(user);
				DataManager.getInstance().setShowUserDetailsScreen(false);
//				moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, true);
			}
			else
			{
				UserDetails previousDetails = ActiveSelectionData.getInstance().getCurrentUser();
				boolean updatedUserIdAllTables = (!(previousDetails.userID.equals(user.userID)));
				
				DataManager.getInstance().insertUser(user);
				DataManager.getInstance().addUser(user);
				ActiveSelectionData.getInstance().setCurrentUser(user);
				
				if(updatedUserIdAllTables)
				{
					DataManager.getInstance().updateUserIdInAllTables(user.userID);
				}
			}

            if(cameFrom == MAIN){
                getActivity().onBackPressed();
            } else if(cameFrom == RECOMMENDATION){

                openRecommendation();				}

		}
	}

    private void openRecommendation() {
        UserDetails userDetails = ActiveSelectionData.getInstance().getCurrentUser();
        final Proposal proposal = createProposal(userDetails);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.recommendation_dialog));
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                    ActiveSelectionData.getInstance().setCurrentProposal(proposal);
                    moveToScreen(MobgageMainActivity.SCREEN_FRIEND_RECOMMENDATION, true);
                }
            }
        }, 3500);
    }

    public void assetLocationClick()
	{
		String title = getResources().getString(R.string.city_picker_title);
		String[] data = getResources().getStringArray(R.array.cities);
		PickerDialog cityPicker = new PickerDialog(title, data, new IPickerCallback() 
		{
			@Override
			public void onUserSelection(String stringValue, int position) 
			{
				TVassetLocation.setText(stringValue);
			}
		}, false);
		cityPicker.show(getFragmentManager(), "");
	}

	//	Text Watch
	@Override
	public void afterTextChanged(Editable s) 
	{
		try 
		{
			String text = s.toString();
			int cursorPosition = currentFocusEditText.getSelectionStart();
			int leftSize = text.substring(0, cursorPosition).split(",").length - 1;
			int rightSize = text.substring(cursorPosition, text.length()).split(",").length - 1;
			cursorPosition = cursorPosition - leftSize + rightSize;
			text = text.replace(",", "");
			text = NumberUtils.formatStr(text);
			leftSize = text.substring(0, cursorPosition).split(",").length - 1;
			rightSize = text.substring(cursorPosition, text.length()).split(",").length - 1;
			cursorPosition = cursorPosition + leftSize - rightSize;
			currentFocusEditText.removeTextChangedListener(this);
			currentFocusEditText.setText(text);
			currentFocusEditText.setSelection(cursorPosition);
			currentFocusEditText.addTextChangedListener(this);
		}
		catch (Exception e)
		{
			String text = s.toString();
			text = text.replace(",", "");
			text = NumberUtils.formatStr(text);
			currentFocusEditText.removeTextChangedListener(this);
			currentFocusEditText.setText(text);
			currentFocusEditText.setSelection(text.length());
			currentFocusEditText.addTextChangedListener(this);
		}
		if(currentFocusEditText==ETmortgageAmount || currentFocusEditText==ETassetValue)
		{
			updateTVleveragePercent();
		}
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	//	Text Watch
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		if(hasFocus)
		{
			currentFocusEditText = (EditText)v;

            if(ETmortgageAmount.getText().toString().length() > 0 &&
                    ETassetValue.getText().toString().length() > 0 &&
                    percent > leverageThtreshold &&
                    isShowLeverageAlert){
                AlertDialog dialog = Utils.getAlert(getResources().getString(R.string.alert6_title),
                        getResources().getString(R.string.alert6_msg), getActivity());
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm)
                        , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                // dialog.setCancelable(false);
                dialog.show();
                isShowLeverageAlert = false;
            }
		}



	}
	
	public void updateTVleveragePercent()
	{
		String amountText = ETmortgageAmount.getText().toString();
		amountText = amountText.replace(",", "");
		if(amountText.length()==0) { amountText = "0.00"; }
		String assetValueText = ETassetValue.getText().toString();
		assetValueText = assetValueText.replace(",", "");
		if(assetValueText.length()==0) { assetValueText = "0.00"; }
		
		double amount = Double.parseDouble(amountText);
        double assuetValue = Double.parseDouble(assetValueText);
		percent = (amount/assuetValue)*100;
		String percentString = NumberUtils.formatedRoundWithPercent(percent+"");


		TVleveragePercent.setText(percentString);
	}
	
	private void moveToScreen(int screen, boolean forward)
	{
		((MobgageMainActivity)(getActivity())).showScreen(screen, forward);
	}
	
	private boolean isInputValid()
	{
		String email = ETEmail.getText().toString();
		String mortgageAmountText = ETmortgageAmount.getText().toString();
		String monthRepaymentText = ETmonthRepayment.getText().toString();
		String assetValueText = ETassetValue.getText().toString();
		String assetLocation = TVassetLocation.getText().toString();
		String freeIncoming = ETsalary.getText().toString();
		
		return (!(email.isEmpty() || mortgageAmountText.isEmpty() || monthRepaymentText.isEmpty() || 
				freeIncoming.isEmpty() || assetValueText.isEmpty() || assetLocation.equals(getResources().getString(R.string.form_choose))));
	}
	private boolean isEmailValid()
	{
		String email = ETEmail.getText().toString();
		return (Utils.isStringMatchesRegex(email, Utils.REGEX_EMAIL_VALIDATION));
	}
}
