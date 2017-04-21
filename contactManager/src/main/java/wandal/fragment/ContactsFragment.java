package wandal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import wandal.Application.TApplication;
import wandal.activity.ContactDetailActivity;
import wandal.activity.R;
import wandal.adapter.ContactIndexToolBarLayout;
import wandal.adapter.ContactIndexToolBarLayout.OnIndexChangeListener;
import wandal.adapter.ContactsListAdapter;
import wandal.model.Entity.ContactBean;

public class ContactsFragment extends Fragment {
	private static final int MENU_VIEW_CONTACT = 1;
	private static final int MENU_EDIT_CONTACT = 2;
	View RootView;// ����Ƭ�ε���ɫ
	// �������е�����Ͳ�����ͼ,��������,�㲥,�۲���.
	ListView mContactsListView;
	ContactIndexToolBarLayout mIndexToolBar;
	ContactsListAdapter mContactsListAdapter;
	UpContactsListReceiver mUpContactsListReceiver;

	// ������е���ͼ,������,�㲥,�۲��ߵĴ����ͳ�ʼ��
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// �����õ�����
		RootView = inflater.inflate(R.layout.contacts_fragment_layout, null);
		initView();
		initData();
		initListener();
		// ע��㲥
		mUpContactsListReceiver = new UpContactsListReceiver();
		getActivity().registerReceiver(mUpContactsListReceiver,
				new IntentFilter(TApplication.CONTACTS_DATA_UP_ACTION));
		// ע�ⷵ�������,�����û����ʾ.
		return RootView;
	}

	// �õ��������.
	private void initView() {
		mContactsListView = (ListView) RootView
				.findViewById(R.id.contacts_listview);
		mIndexToolBar = (ContactIndexToolBarLayout) RootView
				.findViewById(R.id.contacts_index_tool_bar);
	}

	// ����������������䵽������.
	private void initData() {
		List<ContactBean> contactListData = new ArrayList<ContactBean>();
		// ע�����,û����CallLogListAdapter�г������,����������,����ɫ
		mContactsListAdapter = new ContactsListAdapter(getActivity(),
				contactListData, R.layout.contacts_list_item_layout);
		mContactsListView.setAdapter(mContactsListAdapter);
	}
	// ��ContactIndexToolBarLayout��ListView�����¼�������
	private void initListener() {
		mIndexToolBar.setOnIndexChangeListener(new OnIndexChangeListener() {
			public void onChange(String changeChar) {
				// �Ա����ݣ���ѯ�����ϵ�˵Ķ�λ
				List<ContactBean> contactListData = TApplication.ContactsListData;
				for (int i = 0; i < contactListData.size(); i++) {
					String indexString = contactListData.get(i).ContactSortIndex;
					if (changeChar.equals(indexString)) {
						mContactsListView.setSelection(i);
						Log.i("ListViewSelection", "��תItem��" + i);
						return;
					}
				}
			}
		});

		mContactsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(
					AdapterView<?> parent,
					View view,
					int position,
					long id) {
				// ����ϵ����Ϣ��Intent�洢������detailActivity
				//"ContactId","ContactPhotoId","ContactName"
				ContactBean mContactBean =
					(ContactBean) parent.getItemAtPosition(position);
				Intent detailIntent = new Intent(getActivity(), ContactDetailActivity.class);
				detailIntent.putExtra("ContactId", mContactBean.ContactId);
				detailIntent.putExtra("ContactPhotoId", mContactBean.ContactPhotoId);
				detailIntent.putExtra("ContactName", mContactBean.ContactName);
				getActivity().startActivity(detailIntent);
			}
		});

		mContactsListView .setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
											ContextMenu.ContextMenuInfo menuInfo) {
				menu.add(1, MENU_VIEW_CONTACT, 1, "�鿴��ϵ��");
				menu.add(1, MENU_EDIT_CONTACT, 2, "�༭��ϵ��");
			}
		});
	}

	public boolean onContextItemSelected(MenuItem item) {
		// ��ȡ����������
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		ContactBean mContactBean = (ContactBean) mContactsListAdapter.getItem(menuInfo.position);

		switch (item.getItemId()) {
			case MENU_VIEW_CONTACT:
				Intent detailIntent = new Intent(getActivity(), ContactDetailActivity.class);
				detailIntent.putExtra("ContactId", mContactBean.ContactId);
				detailIntent.putExtra("ContactPhotoId", mContactBean.ContactPhotoId);
				detailIntent.putExtra("ContactName", mContactBean.ContactName);
				getActivity().startActivity(detailIntent);
				break;
			case MENU_EDIT_CONTACT:
				Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse("content://com.android.contacts/contacts/"+mContactBean.ContactId));
				startActivity(intent);
				break;
		}
		return super.onContextItemSelected(item);
	}



	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mUpContactsListReceiver);
	}

	// ע��㲥����������׼����ɽ������
	class UpContactsListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i("Contacts_Info","UpContactsListReceiver");
			mContactsListAdapter.setChangeData(TApplication.ContactsListData);
			mContactsListAdapter.notifyDataSetChanged();
		}
	}

}
