package models;

import android.provider.BaseColumns;

public final class ReportContract {
	
	public ReportContract(){
		
	}
	
	public static abstract class ReportEntry implements BaseColumns{
		public static final String TABLE_NAME = "report";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_DATE = "date";
	}
}
