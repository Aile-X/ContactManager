package wandal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import wandal.Application.TApplication;
import wandal.activity.R;
import wandal.activity.SmsNewActivity;
import wandal.adapter.SmsThreadListAdapter;
import wandal.model.Entity.SmsThreadBean;

//
public class SmsFragment extends Fragment {

	View rootView;// 这是片段的特色
	ImageButton sendSmsButton;
	// 声明视图,适配器,广播,观察者
	ListView mSmsThreadListView;
	SmsThreadListAdapter mSmsThreadListAdapter;
	UpSmsThreadListReceiver mUpSmsThreadListReceiver;

	// 完成所有的视图,监听器,广播,观察者的创建和初始化
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 充气得到界面
		rootView = inflater.inflate(R.layout.sms_fragment_layout, null);
		initView();
		initData();
		setListener();

		// 注册接收更新数据的驻留广播
		// 注册接收观察者(短信内容观察者)的观察者实现类
		mUpSmsThreadListReceiver = new UpSmsThreadListReceiver();
		getActivity().registerReceiver(mUpSmsThreadListReceiver,
				new IntentFilter(TApplication.SMS_THREAD_DATA_UP_ACTION));
		// 注意返回是这个,否则就没有显示.
		return rootView;
	}

	private void setListener() {
		sendSmsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),SmsNewActivity.class);
				startActivity(intent);
			}
		});
	}

	// 将数据用适配器类充到界面上.
	private void initData() {
		List<SmsThreadBean> tempListData = new ArrayList<SmsThreadBean>();
		mSmsThreadListAdapter = new SmsThreadListAdapter(getActivity(),
				tempListData, R.layout.sms_list_item_layout);
		mSmsThreadListView.setAdapter(mSmsThreadListAdapter);
	}

	// 得到组件.
	private void initView() {
		mSmsThreadListView = (ListView) this.rootView
				.findViewById(R.id.sms_thread_listview);
		sendSmsButton=(ImageButton) this.rootView.findViewById(R.id.sms_detail_send_sms);
	}

	// 注销广播和观察者
	public void onDestroyView() {
		super.onDestroyView();
		// 注销接收更新数据的驻留广播
		getActivity().unregisterReceiver(mUpSmsThreadListReceiver);
		// 注销接收观察者(短信内容观察者)的观察者实现类
	}
	
	public void onResume() {
		super.onResume();
	}

	// 这里的广播主要就是为了更新
	class UpSmsThreadListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			mSmsThreadListAdapter.changeData(TApplication.SmsThreadListData);
			mSmsThreadListAdapter.notifyDataSetChanged();
		}
	}


}
