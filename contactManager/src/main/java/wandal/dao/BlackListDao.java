package wandal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class BlackListDao {
	private DBOpenHelper helper;

	public BlackListDao(Context context) {
		helper = new DBOpenHelper(context);
	}

	/**
	 * ���������������º���
	 * 
	 * @param number
	 * @return
	 */
	public long addNumber(String number) {
		long rowId = -1;
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		rowId = db.insert(DBOpenHelper.TBL_NAME, null, values);
		db.close();
		return rowId;
	}

	/**
	 * �Ӻ��������Ƴ�ָ������
	 * 
	 * @param number
	 * @return
	 */
	public int removeNumber(String number) {
		int count = 0;
		SQLiteDatabase db = helper.getWritableDatabase();
		count = db.delete(DBOpenHelper.TBL_NAME, "number=?",
				new String[] { number });
		db.close();
		return count;
	}

	/**
	 * ��ѯ�����������к���
	 * 
	 * @return
	 */
	public ArrayList<String> getNumbers() {
		ArrayList<String> numbers = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select number from " + DBOpenHelper.TBL_NAME,
				null);
		if (c != null) {
			numbers = new ArrayList<String>();
			while (c.moveToNext()) {
				numbers.add(c.getString(0));
			}
			c.close();
		}
		db.close();
		return numbers;
	}

	/**
	 * �ж�ָ�������Ƿ�����ں�������
	 * 
	 * @param number
	 * @return
	 */
	public boolean exists(String number) {
		boolean isExists = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from " + DBOpenHelper.TBL_NAME
				+ " where number=?", new String[] { number });
		if (c != null && c.moveToFirst()) {
			isExists = true;
			c.close();
		}
		db.close();
		return isExists;
	}

}
