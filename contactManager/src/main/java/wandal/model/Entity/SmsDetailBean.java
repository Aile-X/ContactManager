package wandal.model.Entity;

//发送短信界面的数据实体类
public class SmsDetailBean {
	public int SmsId; // 短信自己的_id
	public int SmsThreadId;// 短信会话ID
	public String SmsBody;// 短信内容
	public int SmsType;// 除了收到，其他都是发出处理
	public long SmsDate;// 日期
	public String SmsDateFormat;// 日期的整形
}
