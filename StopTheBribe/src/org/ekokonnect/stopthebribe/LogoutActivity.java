package org.ekokonnect.stopthebribe;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import org.ekokonnect.stopthebribe.R;

public class LogoutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_logout);
		ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Logging out");
		Log.i("LOGOUT ACTIVITY", "logging out started");
		mProgressDialog.show();
		LoginActivity.logout(getApplicationContext());
		finish();
		/*Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
		Intent intent = new Intent(getApplicationContext(), ReportListActivity.class);
		startActivity(intent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logout, menu);
		return true;
	}
}