package wandal.model.thread;

import wandal.Application.TApplication;
import wandal.model.Utils.SmsUtil;
import android.content.Intent;


public class SmsDataThread extends Thread {
	// 构造方法.
	public SmsDataThread() {
		super();
	}

	public void run() {
		super.run();
		// 将数据放入Application,用于更新通话记录初步数据
		TApplication.SmsThreadListData = SmsUtil.getAllSmsThreadListData();
		// 发送指定的驻留广播
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.SMS_THREAD_DATA_UP_ACTION));

		// 调用方法更新有无头像,用于更新通话记录对应联系人姓名和头像
		SmsUtil.setSmsThreadListContactsInfo(TApplication.SmsThreadListData);
		// 发送指定的驻留广播
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.SMS_THREAD_DATA_UP_ACTION));

	}

}
