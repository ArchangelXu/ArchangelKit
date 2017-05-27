package studio.archangel.toolkit3.views;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import studio.archangel.toolkit3.adapters.CommonRecyclerAdapter;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.UIUtil;

/**
 * Created by xmk on 16/5/14.
 */
public class AngelRecyclerView extends RecyclerView {

	AngelLinearLayoutManager layout_manager;
	CommonRecyclerAdapter adapter;

	public AngelRecyclerView(Context context) {
		super(context);
		init();
	}

	public AngelRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AngelRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		layout_manager = new AngelLinearLayoutManager(getContext());
		setLayoutManager(layout_manager);
		SlideInUpAnimator animator = new SlideInUpAnimator(new DecelerateInterpolator());
		animator.setAddDuration(300);
		animator.setChangeDuration(150);
		animator.setMoveDuration(200);
		animator.setRemoveDuration(150);
		setItemAnimator(animator);
	}

	@Override
	public void setLayoutManager(LayoutManager layout) {
		if (layout instanceof AngelLinearLayoutManager) {
			super.setLayoutManager(layout);
		} else {
			Logger.err("Your LayoutManager must extends AngelLinearLayoutManager.");
		}
	}

	public void setAdapter(CommonRecyclerAdapter adapter) {
		super.setAdapter(adapter);
		this.adapter = adapter;
	}

	@Override
	public void onChildDetachedFromWindow(View child) {
		super.onChildDetachedFromWindow(child);
		if (adapter != null) {
//TODO
		}
	}

	@Override
	public void smoothScrollToPosition(int position) {
		smoothScrollToPosition(position, false);
	}

	public void smoothScrollToPosition(int position, boolean slow) {
		if (isLayoutFrozen()) {
			return;
		}
		if (layout_manager == null) {
			Logger.err("Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
			return;
		}
		layout_manager.smoothScrollToPosition(this, null, position, slow);
	}

	public class AngelLinearLayoutManager extends LinearLayoutManager {
		LinearSmoothScroller fast_scroller;
		LinearSmoothScroller slow_scroller;

		public AngelLinearLayoutManager(Context context) {
			super(context);
			init();
		}

		public AngelLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
			super(context, orientation, reverseLayout);
			init();
		}

		public AngelLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
			super(context, attrs, defStyleAttr, defStyleRes);
			init();
		}

		void init() {
			fast_scroller = new LinearSmoothScroller(getContext()) {
				@Override
				public PointF computeScrollVectorForPosition(int targetPosition) {
					return AngelLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
				}

				@Override
				protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//					return super.calculateSpeedPerPixel(displayMetrics);
					return (float) (1000.0 / (UIUtil.getPX(getContext(), 48) * 40));
				}

			};
			slow_scroller = new LinearSmoothScroller(getContext()) {
				@Override
				public PointF computeScrollVectorForPosition(int targetPosition) {
					return AngelLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
				}

				@Override
				protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
					return (float) (1000.0 / (UIUtil.getPX(getContext(), 48) * 10));
				}
			};
		}

		public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position, boolean slow) {
			if (!slow) {
				fast_scroller.setTargetPosition(position);
				startSmoothScroll(fast_scroller);
			} else {
				slow_scroller.setTargetPosition(position);
				startSmoothScroll(slow_scroller);
			}
		}

		@Override
		public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
			smoothScrollToPosition(recyclerView, state, position, true);
		}
	}
}
