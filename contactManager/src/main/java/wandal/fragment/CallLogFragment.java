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

	View RootView;// ����Ƭ�ε���ɫ
	// �������е�����Ͳ�����ͼ,����,��������,�㲥,�۲���.
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

	// ������е���ͼ,������,�㲥,�۲��ߵĴ����ͳ�ʼ��
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// �����õ�����
		RootView = inflater.inflate(R.layout.call_log_fragment_layout, null);
		blackListBiz=new BlackListBiz(getActivity());
		initView();
		initData();
		initListener();

		// �����绰��������
		Intent intent = new Intent(getActivity(), TelService.class);
		getActivity().startService(intent);

		// ע��㲥
		mUpCallLogListReceiver = new UpCallLogListReceiver();
		getActivity().registerReceiver(mUpCallLogListReceiver,
				new IntentFilter(TApplication.CALL_LOG_DATA_UP_ACTION));

		// ע�ⷵ�������,�����û����ʾ.
		return RootView;
	}

	// ע���㲥�͹۲���
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mUpCallLogListReceiver);
	}

	public void onResume() {
		super.onResume();
	}

	// �õ��������.
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

	// ����������������䵽������.
	private void initData() {
		mCallLogListData = new ArrayList<CallLogBean>();
		// ע�����,û����CallLogListAdapter�г������,����������,����ɫ
		mCallLogListAdapter = new CallLogListAdapter(getActivity(),
				mCallLogListData, R.layout.call_log_list_item_layout);
		callLogListView.setAdapter(mCallLogListAdapter);
	}

	// ��ÿ����ť�����¼�������
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
                        menu.add(1, MENU_DIAL_PHONE, 1, "����绰");
                        menu.add(1, MENU_SEND_MESSAGE, 2, "���Ͷ���");
                        menu.add(1, MENU_DELETE_CALLLOG, 3, "ɾ��ͨ����¼");
						menu.add(1, MENU_NEW_CONTACT, 4, "�����ϵ��");
						menu.add(1, MENU_BLACKLISTIN, 5, "���������");
						menu.add(1, MENU_BLACKLISTOUT, 6, "�Ƴ�������");


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

	// ��������.
	CallLogDataThread mCallLogDataThread;
	OnClickListener keyboardOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.call_log_show_in:
				// �������̣߳�����ͨ����¼����,��ͬ,�����»�ȡ���¹�������
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
					strNumber = strNumber.substring(0, strNumber.length() - 1);// ÿ��ֻ��ɾ��һ������
					keyDialCall.setText(strNumber);
				}
				break;
			case R.id.keyboard_call_number:
				String number=((Button) v).getText().toString();
				if(number.isEmpty()){
					if (toast != null) {
						toast.setText("���벻��Ϊ��");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(), "���벻��Ϊ��", Toast.LENGTH_SHORT);
						toast.show();
					}
					return ;
				}
				if(number.length()<3){
					if (toast != null) {
						toast.setText("���������λ����");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(), "���������λ����", Toast.LENGTH_SHORT);
						toast.show();
					}
					return;
				}
				// ��绰������
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
        // ��ȡ����������
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
                Toast.makeText(getActivity(), "ɾ���ɹ�",
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
				Toast.makeText(getActivity(), "�Ѿ����������",
						Toast.LENGTH_SHORT).show();
				blackListBiz.addNumber(mCallLogBean.CallLogNumber);
                break;
			case MENU_BLACKLISTOUT:
				Toast.makeText(getActivity(), "�Ѿ��Ƴ�������",
						Toast.LENGTH_SHORT).show();
				blackListBiz.removeNumber(mCallLogBean.CallLogNumber);
				break;
        }
        return super.onContextItemSelected(item);
    }

	// ����Ĺ㲥��Ҫ����Ϊ�˸���
	class UpCallLogListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			// �������ݣ����½���
			mCallLogListAdapter.changeData(TApplication.CallLogListData);
			mCallLogListAdapter.notifyDataSetChanged();
		}
	}

}
