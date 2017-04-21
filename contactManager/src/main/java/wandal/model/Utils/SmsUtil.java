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

	// ������еĶ��ŻỰ�����յ绰����,�ǳ�������
	public static List<SmsThreadBean> getAllSmsThreadListData() {
		List<SmsThreadBean> mSmsThreadListData = new ArrayList<SmsThreadBean>();
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		// �Ựuri.
		Uri threadUri = Uri.parse("content://mms-sms/conversations");
		Cursor threadCursor = mContentResolver.query(threadUri, new String[] {
				"thread_id", "body", "address", "date", "type" }, null, null,
				"date desc");
		// ���ڿɲ��һỰ������uri.
		Uri threadSimpleUri = Uri
				.parse("content://mms-sms/conversations?simple=true");
		Cursor threadSimpleCursor = mContentResolver.query(threadSimpleUri,
				new String[] { "recipient_ids", "message_count", "date" },
				null, null, "date desc");
		// ����ͬʱ����.һ���ǻ�������,һ���ǻỰ����.
		while (threadCursor.moveToNext() && threadSimpleCursor.moveToNext()) {
			SmsThreadBean mSmsThreadBean = new SmsThreadBean();
			mSmsThreadBean.SmsThreadId = threadCursor.getInt(0);
			mSmsThreadBean.SmsBody = threadCursor.getString(1);
			mSmsThreadBean.SmsAddress = threadCursor.getString(2);
			mSmsThreadBean.SmsDate = threadCursor.getLong(3);
			mSmsThreadBean.SmsDateFormat = getFormateDate(mSmsThreadBean.SmsDate);
			mSmsThreadBean.SmsType = threadCursor.getInt(4);
			// �ж���recipient_ids��thread_idһ��,�ǾͿ��Լ������
			if (threadSimpleCursor.getInt(0) == mSmsThreadBean.SmsThreadId) {
				mSmsThreadBean.SmsThreadCount = threadSimpleCursor.getInt(1);// ��"recipient_ids"����thread_id,
				Log.i("SMS_INFO_UP", "��������Ϊ" + mSmsThreadBean.SmsThreadCount);
			} else {
				// ���ŻỰ���ݳ����쳣��
				// ��Ҫ�ñȽ��鷳�ķ�����������
				Log.i("SMS_INFO_UP", "�����������");
			}
			mSmsThreadListData.add(mSmsThreadBean);
		}
		// ��عر�,�����ڴ����
		threadSimpleCursor.close();
		threadCursor.close();
		return mSmsThreadListData;
	}

	// ����ͷ�����������.
	public static void setSmsThreadListContactsInfo(
			List<SmsThreadBean> smsThreadListData) {
		// �������е�SmsThread���ҵ�ַ�Ƿ��ж�Ӧ��ϵ��,Ҳ������foreach����
		for (int i = 0; i < smsThreadListData.size(); i++) {
			SmsThreadBean mSmsThreadBean = smsThreadListData.get(i);
			// ���ݵ绰�����ѯ�Ƿ�����ϵ�˶�Ӧ,���Ǹ���Ҫ����,�������ѵ���
			Uri filterNumberUri = Uri
					.parse("content://com.android.contacts/data/phones/filter/"
							+ mSmsThreadBean.SmsAddress);
			ContentResolver mContentResolver = TApplication.MY_SELF
					.getContentResolver();
			Cursor mCursor = mContentResolver.query(filterNumberUri,
					new String[] { "contact_id", "display_name", "photo_id" },
					null, null, null);
			if (mCursor.moveToNext()) {
				// ������ϵ�˻�����Ϣ,�õ������,��Ϊ�����ǩ��.
				mSmsThreadBean.ContactId = mCursor.getInt(0);
				mSmsThreadBean.ContactName = mCursor.getString(1);
				mSmsThreadBean.ContactPhotoId = mCursor.getInt(2);

				// ����ϵ��ͼƬ���������ϵ��ͼƬ
				// �õ���ϵ��ͷ��Bitamp
				Bitmap contactPhoto = null;
				// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
				if (mSmsThreadBean.ContactPhotoId > 0) {
					// ��δ���Ҳ��������,�Ǵ����ݿ�����ȡͼƬ,
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							mSmsThreadBean.ContactId);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(mContentResolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					// ��ȡ����Ĭ��ͼƬ
					contactPhoto = BitmapFactory.decodeResource(
							TApplication.MY_SELF.getResources(),
							R.drawable.default_contacts_photo);
				}
				// ����ѯ����ͼƬ������.
				mSmsThreadBean.contactPhoto = contactPhoto;
			}
			mCursor.close();
		}
	}

	// ��ʽʱ��Ĵ���.����"n����ǰ,nСʱǰ,���� 10:15,ǰ�� 12:35,03/18 10:55";
	public static String getFormateDate(long longDate) {
		// "n����ǰ,nСʱǰ,���� 10:15,ǰ�� 12:35,03/18 10:55";
		// n����ǰ ����ж� ��now - longDate�� < 1 * 60 * 60 * 1000
		// nСʱǰ ����ж� ����Ҫ�ж��ǲ��ǽ��� (longDate - ����00:00:00[todayDate]) > 0
		// �����ǰ�� ����ж� longDate - ( todayDate -��1 * 24 * 60 * 60 *1000��) > 0

		// ��ǰ���¼�
		Date nowDate = new Date();
		// ���� ��ʱ
		Date oneDate = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate
				.getDate(), 0, 0, 0);
		// ���� ��ʱ
		Date twoDate = new Date(oneDate.getTime() - (1 * 24 * 60 * 60 * 1000));
		// ǰ�� ��ʱ
		Date threeDate = new Date(oneDate.getTime()
				- (1 * 24 * 60 * 60 * 1000 * 2));

		// һСʱ�ڣ�n����֮ǰ
		if ((nowDate.getTime() - longDate) < (1 * 60 * 60 * 1000)) {

			int time = (int) ((nowDate.getTime() - longDate) / 1000 / 60);
			if (time == 0) {
				return "�ո�";
			} else {
				return time + "����֮ǰ";
			}

		} else
		// ����ģ�nСʱ֮ǰ
		if (longDate - oneDate.getTime() > 0) {

			int time = (int) ((nowDate.getTime() - longDate) / 1000 / 60 / 60);

			return time + "Сʱ֮ǰ";

		} else
		// ����ģ����� ʱ:��
		if (longDate - (twoDate.getTime()) > 0) {
			// ע��SimpleDateFormat�������÷�,���ǲ�֪��
			SimpleDateFormat sdf = new SimpleDateFormat("���� hh:mm");

			return sdf.format(new Date(longDate));

		} else
		// ǰ��ģ�ǰ�� ʱ����
		if (longDate - (threeDate.getTime()) > 0) {
			// ע��SimpleDateFormat�������÷�,���ǲ�֪��
			SimpleDateFormat sdf = new SimpleDateFormat("ǰ�� hh:mm");
			return sdf.format(new Date(longDate));

		} else {
			// ��/�� ʱ:��
			// ע��SimpleDateFormat�������÷�,���ǲ�֪��
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
			return sdf.format(new Date(longDate));
		}

	}

	//����thread_id���Ҿ���Ự��Ϣ
	public static List<SmsDetailBean> getSmsDetailListByThreadId(int threadId) {
		List<SmsDetailBean> mSmsDetailListData = new ArrayList<SmsDetailBean>();
		// ����ָ���Ķ��ŻỰ
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
		// ��֯bean
		return mSmsDetailListData;
	}

}
