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

//具体联系人的.在里面可以打电话和发短信,还有发邮件.但只实现了打电话.
public class ContactDetailActivity extends Activity {
	int ContactId;
	int ContactPhotoId;
	String ContactName;
	Bitmap contactPhoto;
	TextView phoneTypeTv;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.contact_detail_layout);
		// 接收来自ContactsFragment中initListener方法中传过来的数据
		ContactId = getIntent().getIntExtra("ContactId", -1);
		ContactPhotoId = getIntent().getIntExtra("ContactPhotoId", -1);
		ContactName = getIntent().getStringExtra("ContactName");
		init();
	}

	TextView contactNameTv;
	ImageView contactImageView;
	LinearLayout rootLayout;

	private void init() {
		// 找到各组件.
		rootLayout = (LinearLayout) findViewById(R.id.contact_detail_root_layout);
		contactNameTv = (TextView) findViewById(R.id.contact_detail_name);
		contactImageView = (ImageView) findViewById(R.id.contact_detail_photo);
		contactNameTv.setText(ContactName);

		// 查询联系人ID对应的电话,显示所有的联系人电话，并支持点击拨打
		List<ContactPhoneBean> contactPhoneListData = ContactsUtil
				.getContactDetailPhoneListData(ContactId);
		LayoutInflater mLayoutInflater = LayoutInflater
				.from(ContactDetailActivity.this);
		for (int i = 0; i < contactPhoneListData.size(); i++) {
			ContactPhoneBean mContactPhoneBean = contactPhoneListData.get(i);
			View phoneItemView = mLayoutInflater.inflate(
					R.layout.contact_detail_number_item_layout, null);
			//给拨打电话按钮设置监听
			ImageButton callPhoneButton = (ImageButton) phoneItemView
					.findViewById(R.id.contact_detail_call_phone);
			callPhoneButton.setTag(mContactPhoneBean.ContactPhoneNumber);
			callPhoneButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//获得电话并打通
					String number = v.getTag().toString();
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + number));
					startActivity(callIntent);
				}
			});
			//获得具体的电话
			TextView numberTv = (TextView) phoneItemView
					.findViewById(R.id.contact_detail_phone_number);
			numberTv.setText(mContactPhoneBean.ContactPhoneNumber);
			//获得具体的电话类型,比如家庭还是工作类型
			 phoneTypeTv = (TextView) phoneItemView
					.findViewById(R.id.contact_detail_number_type);
			switch (mContactPhoneBean.ContactPhoneType) {
			case 0:
				phoneTypeTv.setText("自定义电话");
				break;
			case 1:
				phoneTypeTv.setText("家庭电话");
				break;
			case 2:
				phoneTypeTv.setText("手机");
				break;
			case 3:
				phoneTypeTv.setText("工作电话");
				break;
			case 4:
				phoneTypeTv.setText("工作传真");
				break;
			case 5:
				phoneTypeTv.setText("家庭传真");
				break;
			case 6:
				phoneTypeTv.setText("主页");
				break;
			case 7:
				phoneTypeTv.setText("其他");
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
			//完成.
			rootLayout.addView(phoneItemView);
		}
	}
}
