package wandal.model.Entity;

import android.graphics.Bitmap;

//短信实体类.强烈推荐这样写.
public class SmsThreadBean {
	// 这部分是初步加载数据,在getAllSmsThreadListData中加载.
	public int SmsThreadId;// 短信会话ID
	public String SmsBody;// 短信内容
	public int SmsType;// 除了收到，其他都是发出处理
	public long SmsDate;// 日期
	public String SmsDateFormat;// 日期的整形
	public String SmsAddress;// 地址：电话号码
	public int SmsThreadCount;// 同一个ThreadID对应的短信数量
	// 下面是加载头像和有无姓名需要的数据,在setSmsThreadListContactsInfo再次加载的数据
	public int ContactId;
	public String ContactName;
	public int ContactPhotoId;
	public Bitmap contactPhoto;
}
