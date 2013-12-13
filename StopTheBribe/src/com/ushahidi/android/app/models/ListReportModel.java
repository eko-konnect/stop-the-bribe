/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.ushahidi.android.app.models;

import java.util.List;

import android.content.Context;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.database.IReportSchema;
import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.entities.MediaEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListReportModel{

	public List<ReportEntity> mReports;

	public List<ListReportModel> reportModel;
	private Database db;
	
	public ListReportModel(Context context){
		db = new Database(context);
		db.open();
	}
	
	public void close(){
		db.close();
	}

	public boolean load() {
		mReports = db.mReportDao.fetchAllReports();

		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadReportById(long id) {
		mReports = db.mReportDao.fetchReportById(id);

		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadPendingReports() {
		mReports = db.mReportDao.fetchAllPendingReports();
		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadPendingReportsByCategory(int categoryId) {

		mReports = db.mReportDao
				.fetchPendingReportByCategoryId(categoryId);
		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadReportByCategory(int categoryId) {
		mReports = db.mReportDao.fetchReportByCategoryId(categoryId);

		if (mReports != null) {
			return true;
		}
		return false;
	}
	
	public List<ReportEntity> getReports() {
		return mReports;
	}
	
	public List<CategoryEntity> getParentCategories() {
		return db.mCategoryDao.fetchAllCategoryTitles();

	}
	
	public List<CategoryEntity> getAllCategories() {
		return db.mCategoryDao.fetchAllCategories();

	}
	
	public List<CategoryEntity> getChildrenCategories(int parentId) {
		return db.mCategoryDao.fetchChildrenCategories(parentId);
	}

	public List<CategoryEntity> getCategoriesByReportId(int reportId) {

		return db.mCategoryDao.fetchCategoryByReportId(reportId);
	}

	public String getImage(int reportId) {

		List<MediaEntity> sMedia = db.mMediaDao.fetchMedia(
				IMediaSchema.REPORT_ID, reportId, IMediaSchema.IMAGE, 1);
		if (sMedia != null && sMedia.size() > 0) {
			return sMedia.get(0).getLink();
		}
		return null;
	}

	/**
	 * Deletes all fetched reports.
	 * 
	 * @param reportId
	 *            The id of the report to be deleted.
	 * 
	 * @return boolean
	 */
	public boolean deleteAllFetchedReport(int reportId) {

		// delete fetched reports
		if (db.mReportDao.deleteReportById(reportId)) {
			new Util().log("All fetched report deleted");
		}

		// delete categories
		if (db.mReportCategoryDao
				.deleteReportCategoryByReportId(reportId,IReportSchema.FETCHED)) {
			new Util().log("All fetched report categories deleted");
		}

		if (db.mCategoryDao.deleteAllCategories()) {
			new Util().log("Category deleted");
		}

		// delete media
		if (db.mMediaDao.deleteReportPhoto(reportId)) {
			new Util().log("Media deleted");
		}
		return true;
	}

	public boolean deleteReport() {
		// delete fetched reports
		if (db.mReportDao.deleteAllReport()) {
			new Util().log("Report deleted");
		}

		// delete categories
		if (db.mReportCategoryDao.deleteAllReportCategory()) {
			new Util().log("Report categories deleted");
		}

		if (db.mCategoryDao.deleteAllCategories()) {
			new Util().log( "Category deleted");
		}

		// delete media
		if (db.mMediaDao.deleteAllMedia()) {
			new Util().log("Media deleted");
		}
		return true;
	}
}
