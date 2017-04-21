package wandal.model.thread;

import android.content.Intent;
import android.util.Log;

import wandal.Application.TApplication;
import wandal.model.Utils.ContactsUtil;


public class ContactsThread extends Thread {

	public ContactsThread() {
	}

	public void run() {
		Log.i("Contacts_Info","ContactsThread");
		
		super.run();
		TApplication.ContactsListData = ContactsUtil.getContactListData();
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.CONTACTS_DATA_UP_ACTION));

		ContactsUtil.setContactPhotoListData(TApplication.ContactsListData);
		TApplication.MY_SELF.sendStickyBroadcast(new Intent(
				TApplication.CONTACTS_DATA_UP_ACTION));
	}
}
