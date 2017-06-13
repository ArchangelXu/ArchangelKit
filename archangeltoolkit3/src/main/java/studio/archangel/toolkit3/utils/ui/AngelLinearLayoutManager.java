package studio.archangel.toolkit3.utils.ui;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import studio.archangel.toolkit3.utils.UIUtil;

/**
 * Created by xmk on 2017/5/31.
 */
public class AngelLinearLayoutManager extends LinearLayoutManager {
	LinearSmoothScroller fast_scroller;
	LinearSmoothScroller slow_scroller;

	public AngelLinearLayoutManager(Context context) {
		super(context);
		init(context);
	}

	public AngelLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
		init(context);
	}

	public AngelLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	void init(final Context context) {
		fast_scroller = new LinearSmoothScroller(context) {
			@Override
			public PointF computeScrollVectorForPosition(int targetPosition) {
				return AngelLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
			}

			@Override
			protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//					return super.calculateSpeedPerPixel(displayMetrics);
				return (float) (1000.0 / (UIUtil.getPX(context, 48) * 40));
			}

		};
		slow_scroller = new LinearSmoothScroller(context) {
			@Override
			public PointF computeScrollVectorForPosition(int targetPosition) {
				return AngelLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
			}

			@Override
			protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
				return (float) (1000.0 / (UIUtil.getPX(context, 48) * 10));
			}
		};
	}

	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position, boolean slow) {
		if (!slow) {
			fast_scroller.setTargetPosition(position);
			startSmoothScroll(fast_scroller);
		} else {
			slow_scroller.setTargetPosition(position);
			startSmoothScroll(slow_scroller);
		}
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		smoothScrollToPosition(recyclerView, state, position, true);
	}
}
