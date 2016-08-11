package com.android.mobgage.provider;

import com.android.mobgage.R;
import com.android.mobgage.managers.DataManager;
import com.android.mobgage.provider.tables.TableNames;
import com.android.mobgage.provider.tablesColumns.BanksColumns;
import com.android.mobgage.provider.tablesColumns.ProposalColumns;
import com.android.mobgage.provider.tablesColumns.RouteColumns;
import com.android.mobgage.provider.tablesColumns.RoutesKindsColumns;
import com.android.mobgage.provider.tablesColumns.UserColumns;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper 
{
	public static final String DATABASE_NAME = "DB.db";
	public final static String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobgage/db/" + DATABASE_NAME;
	private static final int DATABASE_VERSION = 1;
	private Context mContext;

	private static final String ENCODING_SETTING = "PRAGMA encoding ='UTF-8'";

	private static final String DROP_METADATA = "DROP TABLE IF EXISTS android_metadata";

	private static final String CREATE_METADATA = "CREATE TABLE android_metadata (locale TEXT DEFAULT 'iw_IL')";

	private static final String INSERT_METADATA = "INSERT INTO android_metadata VALUES('iw_IL')";

	private static final String CREATE_PROPOSALS = "create table "
		      + TableNames.TABLE_PROPOSALS + "(" + 
			  ProposalColumns._ID + " integer primary key autoincrement, " + 
			  ProposalColumns.COLUMN_USER_ID + " nvarchar, " +
			  ProposalColumns.COLUMN_BANK + " int, " + 
		      ProposalColumns.COLUMN_MONTH_REPAYMENT + " float, " + 
		      ProposalColumns.COLUMN_MORTGAGE_AMOUNT + " float, " + 
		      ProposalColumns.COLUMN_MY_MORTGAGE + " int, " + 
		      ProposalColumns.COLUMN_PROPOSAL_ID + " nvarchar, " +
		      ProposalColumns.COLUMN_PROPOSAL_NUM + " int, " + 
		      ProposalColumns.COLUMN_TOTAL_REPAYMENT + " float, " + 
		      ProposalColumns.COLUMN_YEARS + " int" +");";
	
	private static final String CREATE_ROUTES = "create table "
		      + TableNames.TABLE_ROUTES + "(" + 
			  RouteColumns._ID + " integer primary key autoincrement, " + 
			  RouteColumns.COLUMN_USER_ID + " nvarchar, " +
			  RouteColumns.COLUMN_INTEREST + " float, " + 
			  RouteColumns.COLUMN_LOAN_AMOUNT + " float, " + 
			  RouteColumns.COLUMN_MONTH_REPAYMENT + " float, " + 
			  RouteColumns.COLUMN_PROPOSAL_ID + " nvarchar, " +
			  RouteColumns.COLUMN_RETURN_METHOD + " int, " + 
			  RouteColumns.COLUMN_ROUTE_NUM + " int, " + 
			  RouteColumns.COLUMN_TOTAL_REPAYMENT + " float, " + 
			  RouteColumns.COLUMN_ROUTE_KIND + " int, " + 
			  RouteColumns.COLUMN_YEARS + " int"  +  ");";
	
	private static final String CREATE_USERS = "create table "
		      + TableNames.TABLE_USERS + "(" + 
			  UserColumns._ID + " integer primary key autoincrement, " + 
			  UserColumns.COLUMN_USER_ID + " nvarchar, " +
			  UserColumns.COLUMN_ASSET_LOCATION + " nvarchar, " +
			  UserColumns.COLUMN_ASSET_VALUE + " float, " + 
			  UserColumns.COLUMN_EMAIL + " nvarchar, " +
			  UserColumns.COLUMN_LEVERAGE_PERCENT + " float, " + 
			  UserColumns.COLUMN_MONTH_REPAYMENT + " float, " +
			  UserColumns.COLUMN_FREE_INCOMING + " float, " +
  			  UserColumns.COLUMN_MORTGAGE_AMOUNT + " float"  + ");";
	
	private static final String CREATE_BANKS = "create table "
		      + TableNames.TABLE_BANKS + "(" + 
			  UserColumns._ID + " integer primary key autoincrement, " + 
			  BanksColumns.COLUMN_BANK_ID + " int, " + 
			  BanksColumns.COLUMN_BANK_NAME + " nvarchar" + ");";
	
	private static final String CREATE_ROUTES_KINDS = "create table "
		      + TableNames.TABLE_ROUTES_KINDS + "(" + 
			  UserColumns._ID + " integer primary key autoincrement, " + 
			  RoutesKindsColumns.COLUMN_ROUTES_KIND_ID + " int, " + 
			  RoutesKindsColumns.COLUMN_ROUTES_KIND_NAME + " nvarchar" + ");";
	
	
	public DBHelper(Context context) 
	{
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    mContext = context;
	}
	
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) 
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{
			db.execSQL(ENCODING_SETTING);
			db.execSQL(DROP_METADATA);
			db.execSQL(CREATE_METADATA);
			db.execSQL(INSERT_METADATA);
			db.execSQL(CREATE_USERS);
			db.execSQL(CREATE_PROPOSALS);
			db.execSQL(CREATE_ROUTES);	
			db.execSQL(CREATE_BANKS);
			db.execSQL(CREATE_ROUTES_KINDS);
			initBanks(db);
			initRoutesKinds(db);
		}
		catch(Exception e)
		{
			Log.e(DataManager.LOG_TAG, e.getMessage());
		}
	}

	public void initBanks(SQLiteDatabase db)
	{
		try
		{
			db.beginTransaction();
			String[] banksNames = mContext.getResources().getStringArray(R.array.banks_names);
			
			for(int bankID = 0 ; bankID < banksNames.length ; bankID ++)
			{
				ContentValues values = new ContentValues();
				values.put(BanksColumns.COLUMN_BANK_ID, bankID);
				values.put(BanksColumns.COLUMN_BANK_NAME, banksNames[bankID]);
				db.insert(TableNames.TABLE_BANKS, null, values);
			}	
		}
		catch(Exception e)
		{
			Log.e(DataManager.LOG_TAG, e.getMessage());
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void initRoutesKinds(SQLiteDatabase db)
	{
		try
		{
			db.beginTransaction();
			String[] routesKindsNames = mContext.getResources().getStringArray(R.array.routes_kinds_names);
			
			for(int kindID = 0 ; kindID < routesKindsNames.length ; kindID ++)
			{
				ContentValues values = new ContentValues();
				values.put(RoutesKindsColumns.COLUMN_ROUTES_KIND_ID, kindID);
				values.put(RoutesKindsColumns.COLUMN_ROUTES_KIND_NAME, routesKindsNames[kindID]);
				db.insert(TableNames.TABLE_ROUTES_KINDS, null, values);
			}	
		}
		catch(Exception e)
		{
			Log.e(DataManager.LOG_TAG, e.getMessage());
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		
	}
}
