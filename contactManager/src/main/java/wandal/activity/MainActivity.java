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
//这是片段实现体,只需继承FragmentActivity,初始化并设置三个按钮的监听事件,
//主要需要导包android-support-v4.jar
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
		//初始化片段管理器
		mFragmentManager = this.getSupportFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();

		// 用于启动时默认的界面
		mCallLogFragment = new CallLogFragment();
		mFragmentTransaction.replace(R.id.main_fragment_layout,
				mCallLogFragment, "CallLogFragment");
		mFragmentTransaction.commit();

		initView();
		initListener();

	}

	//初始化三个按钮.
	private void initView() {
		navCallLogButton = (ImageButton) findViewById(R.id.main_top_bar_call_log_nav);
		navSmsButton = (ImageButton) findViewById(R.id.main_top_bar_sms_nav);
		navContactsButton = (ImageButton) findViewById(R.id.main_top_bar_contacts_nav);
	}

	//设置三个按钮监听单击事件
	private void initListener() {
		navCallLogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//创建实例
				if (null == mCallLogFragment) {
					mCallLogFragment = new CallLogFragment();
				}
				if(null==mFragmentManager.findFragmentByTag("CallLogFragment")){
					//使用mFragmentManager完成所需片段的显示.
					mFragmentManager.beginTransaction().replace(
							R.id.main_fragment_layout, mCallLogFragment,
							"CallLogFragment").commit();
				}
			}
		});

		navSmsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//创建实例
				if (null == mSmsFragment) {
					mSmsFragment = new SmsFragment();
				}
				if(null==mFragmentManager.findFragmentByTag("SmsFragment")){
					//使用mFragmentManager完成所需片段的显示.
					mFragmentManager.beginTransaction().replace(
							R.id.main_fragment_layout, mSmsFragment, "SmsFragment")
							.commit();
				}
			}
		});

		navContactsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//创建实例
				if (null == mContactsFragment) {
					mContactsFragment = new ContactsFragment();
				}
				if(null==mFragmentManager.findFragmentByTag("ContactsFragment")){
					//使用mFragmentManager完成所需片段的显示.
					mFragmentManager.beginTransaction().replace(
							R.id.main_fragment_layout, mContactsFragment,
							"ContactsFragment").commit();
				}
			}
		});
	}

}
