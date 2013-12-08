package org.ekokonnect.stopthebribe;

import java.util.ArrayList;
import java.util.List;

import org.ekokonnect.stopthebribe.modelhelpers.ReportHelper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class ViewReportActivity extends Activity {
	TextView editTitle, editAuthor, editDate, editDescription, placeName, link;
	ReportHelper model;
	ReportEntity report;	
	private ArrayList<ImageToLoad> images;
	private HorizontalImageScrollerAdapter pendingPhoto;
	private HorizontalImageScroller scroller;
	
	private GoogleMap mMap;
	private MapView mMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_report);
		editTitle = (TextView) findViewById(R.id.reportTitle);
		editAuthor = (TextView) findViewById(R.id.reportAuthor);
		editDate = (TextView) findViewById(R.id.reportDate);
		editDescription = (TextView) findViewById(R.id.reportDescription);
		placeName = (TextView) findViewById(R.id.reportPlace);
		link = (TextView) findViewById(R.id.reportLink);
		mMapView = (MapView) findViewById(R.id.loc_map);
		
		Bundle extras = getIntent().getExtras();
		
		int id = extras.getInt("id");
		model = new ReportHelper(getApplicationContext());
		report = model.fetchReportById(id);
		Incident inc = report.getIncident();
		initializeUI(inc);
		
		
		setListPhotos(id);
		setNews(id);
		initializeMap(inc, savedInstanceState);
//		String title = (String)extras.getString("title");
//		String author = (String)extras.getString("author");
//		String date = (String)extras.getString("date");
//		String description = (String)extras.getString("description");
		
		
		
	}
	
	private void initializeMap(Incident inc, Bundle bundle){
		mMapView.onCreate(bundle);
        // Get GoogleMap from MapView
        mMap = mMapView.getMap();

        try {
            MapsInitializer.initialize(this);
            
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
		
		scroller = (HorizontalImageScroller) findViewById(R.id.view_report_photo);
		images = new ArrayList<ImageToLoad>();
		pendingPhoto = new HorizontalImageScrollerAdapter(this, images);
		scroller.setAdapter(pendingPhoto);
		
	}
	
	@Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();        
    }

    public void onPause() {
        super.onPause();
//        try {
//            getActivity().unregisterReceiver(fetchBroadcastReceiver);
//        } catch (IllegalArgumentException e) {
//        }
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viewreport, menu);
		return true;
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
		
			ListPhotoModel mListPhotoModel = new ListPhotoModel();
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
						imb.setBitmap(ImageManager.getBitmaps(getApplicationContext(), path));
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
