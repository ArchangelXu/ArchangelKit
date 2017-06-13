package studio.archangel.toolkit3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public abstract class AngelPageIndicatorUnit2 extends FrameLayout {
	protected boolean is_selected = true;

	public AngelPageIndicatorUnit2(Context context) {
		super(context);
		initLayoutParameters(context);
	}

	public AngelPageIndicatorUnit2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayoutParameters(context);
	}

	void initLayoutParameters(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getSize(), getSize());
		params.weight = 1;
		params.rightMargin = getHorizontalSpacing();
		setLayoutParams(params);

	}

	public abstract int getSize();

	public abstract int getHorizontalSpacing();

	public void setSelected(boolean selected) {
		setSelected(selected, true);
	}

	public abstract void setSelected(boolean selected, boolean need_animation);
}
