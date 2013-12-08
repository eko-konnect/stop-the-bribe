package org.ekokonnect.stopthebribe.modelhelpers;

import java.io.File;
import java.util.List;
import java.util.Vector;


import android.content.Context;
import android.util.Log;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoad;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadBitmap;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.database.IReportSchema;
import com.ushahidi.android.app.entities.MediaEntity;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportCategory;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.Model;

public class ReportHelper extends Model {
	private static Database db;
	
	public ReportHelper(Context context){
		db = new Database(context).open();
	}

	public boolean addPendingReport(ReportEntity report,
			Vector<Integer> category, List<ImageToLoad> pendingPhotos, String news) {
		boolean status;
		// add pending reports
		status = db.mReportDao.addReport(report);
		final String date = db.mReportDao.getDate(report.getIncident()
				.getDate());
		int id = db.mReportDao.fetchPendingReportIdByDate(date);
		report.setDbId(id);
		// add category
		if (status) {
			if (category != null && category.size() > 0) {
				for (Integer cat : category) {
					ReportCategory reportCategory = new ReportCategory();
					reportCategory.setCategoryId(cat);
					reportCategory.setReportId(id);
					reportCategory.setStatus(IReportSchema.PENDING);
					db.mReportCategoryDao
							.addReportCategory(reportCategory);

				}
			}

			// add photos
			if (pendingPhotos != null && pendingPhotos.size() > 0) {
				for (ImageToLoad image : pendingPhotos) {
					ImageToLoadBitmap imb = (ImageToLoadBitmap)image;
					File file = new File(imb.getPath());
					if (file.exists()) {
						Log.d("AddPhoto", "Photo Exists");
						MediaEntity media = new MediaEntity();
						media.setMediaId(0);
						media.setLink(file.getPath());
						Log.d("AddPhoto", file.getPath());

						// get report ID;
						media.setReportId(id);
						media.setType(IMediaSchema.IMAGE);
						db.mMediaDao.addMedia(media);
					} else
						Log.e("AddPhoto", "Photo Doesnt Exist");
				}

			}

			// add news
			if (news != null && news.length() > 0) {

				MediaEntity media = new MediaEntity();
				media.setMediaId(0);
				media.setLink(news);
				// get report ID;
				media.setReportId(id);
				media.setType(IMediaSchema.NEWS);
				db.mMediaDao.addMedia(media);
			}
		}

		return status;
	}

	public boolean updatePendingReport(int reportId, ReportEntity report,
			Vector<Integer> category, List<PhotoEntity> pendingPhotos,
			String news) {
		boolean status;
		// update pending reports
		status = db.mReportDao.updatePendingReport(reportId, report);

		// update category
		if (status) {
			if (category != null && category.size() > 0) {
				// delete existing categories. It's easier this way
				db.mReportCategoryDao.deleteReportCategoryByReportId(
						reportId, IReportSchema.PENDING);

				for (Integer cat : category) {
					ReportCategory reportCategory = new ReportCategory();
					reportCategory.setCategoryId(cat);
					reportCategory.setReportId(reportId);
					reportCategory.setStatus(IReportSchema.PENDING);
					db.mReportCategoryDao
							.addReportCategory(reportCategory);

				}

			}

			// update photos
			if (pendingPhotos != null && pendingPhotos.size() > 0) {
				// delete existing photo
				db.mMediaDao.deleteReportPhoto(reportId);
				for (PhotoEntity photo : pendingPhotos) {
					MediaEntity media = new MediaEntity();
					media.setMediaId(0);
					// FIXME:: this is nasty.
					String sections[] = photo.getPhoto().split("/");
					media.setLink(sections[1]);

					// get report ID
					media.setReportId(reportId);
					media.setType(IMediaSchema.IMAGE);
					db.mMediaDao.addMedia(media);
				}

			}

			// add news
			if (news != null && news.length() > 0) {
				// delete existing news item
				db.mMediaDao.deleteReportNews(reportId);
				MediaEntity media = new MediaEntity();
				media.setMediaId(0);
				media.setLink(news);
				// get report ID;
				media.setReportId(reportId);
				media.setType(IMediaSchema.NEWS);
				db.mMediaDao.addMedia(media);

			}
		}
		return status;
	}

	public ReportEntity fetchPendingReportById(int reportId) {
		return db.mReportDao.fetchPendingReportIdById(reportId);
	}
	
	public ReportEntity fetchReportById(int reportId) {
		return db.mReportDao.fetchReportBId(reportId);
	}
	

	public List<ReportCategory> fetchReportCategories(int reportId, int status) {
		return db.mReportCategoryDao.fetchReportCategoryByReportId(
				reportId, status);
	}

	public List<MediaEntity> fetchReportNews(int reportId) {
		return db.mMediaDao.fetchReportNews(reportId);
	}

	public boolean deleteReport(int reportId) {
		// delete report
		db.mReportDao.deletePendingReportById(reportId);

		// delete categories
		db.mReportCategoryDao.deleteReportCategoryByReportId(reportId,
				IReportSchema.PENDING);

		// delete media
		db.mMediaDao.deleteMediaByReportId(reportId);
		return true;
	}

}
