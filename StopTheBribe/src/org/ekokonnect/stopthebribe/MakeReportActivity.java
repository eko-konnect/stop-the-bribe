package org.ekokonnect.stopthebribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.ekokonnect.stopthebribe.modelhelpers.ReportHelper;
import org.ekokonnect.stopthebribe.models.Gallery;
import org.ekokonnect.stopthebribe.ui.AttachActionProvider;
import org.ekokonnect.stopthebribe.utils.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoad;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadBitmap;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScroller;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScrollerAdapter;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalListView;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.java.sdk.api.Incident;
import com.ushahidi.java.sdk.api.ReportFields;
import com.ushahidi.java.sdk.api.json.Response;

/**
 * @author oyewale
 *
 */
public class MakeReportActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	private static final String TAG = "MakeReport";
	AttachActionProvider attachActionProvider;
	Spinner category_spinner;
	private ReportHelper reportHelper;
	private Date date;
	private int id = 0;
	private Vector<Integer> mVectorCategories = new Vector<Integer>();
	private HorizontalListView listview;
	//private ArrayList<Gallery> galleryItems;
	private HorizontalImageScrollerAdapter pendingPhoto;
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private String placeString = "No Location";
	private EditText mIncidentTitle;
	private EditText mIncidentDescTitle;
	private HorizontalImageScroller scroller;
	private ArrayList<ImageToLoad> images;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_report);
		reportHelper = new ReportHelper(getApplicationContext());
		mLocationClient = new LocationClient(this, this, this);
		initializeUI();
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.disconnect();
		super.onStop();
		
	}

	/**
	 * Initialize UI Controls and Widgets
	 */
	private void initializeUI() {
		//Initialize EditText
		mIncidentTitle = (EditText) findViewById(R.id.make_report_title_text);
		mIncidentDescTitle = (EditText) findViewById(R.id.make_report_description);
		
		// Initialize Category Spinner
		category_spinner = (Spinner) findViewById(R.id.make_report_category_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.report_categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(adapter);
		
		mVectorCategories.add(Integer.valueOf(1));
		
		//galleryItems = new ArrayList<Gallery>();
		scroller = (HorizontalImageScroller) findViewById(R.id.make_report_gallery_listview);
		images = new ArrayList<ImageToLoad>();
		pendingPhoto = new HorizontalImageScrollerAdapter(this, images);
		scroller.setAdapter(pendingPhoto);
//		pendingPhoto.
//		listview = (HorizontalListView) findViewById(R.id.make_report_gallery_listview);  
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				log("item clicked: " + arg2);
//			}
//		});
//        listview.setAdapter(pendingPhoto);
//        registerForContextMenu(listview);
		
		Button reportButton = (Button) findViewById(R.id.make_report_button);
		reportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addReport();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_report, menu);
		
		// Set up ShareActionProvider's default share intent
	    MenuItem attachItem = menu.findItem(R.id.action_add_attachment);
	    attachActionProvider = (AttachActionProvider)
	            attachItem.getActionProvider();
	    attachActionProvider.callingActivity = this;
//	    //doShare(getDefaultIntent());
//	    attachActionProvider.setShareIntent(getDefaultIntent());
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		//ContextMen
		super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.make_report_attachment_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		log("context: "+listview.getSelectedItemPosition());
	    switch (item.getItemId()) {
	        case R.id.make_report_context_attach_view:
	            //editNote(info.id);
	            return true;
	        case R.id.make_report_context_attach_remove:
	            //deleteNote(info.id);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	/**
	 * Post to local database
	 * 
	 * @author henryaddo [modified by waleoyediran]
	 */
	private boolean addReport() {
		log("Adding new reports");
		//File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);
		

		ReportEntity report = new ReportEntity();
		Incident incident = new Incident();
		incident.setTitle(mIncidentTitle.getText().toString());
		incident.setDescription(mIncidentDescTitle.getText().toString());
		incident.setMode(0);
		incident.setLocationName(placeString);
		incident.setVerified(0);
		incident.setLatitude(Double
				.valueOf(mCurrentLocation.getLatitude()));
		incident.setLongitude(Double.valueOf(mCurrentLocation.getLongitude()));
		if (date != null) {
			incident.setDate(date);
		} else {
			incident.setDate(new Date());
		}

		report.setIncident(incident);
		report.setPending(1);
		//File[] files = new File[1];
		//files[0] = new File("file:///storage/emulated/0/Pictures/StopTheBribe/IMG_20131130_195341.jpg");

		if (id == 0) {
			// Add a new pending report
			
			if (reportHelper.addPendingReport(report, mVectorCategories,
					pendingPhoto.getItems(), "http://demo.link.com")) {
				// move saved photos
				//log("Moving photos to fetched folder");
				//ImageManager.movePendingPhotos(this);			
				
				id = report.getDbId();
				log("report saved");
				ReportFields rf = new ReportFields();
				rf.fill(incident);
				rf.addCategory(1);
				List<File> file = new ArrayList<File>();
				for (ImageToLoad photo : pendingPhoto.getItems()){
					String path = ((ImageToLoadBitmap)photo).getPath();
					file.add(new File(path));			
				}
				if (file.size() > 0){
					log("Added "+file.size()+" photos to the report");
					rf.addPhotos(file);
				}
				
				
				//rf.add
				final ReportFields rf2 = rf;
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ReportsApi reportsApi = new ReportsApi();
						Response response = reportsApi.submitReport(rf2);
						log(response.getErrorCode()+": "+response.getErrorMessage());
					}
				}).start();
			} else {
				return false;
			}
		} else {
			log("Repport ID != 0");
			// Update existing report
//			List<PhotoEntity> photos = new ArrayList<PhotoEntity>();
//			for (int i = 0; i < pendingPhoto.getCount(); i++) {
//				photos.add(pendingPhoto.getItem(i));
//			}
//			if (model.updatePendingReport(id, report, mVectorCategories,
//					photos, view.mNews.getText().toString())) {
//				// move saved photos
//				log("Moving photos to fetched folder");
//				ImageManager.movePendingPhotos(this);
//			} else {
//				return false;
//			}
		}
