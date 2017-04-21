package wandal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
//����ʵ�ֹ�����,��java�����дxml����
public class ContactIndexToolBarLayout extends LinearLayout {
	private String[] arrChar = new String[] { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z" };

	private float indexCharHeight = 0.0f;

	private int oldIndex = -1;

	public ContactIndexToolBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		for (int i = 0; i < arrChar.length; i++) {

			TextView IndexChar = new TextView(context);

			LayoutParams textLP = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			textLP.weight = 1.0f;

			IndexChar.setLayoutParams(textLP);
			IndexChar.setText(arrChar[i]);
			IndexChar.setTextColor(Color.BLACK);
			IndexChar.setGravity(Gravity.CENTER);

			this.addView(IndexChar);

		}

		// 1����XML-������
		// 2����һ�β������ж���ĸ߶�
		// 3���ڶ��β������ж���ĸ߶�
		// 4��ȷ�����ո߶Ⱥ󣬵��ÿؼ��������ƽ���

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float pointY = event.getY();
		int index = (int) (pointY / indexCharHeight);
		if (index > 25) {
			index = 25;
		}else if(index < 0){
			index = 0;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (oldIndex != index) {
				oldIndex = index;
				callBackChangeListener.onChange(arrChar[index]);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldIndex != index) {
				oldIndex = index;
				callBackChangeListener.onChange(arrChar[index]);
			}
			break;
		case MotionEvent.ACTION_UP:

			break;

		default:
			break;
		}

		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int layoutHeight = this.getHeight();

		indexCharHeight = layoutHeight / 26;

		for (int i = 0; i < this.getChildCount(); i++) {
			TextView nowTv = (TextView) this.getChildAt(i);
			nowTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, layoutHeight / 30);
		}

		super.onLayout(changed, l, t, r, b);

	}
	
	private OnIndexChangeListener callBackChangeListener ;

	public void setOnIndexChangeListener(OnIndexChangeListener listener){
		
		callBackChangeListener = listener;
		
	}
	
	public interface OnIndexChangeListener{
		
		public void onChange(String changeChar);
		
	}
}
