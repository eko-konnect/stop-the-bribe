package org.ekokonnect.stopthebribe;

import java.io.File;
import java.util.List;

import org.ekokonnect.stopthebribe.adapters.FetchedReportListAdapter;
import org.ekokonnect.stopthebribe.adapters.PendingReportListAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.AddReportModel;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.api.Incident;
import com.ushahidi.java.sdk.api.Person;
import com.ushahidi.java.sdk.api.ReportFields;
import com.ushahidi.java.sdk.api.json.Response;

//import org.ekokonnect.reprohealth.TipListActivity.DatePickerFragment;

/**
 * A list fragment representing a list of Places. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link PlaceDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ReportListFragment extends ListFragment {

	private static final String TAG = "ReportListFragment";
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
//	private static final String TAG = "ReportListActivity";	
	ListReportModel model;
	//List<ReportEntity> reportList;
	private boolean refreshState = false;
	private FetchedReportListAdapter fetchedAdapter;
	private PendingReportListAdapter pendingAdapter;
	private View progressView;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int id) {
		}
	};
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                try {
                    getActivity().unregisterReceiver(broadcastReceiver);
                    refreshReportLists();
                    showProgress(false);
                } catch (IllegalArgumentException e) {
                	e.printStackTrace();
                }
                Log.d(TAG, "Broadcast recieved");
//                goToReports();

            }
        }
    };
    
    void showProgress(boolean b){
    	if (b){
    		progressView.setVisibility(View.VISIBLE);
    	} else {
    		progressView.setVisibility(View.GONE);
    	}
    	
    }

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ReportListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadAdapters();
		//pendingAdapter.refresh();
		
		setListAdapter(fetchedAdapter);
		
		setHasOptionsMenu(true);
	}	
	
	void loadAdapters(){
		fetchedAdapter = new FetchedReportListAdapter(getActivity());
		//fetchedAdapter.refresh();
		pendingAdapter = new PendingReportListAdapter(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_report_list, container, false);	

		progressView = (View) view.findViewById(R.id.progressContainer);
		
		Preferences.loadSettings(getActivity());
		int serviceStatus = Preferences.serviceStatus;
		if (serviceStatus == 1){
			showProgress(true);
		} else {
			showProgress(false);
		}
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
Log.e(TAG, "Activated Position: "+ STATE_ACTIVATED_POSITION);
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
		
		return view;
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

//		String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
        
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		int incidentId = fetchedAdapter.getItem(position).getIncident().getId();
		mCallbacks.onItemSelected(incidentId);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		Log.e(TAG, "Activate OnClick");
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		MenuInflater inflater = getMenuInflater();
		Log.d(TAG, "Inflating Menu");
		inflater.inflate(R.menu.reportlist, menu);
//		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		switch (id) {
		case R.id.action_create_report:
			startActivity(new Intent(getActivity().getApplicationContext(), MakeReportActivity.class));
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
		Preferences.loadSettings(getActivity().getApplicationContext());
		Preferences.isSignedIn = false;
		Preferences.saveSettings(getActivity().getApplicationContext());
		
		//FB Logout
		Session session = Session.getActiveSession();
		if (session != null){
			session.closeAndClearTokenInformation();
		}
		
		getActivity().finish();
		startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
	}
	
	@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
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
	public void onDestroy() {
    	// TODO Auto-generated method stub
    	if (model != null)
    		model.close();
    	super.onDestroy();
    	
    }

    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
        }

    }
    
    private void executeUploadTask() {
        if (!pendingAdapter.isEmpty()) {
            new UploadTask(getActivity()).execute((String) null);
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
                if (Util.isConnected(getActivity().getApplicationContext())) {
                    // upload pending reports.
                    if (!pendingAdapter.isEmpty()) {
                        uploadPendingReports();
                    }

                    // delete everything before updating with a new one
                    deleteFetchedReport();

                    status = new ReportsApi().saveReports(getActivity().getApplicationContext()) ? 0
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(getActivity(), getText(message), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastShort(int message) {
        Toast.makeText(getActivity(), getText(message), Toast.LENGTH_SHORT)
                .show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT)
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
                Preferences.loadSettings(getActivity().getApplicationContext());
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
                List<File> photos = new UploadPhotoAdapter(getActivity())
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
        UploadPhotoAdapter pendingPhoto = new UploadPhotoAdapter(getActivity());
        if (reportId > 0) {
            if (model.deleteReport(reportId)) {
                // delete images
                for (int i = 0; i < pendingPhoto.getCount(); i++) {
                    ImageManager.deletePendingPhoto(getActivity(), "/"
                            + pendingPhoto.getItem(i).getPhoto());
                }
            }
        }
    }

    private void deleteFetchedReport() {
        final List<ReportEntity> items = fetchedAdapter.fetchedReports();
        for (ReportEntity report : items) {
            if (new ListReportModel(getActivity().getApplicationContext()).deleteAllFetchedReport(report
                    .getIncident().getId())) {
                final List<PhotoEntity> photos = new ListPhotoModel(getActivity().getApplicationContext())
                        .getPhotosByReportId(report.getIncident().getId());

                for (PhotoEntity photo : photos) {
                    ImageManager.deletePendingPhoto(getActivity(),
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
