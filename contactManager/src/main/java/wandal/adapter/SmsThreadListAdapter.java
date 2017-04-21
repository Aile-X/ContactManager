package wandal.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import wandal.activity.R;
import wandal.activity.SmsDetailActivity;
import wandal.model.Entity.SmsThreadBean;

//短信界面的适配器类
public class SmsThreadListAdapter extends BaseAdapter {

	Context mContext;
	List<SmsThreadBean> mSmsThreadListData;// 数据
	int mItemLayoutResId;
	LayoutInflater mLayoutInflater;// 充气机

	// 构造方法
	public SmsThreadListAdapter(Context context,
			List<SmsThreadBean> smsThreadListData, int itemLayoutResId) {
		mContext = context;
		mSmsThreadListData = smsThreadListData;
		mItemLayoutResId = itemLayoutResId;
		mLayoutInflater = LayoutInflater.from(context);

	}

	// 用于数据的更新
	public void changeData(List<SmsThreadBean> smsThreadListData) {
		mSmsThreadListData = smsThreadListData;
	}

	@Override
	public int getCount() {
		return mSmsThreadListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mSmsThreadListData.get(position);
	}

	// 注意这次的变化,强烈推荐.
	public long getItemId(int position) {
		return mSmsThreadListData.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// 注意是充气是mItemLayoutResId,那就具体是在CallLogFragment中的onCreateView中充气的.
			convertView = mLayoutInflater.inflate(mItemLayoutResId, null);
		}
		// 添加短信Thread数据
		TextView addressTv = (TextView) convertView
				.findViewById(R.id.sms_thread_list_item_address);
		TextView bodyTv = (TextView) convertView
				.findViewById(R.id.sms_thread_list_item_body);
		TextView timeTv = (TextView) convertView
				.findViewById(R.id.sms_thread_list_item_time);
		ImageView contactPhotoIv = (ImageView) convertView
				.findViewById(R.id.sms_thread_list_item_contact_photo);

		SmsThreadBean nowSmsThreadBean = mSmsThreadListData.get(position);

		// 判断是否有联系人对应信息，有则显示联系人姓名，没有则显示电话号码
		if (nowSmsThreadBean.ContactId > 0) {
			addressTv.setText(nowSmsThreadBean.ContactName + "  ("
					+ nowSmsThreadBean.SmsThreadCount + ")");
		} else {
			addressTv.setText(nowSmsThreadBean.SmsAddress + "  ("
					+ nowSmsThreadBean.SmsThreadCount + ")");
		}

		// 用图片就显示.没图片默认
		if (nowSmsThreadBean.ContactPhotoId > 0) {
			contactPhotoIv.setImageBitmap(nowSmsThreadBean.contactPhoto);
		} else {
			contactPhotoIv.setImageResource(R.drawable.default_contacts_photo);
		}

		bodyTv.setText(nowSmsThreadBean.SmsBody);
		timeTv.setText(nowSmsThreadBean.SmsDateFormat);

		// 给视图中触摸的具体的视图设置标识
		convertView.setTag(position);
		convertView.setOnTouchListener(new OnTouchListener() {
			float downXPoint = -1;
			int touchItemIndex = -1; // 对应我们选择的Item

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 记录我们在item上按下的位置
				case MotionEvent.ACTION_DOWN:
					downXPoint = event.getX();// 刚触摸时的X坐标
					break;
				case MotionEvent.ACTION_UP:
					// 将按下的位置和抬起的位置进行对比,>150认为是要删除对应thread_id的所有短信
					if (Math.abs(event.getX() - downXPoint) > 150) {
						// 取得所触摸的组件的索引
						touchItemIndex = (Integer) v.getTag();
						// 取得应删除短信集合的threadID,这里的删除与前面的不同
						SmsThreadBean delSmsThreadBean = mSmsThreadListData
								.remove(touchItemIndex);
						int delCount = mContext.getContentResolver().delete(
								Uri.parse("content://sms"),
								"thread_id = " + delSmsThreadBean.SmsThreadId,
								null);
						Toast.makeText(mContext, "删除" + delCount + "条短信记录",
								Toast.LENGTH_LONG).show();
						mSmsThreadListData
										.remove(delSmsThreadBean);
						// 删除之后更新界面
						SmsThreadListAdapter.this.changeData(mSmsThreadListData);
						SmsThreadListAdapter.this.notifyDataSetChanged();
					} else if (Math.abs(event.getX() - downXPoint) < 20) {// <20
																			// 认为是点击这个item
						// 当做点击：查看短信会话的详细
						touchItemIndex = (Integer) v.getTag();
						SmsThreadBean showDetailSmsThreadBean = mSmsThreadListData
								.get(touchItemIndex);
						// 将数据传递到SmsDetailActivity
						Intent smsDetailIntent = new Intent(mContext,
								SmsDetailActivity.class);
						smsDetailIntent.putExtra("ThreadId",
								showDetailSmsThreadBean.SmsThreadId);
						smsDetailIntent.putExtra("SmsCount",
								showDetailSmsThreadBean.SmsThreadCount);
						smsDetailIntent.putExtra("ContactName",
								showDetailSmsThreadBean.ContactName);
						smsDetailIntent.putExtra("PhoneNumber",
								showDetailSmsThreadBean.SmsAddress);
						mContext.startActivity(smsDetailIntent);
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
		return convertView;
	}
}