//		if (mSendOpenGeoSms) {
//			mOgsDao.addReport(id);
//		} else {
//			mOgsDao.deleteReport(id);
//		}
		return true;

	}
	
	private void log(String string) {
		// TODO Auto-generated method stub
		Log.d(TAG, string);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		log("OnActivity Result Callback");
		switch (requestCode) {
			case AttachActionProvider.AUDIO_GALLERY_REQUEST_CODE:
			    if (resultCode == Activity.RESULT_OK) {
			    	Uri content;
			    	if (data!=null){
			    		content = data.getData();
			    	} else {
			    		content = AttachActionProvider.imageBugFixUri;
			    	}
			    	log(content.toString());
			    	addContentToReport(content);
			    }
			    break;
			case Utils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
				switch (resultCode) {
	                case Activity.RESULT_OK :
	                /*
	                 * Try the request again
	                 */
	                
	                break;
	            }
				
			default:
				break;
		}

		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String _getRealPathFromURI(Context context, Uri contentUri) {
	    String[] proj = { MediaStore.Files.FileColumns.DATA };
	    CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}

	private void addContentToReport(Uri content) {
		Gallery item = new Gallery();
		String scheme = content.getScheme();
		String path = null;
		log(scheme);
		if (scheme.equals("file")){
			path = content.getPath();
			log(path.substring(path.length()-3));
			if (path.substring(path.length()-3).equals("jpg")){
				
				item.thumb = ThumbnailUtils.extractThumbnail(decodeFile(path), 200, 200);
//				log("thumb");
			} else {
				item.thumb = ThumbnailUtils.createVideoThumbnail(path,
						MediaStore.Video.Thumbnails.MINI_KIND);
//				log("thumb");
			}
			
		} else {			
			path = _getRealPathFromURI(getApplicationContext(), content);
			ContentResolver cR = getApplicationContext().getContentResolver();
			String type = cR.getType(content);
			log("type: "+type);
			if (type.contains("image")){
				item.thumb = ThumbnailUtils.extractThumbnail(decodeFile(path),
						200, 200);
			}else if (type.contains("video")){
				item.thumb = ThumbnailUtils.createVideoThumbnail(path,
						MediaStore.Images.Thumbnails.MINI_KIND);
			}else{
				item.thumb = BitmapFactory.decodeResource(getResources(), R.drawable.audio_icon);
			}
			
			//Bitmap b = MediaStore.Video.Thumbnails.getThumbnail(cR	, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
		}
		 
		log(path);
		//item.thumb = decodeFile(path)
//		PhotoEntity photo = new PhotoEntity();
//		photo.setPhoto(path);
		
		ImageToLoadBitmap imb = new ImageToLoadBitmap();
		imb.setBitmap(ImageManager.getBitmapsAbsolute(getApplicationContext(), path));
		imb.setPath(path);
		pendingPhoto.addItem(imb);
		//pendingPhoto.notifyDataSetChanged();

	}

	public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 300;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
        }
        return null;

    }
//	private void addPhotoToReport() {
//		File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);
//		if (pendingPhotos != null && pendingPhotos.length > 0) {
//			int id = 0;
//			for (File file : pendingPhotos) {
//				if (file.exists()) {
//					id += 1;
//					PhotoEntity photo = new PhotoEntity();
//					photo.setDbId(id);
//					photo.setPhoto(file.getName());
//					//pendingPhoto.addItem(photo);
//					//pendingPhoto.
//				}
//			}
//		}
//	}	
	
	

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        Utils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //Utils.showErrorDialog(connectionResult.getErrorCode());
        	log("Location Service Connection Failed");
        }
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		log("Location Service Connected");
		mCurrentLocation = mLocationClient.getLastLocation();
		log("Location: "+mCurrentLocation.getLongitude());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
				try {
					List<Address> addresses = geocoder.getFromLocation(
							mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
					String address = addresses.get(0).getAddressLine(0);
					String city = addresses.get(0).getAddressLine(1);
					String country = addresses.get(0).getAddressLine(2);
					placeString = address+", "+city+", "+country;
					log(placeString);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		log("Location Service DisConnected");
	}
}