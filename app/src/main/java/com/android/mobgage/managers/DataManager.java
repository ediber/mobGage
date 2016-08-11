package com.android.mobgage.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import com.android.mobgage.R;
import com.android.mobgage.data.Bank;
import com.android.mobgage.data.Proposal;
import com.android.mobgage.data.Route;
import com.android.mobgage.data.RouteKind;
import com.android.mobgage.data.Types.RouteKinds;
import com.android.mobgage.data.UserDetails;
import com.android.mobgage.interfaces.IFinishCallback;
import com.android.mobgage.provider.DBHelper;
import com.android.mobgage.provider.tables.TableNames;
import com.android.mobgage.provider.tablesColumns.BanksColumns;
import com.android.mobgage.provider.tablesColumns.ProposalColumns;
import com.android.mobgage.provider.tablesColumns.RouteColumns;
import com.android.mobgage.provider.tablesColumns.RoutesKindsColumns;
import com.android.mobgage.provider.tablesColumns.UserColumns;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;


public class DataManager 
{
	public static final String LOG_TAG = "Mobgage";
	private boolean showUserDetailsScreen;
	private boolean showIntroScreen;
	private boolean hasProposals;
	private boolean isInMortgageFlow;
	private static Context mContext;
	public static final float PRIME_INTEREST = 1.5f;
	public static final float BANK_ISRAEL_INTEREST = 0.1f;
	public static final float YEARLY_INDEX = 2.5f;
	
	private ArrayList<UserDetails> users = new ArrayList<UserDetails>();
	private ArrayList<Bank> banks = new ArrayList<Bank>();
	private ArrayList<RouteKind> routesKinds = new ArrayList<RouteKind>();
	
//	private ArrayList<Proposal> proposals = new ArrayList<Proposal>();
	private HashMap<String, Proposal> proposals = new HashMap<String, Proposal>();
	private Proposal myMortgageProposal;
	
	private int bankImages[] = {R.drawable.bank8, R.drawable.bank7, R.drawable.bank6, R.drawable.bank1, 
			                    R.drawable.bank5, R.drawable.bank4, R.drawable.bank3, R.drawable.bank2};
	

	private static DataManager instance;

	private DataManager() {}
	
	public static void setContext(Context context)
	{
		mContext = context;
	}

	public static DataManager getInstance()
	{
		if(instance == null)
		{
			instance = new DataManager();
		}
		return instance;
	}
	
	
	public boolean hasProposals()
	{
		return hasProposals;
	}


