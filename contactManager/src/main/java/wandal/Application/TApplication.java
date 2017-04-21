package wandal.Application;

import android.app.Application;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

import java.util.List;

import wandal.model.Entity.CallLogBean;
import wandal.model.Entity.ContactBean;
import wandal.model.Entity.SmsThreadBean;
import wandal.model.thread.CallLogDataThread;
import wandal.model.thread.ContactsThread;
import wandal.model.thread.SmsDataThread;

//ȫ�����ݹ����
public class TApplication extends Application {
	// ���ڹ㲥�ĳ���.
	public static final String CALL_LOG_DATA_UP_ACTION = "com.wandal.tmanager.calllog";
	public static final String SMS_THREAD_DATA_UP_ACTION = "com.wandal.tmanager.sms";
	public static final String CONTACTS_DATA_UP_ACTION = "com.wandal.tmanager.contacts";

	// ���沢�ṩ���ʵ����ݼ���List<CallLogBean>
	public static List<CallLogBean> CallLogListData;
	public static List<SmsThreadBean> SmsThreadListData;
	public static List<ContactBean> ContactsListData;

	public static ContactsContentObserver mContactsContentObserver;
	public static SmsThreadContentObserver mSmsThreadContentObserver;
	public static CallLogContentObserver mCallLogContentObserver;
	// �������κ��඼����ķ���Application,���Է����ʹ��������,�ö��Դ�붼��.���о�����.
	public static TApplication MY_SELF;

	// �������߳�
	public void onCreate() {
		super.onCreate();
		MY_SELF = this;

		// �������̣߳�����CallLog����
		CallLogDataThread mCallLogDataThread = new CallLogDataThread(0);
		mCallLogDataThread.start();

		// �������̣߳�����Sms��thread����
		SmsDataThread mSmsDataThread = new SmsDataThread();
		mSmsDataThread.start();

		// �������̣߳�����Contacts��thread����
		ContactsThread mContactsThread = new ContactsThread();
		mContactsThread.start();

		mContactsContentObserver = new ContactsContentObserver(null);
		TApplication.MY_SELF.getContentResolver().registerContentObserver(
				Uri.parse("content://com.android.contacts"), true,
				mContactsContentObserver);

		mSmsThreadContentObserver = new SmsThreadContentObserver(null);
		TApplication.MY_SELF.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, mSmsThreadContentObserver);

		mCallLogContentObserver = new CallLogContentObserver(null);
		TApplication.MY_SELF.getContentResolver().registerContentObserver(
				CallLog.Calls.CONTENT_URI, true, mCallLogContentObserver);
	}

}

class ContactsContentObserver extends ContentObserver {
	public ContactsContentObserver(Handler handler) {
		super(handler);
	}

	public void onChange(boolean selfChange) {
		Log.i("Contacts_Info", "ContactsContentObserver");
		super.onChange(selfChange);
		// ��ϵ�����ݸı䣬��������
		ContactsThread mContactsThread = new ContactsThread();
		mContactsThread.start();

		SmsDataThread mSmsDataThread = new SmsDataThread();
		mSmsDataThread.start();

		CallLogDataThread mCallLogDataThread = new CallLogDataThread(0);
		mCallLogDataThread.start();
	}

}

class SmsThreadContentObserver extends ContentObserver {
	public SmsThreadContentObserver(Handler handler) {
		super(handler);
	}

	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		// �������̣߳�����Sms��thread����
		SmsDataThread mSmsDataThread = new SmsDataThread();
		mSmsDataThread.start();
	}

}

class CallLogContentObserver extends ContentObserver {
	public CallLogContentObserver(Handler handler) {
		super(handler);
	}

	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		// �������̣߳�����ͨ����¼����,��Ϊ�������»�ȡ���¹�������
		CallLogDataThread mCallLogDataThread = new CallLogDataThread(0);
		mCallLogDataThread.start();
	}
}
