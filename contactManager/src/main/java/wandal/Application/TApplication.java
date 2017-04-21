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

//全局数据共享层
public class TApplication extends Application {
	// 用于广播的常量.
	public static final String CALL_LOG_DATA_UP_ACTION = "com.wandal.tmanager.calllog";
	public static final String SMS_THREAD_DATA_UP_ACTION = "com.wandal.tmanager.sms";
	public static final String CONTACTS_DATA_UP_ACTION = "com.wandal.tmanager.contacts";

	// 保存并提供访问的数据集合List<CallLogBean>
	public static List<CallLogBean> CallLogListData;
	public static List<SmsThreadBean> SmsThreadListData;
	public static List<ContactBean> ContactsListData;

	public static ContactsContentObserver mContactsContentObserver;
	public static SmsThreadContentObserver mSmsThreadContentObserver;
	public static CallLogContentObserver mCallLogContentObserver;
	// 让其他任何类都方便的访问Application,可以方便的使用上下文,好多的源码都有.所有就用他.
	public static TApplication MY_SELF;

	// 启动子线程
	public void onCreate() {
		super.onCreate();
		MY_SELF = this;

		// 调用子线程，更新CallLog数据
		CallLogDataThread mCallLogDataThread = new CallLogDataThread(0);
		mCallLogDataThread.start();

		// 调用子线程，更新Sms的thread数据
		SmsDataThread mSmsDataThread = new SmsDataThread();
		mSmsDataThread.start();

		// 调用子线程，更新Contacts的thread数据
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
		// 联系人数据改变，更新数据
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
		// 调用子线程，更新Sms的thread数据
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
		// 调用子线程，更新通话记录数据,因为可以重新获取更新过的数据
		CallLogDataThread mCallLogDataThread = new CallLogDataThread(0);
		mCallLogDataThread.start();
	}
}
