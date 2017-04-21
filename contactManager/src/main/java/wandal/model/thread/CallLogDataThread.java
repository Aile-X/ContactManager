package wandal.model.thread;

import android.content.Intent;

import wandal.Application.TApplication;
import wandal.model.Utils.CallLogDataUtils;


public class CallLogDataThread extends Thread {
	int filterType;

	// 构造方法.
	public CallLogDataThread(int type) {
		filterType = type;
	}

	public void run() {
		super.run();
		// 将数据放入Application,用于更新通话记录初步数据
		TApplication.CallLogListData = CallLogDataUtils
				.getCallLogListData(filterType);
		// 发送广播(驻留)
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.CALL_LOG_DATA_UP_ACTION));

		// 调用方法更新有无头像,用于更新通话记录对应联系人姓名和头像
		CallLogDataUtils
				.setCallLogListContactsInfo(TApplication.CallLogListData);
		// 发送广播(驻留)
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.CALL_LOG_DATA_UP_ACTION));
	}
}
