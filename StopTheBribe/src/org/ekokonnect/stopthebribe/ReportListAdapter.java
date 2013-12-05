package org.ekokonnect.stopthebribe;

import java.util.ArrayList;
import java.util.List;

import models.Report;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.java.sdk.api.Incident;


public class ReportListAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Activity activity;
	List<ReportEntity> reports;
	
	public ReportListAdapter(Activity activity, List<ReportEntity> reports){
		this.reports = reports;
		this.activity = activity;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return reports.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentView) {
		View vi = convertView;
		
		if (convertView == null){
			 vi = inflater.inflate(R.layout.report_list_row, null);
			
			 TextView title = (TextView) vi.findViewById(R.id.title);
			 TextView description = (TextView)vi.findViewById(R.id.summary);
			 TextView date = (TextView)vi.findViewById(R.id.date);
			 
			 ReportEntity report = reports.get(position);
			 Incident inc = report.getIncident();
			 title.setText(inc.getTitle());
			 date.setText(inc.getDate().toLocaleString());
			 
			 String desc = inc.getDescription();
			 String shortdescription = desc.substring(0, Math.min(250, desc.length()));
			 description.setText(shortdescription);
		}
		return vi;
	}
	
	
	
	
}
