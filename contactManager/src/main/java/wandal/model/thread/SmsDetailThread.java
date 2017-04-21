package wandal.model.thread;

import java.util.List;

import wandal.model.Entity.SmsDetailBean;
import wandal.model.Utils.SmsUtil;
import android.os.Handler;
import android.os.Message;


public class SmsDetailThread extends Thread {

	int mThreadId;
	Handler mHandler;

	public SmsDetailThread(int threadId, Handler handler) {
		super();
		mThreadId = threadId;
		mHandler = handler;
	}

	public void run() {
		super.run();
		// ����thread_id���Ҿ���Ự��Ϣ
		List<SmsDetailBean> mSmsDetailListData = SmsUtil
				.getSmsDetailListByThreadId(mThreadId);

		// ͨ����Ϣ��ʱ���͵�SmsDetailActivity����
		Message mMessage = mHandler.obtainMessage();
		mMessage.obj = mSmsDetailListData;
		mHandler.sendMessage(mMessage);
	}
}