package org.ekokonnect.stopthebribe;

import java.util.ArrayList;

import models.Report;
import models.ReportDataSource;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ReportListActivity extends ListActivity {
	private ReportDataSource reportDataSource;
	ArrayList<Report> reports;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_list);
		ListView listview = getListView();
		
		
		reportDataSource = new ReportDataSource(this);
		reportDataSource.open();
		
		reportDataSource.insertIntoTable();//hardcode values into DB
		
		
		reports = reportDataSource.getAllReports();//get values from DB
		
		ReportListAdapter adapter = new ReportListAdapter(this, reports);
		setListAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int position,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), ViewReportActivity.class);
				Report report = reports.get(position);
				
				i.putExtra("title", report.getTitle());
				i.putExtra("author", report.getAuthor());
				i.putExtra("date", report.getDate());
				i.putExtra("description", report.getDescription());
				
				startActivity(i);
				
				
				
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.reportlist, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		switch (id) {
		case R.id.action_create_report:
			startActivity(new Intent(getApplicationContext(), MakeReportActivity.class));
			break;

		default:
			break;
		}
//		return super.onOptionsItemSelected(item);
		return true;
	}
	
}
