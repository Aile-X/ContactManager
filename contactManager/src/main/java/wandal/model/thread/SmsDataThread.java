package wandal.model.thread;

import wandal.Application.TApplication;
import wandal.model.Utils.SmsUtil;
import android.content.Intent;


public class SmsDataThread extends Thread {
	// ���췽��.
	public SmsDataThread() {
		super();
	}

	public void run() {
		super.run();
		// �����ݷ���Application,���ڸ���ͨ����¼��������
		TApplication.SmsThreadListData = SmsUtil.getAllSmsThreadListData();
		// ����ָ����פ���㲥
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.SMS_THREAD_DATA_UP_ACTION));

		// ���÷�����������ͷ��,���ڸ���ͨ����¼��Ӧ��ϵ��������ͷ��
		SmsUtil.setSmsThreadListContactsInfo(TApplication.SmsThreadListData);
		// ����ָ����פ���㲥
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.SMS_THREAD_DATA_UP_ACTION));

	}

}
