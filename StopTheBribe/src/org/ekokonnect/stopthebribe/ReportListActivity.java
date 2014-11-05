package org.ekokonnect.stopthebribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ekokonnect.stopthebribe.adapters.FetchedReportListAdapter;
import org.ekokonnect.stopthebribe.adapters.PendingReportListAdapter;

import models.ReportDataSource;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IOpenGeoSmsSchema;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.AddReportModel;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.services.FetchReports;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.api.Incident;
import com.ushahidi.java.sdk.api.Person;
import com.ushahidi.java.sdk.api.ReportFields;
import com.ushahidi.java.sdk.api.json.Response;

public class ReportListActivity extends ListActivity {
	private static final String TAG = "ReportListActivity";
	private ReportDataSource reportDataSource;
	//ArrayList<Report> reports;
	ListReportModel model;
	//List<ReportEntity> reportList;
	private boolean refreshState = false;
	private FetchedReportListAdapter fetchedAdapter;
	private PendingReportListAdapter pendingAdapter;
	
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
		
		Preferences.loadSettings(getApplicationContext());
		if (!Preferences.isSignedIn){
			finish();
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		}
		
		setContentView(R.layout.activity_report_list);
		ListView listview = getListView();				
		
			
		
		fetchedAdapter = new FetchedReportListAdapter(this);
		//fetchedAdapter.refresh();
		pendingAdapter = new PendingReportListAdapter(this);
		//pendingAdapter.refresh();
		
		setListAdapter(fetchedAdapter);
		
