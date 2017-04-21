package wandal.model.thread;

import android.content.Intent;

import wandal.Application.TApplication;
import wandal.model.Utils.CallLogDataUtils;


public class CallLogDataThread extends Thread {
	int filterType;

	// ���췽��.
	public CallLogDataThread(int type) {
		filterType = type;
	}

	public void run() {
		super.run();
		// �����ݷ���Application,���ڸ���ͨ����¼��������
		TApplication.CallLogListData = CallLogDataUtils
				.getCallLogListData(filterType);
		// ���͹㲥(פ��)
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.CALL_LOG_DATA_UP_ACTION));

		// ���÷�����������ͷ��,���ڸ���ͨ����¼��Ӧ��ϵ��������ͷ��
		CallLogDataUtils
				.setCallLogListContactsInfo(TApplication.CallLogListData);
		// ���͹㲥(פ��)
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.CALL_LOG_DATA_UP_ACTION));
	}
}
