package org.ekokonnect.stopthebribe;

import java.util.ArrayList;

import models.Report;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ReportListAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Activity activity;
	ArrayList<Report> reports;
	
	public ReportListAdapter(Activity activity, ArrayList<Report> reports){
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
			 
			 Report report = reports.get(position);
			 title.setText(report.getTitle());
			 date.setText(report.getDate());
			 
			 String shortdescription = report.getDescription().substring(0, 250);
			 description.setText(shortdescription);;
		}
		return vi;
	}
	
	
	
	
}
