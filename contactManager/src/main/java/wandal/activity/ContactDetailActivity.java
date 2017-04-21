package wandal.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wandal.model.Entity.ContactPhoneBean;
import wandal.model.Utils.ContactsUtil;

//������ϵ�˵�.��������Դ�绰�ͷ�����,���з��ʼ�.��ֻʵ���˴�绰.
public class ContactDetailActivity extends Activity {
	int ContactId;
	int ContactPhotoId;
	String ContactName;
	Bitmap contactPhoto;
	TextView phoneTypeTv;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.contact_detail_layout);
		// ��������ContactsFragment��initListener�����д�����������
		ContactId = getIntent().getIntExtra("ContactId", -1);
		ContactPhotoId = getIntent().getIntExtra("ContactPhotoId", -1);
		ContactName = getIntent().getStringExtra("ContactName");
		init();
	}

	TextView contactNameTv;
	ImageView contactImageView;
	LinearLayout rootLayout;

	private void init() {
		// �ҵ������.
		rootLayout = (LinearLayout) findViewById(R.id.contact_detail_root_layout);
		contactNameTv = (TextView) findViewById(R.id.contact_detail_name);
		contactImageView = (ImageView) findViewById(R.id.contact_detail_photo);
		contactNameTv.setText(ContactName);

		// ��ѯ��ϵ��ID��Ӧ�ĵ绰,��ʾ���е���ϵ�˵绰����֧�ֵ������
		List<ContactPhoneBean> contactPhoneListData = ContactsUtil
				.getContactDetailPhoneListData(ContactId);
		LayoutInflater mLayoutInflater = LayoutInflater
				.from(ContactDetailActivity.this);
		for (int i = 0; i < contactPhoneListData.size(); i++) {
			ContactPhoneBean mContactPhoneBean = contactPhoneListData.get(i);
			View phoneItemView = mLayoutInflater.inflate(
					R.layout.contact_detail_number_item_layout, null);
			//������绰��ť���ü���
			ImageButton callPhoneButton = (ImageButton) phoneItemView
					.findViewById(R.id.contact_detail_call_phone);
			callPhoneButton.setTag(mContactPhoneBean.ContactPhoneNumber);
			callPhoneButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//��õ绰����ͨ
					String number = v.getTag().toString();
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + number));
					startActivity(callIntent);
				}
			});
			//��þ���ĵ绰
			TextView numberTv = (TextView) phoneItemView
					.findViewById(R.id.contact_detail_phone_number);
			numberTv.setText(mContactPhoneBean.ContactPhoneNumber);
			//��þ���ĵ绰����,�����ͥ���ǹ�������
			 phoneTypeTv = (TextView) phoneItemView
					.findViewById(R.id.contact_detail_number_type);
			switch (mContactPhoneBean.ContactPhoneType) {
			case 0:
				phoneTypeTv.setText("�Զ���绰");
				break;
			case 1:
				phoneTypeTv.setText("��ͥ�绰");
				break;
			case 2:
				phoneTypeTv.setText("�ֻ�");
				break;
			case 3:
				phoneTypeTv.setText("�����绰");
				break;
			case 4:
				phoneTypeTv.setText("��������");
				break;
			case 5:
				phoneTypeTv.setText("��ͥ����");
				break;
			case 6:
				phoneTypeTv.setText("��ҳ");
				break;
			case 7:
				phoneTypeTv.setText("����");
				break;

			default:
				break;
			}
			ImageButton sendSmsButton=(ImageButton) phoneItemView.findViewById(R.id.contact_detail_send_sms);
			sendSmsButton.setTag(mContactPhoneBean);
			sendSmsButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ContactPhoneBean mContactPhoneBean=(ContactPhoneBean) v.getTag();
						Intent intent=new Intent(ContactDetailActivity.this,SmsDetailActivity.class);
						intent.putExtra("PhoneNumber", mContactPhoneBean.ContactPhoneNumber);
						startActivity(intent);
				}
			});
			//���.
			rootLayout.addView(phoneItemView);
		}
	}
}
