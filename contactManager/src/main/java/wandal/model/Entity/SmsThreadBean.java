package wandal.model.Entity;

import android.graphics.Bitmap;

//����ʵ����.ǿ���Ƽ�����д.
public class SmsThreadBean {
	// �ⲿ���ǳ�����������,��getAllSmsThreadListData�м���.
	public int SmsThreadId;// ���ŻỰID
	public String SmsBody;// ��������
	public int SmsType;// �����յ����������Ƿ�������
	public long SmsDate;// ����
	public String SmsDateFormat;// ���ڵ�����
	public String SmsAddress;// ��ַ���绰����
	public int SmsThreadCount;// ͬһ��ThreadID��Ӧ�Ķ�������
	// �����Ǽ���ͷ�������������Ҫ������,��setSmsThreadListContactsInfo�ٴμ��ص�����
	public int ContactId;
	public String ContactName;
	public int ContactPhotoId;
	public Bitmap contactPhoto;
}
