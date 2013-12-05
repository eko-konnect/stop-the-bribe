package org.ekokonnect.stopthebribe;

import org.ekokonnect.stopthebribe.modelhelpers.ReportHelper;

import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.java.sdk.api.Incident;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class ViewReportActivity extends Activity {
	TextView editTitle, editAuthor, editDate, editDescription;
	ReportHelper model;
	ReportEntity report;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_report);
		editTitle = (TextView) findViewById(R.id.reportTitle);
		editAuthor = (TextView) findViewById(R.id.reportAuthor);
		editDate = (TextView) findViewById(R.id.reportDate);
		editDescription = (TextView) findViewById(R.id.reportDescription);
		Bundle extras = getIntent().getExtras();
		
		int id = extras.getInt("id");
		model = new ReportHelper(getApplicationContext());
		report = model.fetchPendingReportById(id);
		Incident inc = report.getIncident();
		
//		String title = (String)extras.getString("title");
//		String author = (String)extras.getString("author");
//		String date = (String)extras.getString("date");
//		String description = (String)extras.getString("description");
		
		editTitle.setText(inc.getTitle());
		editAuthor.setText("Anonymous");
		editDate.setText(inc.getDate().toLocaleString());
		editDescription.setText(inc.getDescription());
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viewreport, menu);
		return true;
	}

}
