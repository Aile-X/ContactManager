package wandal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wandal.activity.R;
import wandal.model.Entity.ContactBean;

public class ContactsListAdapter extends BaseAdapter {

	Context mContext;
	List<ContactBean> mContactsListData;//数据
	int mContactItemResId;//老师的特色
	LayoutInflater mLayoutInflater;//充气机

	public ContactsListAdapter(Context context,
			List<ContactBean> contactsListData, int contactItemResId) {
		mContext = context;
		mContactsListData = contactsListData;
		mContactItemResId = contactItemResId;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void setChangeData(List<ContactBean> contactsListData) {
		mContactsListData = contactsListData;
	}

	public int getCount() {
		return mContactsListData.size();
	}

	public Object getItem(int position) {
		return mContactsListData.get(position);
	}

	public long getItemId(int position) {
		return mContactsListData.get(position).hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (null == convertView) {
			convertView = mLayoutInflater.inflate(mContactItemResId, null);
		}
		ContactBean mContactBean = mContactsListData.get(position);
		TextView contactnameTv = (TextView) convertView
				.findViewById(R.id.contact_list_item_name);
		contactnameTv.setText(mContactBean.ContactName);
		
		ImageView contactImageView = (ImageView) convertView.findViewById(R.id.contact_list_item_photo);
		//可以学习这种格式
		if(null != mContactBean.ContactPhoto){
			contactImageView.setImageBitmap(mContactBean.ContactPhoto);			
		}else{
			contactImageView.setImageResource(R.drawable.default_contacts_photo);
		}
		Log.i("Item_Info","联系人"+mContactBean.ContactName);
		return convertView;
	}

}
