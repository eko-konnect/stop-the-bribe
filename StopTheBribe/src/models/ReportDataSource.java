package models;

import java.util.ArrayList;

import models.ReportContract.ReportEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ReportDataSource {
	
	private SQLiteDatabase database;
	private ReportReaderDbHelper dbHelper;
	private String[] allColumns = {ReportEntry.COLUMN_NAME_TITLE, ReportEntry.COLUMN_NAME_DESCRIPTION, 
			ReportEntry.COLUMN_NAME_AUTHOR, ReportEntry.COLUMN_NAME_DATE};
	
	public ReportDataSource(Context context){
		dbHelper = new ReportReaderDbHelper(context);
	}
	
	public void open() throws SQLException{
		if(database == null){
			database = dbHelper.getWritableDatabase();
		}
	}
	
	public void close(){
		if (database != null){
			database.close();
		}
	}
	public void insertIntoTable(){
		String title = "Bonny DPO caught on Camera demanding Bribe";
		String description = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";
		String author = "Oyewale Oyediran";
		String date = "Nov 3, 2013";
		ContentValues values = new ContentValues();
		values.put(ReportEntry.COLUMN_NAME_TITLE, title);
		values.put(ReportEntry.COLUMN_NAME_DESCRIPTION, description);
		values.put(ReportEntry.COLUMN_NAME_AUTHOR, author);
		values.put(ReportEntry.COLUMN_NAME_DATE, date);

		database.insert(ReportEntry.TABLE_NAME, null, values);
	}
	public ArrayList<Report> getAllReports() {
	    ArrayList<Report> reports = new ArrayList<Report>();

	    Cursor cursor = database.query(ReportEntry.TABLE_NAME,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Report report = cursorToReport(cursor);
	      reports.add(report);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return reports;
	  }
	private Report cursorToReport(Cursor cursor) {
	    Report report = new Report(cursor.getString(0), cursor.getString(1),
	    		cursor.getString(2), cursor.getString(3));
	    return report;
	  }
}
