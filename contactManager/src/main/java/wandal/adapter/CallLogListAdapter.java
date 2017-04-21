package wandal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import wandal.activity.R;
import wandal.model.Entity.CallLogBean;

//看看这个适配器类中可以添加监听器和复杂操作.
public class CallLogListAdapter extends BaseAdapter {

	Context mContext;
	List<CallLogBean> mCallLogListData;// 数据
	int mItemLayoutResId;
	LayoutInflater mLayoutInflater;// 充气机

	// 构造方法.
	public CallLogListAdapter(Context context,
			List<CallLogBean> callLogListData, int itemLayoutResId) {
		mContext = context;
		mCallLogListData = callLogListData;
		mItemLayoutResId = itemLayoutResId;
		mLayoutInflater = LayoutInflater.from(context);
	}

	// 用于数据的更新
	public void changeData(List<CallLogBean> listData) {
		mCallLogListData = listData;
	}

	@Override
	public int getCount() {
		return mCallLogListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mCallLogListData.get(position);
	}

	// 注意这次的变化,强烈推荐.
	public long getItemId(int position) {
		return mCallLogListData.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// 注意是充气是mItemLayoutResId,那就具体是在CallLogFragment中的onCreateView中充气的.
			convertView = mLayoutInflater.inflate(mItemLayoutResId, null);
		}
		CallLogBean nowCallLogBean = mCallLogListData.get(position);
		ImageView contactPhotoIv = (ImageView) convertView
				.findViewById(R.id.call_log_contact_photo);
		TextView nameTv = (TextView) convertView
				.findViewById(R.id.call_log_contact_name);
		// 判断对应联系人有无姓名和头像.有就加载.
		if (nowCallLogBean.ContactId > 0) {
			nameTv.setText(nowCallLogBean.ContactName);
			if (nowCallLogBean.ContactPhotoId > 0) {
				contactPhotoIv
						.setImageBitmap(nowCallLogBean.ContactPhotoBitmap);
			} else {
				// 设置默认的
				contactPhotoIv
						.setImageResource(R.drawable.default_contacts_photo);
			}
		} else {
			// 没有对应联系人姓名和头像就设置这样默认.
			nameTv.setText(nowCallLogBean.CallLogNumber);
			contactPhotoIv.setImageResource(R.drawable.default_contacts_photo);
		}

//		TextView numberTv = (TextView) convertView
//				.findViewById(R.id.call_log_phone_number);
//		numberTv.setText(nowCallLogBean.CallLogNumber);

		TextView timeTv = (TextView) convertView
				.findViewById(R.id.call_log_time);
		timeTv.setText(nowCallLogBean.CallLogDateFormat);

		TextView countTypeTv = (TextView) convertView
				.findViewById(R.id.call_log_count_type);
		countTypeTv.setText("" + nowCallLogBean.CallLogCount);

		countTypeTv.setTag(nowCallLogBean);
		countTypeTv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CallLogBean nowCallLogBean = (CallLogBean) v.getTag();
				String count = String.valueOf(nowCallLogBean.CallLogCount);
				Toast.makeText(
						mContext,
						
								"姓名:"+ nowCallLogBean.CallLogName + "\n" 
								+"号码:"+ nowCallLogBean.CallLogNumber+"\n"
								+"通话次数:"+ count+"次", Toast.LENGTH_SHORT).show();
			}
		});
		// 这里的1-3是由于CallLogDataUtils中的strWhere的缘故
		switch (nowCallLogBean.CallLogType) {
		case 1:
			countTypeTv.setBackgroundResource(R.drawable.icon_log_incoming);
			break;
		case 2:
			countTypeTv.setBackgroundResource(R.drawable.icon_log_outgoing);
			break;
		case 3:
			countTypeTv.setBackgroundResource(R.drawable.icon_log_missed);
			break;
		}

		// 给视图中触摸的具体的视图设置标识
//		convertView.setTag(position);
//		convertView.setOnTouchListener(new OnTouchListener() {
//			float downXpoint = -1;
//			int touchItemIndex;// 对应我们选择的Item
//
//			public boolean onTouch(View v, MotionEvent event) {
//				touchItemIndex = (java.lang.Integer) v.getTag();// 取得所触摸的组件的索引.
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					downXpoint = event.getX();// 刚触摸时的X坐标
//					break;
//				case MotionEvent.ACTION_UP:
//					// 若划动长度大于150,就产生动画并删除
//					if (Math.abs(event.getX() - downXpoint) > 150) {
//
//						Animation delAnim = null;
//
//						if (event.getX() - downXpoint > 0) {
//							// 加载xml布局中动画,获得动画对象delAnim
//							delAnim = AnimationUtils.loadAnimation(mContext,
//									R.anim.call_log_item_del_right);
//						} else {
//							delAnim = AnimationUtils.loadAnimation(mContext,
//									R.anim.call_log_item_del_left);
//						}
//						// 设置delAnim动画监听器
//						delAnim.setAnimationListener(new AnimationListener() {
//
//							public void onAnimationStart(Animation animation) {
//							}
//
//							public void onAnimationRepeat(Animation animation) {
//							}
//
//							public void onAnimationEnd(Animation animation) {
//								Toast.makeText(mContext, "删除成功",
//										Toast.LENGTH_LONG).show();
//								ContentResolver mContentResolver = TApplication.MY_SELF
//										.getContentResolver();
//								// 取得要删除的对象.
//								CallLogBean mCallLogBean = mCallLogListData
//										.get(touchItemIndex);
//								// 从系统删除通话记录数据
//								mContentResolver.delete(
//										CallLog.Calls.CONTENT_URI, "number = "
//												+ mCallLogBean.CallLogNumber,
//										null);
//								// 删除List集合内的数据
//								TApplication.CallLogListData
//										.remove(mCallLogBean);
//								// 删除之后更新TApplication数据和界面
//								CallLogListAdapter.this
//										.changeData(TApplication.CallLogListData);
//								CallLogListAdapter.this.notifyDataSetChanged();
//
//							}
//						});
//						v.startAnimation(delAnim);// 启动动画
//					}else if (Math.abs(event.getX() - downXpoint) < 20) {
//						// 若触摸划动的长度很短为20,就是拨打电话
//						Toast.makeText(mContext, "拨打电话", Toast.LENGTH_LONG)
//								.show();
//						// 取得具体操作对象
//						CallLogBean mCallLogBean = mCallLogListData
//								.get(touchItemIndex);
//						// 打电话的死代码
//						Intent callIntent = new Intent(Intent.ACTION_CALL);
//						Uri callNumberUri = Uri.parse("tel:"
//								+ mCallLogBean.CallLogNumber);
//						callIntent.setData(callNumberUri);
//						mContext.startActivity(callIntent);
//					}
//					break;
//				case MotionEvent.ACTION_MOVE:
//					break;
//				case MotionEvent.ACTION_CANCEL:
//					break;
//				default:
//					break;
//				}
//				return true;
//			}
//		});
		return convertView;
	}
}
