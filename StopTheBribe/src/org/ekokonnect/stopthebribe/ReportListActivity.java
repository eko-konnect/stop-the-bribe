package org.ekokonnect.stopthebribe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.services.FetchReports;
import com.ushahidi.android.app.util.ApiUtils;

public class ReportListActivity extends FragmentActivity implements ReportListFragment.Callbacks {
	private static final String TAG = "ReportListActivity";	
	ListReportModel model;
	private boolean mTwoPane;
	//List<ReportEntity> reportList;
//	private boolean refreshState = false;
//	private FetchedReportListAdapter fetchedAdapter;
//	private PendingReportListAdapter pendingAdapter;
	
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_report_list);
		
		Preferences.loadSettings(getApplicationContext());
		if (!Preferences.isSignedIn){
			finish();
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		}
		final String deployment = getString(R.string.deployment_url);				
        if (ApiUtils.validateUshahidiInstance(deployment)) {
            Log.i("Dashboard", "Valid Domain " + deployment);
            Preferences.loadSettings(getApplicationContext());
            Preferences.domain = deployment;
            Preferences.saveSettings(getApplicationContext());

            // refresh for new reports
            if (Preferences.appRunsFirstTime == 0) {
            	Log.d(TAG, "App is running for firstime");
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
		
		
		
		if (findViewById(R.id.report_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ReportListFragment) getSupportFragmentManager().findFragmentById(
					R.id.report_list)).setActivateOnItemClick(true);
		}
							
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.reportlist, menu);
		return true;
	}
	
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		switch (id) {
		case R.id.action_create_report:
			startActivity(new Intent(getApplicationContext(), MakeReportActivity.class));
			break;
		case R.id.action_logout:
			logout();
			break;
		default:
			break;
		}
//		return super.onOptionsItemSelected(item);
		return true;
	}
	
	private void logout() {
		// TODO Auto-generated method stub
		Preferences.loadSettings(getApplicationContext());
		Preferences.isSignedIn = false;
		Preferences.saveSettings(getApplicationContext());
		
		//FB Logout
		Session session = Session.getActiveSession();
		if (session != null){
			session.closeAndClearTokenInformation();
		}
		
		finish();
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
	}*/

	/**
	 * Callback method from {@link PlaceListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(ReportDetailFragment.ARG_ITEM_ID, id);
			ReportDetailFragment fragment = new ReportDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.report_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ViewReportActivity.class);
			detailIntent.putExtra(ReportDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
}
