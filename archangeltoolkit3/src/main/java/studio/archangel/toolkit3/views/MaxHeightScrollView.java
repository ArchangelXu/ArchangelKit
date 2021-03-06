package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import studio.archangel.toolkit3.R;
import uk.co.androidalliance.edgeeffectoverride.EdgeEffectScrollView;

/**
 * Created by xumingke on 2017/3/16.
 */

public class MaxHeightScrollView extends EdgeEffectScrollView {

	private int maxHeight;
	private final int defaultHeight = 200;

	public MaxHeightScrollView(Context context) {
		super(context);
	}

	public MaxHeightScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init(context, attrs);
		}
	}

	public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!isInEditMode()) {
			init(context, attrs);
		}
	}

//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//		super(context, attrs, defStyleAttr, defStyleRes);
//		if (!isInEditMode()) {
//			init(context, attrs);
//		}
//	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
			//200 is a defualt value
			maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, defaultHeight);

			styledAttrs.recycle();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}