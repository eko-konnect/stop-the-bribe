package org.ekokonnect.stopthebribe.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
//import android.support.v4.view.ActionProvider;

public class AttachActionProvider extends ActionProvider implements OnMenuItemClickListener{

	Context mContext;
	private PackageManager mPackageManager;
    private Intent[] mIntent;
    
    public static Uri imageBugFixUri;
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;
    public final static int AUDIO_GALLERY_REQUEST_CODE = 1000; 
    
    public static final int DEFAULT_LIST_LENGTH = 4;
	private static String TAG = null;
    private int mListLength = DEFAULT_LIST_LENGTH;
    
    private final Object mLock = new Object();  
    private HashMap<ResolveInfo, Intent> resolveIntent = new HashMap<ResolveInfo, Intent>();
 
    private List<ResolveInfo> mActivities = new ArrayList<ResolveInfo>();
    private List<String> mCustomPackages = new ArrayList<String>();
    
    public Activity callingActivity = null;
	
	public AttachActionProvider(Context context) {		
		super(context);
		this.mContext = context;
		TAG = getClass().getSimpleName();
		mPackageManager = context.getPackageManager();
		//Image Pick
        Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //imageIntent.setType("image/*");
        //Image Capture
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Audio
        Intent audioIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//        audioIntent.setType("audio/*");
        //Video Pick
        //Intent vidIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        Intent vidIntent = new Intent(Intent.ACTION_PICK);
        vidIntent.setType("video/* image/*");
        //imageIntent.setType("video/*");
        Intent vidRecIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        
        mIntent = new Intent[4];
        mIntent[0] = camIntent;
        //mIntent[4] = imageIntent;        
        mIntent[3] = audioIntent;
        mIntent[2] = vidIntent;
        mIntent[1] = vidRecIntent;
        
        setShareIntent();
	}



	/* (non-Javadoc)
	 * @see android.support.v4.view.ActionProvider#onPrepareSubMenu(android.view.SubMenu)
	 */
	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		//Log.d(TAG, "onPrepareSubMenu: "+mActivities.size()+" activities");
		
		subMenu.clear();
		 
        for (int i = 0; i < Math.min(mListLength, mActivities.size()); i++) {
            ResolveInfo appInfo = mActivities.get(i);
            subMenu.add(0, i, i, appInfo.loadLabel(mPackageManager))
                    .setIcon(appInfo.loadIcon(mPackageManager))
                    .setIntent(resolveIntent.get(appInfo))
                    .setOnMenuItemClickListener(this);
        }
 
        if (mListLength < mActivities.size()) {
            subMenu = subMenu.addSubMenu(Menu.NONE, mListLength, mListLength,
                    "See all…");
 
            for (int i = 0; i < mActivities.size(); i++) {
                ResolveInfo appInfo = mActivities.get(i);
 
                subMenu.add(0, i, i, appInfo.loadLabel(mPackageManager))
                        .setIcon(appInfo.loadIcon(mPackageManager))
                        .setIntent(resolveIntent.get(appInfo))
                        .setOnMenuItemClickListener(this);
            }
        }
	}
	
	public void setShareIntent() {
        //mIntent = intent;
        reloadActivities();
    }
	private void reloadActivities() {
        loadActivities();
        sortActivities();
    }
 
    /**
     * 根据Intent读取Activity列表
     */
    private void loadActivities() {
        if (mIntent != null) {
            synchronized (mLock) {
                mActivities.clear();
                
                List<ResolveInfo> active = new ArrayList<ResolveInfo>();
                for (int i=0; i<mIntent.length; i++){
                	List<ResolveInfo> activities =
                            mPackageManager.queryIntentActivities(mIntent[i], PackageManager.MATCH_DEFAULT_ONLY);
                	
                	for (int j=0; j<activities.size(); j++){
                		resolveIntent.put(activities.get(j), mIntent[i]);
                		//Log.d(TAG, "Activity "+activities.get(j).activityInfo.name);
                	}
                	
                	active.addAll(activities);
                	
                }
                
                
                //if (active != null) {
//                    mActivities.clear();
                    mActivities.addAll(active);
//                }
            }
        }
    }
 
    /**
     * 对Activity列表排序，自定义的分享目标排在前面
     */
    private void sortActivities() {
        if (mCustomPackages.size() > 0) {
            List<ResolveInfo> customActivities = new ArrayList<ResolveInfo>();
            for (String pkg : mCustomPackages) {
                synchronized (mLock) {
                    int index = findPackageIndex(pkg);
                    if (index > 0) {
                        ResolveInfo resolveInfo = mActivities.remove(index);
                        if (resolveInfo != null) {
                            customActivities.add(resolveInfo);
                        }
                    }
                }
            }
            if (customActivities.size() > 0) {
                synchronized (mLock) {
                    mActivities.addAll(0, customActivities);
                }
            }
        }
    }
	
    private int findPackageIndex(String pkg) {
        int index = -1;
        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            ActivityInfo info = mActivities.get(i).activityInfo;
            if (info != null) {
                if (pkg.equals(info.packageName)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
 
    @Override
    public View onCreateActionView() {
        return null;
    }
 
    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }
 
    @Override
    public boolean hasSubMenu() {
        return true;
    }



	@Override
	public boolean onMenuItemClick(MenuItem item) {
		ResolveInfo resolveInfo = mActivities.get(item.getItemId());
        ComponentName chosenName = null;
        if (resolveInfo.activityInfo != null) {
            chosenName = new ComponentName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name);
        }
        Intent intent = item.getIntent();
        intent.setComponent(chosenName);
        
        if (intent == mIntent[0]){
          Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
          imageBugFixUri = fileUri;
          intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//          if (hasImageCaptureBug()) {
//        	    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
//        	} else {
//        	    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        	}
        }else if (intent == mIntent[1]){
          Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
          intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        }
        
        callingActivity.startActivityForResult(intent, AUDIO_GALLERY_REQUEST_CODE);
        return true;
	}
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "StopTheBribe");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("StopTheBribe", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	public boolean hasImageCaptureBug() {

	    // list of known devices that have the bug
	    ArrayList<String> devices = new ArrayList<String>();
	    devices.add("android-devphone1/dream_devphone/dream");
	    devices.add("generic/sdk/generic");
	    devices.add("vodafone/vfpioneer/sapphire");
	    devices.add("tmobile/kila/dream");
	    devices.add("verizon/voles/sholes");
	    devices.add("google_ion/google_ion/sapphire");

	    String deviceName = android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
	            + android.os.Build.DEVICE;
	    Log.i(TAG, deviceName);
	    return devices.contains(deviceName);

	}
}