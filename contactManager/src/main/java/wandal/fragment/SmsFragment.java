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

	View rootView;// ����Ƭ�ε���ɫ
	ImageButton sendSmsButton;
	// ������ͼ,������,�㲥,�۲���
	ListView mSmsThreadListView;
	SmsThreadListAdapter mSmsThreadListAdapter;
	UpSmsThreadListReceiver mUpSmsThreadListReceiver;

	// ������е���ͼ,������,�㲥,�۲��ߵĴ����ͳ�ʼ��
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// �����õ�����
		rootView = inflater.inflate(R.layout.sms_fragment_layout, null);
		initView();
		initData();
		setListener();

		// ע����ո������ݵ�פ���㲥
		// ע����չ۲���(�������ݹ۲���)�Ĺ۲���ʵ����
		mUpSmsThreadListReceiver = new UpSmsThreadListReceiver();
		getActivity().registerReceiver(mUpSmsThreadListReceiver,
				new IntentFilter(TApplication.SMS_THREAD_DATA_UP_ACTION));
		// ע�ⷵ�������,�����û����ʾ.
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

	// ����������������䵽������.
	private void initData() {
		List<SmsThreadBean> tempListData = new ArrayList<SmsThreadBean>();
		mSmsThreadListAdapter = new SmsThreadListAdapter(getActivity(),
				tempListData, R.layout.sms_list_item_layout);
		mSmsThreadListView.setAdapter(mSmsThreadListAdapter);
	}

	// �õ����.
	private void initView() {
		mSmsThreadListView = (ListView) this.rootView
				.findViewById(R.id.sms_thread_listview);
		sendSmsButton=(ImageButton) this.rootView.findViewById(R.id.sms_detail_send_sms);
	}

	// ע���㲥�͹۲���
	public void onDestroyView() {
		super.onDestroyView();
		// ע�����ո������ݵ�פ���㲥
		getActivity().unregisterReceiver(mUpSmsThreadListReceiver);
		// ע�����չ۲���(�������ݹ۲���)�Ĺ۲���ʵ����
	}
	
	public void onResume() {
		super.onResume();
	}

	// ����Ĺ㲥��Ҫ����Ϊ�˸���
	class UpSmsThreadListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			mSmsThreadListAdapter.changeData(TApplication.SmsThreadListData);
			mSmsThreadListAdapter.notifyDataSetChanged();
		}
	}


}
