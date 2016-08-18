package com.android.mobgage.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import com.android.mobgage.R;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.dialogs.TipsDialog;
import com.android.mobgage.fragments.ChooseBankFragment;
import com.android.mobgage.fragments.ChooseRouteFragment;
import com.android.mobgage.fragments.MainFragment;
import com.android.mobgage.fragments.ProposalDetailsFragment;
import com.android.mobgage.fragments.ProposalListFragment;
import com.android.mobgage.fragments.DetailsFormFragment;
import com.android.mobgage.fragments.RouteDetailsFragment;
import com.android.mobgage.fragments.SimulationCompareFragment;
import com.android.mobgage.fragments.SimulationInitFragment;
import com.android.mobgage.fragments.SimulationSingleFragment;
import com.android.mobgage.interfaces.ISortButtonCallback;
import com.android.mobgage.managers.ActiveSelectionData;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.utils.Utils;

public class MobgageMainActivity extends Activity implements OnClickListener {
    public static final int SCREEN_INTRO = 7;
    public static final int SCREEN_MAIN = 2;
    public static final int SCREEN_CHOOSE_BANK = 0;
    public static final int SCREEN_CHOOSE_ROUTE = 1;
    public static final int SCREEN_PROPOSAL_DETAILS = 3;
    public static final int SCREEN_PROPOSAL_LIST = 4;
    public static final int SCREEN_USER_DETAILS_FROM_MAIN = 5;
    public static final int SCREEN_ROUTE_DETAILS = 6;
    public static final int SCREEN_MY_MOTOGAGE = 8;
    public static final int SCREEN_FRIEND_RECOMMENDATION = 9;
    public static final int SCREEN_USER_DETAILS_FROM_RECOMMENDATION = 10;
    public static final int SCREEN_USER_SIMULATION_INIT = 11;
    public static final int SCREEN_USER_SIMULATION_COMPARE = 12;
    public static final int SCREEN_USER_SIMULATION_SINGLE = 13;

    public static final int FILTER_BANK = 0;
    public static final int FILTER_MONTH_REPAYMENT = 1;
    public static final int FILTER_TOTAL_REPAYMENT = 2;
    public static final int FILTER_MOTGAGE_AMOUNT = 3;
    public static final int FILTER_YEARS = 4;
    public static final int FILTER_PROPOSAL_NUM = 5;

