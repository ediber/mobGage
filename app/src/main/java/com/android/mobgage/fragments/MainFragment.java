package com.android.mobgage.fragments;

import com.android.mobgage.R;
import com.android.mobgage.activities.MobgageMainActivity;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.UserDetails;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.Utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import static com.android.mobgage.utils.Utils.createProposal;

public class MainFragment extends Fragment implements OnClickListener {
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        view.findViewById(R.id.main_compare_button).setOnClickListener(this);
        view.findViewById(R.id.main_myMotogage_button).setOnClickListener(this);
        view.findViewById(R.id.main_recomend_button).setOnClickListener(this);

//		TextView tv = (TextView) view.findViewById(R.id.main_recomend_button_text);
//		tv.setTextColor(getResources().getColor(R.color.gray_text));

        view.findViewById(R.id.main_info_button).setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.main_compare_button:
                compareClick();
                break;
            case R.id.main_myMotogage_button:
                myMotogageClick();
                break;
            case R.id.main_recomend_button:
                getRecommendationClick();
                break;
            case R.id.main_info_button:
                infoClick();
                break;

            default:
                break;
        }
    }

    public void compareClick() {
        // If user's profile wasn't editted then launch to ReccomendationInfoFragment (to edit profile)
        // else launch to ProposalListFragment (get all proposals list)
        /*if((DataManager.getInstance().isShowUserDetailsScreen())) // Need to check...
		{
			moveToScreen(MobgageMainActivity.SCREEN_USER_DETAILS_FROM_MAIN, true);
		}
		else
		{
			moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, true);
		}*/

        moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, true);


    }

    public void myMotogageClick() {
        // Check my mortgage proposal
        Proposal myMptogage = DataManager.getInstance().getMyMotogage();
        // If user hasn't choosed his mortgage, show alert dialog which indicates it,
        // else PropsalDetailsFragment is launched and ...

        if (myMptogage == null) {
            Utils.showAlert(getResources().getString(R.string.alert4_title),
                    getResources().getString(R.string.alert4_msg),
                    getResources().getString(R.string.confirm), getActivity(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveToScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, true);
                        }
                    });
        } else {
            // Need to check...
            ActiveSelectionData.getInstance().setCurrentProposal(Proposal.copyProposal(myMptogage));
            moveToScreen(MobgageMainActivity.SCREEN_MY_MOTOGAGE, true);
        }
    }

    public void infoClick() {
        IntroFragment info = new IntroFragment(false, null);
        info.show(getFragmentManager(), "");
    }

    private void moveToScreen(int screen, boolean forward) {
        ((MobgageMainActivity) (getActivity())).showScreen(screen, forward);
    }

    private void getRecommendationClick() {
        UserDetails userDetails = ActiveSelectionData.getInstance().getCurrentUser();
        if (userDetails == null) {
            // TODO dialog about filling details
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.main_proposal_dialog_title));
            builder.setMessage(getResources().getString(R.string.main_proposal_dialog_text));
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    ((MobgageMainActivity)(getActivity())).showScreen(MobgageMainActivity.SCREEN_USER_DETAILS_FROM_RECOMMENDATION, true);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.no), null);
            builder.show();


            return;
        }
        final Proposal proposal = createProposal(userDetails);
        progressDialog = new ProgressDialog(getActivity());
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
}
