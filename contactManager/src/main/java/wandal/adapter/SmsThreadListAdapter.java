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

//���Ž������������
public class SmsThreadListAdapter extends BaseAdapter {

	Context mContext;
	List<SmsThreadBean> mSmsThreadListData;// ����
	int mItemLayoutResId;
	LayoutInflater mLayoutInflater;// ������

	// ���췽��
	public SmsThreadListAdapter(Context context,
			List<SmsThreadBean> smsThreadListData, int itemLayoutResId) {
		mContext = context;
		mSmsThreadListData = smsThreadListData;
		mItemLayoutResId = itemLayoutResId;
		mLayoutInflater = LayoutInflater.from(context);

	}

	// �������ݵĸ���
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

	// ע����εı仯,ǿ���Ƽ�.
	public long getItemId(int position) {
		return mSmsThreadListData.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// ע���ǳ�����mItemLayoutResId,�Ǿ;�������CallLogFragment�е�onCreateView�г�����.
			convertView = mLayoutInflater.inflate(mItemLayoutResId, null);
		}
		// ��Ӷ���Thread����
		TextView addressTv = (TextView) convertView
				.findViewById(R.id.sms_thread_list_item_address);
		TextView bodyTv = (TextView) convertView
				.findViewById(R.id.sms_thread_list_item_body);
		TextView timeTv = (TextView) convertView
				.findViewById(R.id.sms_thread_list_item_time);
		ImageView contactPhotoIv = (ImageView) convertView
				.findViewById(R.id.sms_thread_list_item_contact_photo);

		SmsThreadBean nowSmsThreadBean = mSmsThreadListData.get(position);

		// �ж��Ƿ�����ϵ�˶�Ӧ��Ϣ��������ʾ��ϵ��������û������ʾ�绰����
		if (nowSmsThreadBean.ContactId > 0) {
			addressTv.setText(nowSmsThreadBean.ContactName + "  ("
					+ nowSmsThreadBean.SmsThreadCount + ")");
		} else {
			addressTv.setText(nowSmsThreadBean.SmsAddress + "  ("
					+ nowSmsThreadBean.SmsThreadCount + ")");
		}

		// ��ͼƬ����ʾ.ûͼƬĬ��
		if (nowSmsThreadBean.ContactPhotoId > 0) {
			contactPhotoIv.setImageBitmap(nowSmsThreadBean.contactPhoto);
		} else {
			contactPhotoIv.setImageResource(R.drawable.default_contacts_photo);
		}

		bodyTv.setText(nowSmsThreadBean.SmsBody);
		timeTv.setText(nowSmsThreadBean.SmsDateFormat);

		// ����ͼ�д����ľ������ͼ���ñ�ʶ
		convertView.setTag(position);
		convertView.setOnTouchListener(new OnTouchListener() {
			float downXPoint = -1;
			int touchItemIndex = -1; // ��Ӧ����ѡ���Item

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// ��¼������item�ϰ��µ�λ��
				case MotionEvent.ACTION_DOWN:
					downXPoint = event.getX();// �մ���ʱ��X����
					break;
				case MotionEvent.ACTION_UP:
					// �����µ�λ�ú�̧���λ�ý��жԱ�,>150��Ϊ��Ҫɾ����Ӧthread_id�����ж���
					if (Math.abs(event.getX() - downXPoint) > 150) {
						// ȡ�������������������
						touchItemIndex = (Integer) v.getTag();
						// ȡ��Ӧɾ�����ż��ϵ�threadID,�����ɾ����ǰ��Ĳ�ͬ
						SmsThreadBean delSmsThreadBean = mSmsThreadListData
								.remove(touchItemIndex);
						int delCount = mContext.getContentResolver().delete(
								Uri.parse("content://sms"),
								"thread_id = " + delSmsThreadBean.SmsThreadId,
								null);
						Toast.makeText(mContext, "ɾ��" + delCount + "�����ż�¼",
								Toast.LENGTH_LONG).show();
						mSmsThreadListData
										.remove(delSmsThreadBean);
						// ɾ��֮����½���
						SmsThreadListAdapter.this.changeData(mSmsThreadListData);
						SmsThreadListAdapter.this.notifyDataSetChanged();
					} else if (Math.abs(event.getX() - downXPoint) < 20) {// <20
																			// ��Ϊ�ǵ�����item
						// ����������鿴���ŻỰ����ϸ
						touchItemIndex = (Integer) v.getTag();
						SmsThreadBean showDetailSmsThreadBean = mSmsThreadListData
								.get(touchItemIndex);
						// �����ݴ��ݵ�SmsDetailActivity
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
