package wandal.model.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import wandal.activity.R;
import wandal.Application.TApplication;
import wandal.model.Entity.CallLogBean;

public class CallLogDataUtils {

	// 返回所有的通话记录信息,是初步数据
	public static List<CallLogBean> getCallLogListData(int callType) {
		// 用LinkedHashMap实现可以对时间的顺序排列显示.
		LinkedHashMap<String, CallLogBean> mCallLogMapData = new LinkedHashMap<String, CallLogBean>();
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		//主要用在CallLogListAdapter中的getView方法中
		String strWhere = null;
		if (callType > 0 && callType < 4) {
			strWhere = "type = " + callType;
		}
		Cursor mCallLogCursor = mContentResolver.query(
				CallLog.Calls.CONTENT_URI, null, strWhere, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		// String[] arrCols = mCallLogCursor.getColumnNames();可用作测试
		// 遍历通话记录，汇总通话记录信息
		while (mCallLogCursor.moveToNext()) {
			// 注意同样的电话号码对比：
			String number = mCallLogCursor.getString(mCallLogCursor
					.getColumnIndex("number"));
			if (mCallLogMapData.get(number) == null) {
				// 创建实体对象
				CallLogBean nowCallLogBean = new CallLogBean();
				nowCallLogBean.CallLogId = mCallLogCursor.getInt(mCallLogCursor
						.getColumnIndex("_id"));
				nowCallLogBean.CallLogNumber = mCallLogCursor
						.getString(mCallLogCursor.getColumnIndex("number"));
				nowCallLogBean.CallLogDate = mCallLogCursor
						.getLong(mCallLogCursor.getColumnIndex("date"));
				// 调用下面的方法完成时间的处理,格式为如:"n分钟前,n小时前,昨天 10:15,前天 12:35,03/18 10:55";
				nowCallLogBean.CallLogDateFormat = getFormateDate(nowCallLogBean.CallLogDate);
				// "n分钟前,n小时前,昨天 10:15,前天 12:35,03/18 10:55";

				nowCallLogBean.CallLogType = mCallLogCursor
						.getInt(mCallLogCursor.getColumnIndex("type"));
				nowCallLogBean.CallLogCount = 1;

				nowCallLogBean.CallLogName = mCallLogCursor
						.getString(mCallLogCursor.getColumnIndex("name"));
				if (nowCallLogBean.CallLogName == null) {
					nowCallLogBean.CallLogName = "未知";
				}

				// 将实体对象加入Map
				mCallLogMapData.put(nowCallLogBean.CallLogNumber,
						nowCallLogBean);
			} else {
				// 有重复记录CallLogCount+1
				mCallLogMapData.get(number).CallLogCount += 1;
			}
		}

		mCallLogCursor.close();

		// 将Map转换为List；取得所需要的值
		ArrayList<CallLogBean> mCallLogListData = new ArrayList<CallLogBean>(
				mCallLogMapData.values());

		return mCallLogListData;
	}

	// 加载头像和有无姓名.
	public static void setCallLogListContactsInfo(
			List<CallLogBean> callLogListData) {
		for (int i = 0; i < callLogListData.size(); i++) {
			// 获得通话记录的电话号码
			CallLogBean nowCallLogBean = callLogListData.get(i);

			// 根据电话号码查询是否有联系人对应,这是个重要代码,是网上搜到的
			Uri filterNumberUri = Uri
					.parse("content://com.android.contacts/data/phones/filter/"
							+ nowCallLogBean.CallLogNumber);

			ContentResolver mContentResolver = TApplication.MY_SELF
					.getContentResolver();
			Cursor mCursor = mContentResolver.query(filterNumberUri, null,
					null, null, null);

			if (mCursor.moveToNext()) {
				// 设置联系人基本信息
				nowCallLogBean.ContactId = mCursor.getInt(mCursor
						.getColumnIndex("contact_id"));
				nowCallLogBean.ContactName = mCursor.getString(mCursor
						.getColumnIndex("display_name"));
				nowCallLogBean.ContactPhotoId = mCursor.getInt(mCursor
						.getColumnIndex("photo_id"));

				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (nowCallLogBean.ContactPhotoId > 0) {
					//这段代码也是死代码,是从数据库中提取图片,
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							nowCallLogBean.ContactId);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(mContentResolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					//获取设置默认图片.
					contactPhoto = BitmapFactory.decodeResource(
							TApplication.MY_SELF.getResources(),
							R.drawable.default_contacts_photo);
				}
				//将查询到的图片给对象.
				nowCallLogBean.ContactPhotoBitmap = contactPhoto;
			}
			mCursor.close();
		}
	}

	//格式时间的代码.比如"n分钟前,n小时前,昨天 10:15,前天 12:35,03/18 10:55";
	public static String getFormateDate(long longDate) {
		// "n分钟前,n小时前,昨天 10:15,前天 12:35,03/18 10:55";
		// n分钟前 如何判断 （now - longDate） < 1 * 60 * 60 * 1000
		// n小时前 如何判断 首先要判断是不是今天 (longDate - 今天00:00:00[todayDate]) > 0
		// 昨天和前天 如何判断 longDate - ( todayDate -（1 * 24 * 60 * 60 *1000）) > 0

		// 当前的事件
		Date nowDate = new Date();
		// 今天 零时
		Date oneDate = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate
				.getDate(), 0, 0, 0);
		// 昨天 零时
		Date twoDate = new Date(oneDate.getTime() - (1 * 24 * 60 * 60 * 1000));
		// 前天 零时
		Date threeDate = new Date(oneDate.getTime()
				- (1 * 24 * 60 * 60 * 1000 * 2));
		// 一小时内：n分钟之前
		if ((nowDate.getTime() - longDate) < (1 * 60 * 60 * 1000)) {
			int time = (int) ((nowDate.getTime() - longDate) / 1000 / 60);
			if (time == 0) {
				return "刚刚";
			} else {
				return time + "分钟之前";
			}

		} else
		// 今天的：n小时之前
		if (longDate - oneDate.getTime() > 0) {

			int time = (int) ((nowDate.getTime() - longDate) / 1000 / 60 / 60);

			return time + "小时之前";

		} else
		// 昨天的：昨天 时:分
		if (longDate - (twoDate.getTime()) > 0) {
			//注意SimpleDateFormat的正常用法,就是不知道
			SimpleDateFormat sdf = new SimpleDateFormat("昨天 hh:mm");
			return sdf.format(new Date(longDate));
		} else
		// 前天的：前天 时：分
		if (longDate - (threeDate.getTime()) > 0) {
			//注意SimpleDateFormat的正常用法,就是不知道
			SimpleDateFormat sdf = new SimpleDateFormat("前天 hh:mm");
			return sdf.format(new Date(longDate));
		} else {
			// 月/日 时:分
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
			return sdf.format(new Date(longDate));
		}

	}

}
