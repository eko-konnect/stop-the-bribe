package org.ekokonnect.stopthebribe.adapters;

import java.util.ArrayList;
import java.util.List;

import org.ekokonnect.stopthebribe.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.java.sdk.api.Category;
import com.ushahidi.java.sdk.api.Incident;


public class PendingReportListAdapter extends BaseAdapter {
	private static final String TAG = "ReportListAdapter";
	LayoutInflater inflater;
	Activity activity;
	List<ReportEntity> reports;
	ListReportModel model;
	
	public PendingReportListAdapter(Activity activity){
		reports = new ArrayList<ReportEntity>();
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
		
		if (convertView == null)
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
		
		return vi;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		model = new ListReportModel(activity);
		if(model.loadPendingReports()){
//			model.getReports();
			reports = model.getReports();
			Log.d(TAG, reports.size()+" Reports Loaded");
		} else {
			Log.e(TAG, "error loading reports");
			reports = new ArrayList<ReportEntity>();
		}
		notifyDataSetChanged();
	}
	
	
	public List<Category> fetchCategoriesId(int reportId) {
		List<Category> categories = new ArrayList<Category>();
		Category c = new Category();
		for (CategoryEntity category : model
				.getCategoriesByReportId(reportId)) {
			c.setId(category.getCategoryId());
			categories.add(c);
			

		}

		// delete the last ,
		
		return categories;
	}
	
	public List<ReportEntity> pendingReports() {
		final boolean loaded = model.loadPendingReports();
		if (loaded) {
			return model.getReports();
		}
		return null;
	}
}
