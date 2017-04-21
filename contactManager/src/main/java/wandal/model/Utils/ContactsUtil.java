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

	// 获得所有的联系人列表,是初步数据
	public static List<ContactBean> getContactListData() {
		List<ContactBean> mContactListData = new ArrayList<ContactBean>();
		// 查询联系人数据，观察联系人数据
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		//联系人uri
		Uri contactUri = Uri.parse("content://com.android.contacts/contacts");
		//按联系人键进行排序
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
	// 加载头像和有无姓名.
	public static void setContactPhotoListData(
			List<ContactBean> contactsListData) {
		android.provider.ContactsContract.Contacts.CONTENT_FILTER_URI
				.toString();
		ContentResolver mContentResolver = TApplication.MY_SELF
				.getContentResolver();
		for (int i = 0; i < contactsListData.size(); i++) {
			ContactBean nowContactBean = contactsListData.get(i);
			// 有联系人图片，则加入联系人图片
			// 得到联系人头像Bitamp
			Bitmap contactPhoto = null;
			// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
			if (nowContactBean.ContactPhotoId > 0) {
				//这段代码也是死代码,是从数据库中提取图片,
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						nowContactBean.ContactId);
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
			nowContactBean.ContactPhoto = contactPhoto;
		}

	}

	//根据Phone.CONTACT_ID查找联系人相关信息
	public static List<ContactPhoneBean> getContactDetailPhoneListData(
			int contactId) {
		List<ContactPhoneBean> mContactPhoneListData = new ArrayList<ContactPhoneBean>();
		//查找具体电话信息
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