	public void initData(Context context, IFinishCallback callback)
	{
		showIntroScreen = (!(checkDataBaseExist(DBHelper.DATABASE_NAME, null, context)));

		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		copyDataBase(DBHelper.DATABASE_NAME, context);
		
		initProposals(db);
		initUsers(db);
		initBanks(db);
		initRoutesKinds(db);

		if(callback != null)
		{
			callback.initDataFinished();
		}
	}
	
	
	private void initProposals(SQLiteDatabase db)
	{
		try
		{
			String query = "SELECT * FROM " + TableNames.TABLE_PROPOSALS + " ORDER BY " + ProposalColumns.COLUMN_PROPOSAL_ID;
			Cursor pCursor = db.rawQuery(query, null);
			
			if(pCursor == null){ return; }
			
			while(pCursor.moveToNext())
			{
				hasProposals = true;
				String P_userID = pCursor.getString(pCursor.getColumnIndex(ProposalColumns.COLUMN_USER_ID));
				String proposalID = pCursor.getString(pCursor.getColumnIndex(ProposalColumns.COLUMN_PROPOSAL_ID));
				int proposalNum = pCursor.getInt(pCursor.getColumnIndex(ProposalColumns.COLUMN_PROPOSAL_NUM));
				int bank = pCursor.getInt(pCursor.getColumnIndex(ProposalColumns.COLUMN_BANK));
				float mortgageAmount = pCursor.getFloat(pCursor.getColumnIndex(ProposalColumns.COLUMN_MORTGAGE_AMOUNT));
				int years = pCursor.getInt(pCursor.getColumnIndex(ProposalColumns.COLUMN_YEARS));
				float totalRepayment = pCursor.getFloat(pCursor.getColumnIndex(ProposalColumns.COLUMN_TOTAL_REPAYMENT));
				float monthRepayment = pCursor.getFloat(pCursor.getColumnIndex(ProposalColumns.COLUMN_MONTH_REPAYMENT));
				int myMortgage = pCursor.getInt(pCursor.getColumnIndex(ProposalColumns.COLUMN_MY_MORTGAGE));
				
				String routesQuery = "SELECT * FROM " + TableNames.TABLE_ROUTES + " WHERE " 
				+ RouteColumns.COLUMN_PROPOSAL_ID + " = '" + proposalID + "'" + " ORDER BY " + RouteColumns.COLUMN_ROUTE_NUM;
				
				Cursor rCursor = db.rawQuery(routesQuery, null);
				if(rCursor == null){ return; }
				
				ArrayList<Route> routes = new ArrayList<Route>();
				while(rCursor.moveToNext())
				{
					String R_userID = rCursor.getString(rCursor.getColumnIndex(RouteColumns.COLUMN_USER_ID));
					int routeNum = rCursor.getInt(rCursor.getColumnIndex(RouteColumns.COLUMN_ROUTE_NUM));
					float loanAmount = rCursor.getFloat(rCursor.getColumnIndex(RouteColumns.COLUMN_LOAN_AMOUNT));
					int routeYears = rCursor.getInt(rCursor.getColumnIndex(RouteColumns.COLUMN_YEARS));
					float interest = rCursor.getFloat(rCursor.getColumnIndex(RouteColumns.COLUMN_INTEREST));
					float routeMonthRepayment = rCursor.getFloat(rCursor.getColumnIndex(RouteColumns.COLUMN_MONTH_REPAYMENT));
					int returnMethod = rCursor.getInt(rCursor.getColumnIndex(RouteColumns.COLUMN_RETURN_METHOD));
					float RtotalRepayment = rCursor.getFloat(rCursor.getColumnIndex(RouteColumns.COLUMN_TOTAL_REPAYMENT));
					int routeKind = rCursor.getInt(rCursor.getColumnIndex(RouteColumns.COLUMN_ROUTE_KIND));
					Route route = new Route(R_userID, proposalID, routeNum, loanAmount, routeYears, interest, routeMonthRepayment, returnMethod, routeKind, RtotalRepayment);
					routes.add(route);
				}
				
				Proposal proposal = new Proposal(P_userID, proposalID, proposalNum, bank, mortgageAmount, years, totalRepayment, monthRepayment, myMortgage);
				proposal.setRoutes(routes);
				if(myMortgage == 1){
					myMortgageProposal = proposal;
				}else {
					proposals.put(proposalID, proposal);
				}
				if(rCursor != null) { rCursor.close(); }
			}
			if(pCursor != null) { pCursor.close(); }
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
		if(proposals.values().size() == 0){
			hasProposals = false;
		}
	}
	private void initUsers(SQLiteDatabase db)
	{
		try
		{
			String usersQuery = "SELECT * FROM " + TableNames.TABLE_USERS;

			Cursor uCursor = db.rawQuery(usersQuery, null);
			if(uCursor == null){ return; }
			boolean usersEmpty = true;

			while(uCursor.moveToNext())
			{
				usersEmpty = false;
				String userID = uCursor.getString(uCursor.getColumnIndex(UserColumns.COLUMN_USER_ID));
				String email = uCursor.getString(uCursor.getColumnIndex(UserColumns.COLUMN_EMAIL));
				float mortgageAmount = uCursor.getFloat(uCursor.getColumnIndex(UserColumns.COLUMN_MORTGAGE_AMOUNT));
				float monthRepayment = uCursor.getFloat(uCursor.getColumnIndex(UserColumns.COLUMN_MONTH_REPAYMENT));
				float assetValue = uCursor.getFloat(uCursor.getColumnIndex(UserColumns.COLUMN_ASSET_VALUE));
				String assetLocation = uCursor.getString(uCursor.getColumnIndex(UserColumns.COLUMN_ASSET_LOCATION));
				float leveragePercent = uCursor.getFloat(uCursor.getColumnIndex(UserColumns.COLUMN_LEVERAGE_PERCENT));
				float freeIncoming = uCursor.getFloat(uCursor.getColumnIndex(UserColumns.COLUMN_FREE_INCOMING));

				UserDetails user = new UserDetails(userID, email, mortgageAmount, monthRepayment, assetValue, assetLocation, leveragePercent, freeIncoming);
				users.add(user);
			}
			if(usersEmpty) 
			{
				showUserDetailsScreen = true;
			}
			else
			{
				ActiveSelectionData.getInstance().setCurrentUser(users.get(0));
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
	}
	private void initBanks(SQLiteDatabase db)
	{
		try
		{
			String banksQuery = "SELECT * FROM " + TableNames.TABLE_BANKS + " ORDER BY " + BanksColumns.COLUMN_BANK_ID;
			Cursor bCursor = db.rawQuery(banksQuery, null);
			if(bCursor == null){ return; }

			while(bCursor.moveToNext())
			{
				int bankID = bCursor.getInt(bCursor.getColumnIndex(BanksColumns.COLUMN_BANK_ID));
				String bankName = bCursor.getString(bCursor.getColumnIndex(BanksColumns.COLUMN_BANK_NAME));
				Bank bank = new Bank(bankID, bankName);

				banks.add(bank);
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
	}
	private void initRoutesKinds(SQLiteDatabase db)
	{
		try
		{
			String routesKindsQuery = "SELECT * FROM " + TableNames.TABLE_ROUTES_KINDS + " ORDER BY " + RoutesKindsColumns.COLUMN_ROUTES_KIND_ID;
			Cursor rCursor = db.rawQuery(routesKindsQuery, null);
			if(rCursor == null){ return; }

			while(rCursor.moveToNext())
			{
				int routeKindID = rCursor.getInt(rCursor.getColumnIndex(RoutesKindsColumns.COLUMN_ROUTES_KIND_ID));
				String routeKindName = rCursor.getString(rCursor.getColumnIndex(RoutesKindsColumns.COLUMN_ROUTES_KIND_NAME));
				RouteKind kind = new RouteKind(routeKindID, routeKindName);
				routesKinds.add(kind);
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
	}
	
	
	private boolean checkDataBaseExist(String dbName, String dbPath, Context context) 
	{
		SQLiteDatabase checkDB = null;
		try 
		{
			String packageName = context.getPackageName();
			String pathDB = "/data/data/"+ packageName +"/databases/"+dbName;
			
			pathDB = (dbPath!=null? dbPath : pathDB);
			
			checkDB = SQLiteDatabase.openDatabase(pathDB, null,
					SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} 
		catch (SQLiteException e) 
		{
			// database doesn't exist yet.
		}
		return checkDB != null ? true : false;
	}
	
	private void copyDataBase(String dbName, Context context)
	{
		InputStream myInput=null;
		OutputStream myOutput=null;
		try
		{
			String packageName = context.getPackageName();
			String pathDB = "/data/data/"+ packageName +"/databases/";
			myInput = new FileInputStream(pathDB + dbName);      
			//        File file = new File(DB_PATH);
			//        if (!file.exists()) 
			//        {
			//            file.mkdir();
			//        } 
			String outFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" +dbName;
			
			File dbFile = new File(outFileName);
			if(dbFile.exists())
			{
				dbFile.delete();
			}
			
			myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) 
			{
				myOutput.write(buffer, 0, length);
			}
			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
    }
	
	public boolean isShowUserDetailsScreen() 
	{
		return showUserDetailsScreen;
	}

	public boolean isShowIntroScreen() 
	{
		return showIntroScreen;
	}
	
	
	public void setShowUserDetailsScreen(boolean showUserDetailsScreen) 
	{
		this.showUserDetailsScreen = showUserDetailsScreen;
	}

	public boolean isInMortgageFlow() {
		return isInMortgageFlow;
	}

	public void setIsInMortgageFlow(boolean isInMortgageFlow) {
		this.isInMortgageFlow = isInMortgageFlow;
	}

	public void insertUser(UserDetails user)
	{
		try
		{
			SQLiteDatabase db = new DBHelper(mContext).getReadableDatabase();
			boolean succeeded = db.delete(TableNames.TABLE_USERS, "1", null) > 0;
			Log.i("delete all users from DB.db", succeeded? "succeeded" : "failed");
			
			ContentValues values = new ContentValues();
			values.put(UserColumns.COLUMN_USER_ID, user.userID);
			values.put(UserColumns.COLUMN_MORTGAGE_AMOUNT, user.mortgageAmount);
			values.put(UserColumns.COLUMN_MONTH_REPAYMENT, user.monthRepayment);
			values.put(UserColumns.COLUMN_LEVERAGE_PERCENT, user.leveragePercent);
			values.put(UserColumns.COLUMN_EMAIL, user.email);
			values.put(UserColumns.COLUMN_ASSET_VALUE, user.assetValue);
			values.put(UserColumns.COLUMN_ASSET_LOCATION, user.assetLocation);
			
			db.insert(TableNames.TABLE_USERS, null, values);
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
	}
	
	
	
	public Drawable getBankImage(int bankID, Context context)
	{
		if(bankID < bankImages.length)
			return context.getResources().getDrawable(bankImages[bankID]);
		else
			return null;
	}
	
	public Bank getBankByID(int bankID)
	{
		if(bankID < banks.size())
			return banks.get(bankID);
		else
			return null;
	}
	
	public ArrayList<Bank> getAllBanks()
	{
		return banks;
	}

	
	public ArrayList<RouteKind> getAllRoutesKinds()
	{
		return routesKinds;
	}
	
	public RouteKind getRouteKindByID(int kindID)
	{
		if(kindID < routesKinds.size())
			return routesKinds.get(kindID);
		else
			return null;
	}
	
	public String getStringResource(int resID)
	{
		String s = mContext.getResources().getString(resID);
		return (s==null? "" : s);
	}
	
	public ArrayList<UserDetails> getAllUsers()
	{
		return users;
	}
	
	public void addUser(UserDetails user)
	{
		users.clear();
		users.add(user);
	}
	
	public Proposal getProposalByProposalID(String proposalID)
	{
		return proposals.get(proposalID);
	}
	
	public Route getRouteByRouteNum(Proposal proposal, int routeNum)
	{
		for(Route route : proposal.getRoutes())
		{
			if(route.routeNum == routeNum)
			{
				return route;
			}
		}
		return null;
	}
	
	public boolean isProposalHasRouteOfKind(Proposal p, int routeKind)
	{
	for(Route r : p.getRoutes())
	{
		if(r.routeKind == routeKind)
		{
			return true;
		}
	}
	return false;
}
	
	
	public Proposal getNewProposal(int bankID, String userID)
	{	
		ArrayList<Proposal> proposalsList = new ArrayList<Proposal>(proposals.values());
		if(proposalsList.isEmpty()) 
		{ 
			return (new Proposal(userID, generateUniqueID(), 0, bankID, 0, 0, 0, 0, 0));
		}
		
		Collections.sort(proposalsList, new Comparator<Proposal>() {
            @Override
            public int compare(Proposal lhs, Proposal rhs) 
            {
                return ((Integer)lhs.proposalNum).compareTo(rhs.proposalNum);
            }
        });
		
		int proposalNum = ((proposalsList.get(proposalsList.size()-1)).proposalNum) + 1;
		return new Proposal(userID, generateUniqueID(), proposalNum, bankID, 0, 0, 0, 0, 0);
	}
	
	public ArrayList<Proposal> getProposalsListByOrder()
	{
		ArrayList<Proposal> proposalsList = new ArrayList<Proposal>(proposals.values());
		
		Collections.sort(proposalsList, new Comparator<Proposal>() {
            @Override
            public int compare(Proposal lhs, Proposal rhs) 
            {
                return ((Integer)lhs.proposalNum).compareTo(rhs.proposalNum);
            }
        });
		
		return proposalsList;
	}
	
	public Route getNewRouteForProposalID(Proposal proposal, int routeKind)
	{
		if(proposal.getRoutes().isEmpty())
		{
			return (new Route(proposal.userID, proposal.proposalID, 0, 0, 0, 0, 0, 0, routeKind, 0));
		}
		
		int routeNum = ((proposal.getRoutes().get(proposal.getRoutes().size()-1)).routeNum) + 1;
		return (new Route(proposal.userID, proposal.proposalID, routeNum, 0, 0, 0, 0, 0, routeKind, 0));
	}
	
	
	public int getProposalPositionByID(String proposalID)
	{
		ArrayList<Proposal> proposalsList = new ArrayList<Proposal>(proposals.values());
		
		Collections.sort(proposalsList, new Comparator<Proposal>() {
            @Override
            public int compare(Proposal lhs, Proposal rhs) 
            {
                return ((Integer)lhs.proposalNum).compareTo(rhs.proposalNum);
            }
        });
		
		for(Proposal p : proposalsList)
		{
			if(p.proposalID.equalsIgnoreCase(proposalID))
			{
				return (proposalsList.indexOf(p))+1;
			}
		}
		return proposalsList.size()+1;
	}
	
	public void saveOrUpdateProposal(Proposal proposal, boolean isMyMortgage)
	{
		if(isMyMortgage){
			setMyMortgage(proposal);
		}else {
			boolean existInDB = ((proposals.get(proposal.proposalID)) != null);
			if (existInDB) {
				deleteProposal(proposal.proposalID, isMyMortgage);
				addProposal(proposal, isMyMortgage);
			} else {
				addProposal(proposal, isMyMortgage);
			}
		}
	}
	public void deleteProposal(String proposalID, boolean isMyMortgage)
	{
		if(!isMyMortgage) {
			if (proposals.get(proposalID) == null) {
				return;
			}
			proposals.remove(proposalID);
		}else{
			myMortgageProposal = null;
		}
		SQLiteDatabase db = new DBHelper(mContext).getReadableDatabase();	
		try
		{
			db.beginTransaction();
			
//			String deleteRoutesQuuery = "DELETE FROM " + TableNames.TABLE_ROUTES + " WHERE " + RouteColumns.COLUMN_PROPOSAL_ID + "=" + proposalID;	
			boolean succeeded = db.delete(TableNames.TABLE_ROUTES, RouteColumns.COLUMN_PROPOSAL_ID + "=?", new String[]{proposalID})>0;
			Log.i("delete routes from DB", succeeded? "succeeded" : "failed");
			
			succeeded = db.delete(TableNames.TABLE_PROPOSALS, ProposalColumns.COLUMN_PROPOSAL_ID + "=?", new String[]{proposalID})>0;
			Log.i("delete routes from DB", succeeded? "succeeded" : "failed");	
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}finally {
			if(db != null) {
				db.setTransactionSuccessful();
				db.endTransaction();
				db.close();
			}
		}

	}
	private void addProposal(Proposal proposal, boolean isMyMortgage)
	{
		if(!isMyMortgage) {
			proposals.put(proposal.proposalID, proposal);
		}
		SQLiteDatabase db = new DBHelper(mContext).getReadableDatabase();	
		try
		{
			db.beginTransaction();
			
			for(Route route : proposal.getRoutes())
			{
				ContentValues values = new ContentValues();
				values.put(RouteColumns.COLUMN_USER_ID, route.userID);
				values.put(RouteColumns.COLUMN_INTEREST, route.interest);
				values.put(RouteColumns.COLUMN_LOAN_AMOUNT, route.loanAmount);
				values.put(RouteColumns.COLUMN_MONTH_REPAYMENT, route.monthRepayment);
				values.put(RouteColumns.COLUMN_PROPOSAL_ID, route.proposalID);
				values.put(RouteColumns.COLUMN_RETURN_METHOD, route.returnMethod);
				values.put(RouteColumns.COLUMN_ROUTE_NUM, route.routeNum);
				values.put(RouteColumns.COLUMN_TOTAL_REPAYMENT, route.totalRepayment);
				values.put(RouteColumns.COLUMN_ROUTE_KIND, route.routeKind);
				values.put(RouteColumns.COLUMN_YEARS, route.years);
				
				db.insert(TableNames.TABLE_ROUTES, null, values);
			}
			
			ContentValues values = new ContentValues();
			values.put(ProposalColumns.COLUMN_USER_ID, proposal.userID);
			values.put(ProposalColumns.COLUMN_BANK, proposal.bank);
			values.put(ProposalColumns.COLUMN_MONTH_REPAYMENT, proposal.monthRepayment);
			values.put(ProposalColumns.COLUMN_MORTGAGE_AMOUNT, proposal.mortgageAmount);
			values.put(ProposalColumns.COLUMN_MY_MORTGAGE, proposal.myMortgage);
			values.put(ProposalColumns.COLUMN_PROPOSAL_ID, proposal.proposalID);
			values.put(ProposalColumns.COLUMN_PROPOSAL_NUM, proposal.proposalNum);
			values.put(ProposalColumns.COLUMN_TOTAL_REPAYMENT, proposal.totalRepayment);
			values.put(ProposalColumns.COLUMN_YEARS, proposal.years);
			
			db.insert(TableNames.TABLE_PROPOSALS, null, values);
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
		finally {
			if(db != null) {
				db.setTransactionSuccessful();
				db.endTransaction();
				db.close();
			}
		}
	}
	
	
	public void updateUserIdInAllTables(String userID)
	{
		SQLiteDatabase db = new DBHelper(mContext).getReadableDatabase();	
		try
		{
			db.beginTransaction();
			
			ContentValues values = new ContentValues();
			values.put(ProposalColumns.COLUMN_USER_ID, userID);
			db.update(TableNames.TABLE_PROPOSALS, values, null, null);
			
			values = new ContentValues();
			values.put(RouteColumns.COLUMN_USER_ID, userID);
			db.update(TableNames.TABLE_ROUTES, values, null, null);
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
		finally {
			if(db != null) {
				db.setTransactionSuccessful();
				db.endTransaction();
				db.close();
			}
		}
	}
	
	
	public Proposal getMyMotogage()
	{
		return myMortgageProposal;
	}

	public void setMyMortgage(Proposal proposal)
	{
		try
		{
			if(myMortgageProposal != null){
				deleteProposal(myMortgageProposal.proposalID, true);
			}
			myMortgageProposal = new Proposal(proposal.userID, generateUniqueID(), proposal.proposalNum, proposal.bank, proposal.mortgageAmount, proposal.years, proposal.totalRepayment, proposal.monthRepayment, 1);
			for (Route route : proposal.getRoutes()) {
				Route route1 = new Route(route.userID, myMortgageProposal.proposalID, route.routeNum, route.loanAmount, route.years, route.interest, route.monthRepayment, route.returnMethod, route.routeKind, route.totalRepayment);
				myMortgageProposal.getRoutes().add(route1);
			}
			addProposal(myMortgageProposal, true);
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	public String generateUniqueID() {
		UUID uniqueKey = UUID.randomUUID();
		return uniqueKey.toString();
	}

	public void calculate(Route route) {
		float loan = route.loanAmount;
		float interest = route.interest;
		int years = route.years;

		switch (route.routeKind) {
			case RouteKinds.KIND_PRIME: {
				double C6 = DataManager.PRIME_INTEREST + interest;
				double C7 = (Math.pow((1 + (C6 / 100.00)), (1.00 / 12.00)) - 1) * 100.00;
				double C9 = years * 12;
				double C10 = (1 + (C7 / 100.00)) * 100.00;
				double C11 = Math.pow((C10 / 100.00), C9);
				double C12 = 1 / C11;
				double C13 = 1 - C12;
				double C14 = (C7 / 100.00) / C13;
				route.monthRepayment = (float) (C14 * loan);
				route.totalRepayment = (float) (route.monthRepayment * C9);
				break;
			}
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
				route.monthRepayment = (float) (C14 * loan);
				route.totalRepayment = (float) (route.monthRepayment * C9);
				break;
			}
		}
	}
}
