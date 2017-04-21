package wandal.model.Entity;

import android.graphics.Bitmap;

//通讯录实体类,强烈推荐这样写
public class CallLogBean {
	// 实体对象，记录我们需要的通话记录信息,具体有哪些属性，应该根据Calls表
	//这部分是初步加载数据,在getCallLogListData初步加载.
	public int CallLogId;// _id
	public String CallLogNumber;// number
	public long CallLogDate;// date
	public String CallLogDateFormat;//
	public int CallLogType;// type
	public int CallLogCount;// 计算的同一电话号码的通话次数
	public String CallLogName;// name
//下面是加载头像和有无姓名需要的数据,在setCallLogListContactsInfo再次加载的数据
	public int ContactId;// contact_id:1
	public int ContactPhotoId;// photo_id:0
	public String ContactName;// display_name:张老
	public Bitmap ContactPhotoBitmap;

}
