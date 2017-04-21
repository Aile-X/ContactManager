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

	// �������е�ͨ����¼��Ϣ,�ǳ�������
	public static List<CallLogBean> getCallLogListData(int callType) {
		// ��LinkedHashMapʵ�ֿ��Զ�ʱ���˳��������ʾ.
		LinkedHashMap<String, CallLogBean> mCallLogMapData = new LinkedHashMap<String, CallLogBean>();
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		//��Ҫ����CallLogListAdapter�е�getView������
		String strWhere = null;
		if (callType > 0 && callType < 4) {
			strWhere = "type = " + callType;
		}
		Cursor mCallLogCursor = mContentResolver.query(
				CallLog.Calls.CONTENT_URI, null, strWhere, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		// String[] arrCols = mCallLogCursor.getColumnNames();����������
		// ����ͨ����¼������ͨ����¼��Ϣ
		while (mCallLogCursor.moveToNext()) {
			// ע��ͬ���ĵ绰����Աȣ�
			String number = mCallLogCursor.getString(mCallLogCursor
					.getColumnIndex("number"));
			if (mCallLogMapData.get(number) == null) {
				// ����ʵ�����
				CallLogBean nowCallLogBean = new CallLogBean();
				nowCallLogBean.CallLogId = mCallLogCursor.getInt(mCallLogCursor
						.getColumnIndex("_id"));
				nowCallLogBean.CallLogNumber = mCallLogCursor
						.getString(mCallLogCursor.getColumnIndex("number"));
				nowCallLogBean.CallLogDate = mCallLogCursor
						.getLong(mCallLogCursor.getColumnIndex("date"));
				// ��������ķ������ʱ��Ĵ���,��ʽΪ��:"n����ǰ,nСʱǰ,���� 10:15,ǰ�� 12:35,03/18 10:55";
				nowCallLogBean.CallLogDateFormat = getFormateDate(nowCallLogBean.CallLogDate);
				// "n����ǰ,nСʱǰ,���� 10:15,ǰ�� 12:35,03/18 10:55";

				nowCallLogBean.CallLogType = mCallLogCursor
						.getInt(mCallLogCursor.getColumnIndex("type"));
				nowCallLogBean.CallLogCount = 1;

				nowCallLogBean.CallLogName = mCallLogCursor
						.getString(mCallLogCursor.getColumnIndex("name"));
				if (nowCallLogBean.CallLogName == null) {
					nowCallLogBean.CallLogName = "δ֪";
				}

				// ��ʵ��������Map
				mCallLogMapData.put(nowCallLogBean.CallLogNumber,
						nowCallLogBean);
			} else {
				// ���ظ���¼CallLogCount+1
				mCallLogMapData.get(number).CallLogCount += 1;
			}
		}

		mCallLogCursor.close();

		// ��Mapת��ΪList��ȡ������Ҫ��ֵ
		ArrayList<CallLogBean> mCallLogListData = new ArrayList<CallLogBean>(
				mCallLogMapData.values());

		return mCallLogListData;
	}

	// ����ͷ�����������.
	public static void setCallLogListContactsInfo(
			List<CallLogBean> callLogListData) {
		for (int i = 0; i < callLogListData.size(); i++) {
			// ���ͨ����¼�ĵ绰����
			CallLogBean nowCallLogBean = callLogListData.get(i);

			// ���ݵ绰�����ѯ�Ƿ�����ϵ�˶�Ӧ,���Ǹ���Ҫ����,�������ѵ���
			Uri filterNumberUri = Uri
					.parse("content://com.android.contacts/data/phones/filter/"
							+ nowCallLogBean.CallLogNumber);

			ContentResolver mContentResolver = TApplication.MY_SELF
					.getContentResolver();
			Cursor mCursor = mContentResolver.query(filterNumberUri, null,
					null, null, null);

			if (mCursor.moveToNext()) {
				// ������ϵ�˻�����Ϣ
				nowCallLogBean.ContactId = mCursor.getInt(mCursor
						.getColumnIndex("contact_id"));
				nowCallLogBean.ContactName = mCursor.getString(mCursor
						.getColumnIndex("display_name"));
				nowCallLogBean.ContactPhotoId = mCursor.getInt(mCursor
						.getColumnIndex("photo_id"));

				Bitmap contactPhoto = null;
				// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
				if (nowCallLogBean.ContactPhotoId > 0) {
					//��δ���Ҳ��������,�Ǵ����ݿ�����ȡͼƬ,
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							nowCallLogBean.ContactId);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(mContentResolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					//��ȡ����Ĭ��ͼƬ.
					contactPhoto = BitmapFactory.decodeResource(
							TApplication.MY_SELF.getResources(),
							R.drawable.default_contacts_photo);
				}
				//����ѯ����ͼƬ������.
				nowCallLogBean.ContactPhotoBitmap = contactPhoto;
			}
			mCursor.close();
		}
	}

	//��ʽʱ��Ĵ���.����"n����ǰ,nСʱǰ,���� 10:15,ǰ�� 12:35,03/18 10:55";
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
			//ע��SimpleDateFormat�������÷�,���ǲ�֪��
			SimpleDateFormat sdf = new SimpleDateFormat("���� hh:mm");
			return sdf.format(new Date(longDate));
		} else
		// ǰ��ģ�ǰ�� ʱ����
		if (longDate - (threeDate.getTime()) > 0) {
			//ע��SimpleDateFormat�������÷�,���ǲ�֪��
			SimpleDateFormat sdf = new SimpleDateFormat("ǰ�� hh:mm");
			return sdf.format(new Date(longDate));
		} else {
			// ��/�� ʱ:��
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
			return sdf.format(new Date(longDate));
		}

	}

}
