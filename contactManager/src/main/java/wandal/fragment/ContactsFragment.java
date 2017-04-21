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
	View RootView;// 这是片段的特色
	// 声明所有的组件和布局视图,适配器类,广播,观察者.
	ListView mContactsListView;
	ContactIndexToolBarLayout mIndexToolBar;
	ContactsListAdapter mContactsListAdapter;
	UpContactsListReceiver mUpContactsListReceiver;

	// 完成所有的视图,监听器,广播,观察者的创建和初始化
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 充气得到界面
		RootView = inflater.inflate(R.layout.contacts_fragment_layout, null);
		initView();
		initData();
		initListener();
		// 注册广播
		mUpContactsListReceiver = new UpContactsListReceiver();
		getActivity().registerReceiver(mUpContactsListReceiver,
				new IntentFilter(TApplication.CONTACTS_DATA_UP_ACTION));
		// 注意返回是这个,否则就没有显示.
		return RootView;
	}

	// 得到所有组件.
	private void initView() {
		mContactsListView = (ListView) RootView
				.findViewById(R.id.contacts_listview);
		mIndexToolBar = (ContactIndexToolBarLayout) RootView
				.findViewById(R.id.contacts_index_tool_bar);
	}

	// 将数据用适配器类充到界面上.
	private void initData() {
		List<ContactBean> contactListData = new ArrayList<ContactBean>();
		// 注意这点,没有在CallLogListAdapter中充气获得,而是在这里,很特色
		mContactsListAdapter = new ContactsListAdapter(getActivity(),
				contactListData, R.layout.contacts_list_item_layout);
		mContactsListView.setAdapter(mContactsListAdapter);
	}
	// 给ContactIndexToolBarLayout和ListView设置事件监听器
	private void initListener() {
		mIndexToolBar.setOnIndexChangeListener(new OnIndexChangeListener() {
			public void onChange(String changeChar) {
				// 对比数据，查询相关联系人的定位
				List<ContactBean> contactListData = TApplication.ContactsListData;
				for (int i = 0; i < contactListData.size(); i++) {
					String indexString = contactListData.get(i).ContactSortIndex;
					if (changeChar.equals(indexString)) {
						mContactsListView.setSelection(i);
						Log.i("ListViewSelection", "跳转Item：" + i);
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
				// 将联系人信息，Intent存储，启动detailActivity
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
				menu.add(1, MENU_VIEW_CONTACT, 1, "查看联系人");
				menu.add(1, MENU_EDIT_CONTACT, 2, "编辑联系人");
			}
		});
	}

	public boolean onContextItemSelected(MenuItem item) {
		// 获取被操作对象
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

	// 注册广播，接收数据准备完成界面更新
	class UpContactsListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i("Contacts_Info","UpContactsListReceiver");
			mContactsListAdapter.setChangeData(TApplication.ContactsListData);
			mContactsListAdapter.notifyDataSetChanged();
		}
	}

}
