package org.ekokonnect.stopthebribe;

import java.util.Arrays;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.internal.SessionTracker;
import com.facebook.internal.Utility;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;
import com.ushahidi.android.app.Preferences;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */

	
	//Google Required
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	 private static final String TAG = "LogInActivity";
	
	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mFirstName;
	private String mLastName;
	private String loginMethod;

	// UI references.
//	private EditText mEmailView;
//	private EditText mFirstNameView;
//	private EditText mLastNameView;
//	private Button mLoginView;
	private LoginButton mFacebookLogin;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private View googleSigninButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Check if a user has been logged in
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		mFirstName = preferences.getString("Firstname", null);
//		mLastName = preferences.getString("Lastname", null);
//		
//		if(mFirstName != null && mLastName != null){
//			Intent intent = new Intent(getApplicationContext(), ReportListActivity.class);
//			startActivity(intent);
//		}
		

		setContentView(R.layout.activity_login);

		// Set up the login form.
//		mEmailView = (EditText) findViewById(R.id.email);
//		mFirstNameView = (EditText)findViewById(R.id.firstname);
//		mLastNameView = (EditText)findViewById(R.id.lastname);
		mFacebookLogin = (LoginButton)findViewById(R.id.facebook_sigin);
//		mLoginView = (Button)findViewById(R.id.sign_in_button);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		googleSigninButton = (View) findViewById(R.id.google_signin);
		
		//Google Requirements
		mPlusClient = new PlusClient.Builder(this, this, this)
        .setActions("http://schemas.google.com/AddActivity")
        .build();
// Progress bar to be displayed if the connection failure is not resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

		
		mFacebookLogin.setReadPermissions(Arrays.asList("email"));
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);		
		
		googleSigninButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				googleLogin();
			}
		});
		
		
	}
	
	private void googleLogin(){
		if (!mPlusClient.isConnected()) {
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	
	//Beginning of Method Groups which handle in app validation

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	
	//Google Signin Methods
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		Log.d(TAG, result.toString());
		if (mConnectionProgressDialog.isShowing()){
			if(result.hasResolution()){
				 try {
                     result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
             } catch (SendIntentException e) {
                     mPlusClient.connect();
             }
			}
		}
		
		mConnectionResult = result;
		
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
//		Log.d(TAG, connectionHint.toString());
		showProgress(true);
		if (mPlusClient.getCurrentPerson() != null) {
	        Person currentPerson = mPlusClient.getCurrentPerson();
	        String personName = currentPerson.getDisplayName();
//	        Image personPhoto = currentPerson.getImage();
//	        String personGooglePlusProfile = currentPerson.getUrl();
	        String email = mPlusClient.getAccountName();
	        
	        mFirstName = currentPerson.getName().getGivenName();
	        mLastName = currentPerson.getName().getFamilyName();
	        mEmail = email;
	        Log.i("Login", mFirstName+" "+mLastName+": "+mEmail);
	        
	        toSharedPreferences();
			startMainActivity();
	    }
		
		mConnectionProgressDialog.dismiss();
		
	}
	
	private void startMainActivity() {
		// TODO Auto-generated method stub
		finish();
		Intent intent = new Intent(getApplicationContext(), ReportListActivity.class);
        startActivity(intent);
	}

	@Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
	//End of google required methods
	
	
	//General Purpose Methods
	public void toSharedPreferences(){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = preferences.edit();
        
        Preferences.loadSettings(getApplicationContext());
        Preferences.firstname = mFirstName;
        Preferences.lastname = mLastName;
        Preferences.email = mEmail;
        Preferences.isSignedIn = true;
        Log.i(TAG, "Saved "+Preferences.firstname+
        		":"+Preferences.lastname+":"+Preferences.email+" >> SharedPref");
        Preferences.saveSettings(getApplicationContext());

    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d(TAG, "OnActivityResult");
			super.onActivityResult(requestCode, resultCode, data);
			uiHelper.onActivityResult(requestCode, resultCode, data);			
		
		
			if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
		        mConnectionResult = null;
		        mPlusClient.connect();
		    }
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}
	
	 @Override
	    protected void onStart() {
	        super.onStart();
	        //if(loginMethod == "google"){
	        	//mPlusClient.connect();
	        //}
	        
	    }

	    @Override
	    protected void onStop() {
	        super.onStop();
	        //if(loginMethod=="google"){
	        	mPlusClient.disconnect();
	        	
	        //}
	        
	    }
	//End of general Purpose Methods
	
	    @SuppressWarnings("deprecation")
		private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	        if (state.isOpened()) {
	            Log.i(TAG, "Logged in...");
	            showProgress(true);
	            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                  @Override
                  public void onCompleted(GraphUser user, Response response) {
                  	Log.d(TAG, response.toString());
                      if (user != null) {
                      	
                          mFirstName = user.getFirstName();

//                          session.closeAndClearTokenInformation();
                          
                          mLastName = user.getLastName();
//                          user.get
                          Log.d(TAG, mFirstName);

                          mEmail = user.getProperty("email").toString();
                          Log.d(TAG, mEmail );
                          //mEmail = "";
                          //mSession.closeAndClearTokenInformation();
                          
                          toSharedPreferences();
                          startMainActivity();
                          
                      }
                  }
                  
              });
              
          
	        } else if (state.isClosed()) {
	            Log.i(TAG, "Logged out...");
	        }
	    }
	    
}
