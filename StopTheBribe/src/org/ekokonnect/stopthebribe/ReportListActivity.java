package org.ekokonnect.stopthebribe;

import java.util.ArrayList;
import java.util.List;

import models.ReportDataSource;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.services.FetchReports;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.util.ApiUtils;

public class ReportListActivity extends ListActivity {
	private static final String TAG = "ReportListActivity";
	private ReportDataSource reportDataSource;
	//ArrayList<Report> reports;
	ListReportModel model;
	List<ReportEntity> reportList;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (IllegalArgumentException e) {
                	e.printStackTrace();
                }
                Log.d(TAG, "Broadcast recieved");
//                goToReports();

            }
        }
    };
    
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
		if(model.load()){
//			model.getReports();
			reportList = model.getReports();
			Log.d(TAG, reportList.size()+" Reports Loaded");
		} else {
			Log.e(TAG, "error loading reports");
			reportList = new ArrayList<ReportEntity>();
		}
			
		
		ReportListAdapter adapter = new ReportListAdapter(this, reportList);
		setListAdapter(adapter);
		
		// validate URL
				final String deployment = getString(R.string.deployment_url);
		        if (ApiUtils.validateUshahidiInstance(deployment)) {
		            Log.i("Dashboard", "Valid Domain " + deployment);
		            Preferences.loadSettings(getApplicationContext());
		            Preferences.domain = deployment;
		            Preferences.saveSettings(this);

		            // refresh for new reports
		            if (Preferences.appRunsFirstTime == 0) {
		                // refreshReports();
		                Preferences.appRunsFirstTime = 1;
		                Preferences.saveSettings(this);
		                startService(new Intent(this, FetchReports.class));
		                //return true;
		                //Log.d(TAG, "FetchReports Service started");
		            } else {
		            	Log.d(TAG, "App Not running for firstime");
		            }
		        } else {
		        	Log.d(TAG, "Not valid Ushahidi Instance");
		        }
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int position,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), ViewReportActivity.class);
				ReportEntity report = reportList.get(position);
				int id = report.getIncident().getId();
				
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
	
	@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        registerReceiver(broadcastReceiver, new IntentFilter(
                SyncServices.SYNC_SERVICES_ACTION));
    }

    @Override
    public void onStart() {
        super.onStart();
//        AnalyticsUtils.activityStop(this);
    }

    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
        }

    }
	
}
