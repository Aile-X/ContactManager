package wandal.dao;

import android.content.Context;

import java.util.ArrayList;

public class BlackListBiz {
	private BlackListDao dao;

	public BlackListBiz(Context context) {
		dao = new BlackListDao(context);}

	/**
	 * 向黑名单表中添加新号码
	 * 
	 * @param number
	 * @return
	 */
	public long addNumber(String number) {
		return dao.addNumber(number);
	}

	/**
	 * 从黑名单中移除指定号码
	 * 
	 * @param number
	 * @return
	 */
	public int removeNumber(String number) {
		return dao.removeNumber(number);
	}

	/**
	 * 查询黑名单中所有号码
	 * 
	 * @return
	 */
	public ArrayList<String> getNumbers() {
		return dao.getNumbers();
	}

	/**
	 * 判断指定号码是否存在于黑名单内
	 * 
	 * @param number
	 * @return
	 */
	public boolean exists(String number) {
		return dao.exists(number);
	}
}