    private int activeScreen = SCREEN_MAIN;
    private int previousActiveScreen = SCREEN_MAIN;
    private boolean startEditProfile = false;
    private View sortButton;
    private ISortButtonCallback sortButtonCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataManager.setContext(this);
        setContentView(R.layout.main);
        initActionBar();
        showScreen(SCREEN_MAIN, true, null);
        //activeScreen = SCREEN_INTRODUCTION_DIALOG;
    }

    public void initActionBar() {
        View actionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
        actionBarView.findViewById(R.id.help_button).setOnClickListener(this);

        sortButton = actionBarView.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(this);

        actionBarView.findViewById(R.id.menu_button).setOnClickListener(this);
        actionBarView.findViewById(R.id.simulation_button).setOnClickListener(this);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarView);
        final Drawable actionBarBG = getResources().getDrawable(R.drawable.action_bar_bg);
        actionBar.setBackgroundDrawable(actionBarBG);
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(sortButton.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }
    //

    public void showScreen(int screen, boolean forward, String extra) {
        previousActiveScreen = activeScreen;
        sortButton.setVisibility(View.INVISIBLE);
        hideKeyboard();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (!(forward && screen == SCREEN_MAIN)) {
            if (forward) {
                transaction.setCustomAnimations(R.anim.slide_in_right2, R.anim.slide_out_left2);        //	set animation in
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_left2, R.anim.slide_out_right2);        //	set animation out
            }
        }

        switch (screen) {
        /* Screen with options of
		 1. My Mortgage --> clicking --> if user hasn't defined his motgage, alert dialog which notify
		    that user should define his mortgage, else PropsalDetailsFragment will be launched
		    Clicking back button, application will exit
		 2. Proposals comaparison --> clicking --> if user's profile was editted
		 	(Edit Profile in menu button) ProposalListFragment is launched else ReccomendationInfoFragment
		 	is launched
		 3. What is MOBGAGE --> clicking --> IntroFragment is launched
		 4. Want reccommendation --> clicking --> curreently no functionality

		 Action bar contains options of
		 1. Menu --> 1 option is shown Edit profile --> clicking --> ReccomendationFormFragment is launched
		 2. Help --> clicking --> TipDialog (help) is launched
		*/
            case SCREEN_MAIN: {
                activeScreen = SCREEN_MAIN;
                DataManager.getInstance().setIsInMortgageFlow(false);
                MainFragment main = new MainFragment();
                transaction.replace(R.id.frame, main);
                break;
            }
		/*  Screen in which user can edit his profile. Screen options:
			1. Continue --> clicking --> If user's profile was already editted then ProposalListFragment
			   is launched, else previous screen is launched. Application crash can occur if user
			   insert unreasonable inputs to his profile - need to take care of it!!!!
		 */
            case SCREEN_USER_DETAILS_FROM_MAIN: {
                activeScreen = SCREEN_USER_DETAILS_FROM_MAIN;
                DetailsFormFragment detailsFormFragment = DetailsFormFragment.newInstance(DetailsFormFragment.MAIN);
                transaction.replace(R.id.frame, detailsFormFragment);
                break;
            }

            case SCREEN_USER_DETAILS_FROM_RECOMMENDATION: {
                activeScreen = SCREEN_USER_DETAILS_FROM_MAIN;
                DetailsFormFragment detailsFormFragment = DetailsFormFragment.newInstance(DetailsFormFragment.RECOMMENDATION);
                transaction.replace(R.id.frame, detailsFormFragment);
                break;
            }

		/* Screen in which Banks list is shown.
		   Clicking in each of item in the list --> ChooseRouteFragment is launched
		 */
            case SCREEN_CHOOSE_BANK: {
                activeScreen = SCREEN_CHOOSE_BANK;
                ChooseBankFragment chooseBank = new ChooseBankFragment();
                transaction.replace(R.id.frame, chooseBank);
                break;
            }

		/* Screen in which Routes list is shown.
		   Clicking in each of item in the list --> RouteDetailsFragment is launched
		 */
            case SCREEN_CHOOSE_ROUTE: {
                activeScreen = SCREEN_CHOOSE_ROUTE;
                ChooseRouteFragment chooseRoute = new ChooseRouteFragment();
                transaction.replace(R.id.frame, chooseRoute);
                break;
            }

		/* Screen in which Route details is shown (user needs to fullfill it). Screen options:
		   1. Save route --> clicking --> if 1 of the fields is missing, appropriate alert dialog
		      is shown, else ProposalDetailsFragment is launched (the last fullfilled route is added_
		 */
            case SCREEN_ROUTE_DETAILS: {
                activeScreen = SCREEN_ROUTE_DETAILS;
                RouteDetailsFragment routeDetails = new RouteDetailsFragment();
                transaction.replace(R.id.frame, routeDetails);
                break;
            }

		/* Screen in which all routes per bank are shown. Screen options:
		   1. 3 points button in each route --> clicking --> 2 options are shown:
		   	  1. edit --> clicking --> RouteDetailsFragment is launched fot editting
		   	  2. delete --> clicking --> route is deleted
		   2. Add route --> clicking --> ChooseRouteFragment is launched
		   3. Save proposal --> clicking --> ProposalListFragment is launched
		 */
            case SCREEN_PROPOSAL_DETAILS: {
                activeScreen = SCREEN_PROPOSAL_DETAILS;
                ProposalDetailsFragment proposalDetails = new ProposalDetailsFragment();
                transaction.replace(R.id.frame, proposalDetails);
                break;
            }
        /* Screen in which all proposals are shown. Screen options:
           1. Add proposal --> clicking --> ChooseBankFragment is launched
           2. Get reccomendation --> clicking --> ProposalListFragment is launched
           3. Add reccommendation --> clicking --> ChooseBankFragmentReccommendation is launched
         */
            case SCREEN_PROPOSAL_LIST: {
                sortButton.setVisibility(View.VISIBLE);
                activeScreen = SCREEN_PROPOSAL_LIST;
                ProposalListFragment proposalList = new ProposalListFragment();
                this.sortButtonCallback = proposalList;
                transaction.replace(R.id.frame, proposalList);
                break;
            }

        /* Screen in which user's mortgage is shown (proposalDetailsFragment)

         */
            case SCREEN_MY_MOTOGAGE: {
                activeScreen = SCREEN_MY_MOTOGAGE;
                ProposalDetailsFragment proposalDetails = new ProposalDetailsFragment();
                proposalDetails.setReadOnly(false);
                DataManager.getInstance().setIsInMortgageFlow(true);
                transaction.replace(R.id.frame, proposalDetails);
                break;
            }

            case SCREEN_FRIEND_RECOMMENDATION:
                activeScreen = SCREEN_FRIEND_RECOMMENDATION;
                ProposalDetailsFragment proposalDetails = new ProposalDetailsFragment();
                proposalDetails.setReadOnly(true);
                transaction.replace(R.id.frame, proposalDetails);
                break;

            /*case SCREEN_USER_DETAILS_FROM_MAIN: {
                activeScreen = SCREEN_USER_DETAILS_FROM_MAIN;
                DetailsFormFragment detailsFormFragment = new DetailsFormFragment();
                transaction.replace(R.id.frame, detailsFormFragment);
                break;
            }*/

            case SCREEN_USER_SIMULATION_INIT:
                activeScreen = SCREEN_USER_SIMULATION_INIT;
                SimulationInitFragment simulationInitFragment = SimulationInitFragment.newInstance();
                transaction.replace(R.id.frame, simulationInitFragment);
                break;

            case SCREEN_USER_SIMULATION_COMPARE:
                activeScreen = SCREEN_USER_SIMULATION_COMPARE;
                SimulationCompareFragment simulationCompareFragment = SimulationCompareFragment.newInstance();
                transaction.replace(R.id.frame, simulationCompareFragment);
                break;

            case SCREEN_USER_SIMULATION_SINGLE:
                activeScreen = SCREEN_USER_SIMULATION_SINGLE;
                SimulationSingleFragment simulationSingleFragment = SimulationSingleFragment.newInstance(extra);
                transaction.replace(R.id.frame, simulationSingleFragment);
                break;

            default:
                break;
        }
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        Proposal current = ActiveSelectionData.getInstance().getCurrentProposal();
        boolean isEditMode = false;
        if (current != null) {
            isEditMode = (DataManager.getInstance().getProposalByProposalID(current.proposalID) != null) || DataManager.getInstance().isInMortgageFlow();
        }

        switch (activeScreen) {
            case SCREEN_MAIN: {
                //finish();
                moveTaskToBack(true);
                break;
            }
            case SCREEN_USER_DETAILS_FROM_MAIN: {
                if (startEditProfile) {
                    showScreen(previousActiveScreen, false, null);
                    startEditProfile = false;
                } else {
                    showScreen(SCREEN_MAIN, false, null);
                }
                break;
            }


            case SCREEN_CHOOSE_BANK: {
                showScreen(SCREEN_PROPOSAL_LIST, false, null);
                break;
            }
            case SCREEN_CHOOSE_ROUTE: {
                if (previousActiveScreen == SCREEN_PROPOSAL_DETAILS) {
                    showScreen(previousActiveScreen, false, null);
                } else {
                    if (isEditMode) {
                        showScreen(SCREEN_PROPOSAL_DETAILS, false, null);
                    } else {
                        showScreen(SCREEN_CHOOSE_BANK, false, null);
                    }
                }
                break;
            }
            case SCREEN_ROUTE_DETAILS: {
                if (previousActiveScreen == SCREEN_PROPOSAL_DETAILS || previousActiveScreen == SCREEN_MY_MOTOGAGE || previousActiveScreen == SCREEN_FRIEND_RECOMMENDATION) {
                    showScreen(previousActiveScreen, false, null);
                } else {
                    showScreen(SCREEN_CHOOSE_ROUTE, false, null);
                }

                break;
            }
            case SCREEN_PROPOSAL_DETAILS: {
                if (DataManager.getInstance().isInMortgageFlow()) {
                    showScreen(MobgageMainActivity.SCREEN_MAIN, false, null);
                } else if (previousActiveScreen == SCREEN_PROPOSAL_LIST) {
                    showScreen(previousActiveScreen, false, null);
                } else {
                    if (isEditMode) {
                        showScreen(SCREEN_PROPOSAL_LIST, false, null);
                    } else {
                        Resources res = getResources();
                        AlertDialog alert = Utils.getAlert(res.getString(R.string.alert5_title), res.getString(R.string.alert5_msg), this);
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, res.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showScreen(MobgageMainActivity.SCREEN_PROPOSAL_LIST, false, null);
                            }
                        });
                        alert.setButton(AlertDialog.BUTTON_NEGATIVE, res.getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                    }
                }
                break;
            }
            case SCREEN_PROPOSAL_LIST: {
                sortButton.setVisibility(View.INVISIBLE);
                showScreen(SCREEN_MAIN, false, null);
                break;
            }
            case SCREEN_MY_MOTOGAGE: {
                ActiveSelectionData.getInstance().clearProposal();
                showScreen(SCREEN_MAIN, false, null);
                break;
            }
            case SCREEN_FRIEND_RECOMMENDATION: {
                ActiveSelectionData.getInstance().clearProposal();
                showScreen(SCREEN_PROPOSAL_LIST, false, null);
                break;
            }

            case SCREEN_USER_SIMULATION_INIT: {
                ActiveSelectionData.getInstance().clearProposal();
                showScreen(SCREEN_MAIN, false, null);
                break;
            }

            case SCREEN_USER_SIMULATION_COMPARE: {
                ActiveSelectionData.getInstance().clearProposal();
                showScreen(SCREEN_MAIN, false, null);
                break;
            }

            case SCREEN_USER_SIMULATION_SINGLE: {
                ActiveSelectionData.getInstance().clearProposal();
                showScreen(SCREEN_USER_SIMULATION_COMPARE, false, null);
                break;
            }

            default:
                moveTaskToBack(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.help_button:
                helpClick(v);
                break;
            case R.id.sort_button:
                sortClick(v);
                break;
            case R.id.menu_button:
                menuClick(v);
                break;
            case R.id.simulation_button:
                simulationClick(v);
                break;

            default:
                break;
        }
    }


    public void helpClick(View v) {
        TipsDialog tips = new TipsDialog(activeScreen);
        tips.show(getFragmentManager(), "");
    }

    public void sortClick(View v) {
        if (sortButtonCallback != null) {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.sort_bank: {
                            sortButtonCallback.sort(FILTER_BANK);
                            break;
                        }
                        case R.id.sort_month_repayment: {
                            sortButtonCallback.sort(FILTER_MONTH_REPAYMENT);
                            break;
                        }
                        case R.id.sort_total_repayment: {
                            sortButtonCallback.sort(FILTER_TOTAL_REPAYMENT);
                            break;
                        }
                        case R.id.sort_motgage_amount: {
                            sortButtonCallback.sort(FILTER_MOTGAGE_AMOUNT);
                            break;
                        }
                        case R.id.sort_years: {
                            sortButtonCallback.sort(FILTER_YEARS);
                            break;
                        }
                        case R.id.sort_proposal_num: {
                            sortButtonCallback.sort(FILTER_PROPOSAL_NUM);
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });
            popup.show();
        }
    }

    public void menuClick(View v) {
        if (activeScreen == SCREEN_USER_DETAILS_FROM_MAIN) {
            return;
        }

        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_profile: {
                        showScreen(SCREEN_USER_DETAILS_FROM_MAIN, true, null);
                        startEditProfile = true;
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void simulationClick(View v) {
        showScreen(SCREEN_USER_SIMULATION_INIT, true, null);
    }
}
