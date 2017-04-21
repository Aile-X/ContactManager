package wandal.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import wandal.fragment.CallLogFragment;
import wandal.fragment.ContactsFragment;
import wandal.fragment.SmsFragment;
//����Ƭ��ʵ����,ֻ��̳�FragmentActivity,��ʼ��������������ť�ļ����¼�,
//��Ҫ��Ҫ����android-support-v4.jar
public class MainActivity extends FragmentActivity {

	FragmentManager mFragmentManager;
	CallLogFragment mCallLogFragment;
	SmsFragment mSmsFragment;
	ContactsFragment mContactsFragment;

	ImageButton navCallLogButton;
	ImageButton navSmsButton;
	ImageButton navContactsButton;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.main);
		//��ʼ��Ƭ�ι�����
		mFragmentManager = this.getSupportFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();

		// ��������ʱĬ�ϵĽ���
		mCallLogFragment = new CallLogFragment();
		mFragmentTransaction.replace(R.id.main_fragment_layout,
				mCallLogFragment, "CallLogFragment");
		mFragmentTransaction.commit();

		initView();
		initListener();

	}

	//��ʼ��������ť.
	private void initView() {
		navCallLogButton = (ImageButton) findViewById(R.id.main_top_bar_call_log_nav);
		navSmsButton = (ImageButton) findViewById(R.id.main_top_bar_sms_nav);
		navContactsButton = (ImageButton) findViewById(R.id.main_top_bar_contacts_nav);
	}

	//����������ť���������¼�
	private void initListener() {
		navCallLogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//����ʵ��
				if (null == mCallLogFragment) {
					mCallLogFragment = new CallLogFragment();
				}
				if(null==mFragmentManager.findFragmentByTag("CallLogFragment")){
					//ʹ��mFragmentManager�������Ƭ�ε���ʾ.
					mFragmentManager.beginTransaction().replace(
							R.id.main_fragment_layout, mCallLogFragment,
							"CallLogFragment").commit();
				}
			}
		});

		navSmsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//����ʵ��
				if (null == mSmsFragment) {
					mSmsFragment = new SmsFragment();
				}
				if(null==mFragmentManager.findFragmentByTag("SmsFragment")){
					//ʹ��mFragmentManager�������Ƭ�ε���ʾ.
					mFragmentManager.beginTransaction().replace(
							R.id.main_fragment_layout, mSmsFragment, "SmsFragment")
							.commit();
				}
			}
		});

		navContactsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//����ʵ��
				if (null == mContactsFragment) {
					mContactsFragment = new ContactsFragment();
				}
				if(null==mFragmentManager.findFragmentByTag("ContactsFragment")){
					//ʹ��mFragmentManager�������Ƭ�ε���ʾ.
					mFragmentManager.beginTransaction().replace(
							R.id.main_fragment_layout, mContactsFragment,
							"ContactsFragment").commit();
				}
			}
		});
	}

}
