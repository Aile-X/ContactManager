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

//发送短信的复杂界面.好好研究吧
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
		// 接收来自SmsThreadListAdapter中传递过来的数据
		ThreadId = getIntent().getIntExtra("ThreadId", -1);
		SmsCount = getIntent().getIntExtra("SmsCount", -1);
		PhoneNumber = getIntent().getStringExtra("PhoneNumber");
		ContactName = getIntent().getStringExtra("ContactName");
		// 找到所有组件.
		smsDetailMainLayout = (LinearLayout) findViewById(R.id.sms_detail_list_layout);
		smsDetailSv = (ScrollView) findViewById(R.id.sms_detail_sv);
		sendMessageBodyEt = (EditText) findViewById(R.id.sms_detail_send_message_body);
		sendMessageButton = (ImageButton) findViewById(R.id.sms_detail_do_send_message);
		// 给发送短信按钮添加事件监听器
		sendMessageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 保存并发送短信
				if (!sendMessageBodyEt.getText().toString().isEmpty()) {
					sendSMS(PhoneNumber, sendMessageBodyEt.getText().toString());
					// 将自己所发送的短信放在右边布局中
					SmsDetailBean sendSmsDetailBean = new SmsDetailBean();
					sendSmsDetailBean.SmsBody = sendMessageBodyEt.getText()
							.toString();
					sendSmsDetailBean.SmsDateFormat = "刚刚";
					View sendOutView = getRightOutLayout(sendSmsDetailBean);
					smsDetailMainLayout.addView(sendOutView);
					sendMessageBodyEt.getText().clear();// 发送完毕后清空
					upHandler.sendEmptyMessageDelayed(0, 1);// 发送消息更新位置.
				}

			}
		});
		smsAdress = (TextView) findViewById(R.id.sms_detail_contact_number);
		smsCount = (TextView) findViewById(R.id.sms_detail_message_count);
		smsAdress.setText(PhoneNumber);
		smsCount.setText(SmsCount + "");
		// 通过thread_id查询数据，并显示
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
			// 接收来自SmsDetailThread发过来的数据
			mSmsDetailListData = (List<SmsDetailBean>) msg.obj;
			// 调用一个方法，将所有数据显示到界面
			showSmsDetailList();
		}
	};

	// 将所有数据显示到界面
	private void showSmsDetailList() {

		// 循环所有短信数据
		for (int i = 0; i < mSmsDetailListData.size(); i++) {
			// 根据类型，加载不同的会话布局
			SmsDetailBean nowSmsDetailBean = mSmsDetailListData.get(i);
			if (nowSmsDetailBean.SmsType == 1) {
				// 加载接收短信的View
				View leftInView = getLeftInLayout(nowSmsDetailBean);
				smsDetailMainLayout.addView(leftInView);
			} else {
				// 加载发送短信的View
				View rightOutView = getRightOutLayout(nowSmsDetailBean);
				smsDetailMainLayout.addView(rightOutView);
			}
		}
		// 发送消息更新位置.
		upHandler.sendEmptyMessageDelayed(0, 1);
	}

	// 用于定时定位到顶端
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
		// 在android设备中应该有一个比值 1dp= 多少像素
		leftLayout.setPadding(8, 4, 32, 4);
		// TextView设置
		TextView bodyTv = new TextView(this);
		bodyTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		bodyTv.setBackgroundResource(R.drawable.sms_detail_left_in);
		bodyTv.setText(smsDetailBean.SmsBody);
		bodyTv.setTextColor(Color.BLACK);
		// TextView设置
		TextView timeTv = new TextView(this);
		timeTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		timeTv.setText(smsDetailBean.SmsDateFormat);
		timeTv.setTextColor(Color.BLACK);
		// 将组件添加到LinearLayout布局中
		leftLayout.addView(bodyTv);
		leftLayout.addView(timeTv);

		// 下面完成划动删除
		leftLayout.setTag(smsDetailBean.SmsId);
		leftLayout.setOnTouchListener(new OnTouchListener() {
			float downXPoint = -1;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downXPoint = event.getX();// 刚触摸时的X坐标
						break;
					case MotionEvent.ACTION_UP:
						if (Math.abs(event.getX() - downXPoint) > 150) {
							// 删除这一条短信
							ContentResolver mContentResolver = SmsDetailActivity.this
									.getContentResolver();
							int delCount = mContentResolver.delete(Uri
											.parse("content://sms"), "_id = " + v.getTag(),
									null);
							Toast.makeText(SmsDetailActivity.this, "已经删除这条短信",
									Toast.LENGTH_SHORT).show();
							if (delCount > 0) {
								// 表示正确删除
								smsDetailMainLayout.removeView(v);
								upHandler.sendEmptyMessageDelayed(0, 1);
							} else {
								Toast.makeText(SmsDetailActivity.this, "不能删除这条短信",
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
		// LinearLayout布局
		LinearLayout RightLayout = new LinearLayout(this);
		RightLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		RightLayout.setGravity(Gravity.RIGHT);
		RightLayout.setOrientation(LinearLayout.VERTICAL);
		// 在android设备中应该有一个比值 1dp= 多少像素
		RightLayout.setPadding(32, 4, 8, 4);
		// textview设置
		TextView bodyTv = new TextView(this);
		bodyTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		bodyTv.setBackgroundResource(R.drawable.sms_detail_right_out);
		bodyTv.setText(smsDetailBean.SmsBody);
		bodyTv.setTextColor(Color.BLACK);
		// textview设置
		TextView timeTv = new TextView(this);
		timeTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		timeTv.setText(smsDetailBean.SmsDateFormat);
		timeTv.setTextColor(Color.BLACK);

		RightLayout.addView(bodyTv);
		RightLayout.addView(timeTv);

		// 下面完成划动删除
		RightLayout.setTag(smsDetailBean.SmsId);
		RightLayout.setOnTouchListener(new OnTouchListener() {
			float downXPoint = -1;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downXPoint = event.getX();// 刚触摸时的X坐标
						break;
					case MotionEvent.ACTION_UP:
						if (Math.abs(event.getX() - downXPoint) > 150) {
							// 删除这一条短信
							ContentResolver mContentResolver = SmsDetailActivity.this
									.getContentResolver();
							int delCount = mContentResolver.delete(Uri
											.parse("content://sms"), "_id = " + v.getTag(),
									null);
							Toast.makeText(SmsDetailActivity.this, "已经删除这条短信",
									Toast.LENGTH_SHORT).show();
							if (delCount > 0) {
								// 表示正确删除
								smsDetailMainLayout.removeView(v);
								upHandler.sendEmptyMessageDelayed(0, 1);
							} else {
								Toast.makeText(SmsDetailActivity.this, "不能删除这条短信",
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

	// 下面都是发送短信的死代码,不要太纠结
	/**
	 * 发送与接收的广播
	 **/
	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

	// 注册两个用于监控短信情况的广播
	public void registerSmsReceiver() {
		// 注册广播 发送消息
		registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
		registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
	}

	public void unRegisterSmsReceiver() {
		// 注销广播 发送消息
		unregisterReceiver(sendMessage);
		unregisterReceiver(receiver);
	}

	private BroadcastReceiver sendMessage = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 判断短信是否发送成功
			switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
					break;

				default:
					Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
					break;
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// 表示对方成功收到短信
			Toast.makeText(context, "对方接收成功", Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * 参数说明 destinationAddress:收信人的手机号码 scAddress:发信人的手机号码 text:发送信息的内容
	 * sentIntent:发送是否成功的回执，用于监听短信是否发送成功。
	 * DeliveryIntent:接收是否成功的回执，用于监听短信对方是否接收成功。
	 */
	private void sendSMS(String phoneNumber, String message) {
		// ---sends an SMS message to another device---
		// 获得短信管理器
		SmsManager sms = SmsManager.getDefault();
		// create the sentIntent parameter
		// 监控短信是否发送成功的Action和PendingIntent
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,
				0);
		// create the deilverIntent parameter
		// 监控对方是否收到短信的Action和PendingIntent
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
				deliverIntent, 0);
		// 如果短信内容超过70个字符 将这条短信拆成多条短信发送出去
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