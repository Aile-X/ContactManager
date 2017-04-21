package wandal.activity;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;

public class SmsNewActivity extends Activity {
	ImageButton sendMessageButton;
	EditText sendMessageBodyEt,sendPhonenumberEt;
	Toast toast = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_thread_new_layout);

		sendMessageBodyEt = (EditText) findViewById(R.id.sms_detail_send_message_body);
		sendPhonenumberEt= (EditText) findViewById(R.id.sms_phone_number);
		sendMessageButton = (ImageButton) findViewById(R.id.sms_detail_do_send_message);

		sendMessageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!sendMessageBodyEt.getText().toString().isEmpty() && !sendPhonenumberEt.getText().toString().isEmpty()) {
					Toast.makeText(SmsNewActivity.this, "短信发送成功",Toast.LENGTH_SHORT).show();
					sendSMS(sendPhonenumberEt.getText().toString(), sendMessageBodyEt.getText().toString());
					sendPhonenumberEt.getText().clear();
					sendMessageBodyEt.getText().clear();
					finish();
				}else {
					if (toast != null) {
						toast.setText("请检查号码和短信内容不能为空");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(SmsNewActivity.this, "请检查号码和短信内容不能为空", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
		});
	}
	protected void onDestroy() {
		super.onDestroy();
	}
	// 下面都是发送短信的死代码,不要太纠结
	/** 发送与接收的广播 **/
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
