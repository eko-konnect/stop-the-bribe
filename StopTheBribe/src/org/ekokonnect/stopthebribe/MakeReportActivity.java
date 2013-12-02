package org.ekokonnect.stopthebribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.ekokonnect.stopthebribe.adapters.GalleryListAdapter;
import org.ekokonnect.stopthebribe.models.Database;
import org.ekokonnect.stopthebribe.models.Gallery;
import org.ekokonnect.stopthebribe.ui.AttachActionProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.devsmart.android.ui.HorizontalListView;
import com.ushahidi.android.app.database.ReportDao;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.AddReportModel;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.PhotoUtils;
import com.ushahidi.java.sdk.api.Incident;

/**
 * @author oyewale
 *
 */
public class MakeReportActivity extends Activity {
	private static final String TAG = "MakeReport";
	AttachActionProvider attachActionProvider;
	Spinner category_spinner;
	private AddReportModel model;
	private Date date;
	private int id = 0;
	private Vector<Integer> mVectorCategories = new Vector<Integer>();
	private HorizontalListView listview;
	private ArrayList<Gallery> galleryItems;
	private GalleryListAdapter galleryAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_report);
		//model = new AddReportModel();
		initializeUI();
		
	}

	/**
	 * Initialize UI Controls and Widgets
	 */
	private void initializeUI() {
		// Initialize Category Spinner
		category_spinner = (Spinner) findViewById(R.id.make_report_category_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.report_categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(adapter);
		
		mVectorCategories.add(Integer.valueOf(1));
		
		galleryItems = new ArrayList<Gallery>();
		galleryAdapter = new GalleryListAdapter(this, galleryItems);
		listview = (HorizontalListView) findViewById(R.id.make_report_gallery_listview);  
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				log("item clicked: " + arg2);
			}
		});
        listview.setAdapter(galleryAdapter);
        registerForContextMenu(listview);
		
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
		super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.make_report_attachment_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		log("context: "+info.id);
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
	 * @author henryaddo
	 */
	private boolean addReport() {
		log("Adding new reports");
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);

		ReportEntity report = new ReportEntity();
		Incident incident = new Incident();
		incident.setTitle("Demo Title");
		incident.setDescription("Demo Desc");
		incident.setMode(0);
		incident.setLocationName("Ile-Ife");
		incident.setVerified(0);
		incident.setLatitude(Double
				.valueOf("6.24443"));
		incident.setLongitude(Double.valueOf("7.23332"
				.toString()));
		if (date != null) {
			incident.setDate(date);
		} else {
			incident.setDate(new Date());
		}

		report.setIncident(incident);
		report.setPending(1);
		File[] files = new File[1];
		files[0] = new File("file:///storage/emulated/0/Pictures/StopTheBribe/IMG_20131130_195341.jpg");

		if (id == 0) {
			// Add a new pending report
			//mVectorCategories.add(
			Database db = new Database(getApplicationContext()).open();
			boolean b = db.mReportDao.addReport(report);
			
			if (model.addPendingReport(report, mVectorCategories,
					files, "http://demo.link.com")) {
				// move saved photos
				log("Moving photos to fetched folder");
				ImageManager.movePendingPhotos(this);
				id = report.getDbId();
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
		if (requestCode == AttachActionProvider.AUDIO_GALLERY_REQUEST_CODE) {
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
		if (scheme == "file"){
			path = content.getPath();
			log(path.substring(path.length()-3));
			if (path.substring(path.length()-3) == "jpg"){
				item.thumb = ThumbnailUtils.extractThumbnail(decodeFile(path),
						200, 200);
			} else {
				item.thumb = ThumbnailUtils.createVideoThumbnail(path,
						MediaStore.Video.Thumbnails.MINI_KIND);
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
		//item.thumb = decodeFile(path);
		galleryItems.add(item);
		galleryAdapter.notifyDataSetChanged();

	}

	public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }
}