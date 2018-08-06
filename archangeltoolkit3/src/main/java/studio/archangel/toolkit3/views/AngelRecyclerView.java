package studio.archangel.toolkit3.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import studio.archangel.toolkit3.adapters.CommonRecyclerAdapter;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.ui.AngelLinearLayoutManager;

/**
 * Created by xumingke on 16/5/14.
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

}
