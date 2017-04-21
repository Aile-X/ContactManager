package wandal.model.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import wandal.activity.R;
import wandal.Application.TApplication;
import wandal.model.Entity.ContactBean;
import wandal.model.Entity.ContactPhoneBean;

public class ContactsUtil {

	// ������е���ϵ���б�,�ǳ�������
	public static List<ContactBean> getContactListData() {
		List<ContactBean> mContactListData = new ArrayList<ContactBean>();
		// ��ѯ��ϵ�����ݣ��۲���ϵ������
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		//��ϵ��uri
		Uri contactUri = Uri.parse("content://com.android.contacts/contacts");
		//����ϵ�˼���������
		Cursor contCursor = mContentResolver.query(contactUri, new String[] {
				"_id", "display_name_alt", "sort_key_alt", "photo_id" }, null,
				null, "sort_key_alt");
		while (contCursor.moveToNext()) {
			ContactBean newContactBean = new ContactBean();
			newContactBean.ContactId = contCursor.getInt(0);
			newContactBean.ContactName = contCursor.getString(1);
			newContactBean.ContactSort = contCursor.getString(2);
			newContactBean.ContactSortIndex = newContactBean.ContactSort
					.substring(0, 1);
			newContactBean.ContactPhotoId = contCursor.getInt(3);
			mContactListData.add(newContactBean);
		}

		return mContactListData;
	}
	// ����ͷ�����������.
	public static void setContactPhotoListData(
			List<ContactBean> contactsListData) {
		android.provider.ContactsContract.Contacts.CONTENT_FILTER_URI
				.toString();
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		for (int i = 0; i < contactsListData.size(); i++) {
			ContactBean nowContactBean = contactsListData.get(i);
			// ����ϵ��ͼƬ���������ϵ��ͼƬ
			// �õ���ϵ��ͷ��Bitamp
			Bitmap contactPhoto = null;
			// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
			if (nowContactBean.ContactPhotoId > 0) {
				//��δ���Ҳ��������,�Ǵ����ݿ�����ȡͼƬ,
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						nowContactBean.ContactId);
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
			nowContactBean.ContactPhoto = contactPhoto;
		}

	}

	//����Phone.CONTACT_ID������ϵ�������Ϣ
	public static List<ContactPhoneBean> getContactDetailPhoneListData(
			int contactId) {
		List<ContactPhoneBean> mContactPhoneListData = new ArrayList<ContactPhoneBean>();
		//���Ҿ���绰��Ϣ
		Uri queryPhoneUri = Uri
				.parse("content://com.android.contacts/data/phones");
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		Cursor phoneCursor = mContentResolver.query(queryPhoneUri,
				new String[] { "data1", "data2", "mimetype", "contact_id" },
				Phone.CONTACT_ID + " = " + contactId, null, null);
		while (phoneCursor.moveToNext()) {
			ContactPhoneBean mContactPhoneBean = new ContactPhoneBean();
			mContactPhoneBean.ContactPhoneNumber = phoneCursor.getString(0);
			mContactPhoneBean.ContactPhoneType = phoneCursor.getInt(1);
			mContactPhoneListData.add(mContactPhoneBean);
		}
		return mContactPhoneListData;
	}

}
