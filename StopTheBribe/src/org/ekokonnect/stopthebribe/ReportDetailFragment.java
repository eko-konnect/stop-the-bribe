package org.ekokonnect.stopthebribe;



import java.util.ArrayList;
import java.util.List;

import org.ekokonnect.stopthebribe.modelhelpers.ReportHelper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoad;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadBitmap;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScroller;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScrollerAdapter;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportNewsModel;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.java.sdk.api.Incident;

/**
 * A fragment representing a single Place detail screen. This fragment is either
 * contained in a {@link PlaceListActivity} in two-pane mode (on tablets) or a
 * {@link PlaceDetailActivity} on handsets.
 */
public class ReportDetailFragment extends Fragment {
	
	TextView editTitle, editAuthor, editDate, editDescription, placeName, link;
	ReportHelper model;
	ReportEntity report;	
	private ArrayList<ImageToLoad> images;
	private HorizontalImageScrollerAdapter pendingPhoto;
	private HorizontalImageScroller scroller;
	private ShareActionProvider mShareActionProvider;
	private GoogleMap mMap;
	private MapView mMapView;
	public static final String ARG_ITEM_ID = "report_id";
	Incident inc;
	int reportId;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ReportDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		if (getArguments().containsKey(title)) {
			
		reportId = getArguments().getInt(ARG_ITEM_ID);
		model = new ReportHelper(getActivity().getApplicationContext());
		report = model.fetchReportById(reportId);
		inc = report.getIncident();
		
//		}
			setHasOptionsMenu(true);
			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_view_report,
				container, false);
		
		editTitle = (TextView) rootView.findViewById(R.id.reportTitle);
		editAuthor = (TextView) rootView.findViewById(R.id.reportAuthor);
		editDate = (TextView) rootView.findViewById(R.id.reportDate);
		editDescription = (TextView) rootView.findViewById(R.id.reportDescription);
		placeName = (TextView) rootView.findViewById(R.id.reportPlace);
		link = (TextView) rootView.findViewById(R.id.reportLink);
		mMapView = (MapView) rootView.findViewById(R.id.loc_map);
		scroller = (HorizontalImageScroller) rootView.findViewById(R.id.view_report_photo);
		
		initializeUI(inc);
		
		
		setListPhotos(reportId);
		setNews(reportId);
		initializeMap(inc, savedInstanceState);
		

		return rootView;
	}
	
	
    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();

    }
    
    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();        
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.viewreport, menu);
		MenuItem item = menu.findItem(R.id.action_share);
		
		mShareActionProvider = (ShareActionProvider)item.getActionProvider();
		mShareActionProvider.setShareIntent(getDefaultShareIntent());
//		return true;
	}
	//This method shares the content of the tip(plain text) using a ShareActionProvider.
	private Intent getDefaultShareIntent() {
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("text/plain");
	    intent.putExtra(Intent.EXTRA_SUBJECT, inc.getTitle());
	    intent.putExtra(Intent.EXTRA_TEXT, inc.getDescription()+ "\n\n-via StopTheBribe App for Android");
	    
	    return intent;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
//		case R.id.action_share:
//			Intent intent = new Intent(Intent.ACTION_SEND);
//		    intent.setType("text/plain");		    
//		    intent.putExtra(Intent.EXTRA_SUBJECT, editTitle.getText());
//		    intent.putExtra(Intent.EXTRA_TEXT, editDescription.getText()+ " \n\n-via StopTheBribe for Android Phones");
//			doShare(intent);
//			return true;
		case android.R.id.home:
			
			return true;			
		default:
			return false;
		}
//		return super.onOptionsItemSelected(item);
	}
	
	// Somewhere in the application.
	  public void doShare(Intent shareIntent) {
	      // When you want to share set the share intent.
	      mShareActionProvider.setShareIntent(shareIntent);
	  }

	private void initializeMap(Incident inc, Bundle bundle){
		mMapView.onCreate(bundle);
        // Get GoogleMap from MapView
        mMap = mMapView.getMap();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
            
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
                    inc.getLatitude(), inc.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.moveCamera(center);
            mMap.moveCamera(zoom);

            // Add a marker to this location
            addMarker(mMap, inc.getLatitude(),
                    inc.getLongitude());
            
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
	}
	
	private void initializeUI(Incident inc){
		
		editTitle.setText(inc.getTitle());
		editAuthor.setText("Anonymous");
		editDate.setText(inc.getDate().toLocaleString());
		editDescription.setText(inc.getDescription());
		placeName.setText(inc.getLocationName());
		
//		scroller = (HorizontalImageScroller) findViewById(R.id.view_report_photo);
		images = new ArrayList<ImageToLoad>();
		pendingPhoto = new HorizontalImageScrollerAdapter(getActivity().getApplicationContext(), images);
		scroller.setAdapter(pendingPhoto);
		
	}
	
	
	private void setNews(int reportId){
		ListReportNewsModel mListNewsModel = new ListReportNewsModel();
        final boolean loaded = mListNewsModel.load(reportId);
        int totalNews = mListNewsModel.totalReportNews();
        if (loaded && totalNews>0) {
            String news = mListNewsModel.getNews().get(0).getUrl();
            link.setText(news);
        }
	}
	
	private void setListPhotos(int reportId) {
		
			ListPhotoModel mListPhotoModel = new ListPhotoModel(getActivity().getApplicationContext());
//			final boolean loaded = mListPhotoModel.load(reportId);
//			int totalPhotos = mListPhotoModel.totalReportPhoto();
//			if (loaded) {
				final List<PhotoEntity> items = mListPhotoModel.getPhotosByReportId(reportId);
				if (items.size() > 0) {
					for (PhotoEntity photo : items){
						//PhotoEntity photo = items.get(0);
						String path = photo.getPhoto();
						Log.d("ViewReport", "Photo: " + path);
						ImageToLoadBitmap imb = new ImageToLoadBitmap();
						imb.setBitmap(ImageManager.getBitmaps(getActivity().getApplicationContext(), path));
						imb.setPath(path);
						
						pendingPhoto.addItem(imb);
					}
					
				} else {
//					photo.setVisibility(View.GONE);
//					total.setVisibility(View.GONE);
//					listPhotosEmptyView.setVisibility(View.VISIBLE);
				}
//			}
		
	}
	
	private void addMarker(GoogleMap map, double lat, double lon) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
    }

	
}
