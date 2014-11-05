package org.ekokonnect.stopthebribe;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Toast;
import com.ushahidi.android.app.Preferences;

public class SettingsActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_settings);
		for(int i=0;i<getPreferenceScreen().getPreferenceCount(); i++){
			initSummary(getPreferenceScreen().getPreference(i));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	private void showDefaultValue(Preference p){
		
		//SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Preferences.loadSettings(getApplicationContext());
		if(p instanceof EditTextPreference){
			EditTextPreference editPref = (EditTextPreference)p;
			//editPref.setDefaultValue(sharedPref.getString(editPref.getKey(), "Not Set"));
			if(editPref.getKey().equals("Firstname")){
				editPref.setSummary(Preferences.firstname);
			}
			if(editPref.getKey().equals("Lastname")){
				editPref.setSummary(Preferences.lastname);
			}
			if(editPref.getKey().equals("Email")){
				editPref.setSummary(Preferences.email);
			}
			//editPref.setSummary(sharedPref.getString(editPref.getKey(), "Not Set"));
			//editPref.getEditText().setText(sharedPref.getString(editPref.getKey(), "Not Set"));
		}
	}
	private void initSummary(Preference p) {
	      if (p instanceof PreferenceCategory) {
	        PreferenceCategory cat = (PreferenceCategory) p;
	        for (int i = 0; i < cat.getPreferenceCount(); i++) {
	          initSummary(cat.getPreference(i));
	        }
	      } else {
	        showDefaultValue(p);
	      }
	    }

}