package wandal.activity;

import java.util.ArrayList;
import java.util.List;

import wandal.model.Entity.SmsDetailBean;
import wandal.model.thread.SmsDetailThread;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

//���Ͷ��ŵĸ��ӽ���.�ú��о���
public class SmsDetailActivity extends Activity {
	int ThreadId;
	int SmsCount;
	String PhoneNumber;
	String ContactName;
	LinearLayout smsDetailMainLayout;
	ScrollView smsDetailSv;
	ImageButton sendMessageButton;
	EditText sendMessageBodyEt;
	TextView smsAdress, smsCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_thread_detail_layout);
		// ��������SmsThreadListAdapter�д��ݹ���������
		ThreadId = getIntent().getIntExtra("ThreadId", -1);
		SmsCount = getIntent().getIntExtra("SmsCount", -1);
		PhoneNumber = getIntent().getStringExtra("PhoneNumber");
		ContactName = getIntent().getStringExtra("ContactName");
		// �ҵ��������.
		smsDetailMainLayout = (LinearLayout) findViewById(R.id.sms_detail_list_layout);
		smsDetailSv = (ScrollView) findViewById(R.id.sms_detail_sv);
		sendMessageBodyEt = (EditText) findViewById(R.id.sms_detail_send_message_body);
		sendMessageButton = (ImageButton) findViewById(R.id.sms_detail_do_send_message);
		// �����Ͷ��Ű�ť����¼�������
		sendMessageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ���沢���Ͷ���
				if (!sendMessageBodyEt.getText().toString().isEmpty()) {
					sendSMS(PhoneNumber, sendMessageBodyEt.getText().toString());
					// ���Լ������͵Ķ��ŷ����ұ߲�����
					SmsDetailBean sendSmsDetailBean = new SmsDetailBean();
					sendSmsDetailBean.SmsBody = sendMessageBodyEt.getText()
							.toString();
					sendSmsDetailBean.SmsDateFormat = "�ո�";
					View sendOutView = getRightOutLayout(sendSmsDetailBean);
					smsDetailMainLayout.addView(sendOutView);
					sendMessageBodyEt.getText().clear();// ������Ϻ����
					upHandler.sendEmptyMessageDelayed(0, 1);// ������Ϣ����λ��.
				}

			}
		});
		smsAdress = (TextView) findViewById(R.id.sms_detail_contact_number);
		smsCount = (TextView) findViewById(R.id.sms_detail_message_count);
		smsAdress.setText(PhoneNumber);
		smsCount.setText(SmsCount + "");
		// ͨ��thread_id��ѯ���ݣ�����ʾ
		SmsDetailThread mSmsDetailThread = new SmsDetailThread(ThreadId,
				showSmsDetailHandler);
		mSmsDetailThread.start();
		registerSmsReceiver();
	}

	protected void onDestroy() {
		super.onDestroy();
		unRegisterSmsReceiver();
	}

	List<SmsDetailBean> mSmsDetailListData;
	Handler showSmsDetailHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// ��������SmsDetailThread������������
			mSmsDetailListData = (List<SmsDetailBean>) msg.obj;
			// ����һ��������������������ʾ������
			showSmsDetailList();
		}
	};

	// ������������ʾ������
	private void showSmsDetailList() {

		// ѭ�����ж�������
		for (int i = 0; i < mSmsDetailListData.size(); i++) {
			// �������ͣ����ز�ͬ�ĻỰ����
			SmsDetailBean nowSmsDetailBean = mSmsDetailListData.get(i);
			if (nowSmsDetailBean.SmsType == 1) {
				// ���ؽ��ն��ŵ�View
				View leftInView = getLeftInLayout(nowSmsDetailBean);
				smsDetailMainLayout.addView(leftInView);
			} else {
				// ���ط��Ͷ��ŵ�View
				View rightOutView = getRightOutLayout(nowSmsDetailBean);
				smsDetailMainLayout.addView(rightOutView);
			}
		}
		// ������Ϣ����λ��.
		upHandler.sendEmptyMessageDelayed(0, 1);
	}

	// ���ڶ�ʱ��λ������
	Handler upHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int moveSize = smsDetailMainLayout.getMeasuredHeight()
					- smsDetailSv.getHeight();
			smsDetailSv.scrollTo(0, moveSize);
		}
	};

	private View getLeftInLayout(SmsDetailBean smsDetailBean) {
		LinearLayout leftLayout = new LinearLayout(this);
		leftLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		leftLayout.setGravity(Gravity.LEFT);
		leftLayout.setOrientation(LinearLayout.VERTICAL);
		// ��android�豸��Ӧ����һ����ֵ 1dp= ��������
		leftLayout.setPadding(8, 4, 32, 4);
		// TextView����
		TextView bodyTv = new TextView(this);
		bodyTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		bodyTv.setBackgroundResource(R.drawable.sms_detail_left_in);
		bodyTv.setText(smsDetailBean.SmsBody);
		bodyTv.setTextColor(Color.BLACK);
		// TextView����
		TextView timeTv = new TextView(this);
		timeTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		timeTv.setText(smsDetailBean.SmsDateFormat);
		timeTv.setTextColor(Color.BLACK);
		// �������ӵ�LinearLayout������
		leftLayout.addView(bodyTv);
		leftLayout.addView(timeTv);

		// ������ɻ���ɾ��
		leftLayout.setTag(smsDetailBean.SmsId);
		leftLayout.setOnTouchListener(new OnTouchListener() {
			float downXPoint = -1;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downXPoint = event.getX();// �մ���ʱ��X����
						break;
					case MotionEvent.ACTION_UP:
						if (Math.abs(event.getX() - downXPoint) > 150) {
							// ɾ����һ������
							ContentResolver mContentResolver = SmsDetailActivity.this
									.getContentResolver();
							int delCount = mContentResolver.delete(Uri
											.parse("content://sms"), "_id = " + v.getTag(),
									null);
							Toast.makeText(SmsDetailActivity.this, "�Ѿ�ɾ����������",
									Toast.LENGTH_SHORT).show();
							if (delCount > 0) {
								// ��ʾ��ȷɾ��
								smsDetailMainLayout.removeView(v);
								upHandler.sendEmptyMessageDelayed(0, 1);
							} else {
								Toast.makeText(SmsDetailActivity.this, "����ɾ����������",
										Toast.LENGTH_SHORT).show();
							}
						}
						break;

					default:
						break;
				}
				return true;
			}
		});
		return leftLayout;
	}

	private View getRightOutLayout(SmsDetailBean smsDetailBean) {
		// LinearLayout����
		LinearLayout RightLayout = new LinearLayout(this);
		RightLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		RightLayout.setGravity(Gravity.RIGHT);
		RightLayout.setOrientation(LinearLayout.VERTICAL);
		// ��android�豸��Ӧ����һ����ֵ 1dp= ��������
		RightLayout.setPadding(32, 4, 8, 4);
		// textview����
		TextView bodyTv = new TextView(this);
		bodyTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		bodyTv.setBackgroundResource(R.drawable.sms_detail_right_out);
		bodyTv.setText(smsDetailBean.SmsBody);
		bodyTv.setTextColor(Color.BLACK);
		// textview����
		TextView timeTv = new TextView(this);
		timeTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		timeTv.setText(smsDetailBean.SmsDateFormat);
		timeTv.setTextColor(Color.BLACK);

		RightLayout.addView(bodyTv);
		RightLayout.addView(timeTv);

		// ������ɻ���ɾ��
		RightLayout.setTag(smsDetailBean.SmsId);
		RightLayout.setOnTouchListener(new OnTouchListener() {
			float downXPoint = -1;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downXPoint = event.getX();// �մ���ʱ��X����
						break;
					case MotionEvent.ACTION_UP:
						if (Math.abs(event.getX() - downXPoint) > 150) {
							// ɾ����һ������
							ContentResolver mContentResolver = SmsDetailActivity.this
									.getContentResolver();
							int delCount = mContentResolver.delete(Uri
											.parse("content://sms"), "_id = " + v.getTag(),
									null);
							Toast.makeText(SmsDetailActivity.this, "�Ѿ�ɾ����������",
									Toast.LENGTH_SHORT).show();
							if (delCount > 0) {
								// ��ʾ��ȷɾ��
								smsDetailMainLayout.removeView(v);
								upHandler.sendEmptyMessageDelayed(0, 1);
							} else {
								Toast.makeText(SmsDetailActivity.this, "����ɾ����������",
										Toast.LENGTH_SHORT).show();
							}
						}
						break;

					default:
						break;
				}
				return true;
			}
		});
		return RightLayout;
	}

	// ���涼�Ƿ��Ͷ��ŵ�������,��Ҫ̫����
	/**
	 * ��������յĹ㲥
	 **/
	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

	// ע���������ڼ�ض�������Ĺ㲥
	public void registerSmsReceiver() {
		// ע��㲥 ������Ϣ
		registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
		registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
	}

	public void unRegisterSmsReceiver() {
		// ע���㲥 ������Ϣ
		unregisterReceiver(sendMessage);
		unregisterReceiver(receiver);
	}

	private BroadcastReceiver sendMessage = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// �ж϶����Ƿ��ͳɹ�
			switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(context, "���ŷ��ͳɹ�", Toast.LENGTH_SHORT).show();
					break;

				default:
					Toast.makeText(context, "����ʧ��", Toast.LENGTH_LONG).show();
					break;
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// ��ʾ�Է��ɹ��յ�����
			Toast.makeText(context, "�Է����ճɹ�", Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * ����˵�� destinationAddress:�����˵��ֻ����� scAddress:�����˵��ֻ����� text:������Ϣ������
	 * sentIntent:�����Ƿ�ɹ��Ļ�ִ�����ڼ��������Ƿ��ͳɹ���
	 * DeliveryIntent:�����Ƿ�ɹ��Ļ�ִ�����ڼ������ŶԷ��Ƿ���ճɹ���
	 */
	private void sendSMS(String phoneNumber, String message) {
		// ---sends an SMS message to another device---
		// ��ö��Ź�����
		SmsManager sms = SmsManager.getDefault();
		// create the sentIntent parameter
		// ��ض����Ƿ��ͳɹ���Action��PendingIntent
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,
				0);
		// create the deilverIntent parameter
		// ��ضԷ��Ƿ��յ����ŵ�Action��PendingIntent
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
				deliverIntent, 0);
		// ����������ݳ���70���ַ� ���������Ų�ɶ������ŷ��ͳ�ȥ
		if (message.length() > 70) {
			ArrayList<String> msgs = sms.divideMessage(message);
			for (String msg : msgs) {
				sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
			}
		} else {
			sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
		}
	}
}