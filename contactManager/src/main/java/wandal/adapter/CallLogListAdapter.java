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

//����������������п�����Ӽ������͸��Ӳ���.
public class CallLogListAdapter extends BaseAdapter {

	Context mContext;
	List<CallLogBean> mCallLogListData;// ����
	int mItemLayoutResId;
	LayoutInflater mLayoutInflater;// ������

	// ���췽��.
	public CallLogListAdapter(Context context,
			List<CallLogBean> callLogListData, int itemLayoutResId) {
		mContext = context;
		mCallLogListData = callLogListData;
		mItemLayoutResId = itemLayoutResId;
		mLayoutInflater = LayoutInflater.from(context);
	}

	// �������ݵĸ���
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

	// ע����εı仯,ǿ���Ƽ�.
	public long getItemId(int position) {
		return mCallLogListData.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// ע���ǳ�����mItemLayoutResId,�Ǿ;�������CallLogFragment�е�onCreateView�г�����.
			convertView = mLayoutInflater.inflate(mItemLayoutResId, null);
		}
		CallLogBean nowCallLogBean = mCallLogListData.get(position);
		ImageView contactPhotoIv = (ImageView) convertView
				.findViewById(R.id.call_log_contact_photo);
		TextView nameTv = (TextView) convertView
				.findViewById(R.id.call_log_contact_name);
		// �ж϶�Ӧ��ϵ������������ͷ��.�оͼ���.
		if (nowCallLogBean.ContactId > 0) {
			nameTv.setText(nowCallLogBean.ContactName);
			if (nowCallLogBean.ContactPhotoId > 0) {
				contactPhotoIv
						.setImageBitmap(nowCallLogBean.ContactPhotoBitmap);
			} else {
				// ����Ĭ�ϵ�
				contactPhotoIv
						.setImageResource(R.drawable.default_contacts_photo);
			}
		} else {
			// û�ж�Ӧ��ϵ��������ͷ�����������Ĭ��.
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
						
								"����:"+ nowCallLogBean.CallLogName + "\n" 
								+"����:"+ nowCallLogBean.CallLogNumber+"\n"
								+"ͨ������:"+ count+"��", Toast.LENGTH_SHORT).show();
			}
		});
		// �����1-3������CallLogDataUtils�е�strWhere��Ե��
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

		// ����ͼ�д����ľ������ͼ���ñ�ʶ
//		convertView.setTag(position);
//		convertView.setOnTouchListener(new OnTouchListener() {
//			float downXpoint = -1;
//			int touchItemIndex;// ��Ӧ����ѡ���Item
//
//			public boolean onTouch(View v, MotionEvent event) {
//				touchItemIndex = (java.lang.Integer) v.getTag();// ȡ�������������������.
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					downXpoint = event.getX();// �մ���ʱ��X����
//					break;
//				case MotionEvent.ACTION_UP:
//					// ���������ȴ���150,�Ͳ���������ɾ��
//					if (Math.abs(event.getX() - downXpoint) > 150) {
//
//						Animation delAnim = null;
//
//						if (event.getX() - downXpoint > 0) {
//							// ����xml�����ж���,��ö�������delAnim
//							delAnim = AnimationUtils.loadAnimation(mContext,
//									R.anim.call_log_item_del_right);
//						} else {
//							delAnim = AnimationUtils.loadAnimation(mContext,
//									R.anim.call_log_item_del_left);
//						}
//						// ����delAnim����������
//						delAnim.setAnimationListener(new AnimationListener() {
//
//							public void onAnimationStart(Animation animation) {
//							}
//
//							public void onAnimationRepeat(Animation animation) {
//							}
//
//							public void onAnimationEnd(Animation animation) {
//								Toast.makeText(mContext, "ɾ���ɹ�",
//										Toast.LENGTH_LONG).show();
//								ContentResolver mContentResolver = TApplication.MY_SELF
//										.getContentResolver();
//								// ȡ��Ҫɾ���Ķ���.
//								CallLogBean mCallLogBean = mCallLogListData
//										.get(touchItemIndex);
//								// ��ϵͳɾ��ͨ����¼����
//								mContentResolver.delete(
//										CallLog.Calls.CONTENT_URI, "number = "
//												+ mCallLogBean.CallLogNumber,
//										null);
//								// ɾ��List�����ڵ�����
//								TApplication.CallLogListData
//										.remove(mCallLogBean);
//								// ɾ��֮�����TApplication���ݺͽ���
//								CallLogListAdapter.this
//										.changeData(TApplication.CallLogListData);
//								CallLogListAdapter.this.notifyDataSetChanged();
//
//							}
//						});
//						v.startAnimation(delAnim);// ��������
//					}else if (Math.abs(event.getX() - downXpoint) < 20) {
//						// �����������ĳ��Ⱥܶ�Ϊ20,���ǲ���绰
//						Toast.makeText(mContext, "����绰", Toast.LENGTH_LONG)
//								.show();
//						// ȡ�þ����������
//						CallLogBean mCallLogBean = mCallLogListData
//								.get(touchItemIndex);
//						// ��绰��������
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
