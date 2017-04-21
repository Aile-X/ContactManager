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
					Toast.makeText(SmsNewActivity.this, "���ŷ��ͳɹ�",Toast.LENGTH_SHORT).show();
					sendSMS(sendPhonenumberEt.getText().toString(), sendMessageBodyEt.getText().toString());
					sendPhonenumberEt.getText().clear();
					sendMessageBodyEt.getText().clear();
					finish();
				}else {
					if (toast != null) {
						toast.setText("�������Ͷ������ݲ���Ϊ��");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(SmsNewActivity.this, "�������Ͷ������ݲ���Ϊ��", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
		});
	}
	protected void onDestroy() {
		super.onDestroy();
	}
	// ���涼�Ƿ��Ͷ��ŵ�������,��Ҫ̫����
	/** ��������յĹ㲥 **/
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
