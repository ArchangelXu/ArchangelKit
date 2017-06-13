package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


/**
 * Created by Administrator on 2015/12/08.
 */


public class AngelRedDot extends TextView {
	public static final RedDotStyle[] reddot_style_array = {RedDotStyle.text, RedDotStyle.simple};

	public enum RedDotStyle {
		text, simple,
	}

	static final int COUNT_MAX = 9;
	RedDotStyle style = RedDotStyle.text;
	private int count = 0;

	public AngelRedDot(Context context) {
		this(context, null);
	}

	public AngelRedDot(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelRedDot(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AngelRedDot);
		int index = a.getInt(R.styleable.AngelRedDot_ard_style, 0);
		setStyle(reddot_style_array[index]);
		Drawable drawable = a.getDrawable(R.styleable.AngelRedDot_ard_oval_drawable);
		if (drawable == null) {
			drawable = getResources().getDrawable(R.drawable.shape_oval_red);
		}
		UIUtil.setBackgroundDrawableWithOriginPadding(this, drawable);
		int color = a.getColor(R.styleable.AngelRedDot_ard_text_color, getResources().getColor(R.color.text_white));
		setTextColor(color);
		setTextSize(10);
		a.recycle();
	}

	public void setStyle(RedDotStyle s) {
		style = s;
		ViewGroup.LayoutParams params = getLayoutParams();
		if (params != null) {
			params.width = UIUtil.getPX(getContext(), style == RedDotStyle.text ? 16 : 8);
			params.height = UIUtil.getPX(getContext(), style == RedDotStyle.text ? 16 : 8);
			requestLayout();
		}
	}

	public void setCount(int c) {
		count = c;
		refreshText();
	}

	public void addCount(int c) {
		count += c;
		refreshText();
	}

	void refreshText() {
		if (style == RedDotStyle.text) {
			String text = count + "";
			if (count > COUNT_MAX) {
				text = COUNT_MAX + "+";
			}
			setDotText(text);
		}
		setVisibility(count > 0 ? View.VISIBLE : View.GONE);
	}

	public void setDotText(String s) {
		super.setText(s);

	}
}
