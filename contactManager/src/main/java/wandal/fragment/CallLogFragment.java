package wandal.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wandal.Application.TApplication;
import wandal.activity.R;
import wandal.activity.SmsDetailActivity;
import wandal.adapter.CallLogListAdapter;
import wandal.dao.BlackListBiz;
import wandal.model.Entity.CallLogBean;
import wandal.model.Utils.TelService;
import wandal.model.thread.CallLogDataThread;

public class CallLogFragment extends Fragment {
    private static final int MENU_DIAL_PHONE = 1;
    private static final int MENU_SEND_MESSAGE = 2;
    private static final int MENU_DELETE_CALLLOG = 3;
	private static final int MENU_NEW_CONTACT = 4;
    private static final int MENU_BLACKLISTIN = 5;
	private static final int MENU_BLACKLISTOUT = 6;

	View RootView;// 这是片段的特色
	// 声明所有的组件和布局视图,数据,适配器类,广播,观察者.
	ImageButton callIn, callOut, callMiss, callAll, keyNumber0, keyNumber1,
			keyNumber2, keyNumber3, keyNumber4, keyNumber5, keyNumber6,
			keyNumber7, keyNumber8, keyNumber9, keyNumberStar, keyNumberHash,
			keyHide, keyShow, keyBackspace;
	Button keyDialCall;

	LinearLayout mKeyboardLayout;
	ListView callLogListView;
	List<CallLogBean> mCallLogListData;
	CallLogListAdapter mCallLogListAdapter;
	UpCallLogListReceiver mUpCallLogListReceiver;
	Toast toast = null;
	BlackListBiz blackListBiz;

	// 完成所有的视图,监听器,广播,观察者的创建和初始化
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 充气得到界面
		RootView = inflater.inflate(R.layout.call_log_fragment_layout, null);
		blackListBiz=new BlackListBiz(getActivity());
		initView();
		initData();
		initListener();

		// 启动电话监听服务
		Intent intent = new Intent(getActivity(), TelService.class);
		getActivity().startService(intent);

		// 注册广播
		mUpCallLogListReceiver = new UpCallLogListReceiver();
		getActivity().registerReceiver(mUpCallLogListReceiver,
				new IntentFilter(TApplication.CALL_LOG_DATA_UP_ACTION));

