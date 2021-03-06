package studio.archangel.toolkit3.utils.ui;

import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by xumingke on 2017/6/29.
 */

public class AngelFadeInAnimator extends FadeInAnimator {
	public AngelFadeInAnimator() {
		super();
	}

	public AngelFadeInAnimator(Interpolator interpolator) {
		super(interpolator);
	}


	@Override
	public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
	                             int fromX, int fromY, int toX, int toY) {
//		if (oldHolder != null) {
//			dispatchChangeFinished(oldHolder, true);
//		} else if ((newHolder != null) && (newHolder != oldHolder)) {
//			dispatchChangeFinished(newHolder, false);
//		}
//		return false;
		if (getSupportsChangeAnimations()) {
			return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
		} else {
			if (oldHolder == newHolder) {
				if (oldHolder != null) {
					//if the two holders are equal, call dispatch change only once
					dispatchChangeFinished(oldHolder, /*ignored*/true);
				}
			} else {
				//else call dispatch change once for every non-null holder
				if (oldHolder != null) {
					dispatchChangeFinished(oldHolder, true);
				}
				if (newHolder != null) {
					dispatchChangeFinished(newHolder, false);
				}
			}
			//we don't need a call to requestPendingTransactions after this, return false.
			return false;
		}
	}
}
