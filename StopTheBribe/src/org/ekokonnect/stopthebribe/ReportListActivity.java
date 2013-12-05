package org.ekokonnect.stopthebribe;

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListReportModel;

import models.Report;
import models.ReportDataSource;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ReportListActivity extends ListActivity {
	private static final String TAG = "ReportListActivity";
	private ReportDataSource reportDataSource;
	//ArrayList<Report> reports;
	ListReportModel model;
	List<ReportEntity> reportList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_list);
		ListView listview = getListView();
		
		
		//reportDataSource = new ReportDataSource(this);
		//reportDataSource.open();
		
		//reportDataSource.insertIntoTable();//hardcode values into DB
		
		
		//reports = reportDataSource.getAllReports();//get values from DB
		model = new ListReportModel(getApplicationContext());
		if(model.loadPendingReports()){
//			model.getReports();
			reportList = model.getReports();
			Log.d(TAG, reportList.size()+" Reports Loaded");
		} else {
			Log.e(TAG, "error loading reports");
			reportList = new ArrayList<ReportEntity>();
		}
			
		
		ReportListAdapter adapter = new ReportListAdapter(this, reportList);
		setListAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int position,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), ViewReportActivity.class);
				ReportEntity report = reportList.get(position);
				int id = report.getDbId();
				
				i.putExtra("id", id);
//				i.putExtra("author", report.getAuthor());
//				i.putExtra("date", report.getDate());
//				i.putExtra("description", report.getDescription());
				
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
