package wandal.model.Entity;

import android.graphics.Bitmap;

//ͨѶ¼ʵ����,ǿ���Ƽ�����д
public class CallLogBean {
	// ʵ����󣬼�¼������Ҫ��ͨ����¼��Ϣ,��������Щ���ԣ�Ӧ�ø���Calls��
	//�ⲿ���ǳ�����������,��getCallLogListData��������.
	public int CallLogId;// _id
	public String CallLogNumber;// number
	public long CallLogDate;// date
	public String CallLogDateFormat;//
	public int CallLogType;// type
	public int CallLogCount;// �����ͬһ�绰�����ͨ������
	public String CallLogName;// name
//�����Ǽ���ͷ�������������Ҫ������,��setCallLogListContactsInfo�ٴμ��ص�����
	public int ContactId;// contact_id:1
	public int ContactPhotoId;// photo_id:0
	public String ContactName;// display_name:����
	public Bitmap ContactPhotoBitmap;

}
