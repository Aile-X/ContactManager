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
		// 根据thread_id查找具体会话信息
		List<SmsDetailBean> mSmsDetailListData = SmsUtil
				.getSmsDetailListByThreadId(mThreadId);

		// 通过消息即时发送到SmsDetailActivity接收
		Message mMessage = mHandler.obtainMessage();
		mMessage.obj = mSmsDetailListData;
		mHandler.sendMessage(mMessage);
	}
}