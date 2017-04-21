package wandal.dao;

import android.content.Context;

import java.util.ArrayList;

public class BlackListBiz {
	private BlackListDao dao;

	public BlackListBiz(Context context) {
		dao = new BlackListDao(context);}

	/**
	 * ���������������º���
	 * 
	 * @param number
	 * @return
	 */
	public long addNumber(String number) {
		return dao.addNumber(number);
	}

	/**
	 * �Ӻ��������Ƴ�ָ������
	 * 
	 * @param number
	 * @return
	 */
	public int removeNumber(String number) {
		return dao.removeNumber(number);
	}

	/**
	 * ��ѯ�����������к���
	 * 
	 * @return
	 */
	public ArrayList<String> getNumbers() {
		return dao.getNumbers();
	}

	/**
	 * �ж�ָ�������Ƿ�����ں�������
	 * 
	 * @param number
	 * @return
	 */
	public boolean exists(String number) {
		return dao.exists(number);
	}
}
