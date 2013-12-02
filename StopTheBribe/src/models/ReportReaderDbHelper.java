package models;

import models.ReportContract.ReportEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReportReaderDbHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "ReportReader.db";
	public static final int DATABASE_VERSION = 1;
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + ReportEntry.TABLE_NAME + " (" +
		    ReportEntry._ID + " INTEGER PRIMARY KEY," +
		    ReportEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
		    ReportEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
		    ReportEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
		    ReportEntry.COLUMN_NAME_DATE + TEXT_TYPE + ");";
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + ReportEntry.TABLE_NAME;
	
	public ReportReaderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
	}

}
