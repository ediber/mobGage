package com.android.mobgage.fragments;

import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.Route;
import com.android.mobgage.data.Types.RouteKinds;
import com.android.mobgage.dialogs.PickerDialog;
import com.android.mobgage.enums.EReturnMethod;
import com.android.mobgage.interfaces.IPickerCallback;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.NumberUtils;
import com.android.mobgage.utils.Utils;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteDetailsFragment extends Fragment implements OnClickListener, TextWatcher {
    private Route route;
    private EditText ETloanAmount, ETinterest;
    private TextView TVreturnMethod, TVyears, TVmonthRepayment;
    private TextView TVChangeYears;
    private ImageView monthRepaymentImage;
    private String errorMsg;
    private int selectedReturnMethod;

    private float monthRepayment;
    private float totalRepayment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_details, null);
        route = ActiveSelectionData.getInstance().getCurrentRoute();

        TextView title = (TextView) view.findViewById(R.id.route_title);
        String routKindName = DataManager.getInstance().getRouteKindByID(route.routeKind).routeKindName;
        String maslul = getResources().getString(R.string.route);

        Proposal proposal = ActiveSelectionData.getInstance().getCurrentProposal();
        int routePosition = proposal.getRoutePositionByRouteNum(route.routeNum);

        String titleText = maslul + " " + (routePosition) + " - " + routKindName;
        title.setText(titleText);

        if (route.routeKind == 3 || route.routeKind == 4) {

        }

        view.findViewById(R.id.route_save_route_btn).setOnClickListener(this);
        view.findViewById(R.id.route_return_method).setOnClickListener(this);
        view.findViewById(R.id.route_years).setOnClickListener(this);
        view.findViewById(R.id.route_change_every_years).setOnClickListener(this);

        TextView TVinterestLabel = (TextView) view.findViewById(R.id.route_interest_label);
        if (route.routeKind == RouteKinds.KIND_PRIME) {
            TVinterestLabel.setText(getResources().getString(R.string.route_text2));
        } else {
            TVinterestLabel.setText(getResources().getString(R.string.route_text3));
        }

        ETloanAmount = (EditText) view.findViewById(R.id.route_loan_amount);
        ETinterest = (EditText) view.findViewById(R.id.route_interest);
        TVyears = (TextView) view.findViewById(R.id.route_years);
        TVreturnMethod = (TextView) view.findViewById(R.id.route_return_method);
        TVmonthRepayment = (TextView) view.findViewById(R.id.route_moth_repayment);
        monthRepaymentImage = (ImageView) view.findViewById(R.id.route_moth_repayment_image);
        TVChangeYears = (TextView) view.findViewById(R.id.route_change_every_years);

        if (ActiveSelectionData.getInstance().getCurrentProposal().isRouteExist(route.routeNum) != null) {
            initFields();
        } else {
            EReturnMethod returnMethod = EReturnMethod.SHPITZER;
            selectedReturnMethod = returnMethod.getIntValue();
            TVreturnMethod.setText(returnMethod.stringValue());
        }

        ETloanAmount.addTextChangedListener(this);
        ETinterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();
            }
        });

        return view;
    }

    private void initFields() {
        ETloanAmount.setText(NumberUtils.formatedRound((int) route.loanAmount + ""));
        ETinterest.setText(NumberUtils.formatedRoundPrecision1NoPercent(route.interest + ""));
        TVyears.setText(route.years + "");

        EReturnMethod returnMethod = EReturnMethod.toEReturnMethod(route.returnMethod);
        selectedReturnMethod = returnMethod.getIntValue();
        TVreturnMethod.setText(returnMethod.stringValue());

        calculate();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.route_save_route_btn:
                saveRoute();
                break;
            case R.id.route_return_method:
                returnMethodClick();
                break;
            case R.id.route_years:
                yearsClick();
                break;
            case R.id.route_change_every_years:
                changeYearsClick();
                break;


            default:
                break;
        }
    }


    private void saveRoute() {
        if (calculate()) {
            String returnMethod = TVreturnMethod.getText().toString();
            if (returnMethod.equals(getResources().getString(R.string.form_choose))) {
                errorMsg = getResources().getString(R.string.route_alert_msg1);
                Utils.showAlert(getResources().getString(R.string.route_alert_title), errorMsg, getResources().getString(R.string.back), getActivity(), null);
            } else {
                // add the route
                String S_loan = ETloanAmount.getText().toString();
                S_loan = S_loan.replace(",", "");
                String S_interest = ETinterest.getText().toString();
                String S_years = TVyears.getText().toString();

                float loan = Float.valueOf(S_loan);
                float interest = Float.valueOf(S_interest);
                int years = Integer.valueOf(S_years);
                int mReturnMethod = selectedReturnMethod;

                Route route = ActiveSelectionData.getInstance().getCurrentRoute();
                route.loanAmount = loan;
                route.interest = interest;
                route.years = years;
                route.returnMethod = mReturnMethod;
                route.monthRepayment = monthRepayment;
                route.totalRepayment = totalRepayment;

                ActiveSelectionData.getInstance().getCurrentProposal().addOrUpdateRoute(route);
                ActiveSelectionData.getInstance().clearRoute();
                moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_DETAILS, true);
            }
        } else {
            Utils.showAlert(getResources().getString(R.string.route_alert_title), errorMsg, getResources().getString(R.string.back), getActivity(), null);
        }
    }

    private boolean calculate() {
        String S_loan = ETloanAmount.getText().toString();
        String S_interest = ETinterest.getText().toString();
        String S_years = TVyears.getText().toString();

        if (S_loan.isEmpty() || S_interest.isEmpty() || S_years.equals(getResources().getString(R.string.form_choose))) {
            errorMsg = getResources().getString(R.string.route_alert_msg1);
            removeMonthRepayment();
            return false;
        } else if (!(Utils.isNumeric(S_interest))) {
            errorMsg = getResources().getString(R.string.route_alert_msg2);
            removeMonthRepayment();
            return false;
        } else {
            S_loan = S_loan.replace(",", "");
            float loan = Float.valueOf(S_loan);
            float interest = Float.valueOf(S_interest);
            int years = Integer.valueOf(S_years);

            switch (route.routeKind) {
                case RouteKinds.KIND_PRIME: {
                    switch (selectedReturnMethod) {
                        case 1: //Shpizzer
//						double C6 = DataManager.PRIME_INTEREST + DataManager.BANK_ISRAEL_INTEREST
//								-  interest;
//						double C7 = (Math.pow((1+(C6/100.00)), (1.00/12.00))-1)*100.00;
//						double C9 = years*12;
//						double C10 = (1+(C7/100.00))*100.00;
//						double C11 = Math.pow((C10/100.00), C9);
//						double C12 = 1/C11;
//						double C13 = 1-C12;
//						double C14 = (C7/100.00)/C13;
//						monthRepayment = (float) (C14*loan);
//						totalRepayment = (float) (monthRepayment*C9);

                            double annualLoonInterest = DataManager.PRIME_INTEREST +
                                    DataManager.BANK_ISRAEL_INTEREST - interest;
                            double montlyLoanInterest = annualLoonInterest / 12 / 100;
                            int monthsPaymentNum = years * 12;
                            double numerator = montlyLoanInterest *
                                    Math.pow(1 + montlyLoanInterest, monthsPaymentNum);
                            double denominator = Math.pow(1 + montlyLoanInterest, monthsPaymentNum) - 1;
                            double cooefficient = numerator / denominator;
                            monthRepayment = (float) (cooefficient * loan);
                            totalRepayment = (float) (monthRepayment * monthsPaymentNum);


                            break;

                    }

                    break;
                }
//			case RouteKinds.KIND_KAVUA_TZAMUD:
//			{
//				
//				break;
//			}
//			case RouteKinds.KIND_KAVUA_LO_TZAMUD:
//			{
//				
//				break;
//			}
//			case RouteKinds.KIND_MISHTANA_TZAMUD:
//			{
//				
//				break;
//			}
//			case RouteKinds.KIND_MISHTANA_LO_TZAMUD:
//			{
//				
//				break;
//			}
                case RouteKinds.KIND_DOLAR: {
                    break;
                }
                case RouteKinds.KIND_MAKAM: {
                    break;
                }
                default: {
                    double C6 = interest;
                    double C7 = (Math.pow((1 + (C6 / 100.00)), (1.00 / 12.00)) - 1) * 100.00;
                    double C9 = years * 12;
                    double C10 = (1 + (C7 / 100.00)) * 100.00;
                    double C11 = Math.pow((C10 / 100.00), C9);
                    double C12 = 1 / C11;
                    double C13 = 1 - C12;
                    double C14 = (C7 / 100.00) / C13;
                    monthRepayment = (float) (C14 * loan);
                    totalRepayment = (float) (monthRepayment * C9);
                    break;
                }
            }
            showMonthRepayment();
            return true;
        }
    }


    private void returnMethodClick() {
        String title = getResources().getString(R.string.return_method_picker_title);
        String[] data = {EReturnMethod.EQUAL_FOUNDATION.stringValue(), EReturnMethod.SHPITZER.stringValue(), EReturnMethod.BOLIT.stringValue()};
        PickerDialog picker = new PickerDialog(title, data, new IPickerCallback() {
            @Override
            public void onUserSelection(String stringValue, int position) {
                selectedReturnMethod = position;
                TVreturnMethod.setText(stringValue);
            }
        }, true);
        picker.show(getFragmentManager(), "");
    }

    private void yearsClick() {
        String title = getResources().getString(R.string.years_picker_title);
        String[] data = new String[30];
        for (int i = 1; i <= 30; i++) {
            data[i - 1] = i + "";
        }

        PickerDialog picker = new PickerDialog(title, data, new IPickerCallback() {
            @Override
            public void onUserSelection(String stringValue, int position) {
                TVyears.setText(stringValue);
                calculate();
            }
        }, false);
        picker.show(getFragmentManager(), "");
    }

    private void changeYearsClick() {
        String title = getResources().getString(R.string.change_years_picker_title);
        String[] data = new String[3];
        data[0] = "1";
        data[1] = "3";
        data[2] = "5";

        PickerDialog picker = new PickerDialog(title, data, new IPickerCallback() {
            @Override
            public void onUserSelection(String stringValue, int position) {
                TVChangeYears.setText(stringValue);
                calculate();
            }
        }, false);
        picker.show(getFragmentManager(), "");
    }



    //	Text Watch
    @Override
    public void afterTextChanged(Editable s) {
        try {
            String text = s.toString();
            int cursorPosition = ETloanAmount.getSelectionStart();
            int leftSize = text.substring(0, cursorPosition).split(",").length - 1;
            int rightSize = text.substring(cursorPosition, text.length()).split(",").length - 1;
            cursorPosition = cursorPosition - leftSize + rightSize;
            text = text.replace(",", "");
            text = NumberUtils.formatStr(text);
            leftSize = text.substring(0, cursorPosition).split(",").length - 1;
            rightSize = text.substring(cursorPosition, text.length()).split(",").length - 1;
            cursorPosition = cursorPosition + leftSize - rightSize;
            ETloanAmount.removeTextChangedListener(this);
            ETloanAmount.setText(text);
            ETloanAmount.setSelection(cursorPosition);
            ETloanAmount.addTextChangedListener(this);
        } catch (Exception e) {
            String text = s.toString();
            text = text.replace(",", "");
            text = NumberUtils.formatStr(text);
            ETloanAmount.removeTextChangedListener(this);
            ETloanAmount.setText(text);
            ETloanAmount.setSelection(text.length());
            ETloanAmount.addTextChangedListener(this);
        }
        calculate();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
    //	Text Watch


    private void moveToScreen(int screen, boolean forward) {
        ((MobgageMainActivity) (getActivity())).showScreen(screen, forward);
    }


    private void showMonthRepayment() {
        int intMonthRepayment = (int) monthRepayment;
        String s = NumberUtils.formatedRound(intMonthRepayment + "");

        TVmonthRepayment.setText(s);
        TVmonthRepayment.setVisibility(View.VISIBLE);
        monthRepaymentImage.setVisibility(View.VISIBLE);
    }

    private void removeMonthRepayment() {
        TVmonthRepayment.setVisibility(View.GONE);
        monthRepaymentImage.setVisibility(View.GONE);
    }
}
