package org.ekokonnect.stopthebribe;

import org.ekokonnect.stopthebribe.modelhelpers.ReportHelper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.ushahidi.android.app.entities.ReportEntity;

public class ViewReportActivity extends FragmentActivity {
	TextView editTitle, editAuthor, editDate, editDescription, placeName, link;
	ReportHelper model;
	ReportEntity report;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_detail);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		/*Bundle extras = getIntent().getExtras();
		
		int id = extras.getInt("id");
		model = new ReportHelper(getApplicationContext());
		report = model.fetchReportById(id);
		Incident inc = report.getIncident();*/
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(ReportDetailFragment.ARG_ITEM_ID, getIntent()
					.getIntExtra(ReportDetailFragment.ARG_ITEM_ID, 0));
			ReportDetailFragment fragment = new ReportDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.report_detail_container, fragment).commit();
		}

		
		
		
	}
	

    public void onPause() {
        super.onPause();
//        try {
//            getActivity().unregisterReceiver(fetchBroadcastReceiver);
//        } catch (IllegalArgumentException e) {
//        }
    }


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.viewreport, menu);
		return true;
	}


}
