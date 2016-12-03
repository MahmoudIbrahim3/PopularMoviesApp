package com.mahmoud.popularmovies.Control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
	
	private static final String dbName="DB_Favourites";
	
	private final String POSTER_TABLE="POSTER_TABLE";

	private final String po_id="po_id";
	private final String po_title="po_title";
	private final String po_thumbnail="po_thumbnail";
	private final String po_release_date="po_release_date";
	private final String po_vote_average="po_vote_average";
	private final String po_overview="po_overview";


	private SQLiteDatabase db;
	
	public static String getDbName() {
		return dbName;
	}

	public String getPOSTER_TABLE() {
		return POSTER_TABLE;
	}

	public String getPo_id() {
		return po_id;
	}

	public String getPo_title() {
		return po_title;
	}

	public String getPo_thumbnail() {
		return po_thumbnail;
	}

	public String getPo_release_date() {
		return po_release_date;
	}

	public String getPo_vote_average() {
		return po_vote_average;
	}

	public String getPo_overview() {
		return po_overview;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public DB(Context context) {
		super(context, dbName, null, 33);
		
		db=context.openOrCreateDatabase(dbName, SQLiteDatabase.CREATE_IF_NECESSARY, null);

		//POSTER_TABLE
		db.execSQL("CREATE TABLE IF NOT EXISTS " + POSTER_TABLE+"("+
				po_id + " INTEGER PRIMARY KEY , " +
				po_title + " TEXT , " +
				po_thumbnail + " TEXT , " +
				po_release_date + " TEXT , " +
				po_vote_average + " TEXT , " +
				po_overview + " TEXT )");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
	}

}
