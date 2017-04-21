package wandal.model.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wandal.activity.R;
import wandal.Application.TApplication;
import wandal.model.Entity.SmsDetailBean;
import wandal.model.Entity.SmsThreadBean;

public class SmsUtil {

	// 获得所有的短信会话：按照电话号码,是初步数据
	public static List<SmsThreadBean> getAllSmsThreadListData() {
		List<SmsThreadBean> mSmsThreadListData = new ArrayList<SmsThreadBean>();
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		// 会话uri.
		Uri threadUri = Uri.parse("content://mms-sms/conversations");
		Cursor threadCursor = mContentResolver.query(threadUri, new String[] {
				"thread_id", "body", "address", "date", "type" }, null, null,
				"date desc");
		// 基于可查找会话次数的uri.
		Uri threadSimpleUri = Uri
				.parse("content://mms-sms/conversations?simple=true");
		Cursor threadSimpleCursor = mContentResolver.query(threadSimpleUri,
				new String[] { "recipient_ids", "message_count", "date" },
				null, null, "date desc");
		// 两个同时查找.一个是基本数据,一个是会话次数.
		while (threadCursor.moveToNext() && threadSimpleCursor.moveToNext()) {
			SmsThreadBean mSmsThreadBean = new SmsThreadBean();
			mSmsThreadBean.SmsThreadId = threadCursor.getInt(0);
			mSmsThreadBean.SmsBody = threadCursor.getString(1);
			mSmsThreadBean.SmsAddress = threadCursor.getString(2);
			mSmsThreadBean.SmsDate = threadCursor.getLong(3);
			mSmsThreadBean.SmsDateFormat = getFormateDate(mSmsThreadBean.SmsDate);
			mSmsThreadBean.SmsType = threadCursor.getInt(4);
			// 判断若recipient_ids和thread_id一样,那就可以计算次数
			if (threadSimpleCursor.getInt(0) == mSmsThreadBean.SmsThreadId) {
				mSmsThreadBean.SmsThreadCount = threadSimpleCursor.getInt(1);// 即"recipient_ids"就是thread_id,
				Log.i("SMS_INFO_UP", "计算数量为" + mSmsThreadBean.SmsThreadCount);
			} else {
				// 短信会话数据出现异常，
				// 需要用比较麻烦的方法处理数量
				Log.i("SMS_INFO_UP", "数量计算错误");
			}
			mSmsThreadListData.add(mSmsThreadBean);
		}
		// 务必关闭,否则内存溢出
		threadSimpleCursor.close();
		threadCursor.close();
		return mSmsThreadListData;
	}

	// 加载头像和有无姓名.
	public static void setSmsThreadListContactsInfo(
			List<SmsThreadBean> smsThreadListData) {
		// 遍历所有的SmsThread查找地址是否有对应联系人,也可以用foreach遍历
		for (int i = 0; i < smsThreadListData.size(); i++) {
			SmsThreadBean mSmsThreadBean = smsThreadListData.get(i);
			// 根据电话号码查询是否有联系人对应,这是个重要代码,是网上搜到的
			Uri filterNumberUri = Uri
					.parse("content://com.android.contacts/data/phones/filter/"
							+ mSmsThreadBean.SmsAddress);
			ContentResolver mContentResolver = TApplication.MY_SELF
					.getContentResolver();
			Cursor mCursor = mContentResolver.query(filterNumberUri,
					new String[] { "contact_id", "display_name", "photo_id" },
					null, null, null);
			if (mCursor.moveToNext()) {
				// 设置联系人基本信息,用的是序号,因为上面的签名.
				mSmsThreadBean.ContactId = mCursor.getInt(0);
				mSmsThreadBean.ContactName = mCursor.getString(1);
				mSmsThreadBean.ContactPhotoId = mCursor.getInt(2);

				// 有联系人图片，则加入联系人图片
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (mSmsThreadBean.ContactPhotoId > 0) {
					// 这段代码也是死代码,是从数据库中提取图片,
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							mSmsThreadBean.ContactId);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(mContentResolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					// 获取设置默认图片
					contactPhoto = BitmapFactory.decodeResource(
							TApplication.MY_SELF.getResources(),
							R.drawable.default_contacts_photo);
				}
				// 将查询到的图片给对象.
				mSmsThreadBean.contactPhoto = contactPhoto;
			}
			mCursor.close();
		}
	}

	// 格式时间的代码.比如"n分钟前,n小时前,昨天 10:15,前天 12:35,03/18 10:55";
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
			// 注意SimpleDateFormat的正常用法,就是不知道
			SimpleDateFormat sdf = new SimpleDateFormat("昨天 hh:mm");

			return sdf.format(new Date(longDate));

		} else
		// 前天的：前天 时：分
		if (longDate - (threeDate.getTime()) > 0) {
			// 注意SimpleDateFormat的正常用法,就是不知道
			SimpleDateFormat sdf = new SimpleDateFormat("前天 hh:mm");
			return sdf.format(new Date(longDate));

		} else {
			// 月/日 时:分
			// 注意SimpleDateFormat的正常用法,就是不知道
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
			return sdf.format(new Date(longDate));
		}

	}

	//根据thread_id查找具体会话信息
	public static List<SmsDetailBean> getSmsDetailListByThreadId(int threadId) {
		List<SmsDetailBean> mSmsDetailListData = new ArrayList<SmsDetailBean>();
		// 查找指定的短信会话
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		Cursor smsDetailCursor = mContentResolver.query(Uri
				.parse("content://sms"), new String[] { "_id", "thread_id",
				"body", "type", "date" }, "thread_id = " + threadId, null,
				"date");
		while (smsDetailCursor.moveToNext()) {
			SmsDetailBean mSmsDetailBean = new SmsDetailBean();
			mSmsDetailBean.SmsId = smsDetailCursor.getInt(0);
			mSmsDetailBean.SmsThreadId = smsDetailCursor.getInt(1);
			mSmsDetailBean.SmsBody = smsDetailCursor.getString(2);
			mSmsDetailBean.SmsType = smsDetailCursor.getInt(3);
			mSmsDetailBean.SmsDate = smsDetailCursor.getLong(4);
			mSmsDetailBean.SmsDateFormat = getFormateDate(mSmsDetailBean.SmsDate);
			mSmsDetailListData.add(mSmsDetailBean);
		}
		smsDetailCursor.close();
		// 组织bean
		return mSmsDetailListData;
	}

}