		// validate URL
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
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int position,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), ViewReportActivity.class);
//				ReportEntity report = reportList.get(position);
				ReportEntity report = fetchedAdapter.getItem(position);
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
	}

	@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        registerReceiver(broadcastReceiver, new IntentFilter(
                SyncServices.SYNC_SERVICES_ACTION));
        
        refreshReportLists();
        
        executeUploadTask();
    }

    @Override
    public void onStart() {
        super.onStart();
//        AnalyticsUtils.activityStop(this);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	if (model != null)
    		model.close();
    	super.onDestroy();
    	
    }

    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
        }

    }
    
    private void executeUploadTask() {
        if (!pendingAdapter.isEmpty()) {
            new UploadTask(this).execute((String) null);
        }
    }
    
    /**
     * Background progress task for saving Model
     */
    protected class UploadTask extends ProgressTask {
        public UploadTask(Activity activity) {
            super(activity, R.string.uploading);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            preparePendingReports();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            // delete pending reports
            return uploadPendingReports();

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                toastLong(R.string.uploaded);
            } else {
                toastLong(R.string.failed);
            }
            refreshReportLists();
            showCategories();
        }
    }

    /**
     * Refresh for new reports
     */
    class RefreshReports extends ProgressTask {

        protected Integer status = 4; // there is no internet

        public RefreshReports(Activity activity) {
            super(activity, R.string.loading);
            // pass custom loading message to super call
            refreshState = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
            refreshState = true;
            updateRefreshStatus();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                // check if there is internet
                if (Util.isConnected(getApplicationContext())) {
                    // upload pending reports.
                    if (!pendingAdapter.isEmpty()) {
                        uploadPendingReports();
                    }

                    // delete everything before updating with a new one
                    deleteFetchedReport();

                    status = new ReportsApi().saveReports(getApplicationContext()) ? 0
                            : 99;

                    // fetch categories -- assuming everything will go just
                    new CategoriesApi().getCategoriesList();
                    return true;
                }

                Thread.sleep(1000);
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (status == 4) {
                    toastLong(R.string.internet_connection);
                } else if (status == 110) {
                    toastLong(R.string.connection_timeout);
                } else if (status == 100) {
                    toastLong(R.string.could_not_fetch_reports);
                } else if (status == 0) {
                	Log.e(TAG, "Unknown Error");
//                    if (filterCategory > 0) {
//                        reportByCategoryList();
//                    } else {
//                        refreshReportLists();
//                    }
//                    showCategories();
                }
                refreshState = false;
                updateRefreshStatus();
            }
        }
    }
    
    protected void toastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastShort(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_SHORT)
                .show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT)
                .show();
    }
    
    public void showCategories() {
    	// Categories Filter
//        spinnerArrayAdapter = new CategorySpinnerAdater(getActivity());
//        spinnerArrayAdapter.refresh();
    }

    private void updateRefreshStatus() {
    	// TASK
//        if (refresh != null) {
//            if (refreshState)
//                refresh.setActionView(R.layout.indeterminate_progress_action);
//            else
//                refresh.setActionView(null);
//        }

    }
    
    private List<ReportEntity> mPendingReports;

    private void preparePendingReports() {
        mPendingReports = pendingAdapter.pendingReports();
        if (mPendingReports != null) {
            for (ReportEntity report : mPendingReports) {
                long rid = report.getDbId();

                report.setCategories(pendingAdapter
                        .fetchCategoriesId((int) rid));
            }
        }
    }

    private boolean uploadPendingReports() {

        boolean retVal = true;

        ReportFields fields = new ReportFields();
        Incident incident = new Incident();
        ReportsApi reportApi = new ReportsApi();
        if (mPendingReports != null) {
            for (ReportEntity report : mPendingReports) {
//                long rid = report.getDbId();
//                ;
//                int state = Database.mOpenGeoSmsDao.getReportState(rid);
//                if (state != IOpenGeoSmsSchema.STATE_NOT_OPENGEOSMS) {
//                    if (!sendOpenGeoSmsReport(report, state)) {
//                        retVal = false;
//                    }
//                    continue;
//                }

                // Set the incident details
                incident.setTitle(report.getIncident().getTitle());
                incident.setDescription(report.getIncident().getDescription());
                incident.setDate(report.getIncident().getDate());

                incident.setLatitude(report.getIncident().getLatitude());
                incident.setLongitude(report.getIncident().getLongitude());
                incident.setLocationName(report.getIncident().getLocationName());
                fields.fill(incident);

                // Set person details
                Preferences.loadSettings(getApplicationContext());
                if ((!TextUtils.isEmpty(Preferences.lastname))
                        && (!TextUtils.isEmpty(Preferences.email))) {
                    fields.setPerson(new Person(Preferences.firstname,
                            Preferences.lastname, Preferences.email));
                    Log.d(TAG, Preferences.email);
                } else {
                	Log.e(TAG, "No name");
                }

                // Add categories
                fields.addCategory(report.getCategories());

                // Add photos
                List<File> photos = new UploadPhotoAdapter(this)
                        .pendingPhotos((int) report.getDbId());
                if (photos != null && photos.size() > 0)
                    fields.addPhotos(photos);

                // Upload
                Response response = reportApi.submitReport(fields);
                if (response != null) {
                    if (response.getErrorCode() == 0) {
                        deletePendingReport((int) report.getDbId());
                    } else {
                        retVal = false;
                    }
                } else {
                    deletePendingReport((int) report.getDbId());
                }
            }
        }
        return retVal;
    }

    private void deletePendingReport(int reportId) {

        // make sure it's an existing report
        AddReportModel model = new AddReportModel();
        UploadPhotoAdapter pendingPhoto = new UploadPhotoAdapter(this);
        if (reportId > 0) {
            if (model.deleteReport(reportId)) {
                // delete images
                for (int i = 0; i < pendingPhoto.getCount(); i++) {
                    ImageManager.deletePendingPhoto(this, "/"
                            + pendingPhoto.getItem(i).getPhoto());
                }
            }
        }
    }

    private void deleteFetchedReport() {
        final List<ReportEntity> items = fetchedAdapter.fetchedReports();
        for (ReportEntity report : items) {
            if (new ListReportModel(getApplicationContext()).deleteAllFetchedReport(report
                    .getIncident().getId())) {
                final List<PhotoEntity> photos = new ListPhotoModel(getApplicationContext())
                        .getPhotosByReportId(report.getIncident().getId());

                for (PhotoEntity photo : photos) {
                    ImageManager.deletePendingPhoto(this,
                            "/" + photo.getPhoto());
                }
            }

        }

    }
    
    private void refreshReportLists(){
    	pendingAdapter.refresh();
    	fetchedAdapter.refresh();
    }
	
}