		// 注意返回是这个,否则就没有显示.
		return RootView;
	}

	// 注销广播和观察者
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mUpCallLogListReceiver);
	}

	public void onResume() {
		super.onResume();
	}

	// 得到所有组件.
	private void initView() {
		callIn = (ImageButton) RootView.findViewById(R.id.call_log_show_in);
		callOut = (ImageButton) RootView.findViewById(R.id.call_log_show_out);
		callMiss = (ImageButton) RootView.findViewById(R.id.call_log_show_miss);
		callAll = (ImageButton) RootView.findViewById(R.id.call_log_show_all);
		callLogListView = (ListView) RootView
				.findViewById(R.id.call_log_show_listview);
		mKeyboardLayout = (LinearLayout) RootView
				.findViewById(R.id.call_log_keyboard_layout);
		keyNumber0 = (ImageButton) RootView.findViewById(R.id.keyboard_0);
		keyNumber1 = (ImageButton) RootView.findViewById(R.id.keyboard_1);
		keyNumber2 = (ImageButton) RootView.findViewById(R.id.keyboard_2);
		keyNumber3 = (ImageButton) RootView.findViewById(R.id.keyboard_3);
		keyNumber4 = (ImageButton) RootView.findViewById(R.id.keyboard_4);
		keyNumber5 = (ImageButton) RootView.findViewById(R.id.keyboard_5);
		keyNumber6 = (ImageButton) RootView.findViewById(R.id.keyboard_6);
		keyNumber7 = (ImageButton) RootView.findViewById(R.id.keyboard_7);
		keyNumber8 = (ImageButton) RootView.findViewById(R.id.keyboard_8);
		keyNumber9 = (ImageButton) RootView.findViewById(R.id.keyboard_9);
		keyNumberStar = (ImageButton) RootView.findViewById(R.id.keyboard_star);
		keyNumberHash = (ImageButton) RootView.findViewById(R.id.keyboard_hash);
		keyHide = (ImageButton) RootView.findViewById(R.id.keyboard_hide);
		keyShow = (ImageButton) RootView.findViewById(R.id.keyboard_show);
		keyBackspace = (ImageButton) RootView
				.findViewById(R.id.keyboard_backspace);
		keyDialCall = (Button) RootView.findViewById(R.id.keyboard_call_number);

	}

	// 将数据用适配器类充到界面上.
	private void initData() {
		mCallLogListData = new ArrayList<CallLogBean>();
		// 注意这点,没有在CallLogListAdapter中充气获得,而是在这里,很特色
		mCallLogListAdapter = new CallLogListAdapter(getActivity(),
				mCallLogListData, R.layout.call_log_list_item_layout);
		callLogListView.setAdapter(mCallLogListAdapter);
	}

	// 给每个按钮设置事件监听器
	private void initListener() {
		keyNumber0.setOnClickListener(keyboardOnClickListener);
		keyNumber1.setOnClickListener(keyboardOnClickListener);
		keyNumber2.setOnClickListener(keyboardOnClickListener);
		keyNumber3.setOnClickListener(keyboardOnClickListener);
		keyNumber4.setOnClickListener(keyboardOnClickListener);
		keyNumber5.setOnClickListener(keyboardOnClickListener);
		keyNumber6.setOnClickListener(keyboardOnClickListener);
		keyNumber7.setOnClickListener(keyboardOnClickListener);
		keyNumber8.setOnClickListener(keyboardOnClickListener);
		keyNumber9.setOnClickListener(keyboardOnClickListener);
		keyNumberStar.setOnClickListener(keyboardOnClickListener);
		keyNumberHash.setOnClickListener(keyboardOnClickListener);
		keyHide.setOnClickListener(keyboardOnClickListener);
		keyShow.setOnClickListener(keyboardOnClickListener);
		keyBackspace.setOnClickListener(keyboardOnClickListener);
		keyDialCall.setOnClickListener(keyboardOnClickListener);
		callIn.setOnClickListener(keyboardOnClickListener);
		callOut.setOnClickListener(keyboardOnClickListener);
		callMiss.setOnClickListener(keyboardOnClickListener);
		callAll.setOnClickListener(keyboardOnClickListener);

        callLogListView .setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(1, MENU_DIAL_PHONE, 1, "拨打电话");
                        menu.add(1, MENU_SEND_MESSAGE, 2, "发送短信");
                        menu.add(1, MENU_DELETE_CALLLOG, 3, "删除通话记录");
						menu.add(1, MENU_NEW_CONTACT, 4, "添加联系人");
						menu.add(1, MENU_BLACKLISTIN, 5, "加入黑名单");
						menu.add(1, MENU_BLACKLISTOUT, 6, "移除黑名单");


                    }
                });

		callLogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(
					AdapterView<?> parent,
					View view,
					int position,
					long id) {
				CallLogBean mCallLogBean= (CallLogBean) parent.getItemAtPosition(position);
				Intent intent = new Intent(Intent.ACTION_CALL);
				Uri callNumberUri = Uri.parse("tel:"
						+ mCallLogBean.CallLogNumber);
				intent.setData(callNumberUri);
				startActivity(intent);
			}
		});
	}

	// 监听器类.
	CallLogDataThread mCallLogDataThread;
	OnClickListener keyboardOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.call_log_show_in:
				// 调用子线程，更新通话记录数据,下同,即重新获取更新过的数据
				mCallLogDataThread = new CallLogDataThread(1);
				mCallLogDataThread.start();
				break;
			case R.id.call_log_show_out:
				mCallLogDataThread = new CallLogDataThread(2);
				mCallLogDataThread.start();
				break;
			case R.id.call_log_show_miss:
				mCallLogDataThread = new CallLogDataThread(3);
				mCallLogDataThread.start();
				break;
			case R.id.call_log_show_all:
				mCallLogDataThread = new CallLogDataThread(0);
				mCallLogDataThread.start();
				break;
			case R.id.keyboard_0:
				keyDialCall.append("0");
				break;
			case R.id.keyboard_1:
				keyDialCall.append("1");
				break;
			case R.id.keyboard_2:
				keyDialCall.append("2");
				break;
			case R.id.keyboard_3:
				keyDialCall.append("3");
				break;
			case R.id.keyboard_4:
				keyDialCall.append("4");
				break;
			case R.id.keyboard_5:
				keyDialCall.append("5");
				break;
			case R.id.keyboard_6:
				keyDialCall.append("6");
				break;
			case R.id.keyboard_7:
				keyDialCall.append("7");
				break;
			case R.id.keyboard_8:
				keyDialCall.append("8");
				break;
			case R.id.keyboard_9:
				keyDialCall.append("9");
				break;
			case R.id.keyboard_star:
				keyDialCall.append("*");
				break;
			case R.id.keyboard_hash:
				keyDialCall.append("#");
				break;
			case R.id.keyboard_hide:
				mKeyboardLayout.setVisibility(View.GONE);
				break;
			case R.id.keyboard_show:
				mKeyboardLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.keyboard_backspace:
				String strNumber = keyDialCall.getText().toString();
				if (strNumber.length() > 0) {
					strNumber = strNumber.substring(0, strNumber.length() - 1);// 每次只能删除一个数字
					keyDialCall.setText(strNumber);
				}
				break;
			case R.id.keyboard_call_number:
				String number=((Button) v).getText().toString();
				if(number.isEmpty()){
					if (toast != null) {
						toast.setText("号码不能为空");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(), "号码不能为空", Toast.LENGTH_SHORT);
						toast.show();
					}
					return ;
				}
				if(number.length()<3){
					if (toast != null) {
						toast.setText("号码必须两位以上");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(), "号码必须两位以上", Toast.LENGTH_SHORT);
						toast.show();
					}
					return;
				}
				// 打电话死代码
				Uri dailCallUri = Uri.parse("tel:"
						+ number);
				((Button) v).setText("");
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(dailCallUri);
				startActivity(callIntent);
				break;
			default:
				break;
			}
		}
	};

    public boolean onContextItemSelected(MenuItem item) {
        // 获取被操作对象
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
		CallLogBean mCallLogBean= (CallLogBean) mCallLogListAdapter.getItem(menuInfo.position);
        switch (item.getItemId()) {
            case MENU_DIAL_PHONE:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                Uri callNumberUri = Uri.parse("tel:"
								+ mCallLogBean.CallLogNumber);
                callIntent.setData(callNumberUri);
                startActivity(callIntent);
                break;
            case MENU_SEND_MESSAGE:
                Intent intent = new Intent(getActivity(), SmsDetailActivity.class);
                intent.putExtra("PhoneNumber", mCallLogBean.CallLogNumber);
                getActivity().startActivity(intent);
                break;
            case MENU_DELETE_CALLLOG:
                Toast.makeText(getActivity(), "删除成功",
										Toast.LENGTH_SHORT).show();
                ContentResolver mContentResolver = TApplication.MY_SELF
										.getContentResolver();
                mContentResolver.delete(CallLog.Calls.CONTENT_URI, "number = "+ mCallLogBean.CallLogNumber,
										null);
                TApplication.CallLogListData.remove(mCallLogBean);
                break;
			case MENU_NEW_CONTACT:
				intent = new Intent(Intent.ACTION_INSERT);
				intent.setType("vnd.android.cursor.dir/person");
				intent.setType("vnd.android.cursor.dir/contact");
				intent.setType("vnd.android.cursor.dir/raw_contact");
				intent.putExtra(ContactsContract.Intents.Insert.NAME,mCallLogBean.CallLogName);
				intent.putExtra(ContactsContract.Intents.Insert.PHONE,mCallLogBean.CallLogNumber );
				startActivity(intent);
				break;
            case MENU_BLACKLISTIN:
				Toast.makeText(getActivity(), "已经加入黑名单",
						Toast.LENGTH_SHORT).show();
				blackListBiz.addNumber(mCallLogBean.CallLogNumber);
                break;
			case MENU_BLACKLISTOUT:
				Toast.makeText(getActivity(), "已经移除黑名单",
						Toast.LENGTH_SHORT).show();
				blackListBiz.removeNumber(mCallLogBean.CallLogNumber);
				break;
        }
        return super.onContextItemSelected(item);
    }

	// 这里的广播主要就是为了更新
	class UpCallLogListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			// 更新数据，更新界面
			mCallLogListAdapter.changeData(TApplication.CallLogListData);
			mCallLogListAdapter.notifyDataSetChanged();
		}
	}

}
